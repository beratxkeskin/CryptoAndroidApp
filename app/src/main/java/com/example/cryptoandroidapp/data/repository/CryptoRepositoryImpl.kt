package com.example.cryptoandroidapp.data.repository

import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.data.mapper.toCryptoModel
import com.example.cryptoandroidapp.data.mapper.updateWithTicker
import com.example.cryptoandroidapp.data.remote.IBinanceWebSocketService
import com.example.cryptoandroidapp.data.remote.CoinGeckoApi
import com.example.cryptoandroidapp.domain.model.CryptoModel
import com.example.cryptoandroidapp.domain.model.GlobalMarketData
import com.example.cryptoandroidapp.domain.repository.ICryptoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton
import com.example.cryptoandroidapp.data.mapper.toCryptoDetailModel
import com.example.cryptoandroidapp.data.mapper.toCandleModel
import com.example.cryptoandroidapp.domain.model.CryptoDetailModel
import com.example.cryptoandroidapp.domain.model.CandleModel

@Singleton
class CryptoRepositoryImpl @Inject constructor(
    private val coinGeckoApi: CoinGeckoApi,
    private val binanceWebSocketService: IBinanceWebSocketService
) : ICryptoRepository {

    private var cachedCryptoList: List<CryptoModel> = emptyList()

    // Detay ve Grafik verileri için oturum bazlı önbellek (Session Cache) haritaları
    private val cachedCoinDetails = mutableMapOf<String, CryptoDetailModel>()
    private val coinGeckoRequestMutex = Mutex()
    private var lastCoinGeckoRequestAt = 0L
    private val cachedCoinOhlc = mutableMapOf<String, List<CandleModel>>() // Key formatı: "coinId_days"

    private suspend fun <T> executeCoinGeckoRequest(request: suspend () -> T): T =
        coinGeckoRequestMutex.withLock {
            repeat(MAX_RATE_LIMIT_RETRIES) { attempt ->
                val elapsed = System.currentTimeMillis() - lastCoinGeckoRequestAt
                if (elapsed < MIN_REQUEST_INTERVAL_MS) {
                    delay(MIN_REQUEST_INTERVAL_MS - elapsed)
                }

                try {
                    lastCoinGeckoRequestAt = System.currentTimeMillis()
                    return@withLock request()
                } catch (error: HttpException) {
                    if (error.code() != HTTP_TOO_MANY_REQUESTS || attempt == MAX_RATE_LIMIT_RETRIES - 1) {
                        throw error
                    }

                    val retryAfterMs = error.response()?.headers()?.get("Retry-After")
                        ?.toLongOrNull()
                        ?.times(1_000L)
                    delay(retryAfterMs ?: (INITIAL_RETRY_DELAY_MS shl attempt))
                }
            }
            error("Unreachable")
        }

    private fun getBinanceSymbol(coinSymbol: String): String {
        return "${coinSymbol.uppercase()}USDT"
    }

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    override fun getCryptoList(): Flow<Resource<List<CryptoModel>>> = flow {
        emit(Resource.Loading())

        if (cachedCryptoList.isEmpty()) {
            try {
                val coinDtos = executeCoinGeckoRequest { coinGeckoApi.getCoins() }
                cachedCryptoList = coinDtos.map { it.toCryptoModel() }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "CoinGecko verileri yüklenemedi."))
                return@flow
            }
        }

        emit(Resource.Success(cachedCryptoList))

        val symbols = cachedCryptoList.map { it.symbol }
        val coinIndexByBinanceSymbol = cachedCryptoList
            .mapIndexed { index, coin -> getBinanceSymbol(coin.symbol) to index }
            .toMap()
        val liveCoins = cachedCryptoList.toMutableList()

        try {
            binanceWebSocketService.connect(symbols)

            binanceWebSocketService.tickerFlow
                // Her gelen ticker önce kendi coinini bellekte günceller. sample() bu
                // adımdan sonra olduğu için hiçbir coin, başka bir coinin mesajı yüzünden
                // atlanmaz.
                .onEach { ticker ->
                    val index = coinIndexByBinanceSymbol[ticker.symbol] ?: return@onEach
                    liveCoins[index] = liveCoins[index].updateWithTicker(ticker)
                }
                // Sadece Compose listesinin yeniden çizimini sınırlandırıyoruz.
                .sample(300L)
                .collect {
                    val updatedList = liveCoins.toList()
                    cachedCryptoList = updatedList
                    emit(Resource.Success(updatedList))
                }
        } finally {
            binanceWebSocketService.disconnect()
        }
    }

    override suspend fun getGlobalMarketData(): Resource<GlobalMarketData> {
        return try {
            val response = executeCoinGeckoRequest { coinGeckoApi.getGlobalData() }
            val data = response.data
            val totalMarketCap = data.totalMarketCap["usd"] ?: 0.0
            val totalVolume = data.totalVolume["usd"] ?: 0.0
            val btcDominance = data.marketCapPercentage["btc"] ?: 0.0
            val change24h = data.marketCapChangePercentage24hUsd

            Resource.Success(
                GlobalMarketData(
                    totalMarketCapUsd = totalMarketCap,
                    totalVolumeUsd = totalVolume,
                    btcDominance = btcDominance,
                    marketCapChangePercentage24hUsd = change24h
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Küresel piyasa verileri yüklenemedi.")
        }
    }

    // Detay verisini önbellek kontrollü çeker
    override suspend fun getCoinDetail(id: String): Resource<CryptoDetailModel> {
        // Önce belleğe bakıyoruz
        if (cachedCoinDetails.containsKey(id)) {
            return Resource.Success(cachedCoinDetails[id]!!)
        }

        return try {
            val domainModel = executeCoinGeckoRequest {
                coinGeckoApi.getCoinDetail(id).toCryptoDetailModel()
            }
            // Başarılı sonucu belleğe kaydediyoruz
            cachedCoinDetails[id] = domainModel
            Resource.Success(domainModel)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Coin detay verisi alınamadı.")
        }
    }

    // Çizgi grafik fiyat verisini önbellek kontrollü çeker.
    override suspend fun getCoinOhlc(id: String, days: String): Resource<List<CandleModel>> {
        // OHLC tabanlı eski oturum önbelleğiyle karışmasın.
        val cacheKey = "${id}_${days}_line_v2"
        // Önce belleğe bakıyoruz
        if (cachedCoinOhlc.containsKey(cacheKey)) {
            return Resource.Success(cachedCoinOhlc[cacheKey]!!)
        }

        return try {
            val candles = executeCoinGeckoRequest {
                coinGeckoApi.getCoinMarketChart(id = id, days = days).prices
                    .mapNotNull { point ->
                        if (point.size < 2) return@mapNotNull null
                        val timestamp = point[0].toLong()
                        val price = point[1]
                        if (timestamp <= 0L || !price.isFinite()) return@mapNotNull null
                        CandleModel(timestamp, price, price, price, price)
                    }
                    // Bozuk/tekrar eden bir API noktası eksen hesabını kararsızlaştırmamalı.
                    .filter { candle ->
                        candle.timestamp > 0L &&
                            candle.open.isFinite() && candle.high.isFinite() &&
                            candle.low.isFinite() && candle.close.isFinite()
                    }
                    .distinctBy { it.timestamp }
                    .sortedBy { it.timestamp }
            }
            // Başarılı sonucu belleğe kaydediyoruz
            cachedCoinOhlc[cacheKey] = candles
            Resource.Success(candles)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Grafik geçmiş verileri alınamadı.")
        }
    }

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    override fun getCryptoPriceStream(symbol: String): Flow<Double> = flow {
        val binanceSymbol = getBinanceSymbol(symbol)
        try {
            binanceWebSocketService.connect(listOf(symbol))
            binanceWebSocketService.tickerFlow
                .filter { it.symbol.equals(binanceSymbol, ignoreCase = true) }
                .sample(300L)
                .collect { ticker ->
                    ticker.lastPrice.toDoubleOrNull()?.let { emit(it) }
                }
        } finally {
            binanceWebSocketService.disconnect()
        }
    }

    private companion object {
        const val MIN_REQUEST_INTERVAL_MS = 1_500L
        const val INITIAL_RETRY_DELAY_MS = 2_000L
        const val MAX_RATE_LIMIT_RETRIES = 3
        const val HTTP_TOO_MANY_REQUESTS = 429
    }
}

