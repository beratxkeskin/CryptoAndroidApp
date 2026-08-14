package com.example.cryptoandroidapp.domain.model

import androidx.compose.runtime.Immutable

/**
 * Compose derleyicisine bu modelin değişmez (Immutable) olduğunu bildirir.
 * Bu sayede WebSocket'ten canlı fiyat aktığında listede değişmeyen diğer tüm coinlerin
 * gereksiz yere yeniden çizilmesi (Recomposition) engellenir ve 120Hz ekran akıcılığı sağlanır.
 */
@Immutable
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
