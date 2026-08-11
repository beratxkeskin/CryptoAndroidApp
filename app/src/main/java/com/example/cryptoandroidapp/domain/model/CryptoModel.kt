package com.example.cryptoandroidapp.domain.model

data class CryptoModel(
    val id: String,
    val symbol: String,
    val name: String,
    val imageUrl: String,
    val currentPrice: Double,
    val marketCap: Double,
    val marketCapRank: Int?,
    val priceChangePercentage24h: Double,
    val priceHistory7d: List<Double> = emptyList(),
    val totalVolume: Double = 0.0
)
