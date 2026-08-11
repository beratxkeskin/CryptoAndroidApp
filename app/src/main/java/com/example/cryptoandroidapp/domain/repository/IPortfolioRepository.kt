package com.example.cryptoandroidapp.domain.repository

import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.UserPortfolioAsset
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining operations for managing user portfolio assets in Firestore.
 */
interface IPortfolioRepository {
    
    /**
     * Listens to the user's portfolio in real-time from Firestore.
     */
    fun getUserPortfolio(userId: String): Flow<Resource<List<UserPortfolioAsset>>>
    
    /**
     * Adds or updates a crypto asset in the user's Firestore portfolio.
     */
    suspend fun addAssetToPortfolio(userId: String, asset: UserPortfolioAsset): Resource<Unit>
    
    /**
     * Deletes a crypto asset from the user's Firestore portfolio by its unique coin ID.
     */
    suspend fun removeAssetFromPortfolio(userId: String, coinId: String): Resource<Unit>
}
