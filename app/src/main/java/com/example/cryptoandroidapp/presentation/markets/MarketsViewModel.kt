package com.example.cryptoandroidapp.presentation.markets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.CryptoModel
import com.example.cryptoandroidapp.domain.model.GlobalMarketData
import com.example.cryptoandroidapp.domain.repository.IAuthRepository
import com.example.cryptoandroidapp.domain.repository.ICryptoRepository
import com.example.cryptoandroidapp.domain.repository.IFavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketsViewModel @Inject constructor(
    private val cryptoRepository: ICryptoRepository,
    private val favoritesRepository: IFavoritesRepository,
    private val authRepository: IAuthRepository
) : ViewModel(), IMarketsViewModel {

    private val _uiState = MutableStateFlow<MarketsUiState>(MarketsUiState.Loading)
    override val uiState: StateFlow<MarketsUiState> = _uiState.asStateFlow()

    private var searchQuery = ""
    private var selectedFilter = MarketFilter.ALL
    private var isSearchVisible = false
    private var cachedGlobalData = GlobalMarketData(
        totalMarketCapUsd = 2410000000000.0,
        totalVolumeUsd = 98470000000.0,
        btcDominance = 52.48,
        marketCapChangePercentage24hUsd = 2.65
    )

    init {
        loadMarketsData()
    }

    private fun loadMarketsData() {
        val userId = authRepository.getCurrentUser()?.uid ?: ""
        viewModelScope.launch {
            // Fetch global data once
            val globalRes = cryptoRepository.getGlobalMarketData()
            if (globalRes is Resource.Success && globalRes.data != null) {
                cachedGlobalData = globalRes.data
            }

            combine(
                cryptoRepository.getCryptoList(),
                favoritesRepository.getUserFavorites(userId)
            ) { coinsRes, favoritesRes ->
                Pair(coinsRes, favoritesRes)
            }.collect { (coinsRes, favoritesRes) ->
                if (coinsRes is Resource.Loading) {
                    if (_uiState.value !is MarketsUiState.Success) {
                        _uiState.value = MarketsUiState.Loading
                    }
                }

                if (coinsRes is Resource.Error) {
                    _uiState.value = MarketsUiState.Error(coinsRes.message ?: "Coin listesi yüklenemedi")
                    return@collect
                }

                if (coinsRes is Resource.Success && favoritesRes is Resource.Success) {
                    val allCoins = coinsRes.data ?: emptyList()
                    val favoriteIds = favoritesRes.data ?: emptyList()
                    updateUiState(allCoins, favoriteIds)
                }
            }
        }
    }

    private fun updateUiState(allCoins: List<CryptoModel>, favoriteIds: List<String>) {
        // Filter by selected filter
        var filteredList = when (selectedFilter) {
            MarketFilter.ALL -> allCoins
            MarketFilter.WATCHLIST -> allCoins.filter { favoriteIds.contains(it.id) }
            MarketFilter.TRENDING -> allCoins.sortedByDescending { Math.abs(it.priceChangePercentage24h) }
        }

        // Filter by search query
        if (searchQuery.trim().isNotEmpty()) {
            val query = searchQuery.trim()
            filteredList = filteredList.filter { coin ->
                coin.name.contains(query, ignoreCase = true) ||
                        coin.symbol.contains(query, ignoreCase = true)
            }
        }

        _uiState.value = MarketsUiState.Success(
            coins = allCoins,
            filteredCoins = filteredList,
            globalMarketData = cachedGlobalData,
            favoriteCoinIds = favoriteIds,
            searchQuery = searchQuery,
            selectedFilter = selectedFilter,
            isSearchVisible = isSearchVisible
        )
    }

    override fun toggleFavorite(coinId: String) {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            val currentSuccess = _uiState.value as? MarketsUiState.Success ?: return@launch
            val isFavorited = currentSuccess.favoriteCoinIds.contains(coinId)
            if (isFavorited) {
                favoritesRepository.removeFavorite(userId, coinId)
            } else {
                favoritesRepository.addFavorite(userId, coinId)
            }
        }
    }

    override fun updateSearchQuery(query: String) {
        searchQuery = query
        refreshUiState()
    }

    override fun updateFilter(filter: MarketFilter) {
        selectedFilter = filter
        refreshUiState()
    }

    override fun toggleSearchVisibility() {
        isSearchVisible = !isSearchVisible
        if (!isSearchVisible) {
            searchQuery = ""
        }
        refreshUiState()
    }

    private fun refreshUiState() {
        val current = _uiState.value as? MarketsUiState.Success ?: return
        updateUiState(current.coins, current.favoriteCoinIds)
    }
}
