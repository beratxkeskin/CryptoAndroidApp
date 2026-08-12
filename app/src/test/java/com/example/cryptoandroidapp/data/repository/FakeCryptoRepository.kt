package com.example.cryptoandroidapp.data.repository

import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.CandleModel
import com.example.cryptoandroidapp.domain.model.CryptoDetailModel
import com.example.cryptoandroidapp.domain.model.CryptoModel
import com.example.cryptoandroidapp.domain.model.GlobalMarketData
import com.example.cryptoandroidapp.domain.repository.ICryptoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Unit testlerde [HomeViewModel] ve diğer sınıflara sahte veriler sunan Fake Repository sınıfı.
 */
class FakeCryptoRepository : ICryptoRepository {

    // Testlerde dilediğimiz zaman değiştirebileceğimiz sahte coin listemiz
    var sampleCoinsList = listOf(
        CryptoModel(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            imageUrl = "https://assets.coingecko.com/btc.png",
            currentPrice = 60000.0,
            marketCap = 1_200_000_000.0,
            marketCapRank = 1,
            priceChangePercentage24h = 2.5
        ),
        CryptoModel(
            id = "ethereum",
            symbol = "eth",
            name = "Ethereum",
            imageUrl = "https://assets.coingecko.com/eth.png",
            currentPrice = 3000.0,
            marketCap = 350_000_000.0,
            marketCapRank = 2,
            priceChangePercentage24h = -1.2
        )
    )

    override fun getCryptoList(): Flow<Resource<List<CryptoModel>>> {
        return flowOf(Resource.Success(sampleCoinsList))
    }

    override fun getCryptoPriceStream(symbol: String): Flow<Double> {
        return flowOf(60000.0)
    }

    override suspend fun getGlobalMarketData(): Resource<GlobalMarketData> {
        return Resource.Success(
            GlobalMarketData(
                totalMarketCapUsd = 2.5e12,
                totalVolumeUsd = 9.0e10,
                btcDominance = 54.2,
                marketCapChangePercentage24hUsd = 1.5
            )
        )
    }

    override suspend fun getCoinDetail(id: String): Resource<CryptoDetailModel> {
        return Resource.Error("Test mock detail")
    }

    override suspend fun getCoinOhlc(id: String, days: String): Resource<List<CandleModel>> {
        return Resource.Success(emptyList())
    }
}