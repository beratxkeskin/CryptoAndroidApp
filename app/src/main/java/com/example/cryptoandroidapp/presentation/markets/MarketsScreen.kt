package com.example.cryptoandroidapp.presentation.markets

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.ui.theme.CryptoAndroidAppTheme
import com.example.cryptoandroidapp.presentation.home.Background
import com.example.cryptoandroidapp.presentation.home.Green
import com.example.cryptoandroidapp.presentation.home.Muted
import com.example.cryptoandroidapp.presentation.home.PanelBorder
import com.example.cryptoandroidapp.presentation.home.PanelColor
import com.example.cryptoandroidapp.presentation.home.Purple
import com.example.cryptoandroidapp.presentation.home.Red
import kotlinx.coroutines.delay
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MarketsScreen(
    onCoinClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: IMarketsViewModel = hiltViewModel<MarketsViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchFocusRequester = remember { FocusRequester() }

    when (val state = uiState) {
        is MarketsUiState.Loading -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Purple)
            }
        }
        is MarketsUiState.Error -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Hata: ${state.message}", color = Color.White, fontSize = 15.sp)
            }
        }
        is MarketsUiState.Success -> {
            LaunchedEffect(state.isSearchVisible) {
                if (state.isSearchVisible) searchFocusRequester.requestFocus()
            }

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = 92.dp)
            ) {
                // 1. ÜST BAŞLIK ALANI (Piyasalar + Arama ve Bildirim İkonları)
                Row(
                    modifier = Modifier.statusBarsPadding().fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.markets_title), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.markets_subtitle), color = Muted, fontSize = 12.sp)
                    }
                    // Arama ve Bildirim Butonları
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PanelColor)
                            .border(1.dp, PanelBorder, CircleShape)
                            .clickable {
                                viewModel.toggleSearchVisibility()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Search, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.size(36.dp).clip(CircleShape).background(PanelColor).border(1.dp, PanelBorder, CircleShape).clickable {}, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.NotificationsNone, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                if (state.isSearchVisible) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        modifier = Modifier.fillMaxWidth().focusRequester(searchFocusRequester),
                        placeholder = { Text(stringResource(R.string.search_placeholder)) },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.search_clear))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = PanelBorder,
                            focusedLeadingIconColor = Purple,
                            unfocusedLeadingIconColor = Muted,
                            focusedTrailingIconColor = Muted,
                            unfocusedTrailingIconColor = Muted,
                            focusedPlaceholderColor = Muted,
                            unfocusedPlaceholderColor = Muted,
                            cursorColor = Purple
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // 2. ÜST İSTATİSTİK KARTLARI
                val isMcapPositive = state.globalMarketData.marketCapChangePercentage24hUsd >= 0
                val mcapChangeText = String.format(
                    java.util.Locale.US,
                    if (isMcapPositive) "▲ %,.2f%% (24s)" else "▼ %,.2f%% (24s)",
                    Math.abs(state.globalMarketData.marketCapChangePercentage24hUsd)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Kart 1: Toplam Piyasa Değeri
                    StatCard(
                        title = stringResource(R.string.market_cap_stat),
                        value = formatLargeNumber(state.globalMarketData.totalMarketCapUsd),
                        changeText = mcapChangeText,
                        isPositive = isMcapPositive,
                        modifier = Modifier.weight(1f)
                    ) {
                        MiniSparkLine(color = Green, points = listOf(0.2f, 0.35f, 0.28f, 0.45f, 0.4f, 0.58f, 0.5f, 0.65f))
                    }

                    // Kart 2: 24s İşlem Hacmi
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PanelColor),
                        modifier = Modifier.weight(1f).height(106.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PanelBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.volume_24h_stat),
                                color = Muted,
                                fontSize = 9.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = formatLargeNumber(state.globalMarketData.totalVolumeUsd),
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                            ) {
                                MiniBarChart(color = Purple)
                            }
                        }
                    }

                    // Kart 3: BTC Dominansı
                    StatCard(
                        title = stringResource(R.string.btc_dominance_stat),
                        value = String.format(java.util.Locale.US, "%,.2f%%", state.globalMarketData.btcDominance),
                        changeText = stringResource(R.string.market_share),
                        isPositive = true,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { (state.globalMarketData.btcDominance / 100f).toFloat() },
                                modifier = Modifier.fillMaxSize(),
                                color = Purple,
                                strokeWidth = 4.dp,
                                trackColor = PanelBorder.copy(alpha = 0.3f),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 3. FİLTRE VE SIRALAMA ALANI
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MarketFilter.values().forEach { filter ->
                            val isSelected = state.selectedFilter == filter
                            val labelResId = when (filter) {
                                MarketFilter.ALL -> R.string.filter_all
                                MarketFilter.WATCHLIST -> R.string.filter_favorites
                                MarketFilter.TRENDING -> R.string.filter_trending
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) Purple else PanelColor)
                                    .border(1.dp, if (isSelected) Purple else PanelBorder, RoundedCornerShape(20.dp))
                                    .clickable { viewModel.updateFilter(filter) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(stringResource(labelResId), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Text(stringResource(R.string.sort_market_cap), color = Muted, fontSize = 10.sp)
                }

                Spacer(Modifier.height(12.dp))

                // 4. TABLO BAŞLIĞI
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("#", color = Muted, fontSize = 10.sp, modifier = Modifier.width(28.dp))
                    Text(stringResource(R.string.column_name), color = Muted, fontSize = 10.sp, modifier = Modifier.weight(1.3f))
                    Text(stringResource(R.string.column_chart), color = Muted, fontSize = 10.sp, modifier = Modifier.weight(0.9f))
                    Text(stringResource(R.string.column_price), color = Muted, fontSize = 10.sp, modifier = Modifier.weight(1.1f))
                    Text(stringResource(R.string.column_change), color = Muted, fontSize = 10.sp, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(28.dp))
                }

                // 5. KRİPTO LİSTESİ (LazyColumn)
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    itemsIndexed(state.filteredCoins, key = { _, coin -> coin.symbol }) { index, coin ->
                        val isFavorited = state.favoriteCoinIds.contains(coin.id)
                        MarketRow(
                            index = index + 1,
                            coin = coin,
                            isFavorited = isFavorited,
                            onToggleFavorite = { viewModel.toggleFavorite(coin.id) },
                            onCoinClick = onCoinClick
                        )
                    }
                    if (state.filteredCoins.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.search_no_results, state.searchQuery),
                                color = Muted,
                                fontSize = 13.sp,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    changeText: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier,
    chartContent: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        modifier = modifier.height(106.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, PanelBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = Muted,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(value, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text(changeText, color = if (isPositive) Green else Red, fontSize = 10.sp)
                }
                chartContent()
            }
        }
    }
}

