package com.example.cryptoandroidapp.data.repository

import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.repository.IFavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FakeFavoritesRepository : IFavoritesRepository {

    // Canlı Reaktif Akış (StateFlow)
    private val favoritesStateFlow = MutableStateFlow(listOf("bitcoin", "ethereum"))

    var shouldReturnError = false
    var errorMessage = "Firestore favoriler hatası"

    override fun getUserFavorites(userId: String): Flow<Resource<List<String>>> {
        if (shouldReturnError) {
            return flowOf(Resource.Error(errorMessage))
        }
        return favoritesStateFlow.map { Resource.Success(it) }
    }

    override suspend fun addFavorite(userId: String, coinId: String): Resource<Unit> {
        if (shouldReturnError) return Resource.Error(errorMessage)
        val current = favoritesStateFlow.value.toMutableList()
        if (!current.contains(coinId)) {
            current.add(coinId)
            favoritesStateFlow.value = current
        }
        return Resource.Success(Unit)
    }

    override suspend fun removeFavorite(userId: String, coinId: String): Resource<Unit> {
        if (shouldReturnError) return Resource.Error(errorMessage)
        val current = favoritesStateFlow.value.toMutableList()
        current.remove(coinId)
        favoritesStateFlow.value = current
        return Resource.Success(Unit)
    }

    override suspend fun isFavorite(userId: String, coinId: String): Resource<Boolean> {
        if (shouldReturnError) return Resource.Error(errorMessage)
        return Resource.Success(favoritesStateFlow.value.contains(coinId))
    }
}