package com.example.cryptoandroidapp.domain.model

/**
 * Represents a single crypto asset owned by the user in their portfolio,
 * which will be saved and synchronized in Firestore.
 * 
 * Note: Default constructor values are required for Firestore serialization.
 */
data class UserPortfolioAsset(
    val coinId: String = "",
    val symbol: String = "",
    val name: String = "",
    val amount: Double = 0.0,
    val addedPriceUsd: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
