package com.example.cryptoandroidapp.data.mapper

import com.example.cryptoandroidapp.data.model.BinanceTickerDto
import com.example.cryptoandroidapp.data.model.CoinDto
import com.example.cryptoandroidapp.data.model.CoinDetailResponseDto
import com.example.cryptoandroidapp.domain.model.CryptoModel
import com.example.cryptoandroidapp.domain.model.CryptoDetailModel
import com.example.cryptoandroidapp.domain.model.CandleModel

// 1. REST API modelini Domain modeline dönüştürür
fun CoinDto.toCryptoModel(): CryptoModel {
    return CryptoModel(
        id = this.id,
        symbol = this.symbol,
        name = this.name,
        imageUrl = this.imageUrl,
        currentPrice = this.currentPrice,
        marketCap = this.marketCap ?: 0.0,
        totalVolume = this.totalVolume ?: 0.0,
        marketCapRank = this.marketCapRank,
        priceChangePercentage24h = this.priceChangePercentage24h ?: 0.0,
        priceHistory7d = this.sparklineIn7d?.prices.orEmpty().filter { it.isFinite() }
    )
}

// 2. Mevcut Domain modelini WebSocket'ten gelen anlık fiyatlarla günceller
fun CryptoModel.updateWithTicker(ticker: BinanceTickerDto): CryptoModel {
    return this.copy(
        currentPrice = ticker.lastPrice.toDoubleOrNull() ?: this.currentPrice,
        priceChangePercentage24h = ticker.priceChangePercent.toDoubleOrNull() ?: this.priceChangePercentage24h
    )
}

// 3. Detay DTO modelini Domain detay modeline dönüştürür
fun CoinDetailResponseDto.toCryptoDetailModel(): CryptoDetailModel {
    return CryptoDetailModel(
        id = this.id,
        name = this.name,
        symbol = this.symbol,
        description = this.description.tr.orEmpty().ifBlank { this.description.en.orEmpty() },
        currentPriceUsd = this.marketData.currentPrice["usd"] ?: 0.0,
        marketCapUsd = this.marketData.marketCap["usd"] ?: 0.0,
        totalVolumeUsd = this.marketData.totalVolume["usd"] ?: 0.0,
        high24hUsd = this.marketData.high24h["usd"] ?: 0.0,
        low24hUsd = this.marketData.low24h["usd"] ?: 0.0,
        priceChangePercentage24h = this.marketData.priceChangePercentage24h ?: 0.0,
        marketCapRank = this.marketCapRank,
        circulatingSupply = this.marketData.circulatingSupply,
        maxSupply = this.marketData.maxSupply,
        allTimeHighUsd = this.marketData.ath["usd"],
        allTimeHighChangePercentage = this.marketData.athChangePercentage["usd"],
        priceChangePercentage7d = this.marketData.priceChangePercentage7d,
        priceChangePercentage30d = this.marketData.priceChangePercentage30d,
        imageUrl = this.image.large
    )
}

// 4. API'den gelen sayı listesini (array) CandleModel'e dönüştürür
fun List<Double>.toCandleModel(): CandleModel? {
    if (this.size < 5) return null
    return CandleModel(
        timestamp = this[0].toLong(),
        open = this[1],
        high = this[2],
        low = this[3],
        close = this[4]
    )
}
