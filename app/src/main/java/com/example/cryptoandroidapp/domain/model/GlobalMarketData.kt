package com.example.cryptoandroidapp.domain.model

data class GlobalMarketData(
    val totalMarketCapUsd: Double,
    val totalVolumeUsd: Double,
    val btcDominance: Double,
    val marketCapChangePercentage24hUsd: Double
)
