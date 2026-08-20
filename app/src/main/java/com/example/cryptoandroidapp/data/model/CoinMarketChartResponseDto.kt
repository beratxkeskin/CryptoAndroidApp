package com.example.cryptoandroidapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinMarketChartResponseDto(
    @SerialName("prices")
    val prices: List<List<Double>> = emptyList()
)
