package com.example.cryptoandroidapp.presentation.portfolio

import kotlinx.coroutines.flow.StateFlow

interface IPortfolioViewModel {
    val uiState: StateFlow<PortfolioUiState>
    fun addAsset(coinId: String, symbol: String, name: String, amount: Double, costPerUnitUsd: Double)
    fun deleteAsset(coinId: String)
    fun selectTimeRange(days: String)
}
