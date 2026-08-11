package com.example.cryptoandroidapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinGeckoGlobalResponse(
    @SerialName("data")
    val data: CoinGeckoGlobalDataDto
)

@Serializable
data class CoinGeckoGlobalDataDto(
    @SerialName("total_market_cap")
    val totalMarketCap: Map<String, Double>,
    @SerialName("total_volume")
    val totalVolume: Map<String, Double>,
    @SerialName("market_cap_percentage")
    val marketCapPercentage: Map<String, Double>,
    @SerialName("market_cap_change_percentage_24h_usd")
    val marketCapChangePercentage24hUsd: Double
)
