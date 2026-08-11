package com.example.cryptoandroidapp.presentation.favorites

import kotlinx.coroutines.flow.StateFlow

interface IFavoritesViewModel {
    val uiState: StateFlow<FavoritesUiState>
    fun toggleFavorite(coinId: String)
}
