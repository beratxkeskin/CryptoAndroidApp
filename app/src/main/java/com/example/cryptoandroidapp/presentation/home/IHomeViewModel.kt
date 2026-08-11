package com.example.cryptoandroidapp.presentation.home

import kotlinx.coroutines.flow.StateFlow

interface IHomeViewModel {
    val uiState: StateFlow<HomeUiState>
    fun toggleFavorite(coinId: String)
}
