package com.example.cryptoandroidapp.domain.model

data class CryptoDetailModel(
    val id: String,
    val name: String,
    val symbol: String,
    val description: String,
    val currentPriceUsd: Double,
    val marketCapUsd: Double,
    val totalVolumeUsd: Double,
    val high24hUsd: Double,
    val low24hUsd: Double,
    val priceChangePercentage24h: Double,
    val marketCapRank: Int?,
    val circulatingSupply: Double?,
    val maxSupply: Double?,
    val allTimeHighUsd: Double?,
    val allTimeHighChangePercentage: Double?,
    val priceChangePercentage7d: Double?,
    val priceChangePercentage30d: Double?,
    val imageUrl: String
)
