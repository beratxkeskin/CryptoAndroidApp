package com.example.cryptoandroidapp.presentation.crypto_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.CandleModel
import com.example.cryptoandroidapp.domain.model.CryptoDetailModel
import com.example.cryptoandroidapp.domain.use_case.coin.GetCoinDetailUseCase
import com.example.cryptoandroidapp.domain.use_case.coin.GetCoinOhlcUseCase
import com.example.cryptoandroidapp.domain.use_case.coin.ObserveLivePriceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CryptoDetailViewModel @Inject constructor(
    private val getCoinDetailUseCase: GetCoinDetailUseCase,
    private val getCoinOhlcUseCase: GetCoinOhlcUseCase,
    private val observeLivePriceUseCase: ObserveLivePriceUseCase
) : ViewModel(), ICryptoDetailViewModel {

    private val _uiState = MutableStateFlow<CryptoDetailUiState>(CryptoDetailUiState.Loading)
    override val uiState: StateFlow<CryptoDetailUiState> = _uiState.asStateFlow()

    private val _selectedDays = MutableStateFlow("1")
    override val selectedDays: StateFlow<String> = _selectedDays.asStateFlow()

    private var currentCoinId: String? = null
    private var chartJob: Job? = null
    private var webSocketJob: Job? = null

    override fun loadCoinDetail(id: String) {
        currentCoinId = id
        viewModelScope.launch {
            getCoinDetailUseCase(id).collect { detailResult ->
                when (detailResult) {
                    is Resource.Success<CryptoDetailModel> -> {
                        val detail = detailResult.data
                        if (detail == null) {
                            _uiState.value = CryptoDetailUiState.Error("Coin detayı bulunamadı.")
                            return@collect
                        }

                        _uiState.value = CryptoDetailUiState.Success(
                            coinDetail = detail,
                            candles = emptyList()
                        )

                        observeLivePrice(detail.symbol)
                        loadChartData(id, _selectedDays.value)
                    }

                    is Resource.Error<CryptoDetailModel> -> {
                        _uiState.value = CryptoDetailUiState.Error(
                            detailResult.message ?: "Detay verileri yüklenirken hata oluştu."
                        )
                    }

                    is Resource.Loading<CryptoDetailModel> -> {
                        _uiState.value = CryptoDetailUiState.Loading
                    }
                }
            }
        }
    }

    override fun setTimeRange(days: String) {
        if (_selectedDays.value == days) return
        _selectedDays.value = days
        val coinId = currentCoinId ?: return
        loadChartData(coinId, days)
    }

    private fun loadChartData(coinId: String, days: String) {
        chartJob?.cancel()
        chartJob = viewModelScope.launch {
            getCoinOhlcUseCase(coinId, days).collect { ohlcResult ->
                when (ohlcResult) {
                    is Resource.Success<List<CandleModel>> -> {
                        val candles = ohlcResult.data ?: emptyList()
                        val latestState = _uiState.value
                        if (latestState is CryptoDetailUiState.Success) {
                            _uiState.value = latestState.copy(
                                candles = sanitizeCandleList(candles)
                            )
                        }
                    }

                    is Resource.Error<List<CandleModel>> -> Unit
                    is Resource.Loading<List<CandleModel>> -> Unit
                }
            }
        }
    }

    private fun observeLivePrice(symbol: String) {
        webSocketJob?.cancel()
        webSocketJob = viewModelScope.launch {
            observeLivePriceUseCase(symbol).collect { livePrice ->
                val currentState = _uiState.value
                if (currentState is CryptoDetailUiState.Success) {
                    val updatedDetail = currentState.coinDetail.copy(
                        currentPriceUsd = livePrice
                    )

                    val updatedCandles = currentState.candles.toMutableList()
                    if (updatedCandles.isNotEmpty() && _selectedDays.value == "1") {
                        val lastIndex = updatedCandles.lastIndex
                        val lastCandle = updatedCandles[lastIndex]
                        val newHigh = maxOf(lastCandle.high, livePrice)
                        val newLow = minOf(lastCandle.low, livePrice)
                        updatedCandles[lastIndex] = lastCandle.copy(
                            close = livePrice,
                            high = newHigh,
                            low = newLow
                        )
                    }

                    _uiState.value = currentState.copy(
                        coinDetail = updatedDetail,
                        candles = updatedCandles
                    )
                }
            }
        }
    }

    private fun sanitizeCandleList(rawCandles: List<CandleModel>): List<CandleModel> {
        return rawCandles
            .filter { candle ->
                candle.timestamp > 0L &&
                    candle.open.isFinite() && candle.high.isFinite() &&
                    candle.low.isFinite() && candle.close.isFinite()
            }
            .distinctBy { it.timestamp }
            .sortedBy { it.timestamp }
    }

    override fun onCleared() {
        super.onCleared()
        chartJob?.cancel()
        webSocketJob?.cancel()
    }
}
