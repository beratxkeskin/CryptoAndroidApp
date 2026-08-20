 package com.example.cryptoandroidapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinDetailResponseDto(
    @SerialName("id")
    val id: String,

    @SerialName("symbol")
    val symbol: String,

    @SerialName("name")
    val name: String,

    @SerialName("market_cap_rank")
    val marketCapRank: Int? = null,

    @SerialName("description")
    val description: DescriptionDto,

    @SerialName("image")
    val image: ImageDto,

    @SerialName("market_data")
    val marketData: MarketDataDto
)

@Serializable
data class DescriptionDto(
    @SerialName("en")
    val en: String? = "", // İngilizce açıklama
    @SerialName("tr")
    val tr: String? = ""  // Türkçe açıklama
)

@Serializable
data class ImageDto(
    @SerialName("large")
    val large: String     // Büyük boy logo URL adresi
)

@Serializable
data class MarketDataDto(
    @SerialName("current_price")
    val currentPrice: Map<String, Double> = emptyMap(), // Farklı döviz fiyatları (örn: "usd" -> 67800.0)

    @SerialName("market_cap")
    val marketCap: Map<String, Double> = emptyMap(),

    @SerialName("total_volume")
    val totalVolume: Map<String, Double> = emptyMap(),

    @SerialName("high_24h")
    val high24h: Map<String, Double> = emptyMap(),

    @SerialName("low_24h")
    val low24h: Map<String, Double> = emptyMap(),

    @SerialName("price_change_percentage_24h")
    val priceChangePercentage24h: Double? = 0.0,

    @SerialName("price_change_percentage_7d")
    val priceChangePercentage7d: Double? = null,

    @SerialName("price_change_percentage_30d")
    val priceChangePercentage30d: Double? = null,

    @SerialName("circulating_supply")
    val circulatingSupply: Double? = null,

    @SerialName("max_supply")
    val maxSupply: Double? = null,

    @SerialName("ath")
    val ath: Map<String, Double> = emptyMap(),

    @SerialName("ath_change_percentage")
    val athChangePercentage: Map<String, Double> = emptyMap()
)
