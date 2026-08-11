package com.example.cryptoandroidapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BinanceTickerDto(
    @SerialName("s")
    val symbol: String, // İşlem çifti adı (Örn: "BTCUSDT")

    @SerialName("c")
    val lastPrice: String, // Canlı son fiyat (Örn: "65230.50")

    @SerialName("P")
    val priceChangePercent: String // Canlı 24 saatlik değişim yüzdesi (Örn: "-1.25")
)