@Composable
private fun MarketRow(
    index: Int,
    coin: com.example.cryptoandroidapp.domain.model.CryptoModel,
    isFavorited: Boolean,
    onToggleFavorite: () -> Unit,
    onCoinClick: (String) -> Unit
) {
    val priceText = String.format(java.util.Locale.US, "$%,.2f", coin.currentPrice)
    val changeText = String.format(
        java.util.Locale.US,
        if (coin.priceChangePercentage24h >= 0) "▲ %,.2f%%" else "▼ %,.2f%%",
        Math.abs(coin.priceChangePercentage24h)
    )
    val isPositive = coin.priceChangePercentage24h >= 0

    var prevPrice by remember { mutableStateOf(coin.currentPrice) }
    var flashColor by remember { mutableStateOf(Color.Transparent) }
    val animatedColor by animateColorAsState(
        targetValue = flashColor,
        animationSpec = tween(durationMillis = 350),
        label = "market_row_flash"
    )

    LaunchedEffect(coin.currentPrice) {
        if (coin.currentPrice > prevPrice) {
            flashColor = Green.copy(alpha = 0.22f)
            delay(350)
            flashColor = Color.Transparent
        } else if (coin.currentPrice < prevPrice) {
            flashColor = Red.copy(alpha = 0.22f)
            delay(350)
            flashColor = Color.Transparent
        }
        prevPrice = coin.currentPrice
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(animatedColor)
            .clickable { onCoinClick(coin.id) }
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = index.toString(),
            color = Muted,
            fontSize = 11.sp,
            modifier = Modifier.width(28.dp)
        )

        Row(
            modifier = Modifier.weight(1.3f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = coin.imageUrl,
                contentDescription = coin.name,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(PanelBorder.copy(alpha = 0.3f))
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = coin.name,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(coin.symbol.uppercase(), color = Muted, fontSize = 9.sp)
            }
        }

        Box(
            modifier = Modifier
                .weight(0.9f)
                .height(24.dp)
                .padding(horizontal = 4.dp)
        ) {
            MiniSparkLine(
                color = if (isPositive) Green else Red,
                points = if (isPositive) listOf(0.3f, 0.4f, 0.35f, 0.5f, 0.45f, 0.6f) else listOf(0.6f, 0.5f, 0.55f, 0.4f, 0.45f, 0.3f)
            )
        }

        Text(
            text = priceText,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1.1f),
            maxLines = 1
        )

        Text(
            text = changeText,
            color = if (isPositive) Green else Red,
            fontSize = 11.sp,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )

        Icon(
            imageVector = if (isFavorited) Icons.Default.Star else Icons.Default.StarBorder,
            contentDescription = null,
            tint = if (isFavorited) Purple else Muted,
            modifier = Modifier
                .size(20.dp)
                .clickable { onToggleFavorite() }
        )
    }
}

