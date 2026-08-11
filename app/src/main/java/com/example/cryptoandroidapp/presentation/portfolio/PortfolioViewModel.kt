package com.example.cryptoandroidapp.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.CandleModel
import com.example.cryptoandroidapp.domain.model.CryptoModel
import com.example.cryptoandroidapp.domain.model.UserPortfolioAsset
import com.example.cryptoandroidapp.domain.repository.IAuthRepository
import com.example.cryptoandroidapp.domain.repository.ICryptoRepository
import com.example.cryptoandroidapp.domain.repository.IPortfolioRepository
import com.example.cryptoandroidapp.domain.use_case.portfolio.CalculatePortfolioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

sealed interface PortfolioUiState {
    data object Loading : PortfolioUiState
    data class Success(
        val portfolioTotalText: String,
        val portfolioChangeText: String,
        val isPortfolioPositive: Boolean,
        val totalCostUsd: Double,
        val unrealizedPnlUsd: Double,
        val unrealizedPnlPercent: Double,
        val userAssets: List<UserAssetItem>,
        val rawUserAssets: List<UserPortfolioAsset>,
        val allCoins: List<CryptoModel>,
        val selectedDays: String = "7",
        val chartPoints: List<PortfolioChartPoint> = emptyList(),
        val isChartLoading: Boolean = false,
        val chartError: String? = null
    ) : PortfolioUiState
    data class Error(val message: String) : PortfolioUiState
}

data class PortfolioChartPoint(val timestamp: Long, val valueUsd: Double)

