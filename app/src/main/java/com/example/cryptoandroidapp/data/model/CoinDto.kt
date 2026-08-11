package com.example.cryptoandroidapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinDto(
    @SerialName("id")
    val id: String,

    @SerialName("symbol")
    val symbol: String,

    @SerialName("name")
    val name: String,

    @SerialName("image")
    val imageUrl: String,

    @SerialName("current_price")
    val currentPrice: Double,

    @SerialName("market_cap")
    val marketCap: Double? = 0.0,

    @SerialName("total_volume")
    val totalVolume: Double? = 0.0,

    @SerialName("market_cap_rank")
    val marketCapRank: Int? = null,

    @SerialName("price_change_percentage_24h")
    val priceChangePercentage24h: Double? = 0.0,

    @SerialName("sparkline_in_7d")
    val sparklineIn7d: SparklineDto? = null
)

@Serializable
data class SparklineDto(
    @SerialName("price")
    val prices: List<Double> = emptyList()
)
