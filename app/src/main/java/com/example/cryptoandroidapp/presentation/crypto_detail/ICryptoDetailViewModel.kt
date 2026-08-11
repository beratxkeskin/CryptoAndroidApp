package com.example.cryptoandroidapp.presentation.crypto_detail

import com.example.cryptoandroidapp.domain.model.CandleModel
import com.example.cryptoandroidapp.domain.model.CryptoDetailModel
import kotlinx.coroutines.flow.StateFlow

// Ekranımızın 3 farklı durumunu temsil eden UI State yapısı
sealed interface CryptoDetailUiState {
    object Loading : CryptoDetailUiState
    data class Success(
        val coinDetail: CryptoDetailModel,
        val candles: List<CandleModel>
    ) : CryptoDetailUiState
    data class Error(val message: String) : CryptoDetailUiState
}

interface ICryptoDetailViewModel {
    val uiState: StateFlow<CryptoDetailUiState>
    val selectedDays: StateFlow<String>

    fun loadCoinDetail(id: String)
    fun setTimeRange(days: String)
}
