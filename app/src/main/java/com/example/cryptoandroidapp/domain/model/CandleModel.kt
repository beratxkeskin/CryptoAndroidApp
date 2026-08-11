package com.example.cryptoandroidapp.domain.model

data class CandleModel(
    val timestamp: Long, // Mumun kapanış zamanı (milisaniye cinsinden)
    val open: Double,    // Açılış fiyatı
    val high: Double,    // En yüksek fiyat
    val low: Double,     // En düşük fiyat
    val close: Double    // Kapanış fiyatı
)