data class UserAssetItem(
    val coinId: String,
    val name: String,
    val symbol: String,
    val amount: Double,
    val totalValueUsd: Double,
    val currentPriceUsd: Double,
    val averageCostUsd: Double,
    val pnlUsd: Double,
    val pnlPercent: Double,
    val imageUrl: String
)

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val portfolioRepository: IPortfolioRepository,
    private val cryptoRepository: ICryptoRepository,
    private val authRepository: IAuthRepository,
    private val calculatePortfolioUseCase: CalculatePortfolioUseCase
) : ViewModel(), IPortfolioViewModel {

    private val _uiState = MutableStateFlow<PortfolioUiState>(PortfolioUiState.Loading)
    override val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()
    private var chartJob: Job? = null
    private var latestCoins: List<CryptoModel> = emptyList()
    private var latestPortfolio: List<UserPortfolioAsset> = emptyList()

    init { loadPortfolio() }

    private fun loadPortfolio() {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            combine(cryptoRepository.getCryptoList(), portfolioRepository.getUserPortfolio(userId)) { coins, portfolio ->
                coins to portfolio
            }.collect { (coinsRes, portfolioRes) ->
                if (coinsRes is Resource.Loading || portfolioRes is Resource.Loading) {
                    if (_uiState.value !is PortfolioUiState.Success) _uiState.value = PortfolioUiState.Loading
                }
                if (coinsRes is Resource.Error) {
                    _uiState.value = PortfolioUiState.Error(coinsRes.message ?: "Coin verileri yüklenemedi")
                    return@collect
                }
                if (coinsRes is Resource.Success && portfolioRes is Resource.Success) {
                    latestCoins = coinsRes.data.orEmpty()
                    latestPortfolio = portfolioRes.data.orEmpty()
                    calculatePortfolio()
                }
            }
        }
    }

    private fun calculatePortfolio() {
        val calcResult = calculatePortfolioUseCase(latestPortfolio, latestCoins)
        var totalCostUsd = 0.0

        val items = latestPortfolio.mapNotNull { asset ->
            val coin = latestCoins.find { it.id == asset.coinId || it.symbol.equals(asset.symbol, true) } ?: return@mapNotNull null
            val currentPrice = coin.currentPrice
            val currentValue = currentPrice * asset.amount
            val cost = asset.addedPriceUsd * asset.amount
            totalCostUsd += cost
            val pnl = currentValue - cost
            UserAssetItem(
                coinId = asset.coinId, name = asset.name, symbol = asset.symbol, amount = asset.amount,
                totalValueUsd = currentValue, currentPriceUsd = currentPrice, averageCostUsd = asset.addedPriceUsd,
                pnlUsd = pnl, pnlPercent = if (cost > 0.0) pnl / cost * 100.0 else 0.0, imageUrl = coin.imageUrl
            )
        }

        val pnl = calcResult.totalValueUsd - totalCostUsd
        val old = _uiState.value as? PortfolioUiState.Success
        _uiState.value = PortfolioUiState.Success(
            portfolioTotalText = String.format(Locale.US, "$%,.2f", calcResult.totalValueUsd),
            portfolioChangeText = formatChange(calcResult.totalChangeUsd, calcResult.totalChangePercent),
            isPortfolioPositive = calcResult.isPositive,
            totalCostUsd = totalCostUsd,
            unrealizedPnlUsd = pnl,
            unrealizedPnlPercent = if (totalCostUsd > 0.0) pnl / totalCostUsd * 100.0 else 0.0,
            userAssets = items, rawUserAssets = latestPortfolio, allCoins = latestCoins,
            selectedDays = old?.selectedDays ?: "7", chartPoints = old?.chartPoints.orEmpty(),
            isChartLoading = old?.isChartLoading ?: false, chartError = old?.chartError
        )
        if (old == null) loadPortfolioChart("7")
    }

    override fun selectTimeRange(days: String) {
        val current = _uiState.value as? PortfolioUiState.Success ?: return
        if (current.selectedDays == days) return
        _uiState.value = current.copy(selectedDays = days)
        loadPortfolioChart(days)
    }

    private fun loadPortfolioChart(days: String) {
        chartJob?.cancel()
        chartJob = viewModelScope.launch {
            val current = _uiState.value as? PortfolioUiState.Success ?: return@launch
            _uiState.value = current.copy(isChartLoading = true, chartError = null)
            val dayCount = days.toIntOrNull() ?: 7
            val firstAsset = latestPortfolio.firstOrNull()
            if (firstAsset == null) {
                _uiState.value = current.copy(isChartLoading = false, chartPoints = emptyList())
                return@launch
            }
            when (val res = cryptoRepository.getCoinOhlc(firstAsset.coinId, days)) {
                is Resource.Success -> {
                    val points = buildPortfolioChart(res.data.orEmpty(), latestPortfolio, latestCoins, dayCount)
                    val latestState = _uiState.value as? PortfolioUiState.Success ?: return@launch
                    _uiState.value = latestState.copy(isChartLoading = false, chartPoints = points, chartError = null)
                }
                is Resource.Error -> {
                    val latestState = _uiState.value as? PortfolioUiState.Success ?: return@launch
                    _uiState.value = latestState.copy(isChartLoading = false, chartError = res.message ?: "Grafik yüklenemedi")
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun buildPortfolioChart(
        primaryCandles: List<CandleModel>,
        portfolio: List<UserPortfolioAsset>,
        coins: List<CryptoModel>,
        days: Int
    ): List<PortfolioChartPoint> {
        if (primaryCandles.isEmpty()) return emptyList()
        val assetCount = portfolio.sumOf { it.amount }.coerceAtLeast(1.0)
        return primaryCandles.map { candle ->
            PortfolioChartPoint(timestamp = candle.timestamp, valueUsd = candle.close * assetCount)
        }
    }

    override fun addAsset(coinId: String, symbol: String, name: String, amount: Double, costPerUnitUsd: Double) {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        val asset = UserPortfolioAsset(
            coinId = coinId,
            symbol = symbol,
            name = name,
            amount = amount,
            addedPriceUsd = costPerUnitUsd
        )
        viewModelScope.launch { portfolioRepository.addAssetToPortfolio(userId, asset) }
    }

    override fun deleteAsset(coinId: String) {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch { portfolioRepository.removeAssetFromPortfolio(userId, coinId) }
    }

    private fun formatChange(changeUsd: Double, changePercent: Double): String {
        val sign = if (changeUsd >= 0.0) "+" else "-"
        val arrow = if (changeUsd >= 0.0) "▲" else "▼"
        return String.format(Locale.US, "%s$%,.2f (%s %.2f%%) 24s", sign, Math.abs(changeUsd), arrow, Math.abs(changePercent))
    }
}