@Composable
private fun MiniSparkLine(color: Color, points: List<Float>) {
    Canvas(Modifier.fillMaxSize()) {
        val path = Path()
        points.forEachIndexed { index, point ->
            val x = size.width * index / (points.size - 1)
            val y = size.height * (1f - point)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        //drawPath(path, color, style = Stroke(1.5f.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
private fun MiniBarChart(color: Color) {
    Canvas(Modifier.fillMaxSize()) {
        val bars = 8
        val spacing = 2.dp.toPx()
        val totalSpacing = spacing * (bars - 1)
        val barWidth = (size.width - totalSpacing) / bars
        val heights = listOf(0.3f, 0.5f, 0.4f, 0.7f, 0.5f, 0.8f, 0.6f, 0.9f)

        heights.forEachIndexed { index, h ->
            val x = index * (barWidth + spacing)
            val y = size.height * (1f - h)
            drawRect(
                color = color,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, size.height * h)
            )
        }
    }
}

private fun formatLargeNumber(number: Double): String {
    return when {
        number >= 1.0e12 -> String.format(java.util.Locale.US, "$%,.2fT", number / 1.0e12)
        number >= 1.0e9 -> String.format(java.util.Locale.US, "$%,.2fB", number / 1.0e9)
        number >= 1.0e6 -> String.format(java.util.Locale.US, "$%,.2fM", number / 1.0e6)
        else -> String.format(java.util.Locale.US, "$%,.2f", number)
    }
}

private class MockMarketsViewModel(
    val state: MarketsUiState
) : IMarketsViewModel {
    override val uiState: StateFlow<MarketsUiState> = MutableStateFlow(state)
    override fun toggleFavorite(coinId: String) {}
    override fun updateSearchQuery(query: String) {}
    override fun updateFilter(filter: MarketFilter) {}
    override fun toggleSearchVisibility() {}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MarketsScreenPreview() {
    val sampleCoins = listOf(
        com.example.cryptoandroidapp.domain.model.CryptoModel("bitcoin", "btc", "Bitcoin", "", 67823.54, 1.34e12, 1, 2.35),
        com.example.cryptoandroidapp.domain.model.CryptoModel("ethereum", "eth", "Ethereum", "", 3523.18, 4.23e11, 2, 3.02),
        com.example.cryptoandroidapp.domain.model.CryptoModel("solana", "sol", "Solana", "", 174.35, 8.12e10, 3, 6.21),
        com.example.cryptoandroidapp.domain.model.CryptoModel("binancecoin", "bnb", "BNB", "", 595.42, 8.76e10, 4, -0.42),
        com.example.cryptoandroidapp.domain.model.CryptoModel("ripple", "xrp", "XRP", "", 0.61, 3.36e10, 5, -1.15)
    )
    val mockState = MarketsUiState.Success(
        coins = sampleCoins,
        filteredCoins = sampleCoins,
        globalMarketData = com.example.cryptoandroidapp.domain.model.GlobalMarketData(
            totalMarketCapUsd = 2410000000000.0,
            totalVolumeUsd = 98470000000.0,
            btcDominance = 52.48,
            marketCapChangePercentage24hUsd = 2.65
        ),
        favoriteCoinIds = listOf("bitcoin", "ethereum"),
        searchQuery = "",
        selectedFilter = MarketFilter.ALL,
        isSearchVisible = false
    )
    CryptoAndroidAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            MarketsScreen(
                onCoinClick = {},
                viewModel = MockMarketsViewModel(mockState)
            )
        }
    }
}
