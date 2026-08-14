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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.common.toCompactUsd
import com.example.cryptoandroidapp.common.toFormattedPrice
import com.example.cryptoandroidapp.presentation.home.Green
import com.example.cryptoandroidapp.presentation.home.Muted
import com.example.cryptoandroidapp.presentation.home.PanelBorder
import com.example.cryptoandroidapp.presentation.home.PanelColor
import com.example.cryptoandroidapp.presentation.home.Purple
import com.example.cryptoandroidapp.presentation.home.Red
import com.example.cryptoandroidapp.presentation.home.components.HeaderIconButton
import kotlinx.coroutines.delay
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
                Text(text = stringResource(R.string.error_prefix, state.message), color = Color.White, fontSize = 15.sp)
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
                    .padding(top = 12.dp)
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
                    HeaderIconButton(
                        icon = Icons.Default.Search,
                        onClick = { viewModel.toggleSearchVisibility() }
                    )
                    Spacer(Modifier.width(8.dp))
                    HeaderIconButton(
                        icon = Icons.Default.NotificationsNone
                    )
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
                        value = state.globalMarketData.totalMarketCapUsd.toCompactUsd(),
                        changeText = mcapChangeText,
                        isPositive = isMcapPositive,
                        modifier = Modifier.weight(1f)
                    )

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
                                text = state.globalMarketData.totalVolumeUsd.toCompactUsd(),
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

                // 4. TEMİZ & FERAH TABLO BAŞLIĞI (3 Bölge Tasarımı)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.column_name), color = Muted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.35f))
                    Text(stringResource(R.string.column_chart), color = Muted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.95f), textAlign = TextAlign.Center)
                    Text(stringResource(R.string.column_price), color = Muted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.35f), textAlign = TextAlign.End)
                }

                // 5. KRİPTO LİSTESİ (Ferah & Yüksek Standartlı LazyColumn)
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(bottom = 140.dp)
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
                                textAlign = TextAlign.Center
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
    chartContent: (@Composable () -> Unit)? = null
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
                chartContent?.invoke()
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
    val priceText = coin.currentPrice.toFormattedPrice()
    val changeText = String.format(
        java.util.Locale.US,
        if (coin.priceChangePercentage24h >= 0) "▲ %.2f%%" else "▼ %.2f%%",
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

    val sparklinePoints = remember(coin.priceHistory7d, isPositive) {
        if (coin.priceHistory7d.size >= 2) {
            val min = coin.priceHistory7d.minOrNull() ?: 0.0
            val max = coin.priceHistory7d.maxOrNull() ?: 1.0
            val range = (max - min).takeIf { it > 0 } ?: 1.0
            coin.priceHistory7d.map { ((it - min) / range).toFloat() }
        } else {
            if (isPositive) listOf(0.2f, 0.38f, 0.32f, 0.55f, 0.48f, 0.7f, 0.65f, 0.88f)
            else listOf(0.88f, 0.68f, 0.72f, 0.48f, 0.55f, 0.32f, 0.38f, 0.18f)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(animatedColor)
            .clickable { onCoinClick(coin.id) }
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // BÖLGE 1: Logo + İsim + Sembol & Sıralama (Sol Taraf)
        Row(
            modifier = Modifier.weight(1.35f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = coin.imageUrl,
                contentDescription = coin.name,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(PanelBorder.copy(alpha = 0.3f))
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = coin.name,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "#$index",
                        color = Purple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = coin.symbol.uppercase(),
                        color = Muted,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // BÖLGE 2: Geniş ve Ferah Mini Sparkline Grafik (Orta Bölge)
        Box(
            modifier = Modifier
                .weight(0.95f)
                .height(28.dp)
                .padding(horizontal = 6.dp)
        ) {
            MiniSparkLine(
                color = if (isPositive) Green else Red,
                points = sparklinePoints
            )
        }

        // BÖLGE 3: Fiyat + 24s Değişim Rozeti & Favori Yıldızı (Sağ Bölge)
        Row(
            modifier = Modifier.weight(1.35f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = priceText,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isPositive) Green.copy(alpha = 0.15f) else Red.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = changeText,
                        color = if (isPositive) Green else Red,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        maxLines = 1
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (isFavorited) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = if (isFavorited) Purple else Muted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun MiniSparkLine(color: Color, points: List<Float>) {
    Canvas(Modifier.fillMaxSize()) {
        if (points.size < 2) return@Canvas
        val path = Path()
        points.forEachIndexed { index, point ->
            val x = size.width * index / (points.size - 1)
            val y = size.height * (1f - point)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = color.copy(alpha = 0.25f),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        )
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
