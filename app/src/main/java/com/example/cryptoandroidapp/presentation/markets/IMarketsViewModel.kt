package com.example.cryptoandroidapp.presentation.markets

import com.example.cryptoandroidapp.domain.model.CryptoModel
import com.example.cryptoandroidapp.domain.model.GlobalMarketData
import kotlinx.coroutines.flow.StateFlow

sealed interface MarketsUiState {
    object Loading : MarketsUiState
    data class Success(
        val coins: List<CryptoModel>,
        val filteredCoins: List<CryptoModel>,
        val globalMarketData: GlobalMarketData,
        val favoriteCoinIds: List<String>,
        val searchQuery: String,
        val selectedFilter: MarketFilter,
        val isSearchVisible: Boolean
    ) : MarketsUiState
    data class Error(val message: String) : MarketsUiState
}

enum class MarketFilter {
    ALL, WATCHLIST, TRENDING
}

interface IMarketsViewModel {
    val uiState: StateFlow<MarketsUiState>
    fun toggleFavorite(coinId: String)
    fun updateSearchQuery(query: String)
    fun updateFilter(filter: MarketFilter)
    fun toggleSearchVisibility()
}
