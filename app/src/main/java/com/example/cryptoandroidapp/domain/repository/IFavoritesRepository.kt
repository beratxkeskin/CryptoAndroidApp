package com.example.cryptoandroidapp.domain.repository

import com.example.cryptoandroidapp.common.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining operations for managing user favorite coin lists in Firestore.
 */
interface IFavoritesRepository {
    
    /**
     * Listens to the user's favorite coin IDs in real-time from Firestore.
     */
    fun getUserFavorites(userId: String): Flow<Resource<List<String>>>
    
    /**
     * Adds a coin ID to the user's Firestore favorites.
     */
    suspend fun addFavorite(userId: String, coinId: String): Resource<Unit>
    
    /**
     * Removes a coin ID from the user's Firestore favorites.
     */
    suspend fun removeFavorite(userId: String, coinId: String): Resource<Unit>
    
    /**
     * Checks if a coin is favorited.
     */
    suspend fun isFavorite(userId: String, coinId: String): Resource<Boolean>
}
