package com.example.cryptoandroidapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.common.formatCryptoAmount
import com.example.cryptoandroidapp.domain.model.CryptoModel
import com.example.cryptoandroidapp.domain.model.UserPortfolioAsset
import com.example.cryptoandroidapp.domain.repository.IAuthRepository
import com.example.cryptoandroidapp.domain.repository.ICryptoRepository
import com.example.cryptoandroidapp.domain.repository.IFavoritesRepository
import com.example.cryptoandroidapp.domain.repository.IPortfolioRepository
import com.example.cryptoandroidapp.domain.use_case.portfolio.CalculatePortfolioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class UserAsset(
    val name: String,
    val symbol: String,
    val amountText: String,
    val valueText: String,
    val changeText: String,
    val isPositive: Boolean,
    val colorHex: Long,
    val imageUrl: String
)

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val portfolioTotalText: String,
        val portfolioChangeText: String,
        val isPortfolioPositive: Boolean,
        val userAssets: List<UserAsset>,
        val coins: List<CryptoModel>,
        val favoriteCoins: List<CryptoModel>,
        val favoriteCoinIds: List<String>
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cryptoRepository: ICryptoRepository,
    private val portfolioRepository: IPortfolioRepository,
    private val favoritesRepository: IFavoritesRepository,
    private val authRepository: IAuthRepository,
    private val calculatePortfolioUseCase: CalculatePortfolioUseCase
) : ViewModel(), IHomeViewModel {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    override val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var cachedGlobalData: com.example.cryptoandroidapp.domain.model.GlobalMarketData? = null

    init {
        fetchGlobalData()
        fetchHomeData()
    }

    private fun fetchGlobalData() {
        viewModelScope.launch {
            when (val resource = cryptoRepository.getGlobalMarketData()) {
                is Resource.Success -> {
                    cachedGlobalData = resource.data
                }
                else -> {}
            }
        }
    }

    private fun fetchHomeData() {
        val currentUser = authRepository.getCurrentUser()
        val userId = currentUser?.uid ?: ""

        if (userId.isEmpty()) {
            _uiState.value = HomeUiState.Error("Kullanıcı oturumu bulunamadı")
            return
        }

        viewModelScope.launch {
            val coinsFlow = cryptoRepository.getCryptoList()
            val portfolioFlow = portfolioRepository.getUserPortfolio(userId)
            val favoritesFlow = favoritesRepository.getUserFavorites(userId)

            combine(coinsFlow, portfolioFlow, favoritesFlow) { coinsRes, portfolioRes, favoritesRes ->
                Triple(coinsRes, portfolioRes, favoritesRes)
            }.collect { (coinsRes, portfolioRes, favoritesRes) ->

                if (coinsRes is Resource.Loading || portfolioRes is Resource.Loading || favoritesRes is Resource.Loading) {
                    if (_uiState.value !is HomeUiState.Success) {
                        _uiState.value = HomeUiState.Loading
                    }
                }

                when (coinsRes) {
                    is Resource.Error -> {
                        _uiState.value = HomeUiState.Error(coinsRes.message ?: "Veri çekme hatası")
                    }
                    is Resource.Success -> {
                        val coinsList = coinsRes.data ?: emptyList()
                        val portfolioList = (portfolioRes as? Resource.Success)?.data ?: emptyList()
                        val favoriteIds = (favoritesRes as? Resource.Success)?.data ?: emptyList()

                        val favoriteCoins = coinsList.filter { it.id in favoriteIds }

                        calculatePortfolio(coinsList, portfolioList, favoriteCoins, favoriteIds)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun calculatePortfolio(
        coins: List<CryptoModel>,
        portfolioList: List<UserPortfolioAsset>,
        favoriteCoins: List<CryptoModel>,
        favoriteIds: List<String>
    ) {
        val calcResult = calculatePortfolioUseCase(portfolioList, coins)

        val assetsList = portfolioList.mapNotNull { userPortfolioAsset ->
            val symbol = userPortfolioAsset.symbol.uppercase(Locale.ROOT)
            val coin = coins.find { it.id == userPortfolioAsset.coinId || it.symbol.uppercase(Locale.ROOT) == symbol }
                ?: return@mapNotNull null
            val currentPriceUsd = coin.currentPrice
            val priceChangePercent = coin.priceChangePercentage24h
            val amount = userPortfolioAsset.amount
            val assetValueUsd = currentPriceUsd * amount

            val colorHex = when(symbol) {
                "BTC" -> 0xFFFF981F
                "ETH" -> 0xFF8496C7
                "SOL" -> 0xFF906CFF
                else -> 0xFF4F46E5
            }

            UserAsset(
                name = coin.name,
                symbol = symbol,
                amountText = amount.formatCryptoAmount(symbol),
                valueText = String.format(Locale.US, "$%,.2f", assetValueUsd),
                changeText = String.format(
                    Locale.US,
                    if (priceChangePercent >= 0) "▲ %,.2f%%" else "▼ %,.2f%%",
                    Math.abs(priceChangePercent)
                ),
                isPositive = priceChangePercent >= 0,
                colorHex = colorHex,
                imageUrl = coin.imageUrl
            )
        }

        val totalText = String.format(Locale.US, "$%,.2f", calcResult.totalValueUsd)
        val changeText = String.format(
            Locale.US,
            if (calcResult.isPositive) "+$%,.2f (▲ %.2f%%) 24s" else "-$%,.2f (▼ %.2f%%) 24s",
            Math.abs(calcResult.totalChangeUsd),
            Math.abs(calcResult.totalChangePercent)
        )

        _uiState.value = HomeUiState.Success(
            portfolioTotalText = totalText,
            portfolioChangeText = changeText,
            isPortfolioPositive = calcResult.isPositive,
            userAssets = assetsList,
            coins = coins,
            favoriteCoins = favoriteCoins,
            favoriteCoinIds = favoriteIds
        )
    }

    override fun toggleFavorite(coinId: String) {
        val currentUser = authRepository.getCurrentUser() ?: return
        val currentState = _uiState.value as? HomeUiState.Success ?: return
        val isFav = coinId in currentState.favoriteCoinIds
        viewModelScope.launch {
            if (isFav) {
                favoritesRepository.removeFavorite(currentUser.uid, coinId)
            } else {
                favoritesRepository.addFavorite(currentUser.uid, coinId)
            }
        }
    }
}