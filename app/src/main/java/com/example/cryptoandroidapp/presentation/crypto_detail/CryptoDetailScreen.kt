package com.example.cryptoandroidapp.presentation.crypto_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cryptoandroidapp.domain.model.CandleModel
import com.example.cryptoandroidapp.domain.model.CryptoDetailModel
import com.example.cryptoandroidapp.presentation.crypto_detail.components.AboutCoinSection
import com.example.cryptoandroidapp.presentation.crypto_detail.components.CoinOverview
import com.example.cryptoandroidapp.presentation.crypto_detail.components.DetailTopBar
import com.example.cryptoandroidapp.presentation.crypto_detail.components.MarketStatsSection
import com.example.cryptoandroidapp.presentation.crypto_detail.components.PriceChartSection

@Composable
fun CryptoDetailScreen(
    coinId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CryptoDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDays by viewModel.selectedDays.collectAsState()

    androidx.compose.runtime.LaunchedEffect(coinId) { viewModel.loadCoinDetail(coinId) }

    Box(
        modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DetailPurple.copy(alpha = 0.10f), DetailBackground, DetailBackground)
                )
            )
            .statusBarsPadding()
    ) {
        when (val state = uiState) {
            CryptoDetailUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center), color = DetailPurple
            )
            is CryptoDetailUiState.Error -> Text(
                text = state.message,
                color = DetailText,
                modifier = Modifier.align(Alignment.Center).padding(24.dp)
            )
            is CryptoDetailUiState.Success -> DetailContent(
                detail = state.coinDetail,
                candles = state.candles,
                selectedDays = selectedDays,
                onBackClick = onBackClick,
                onTimeRangeSelected = viewModel::setTimeRange
            )
        }
    }
}

@Composable
private fun DetailContent(
    detail: CryptoDetailModel,
    candles: List<CandleModel>,
    selectedDays: String,
    onBackClick: () -> Unit,
    onTimeRangeSelected: (String) -> Unit
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
    ) {
        DetailTopBar(onBackClick)
        CoinOverview(detail)
        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = DetailBorder)
        PriceChartSection(candles, selectedDays, onTimeRangeSelected)
        HorizontalDivider(color = DetailBorder)
        MarketStatsSection(detail)
        HorizontalDivider(color = DetailBorder)
        AboutCoinSection(detail)
        Spacer(Modifier.height(18.dp))
    }
}
