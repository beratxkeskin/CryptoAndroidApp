package com.example.cryptoandroidapp.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.common.NetworkObserver
import com.example.cryptoandroidapp.presentation.favorites.FavoritesScreen
import com.example.cryptoandroidapp.presentation.home.components.AppBackground
import com.example.cryptoandroidapp.presentation.home.components.AppBackgroundMood
import com.example.cryptoandroidapp.presentation.home.components.AssetsSection
import com.example.cryptoandroidapp.presentation.home.components.BottomNavigation
import com.example.cryptoandroidapp.presentation.home.components.Header
import com.example.cryptoandroidapp.presentation.home.components.MarketOverview
import com.example.cryptoandroidapp.presentation.home.components.OfflineBanner
import com.example.cryptoandroidapp.presentation.home.components.PortfolioCard
import com.example.cryptoandroidapp.presentation.home.components.StarField
import com.example.cryptoandroidapp.presentation.home.components.WatchListSection
import com.example.cryptoandroidapp.presentation.markets.MarketsScreen
import com.example.cryptoandroidapp.presentation.portfolio.PortfolioScreen
import com.example.cryptoandroidapp.presentation.profile.ProfileScreen

@Composable
fun HomeScreen(
    userName: String,
    onLogout: () -> Unit,
    onCoinClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    selectedTab: String = "home",
    onTabSelected: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val networkObserver = remember { NetworkObserver(context) }
    val isNetworkConnected by networkObserver.observe().collectAsState(initial = true)

    HomeScreenContent(
        userName = userName,
        uiState = uiState,
        isNetworkConnected = isNetworkConnected,
        modifier = modifier,
        selectedTab = selectedTab,
        onTabSelected = onTabSelected,
        onLogout = onLogout,
        onCoinClick = onCoinClick,
        onToggleFavorite = viewModel::toggleFavorite
    )
}

@Composable
fun HomeScreenContent(
    userName: String,
    uiState: HomeUiState,
    isNetworkConnected: Boolean = true,
    modifier: Modifier = Modifier,
    selectedTab: String = "home",
    onTabSelected: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    onCoinClick: (String) -> Unit = {},
    onToggleFavorite: (String) -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize().background(Background)) {
        AppBackground(
            mood = when (selectedTab) {
                "markets" -> AppBackgroundMood.Markets
                "portfolio" -> AppBackgroundMood.Portfolio
                "favorites" -> AppBackgroundMood.Favorites
                "profile" -> AppBackgroundMood.Profile
                else -> AppBackgroundMood.Home
            }
        )
        StarField()

        // Seçilen sekmeye göre orta kısmın içeriği pürüzsüz cross-fade animasyonuyla değişiyor
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                fadeIn(animationSpec = tween(280)) togetherWith fadeOut(animationSpec = tween(280))
            },
            label = "tab_transition"
        ) { targetTab ->
            when (targetTab) {
                "home" -> {
                    when (val state = uiState) {
                        is HomeUiState.Loading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                androidx.compose.material3.CircularProgressIndicator(color = Purple)
                            }
                        }
                        is HomeUiState.Error -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "Hata: ${state.message}", color = Color.White, fontSize = 15.sp)
                            }
                        }
                        is HomeUiState.Success -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Uzay Görseli (Hero Image)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(280.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.home_space_hero),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Background.copy(alpha = 0.5f),
                                                        Background
                                                    )
                                                )
                                            )
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 12.dp, bottom = 100.dp)
                                        .navigationBarsPadding()
                                ) {
                                    Header(userName)
                                    Spacer(Modifier.height(12.dp))
                                    PortfolioCard(
                                        totalText = state.portfolioTotalText,
                                        changeText = state.portfolioChangeText,
                                        isPositive = state.isPortfolioPositive,
                                        onNavigateToPortfolio = { onTabSelected("portfolio") }
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    AssetsSection(assets = state.userAssets, onAddAsset = { onTabSelected("portfolio") })
                                    Spacer(Modifier.height(12.dp))
                                    WatchListSection(
                                        favoriteCoins = state.favoriteCoins,
                                        favoriteCoinIds = state.favoriteCoinIds,
                                        onCoinClick = onCoinClick,
                                        onToggleFavorite = onToggleFavorite,
                                        onViewAllClick = { onTabSelected("favorites") }
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    MarketOverview(coins = state.coins, onCoinClick = onCoinClick)
                                }
                            }
                        }
                    }
                }
                "markets" -> {
                    MarketsScreen(
                        onCoinClick = onCoinClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                "portfolio" -> {
                    PortfolioScreen(
                        userName = userName,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                "favorites" -> {
                    FavoritesScreen(
                        userName = userName,
                        onCoinClick = onCoinClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                "profile" -> {
                    ProfileScreen(
                        onLogout = onLogout,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Yüzen Alt Gezinme Barı (Floating Bottom Navigation)
        BottomNavigation(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // İnternet koptuğunda süzülen neon kapsül çevrimdışı uyarısı
        OfflineBanner(
            isConnected = isNetworkConnected,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
