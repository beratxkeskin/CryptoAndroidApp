package com.example.cryptoandroidapp.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.CryptoModel
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

sealed interface FavoritesUiState {
    object Loading : FavoritesUiState
    data class Success(
        val favoriteCoins: List<CryptoModel>,
        val favoriteCoinIds: List<String>
    ) : FavoritesUiState
    data class Error(val message: String) : FavoritesUiState
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: IFavoritesRepository,
    private val cryptoRepository: ICryptoRepository,
    private val authRepository: IAuthRepository
) : ViewModel(), IFavoritesViewModel {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    override val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            combine(
                cryptoRepository.getCryptoList(),
                favoritesRepository.getUserFavorites(userId)
            ) { coinsRes, favoritesRes -> coinsRes to favoritesRes }
                .collect { (coinsRes, favoritesRes) ->
                    if (coinsRes is Resource.Loading || favoritesRes is Resource.Loading) {
                        if (_uiState.value !is FavoritesUiState.Success) _uiState.value = FavoritesUiState.Loading
                    }

                    if (coinsRes is Resource.Error) {
                        _uiState.value = FavoritesUiState.Error(coinsRes.message ?: "Kripto paralar yüklenemedi")
                        return@collect
                    }

                    if (coinsRes is Resource.Success && favoritesRes is Resource.Success) {
                        val favoriteIds = favoritesRes.data.orEmpty()
                        val favoriteCoins = coinsRes.data.orEmpty().filter { it.id in favoriteIds }
                        _uiState.value = FavoritesUiState.Success(favoriteCoins, favoriteIds)
                    }
                }
        }
    }

    override fun toggleFavorite(coinId: String) {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        val currentIds = (uiState.value as? FavoritesUiState.Success)?.favoriteCoinIds.orEmpty()
        viewModelScope.launch {
            if (coinId in currentIds) favoritesRepository.removeFavorite(userId, coinId)
            else favoritesRepository.addFavorite(userId, coinId)
        }
    }
}
