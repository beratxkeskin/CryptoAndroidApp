package com.example.cryptoandroidapp.presentation.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.presentation.favorites.FavoritesScreen
import com.example.cryptoandroidapp.presentation.home.components.AssetsSection
import com.example.cryptoandroidapp.presentation.home.components.AppBackground
import com.example.cryptoandroidapp.presentation.home.components.AppBackgroundMood
import com.example.cryptoandroidapp.presentation.home.components.BottomNavigation
import com.example.cryptoandroidapp.presentation.home.components.Header
import com.example.cryptoandroidapp.presentation.home.components.MarketOverview
import com.example.cryptoandroidapp.presentation.home.components.PortfolioCard
import com.example.cryptoandroidapp.presentation.home.components.StarField
import com.example.cryptoandroidapp.presentation.home.components.WatchListSection
import com.example.cryptoandroidapp.presentation.markets.MarketsScreen
import com.example.cryptoandroidapp.presentation.portfolio.PortfolioScreen

val Background = Color(0xFF020916)
val PanelColor = Color(0xFF091629)
val PanelBorder = Color(0xFF33415D)
val Muted = Color(0xFFA4AEC5)
val Purple = Color(0xFF7C4DFF)
val Green = Color(0xFF12D58A)
val Red = Color(0xFFFF4D6D)

/** Tasarım verileriyle çalışan ana ekran. Gerçek portföy verileri HomeViewModel'den beslenir. */
@Composable
fun HomeScreen(
    userName: String,
    viewModel: IHomeViewModel,
    modifier: Modifier = Modifier,
    selectedTab: String = "home",
    onTabSelected: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    onCoinClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        userName = userName,
        uiState = uiState,
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

        // Seçilen sekmeye göre orta kısmın içeriği değişiyor
        when (selectedTab) {
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
                        Box(Modifier.fillMaxSize()) {
                            // Arka Plan Uzay Görseli
                            Image(
                                painter = painterResource(R.drawable.home_space_hero),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(230.dp)
                                    .align(Alignment.TopCenter)
                            )
                            // Yumuşatıcı geçiş gradyanı
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(230.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            0.0f to Color.Transparent,
                                            0.6f to Background.copy(alpha = 0.5f),
                                            1.0f to Background
                                        )
                                    )
                            )

                            // Kaydırılabilir Ana Sayfa İçeriği
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
                                    isPositive = state.isPortfolioPositive
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
                    modifier = Modifier.fillMaxSize().padding(bottom = 80.dp).navigationBarsPadding()
                )
            }
            "portfolio" -> {
                PortfolioScreen(
                    userName = userName,
                    modifier = Modifier.fillMaxSize().padding(bottom = 80.dp).navigationBarsPadding()
                )
            }
            "favorites" -> {
                FavoritesScreen(
                    userName = userName,
                    onCoinClick = onCoinClick,
                    modifier = Modifier.fillMaxSize().padding(bottom = 80.dp).navigationBarsPadding()
                )
            }
            "profile" -> {
                com.example.cryptoandroidapp.presentation.profile.ProfileScreen(
                    onLogout = onLogout,
                    modifier = Modifier.fillMaxSize().padding(bottom = 80.dp).navigationBarsPadding()
                )
            }
        }

        // Sabit Alt Gezinme Çubuğu (Bottom Navigation Bar)
        BottomNavigation(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
