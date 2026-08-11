package com.example.cryptoandroidapp.presentation.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.example.cryptoandroidapp.R
import coil.compose.AsyncImage
import com.example.cryptoandroidapp.domain.model.CryptoModel
import com.example.cryptoandroidapp.presentation.home.Green
import com.example.cryptoandroidapp.presentation.home.Muted
import com.example.cryptoandroidapp.presentation.home.PanelBorder
import com.example.cryptoandroidapp.presentation.home.PanelColor
import com.example.cryptoandroidapp.presentation.home.Purple
import com.example.cryptoandroidapp.presentation.home.Red
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import kotlin.math.abs

@Composable
fun FavoritesScreen(
    userName: String,
    onCoinClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: IFavoritesViewModel = hiltViewModel<FavoritesViewModel>()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is FavoritesUiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Purple)
        }
        is FavoritesUiState.Error -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Hata: ${state.message}", color = Color.White, fontSize = 15.sp)
        }
        is FavoritesUiState.Success -> FavoritesContent(
            userName = userName,
            coins = state.favoriteCoins,
            onCoinClick = onCoinClick,
            onToggleFavorite = viewModel::toggleFavorite,
            modifier = modifier
        )
    }
}

@Composable
private fun FavoritesContent(
    userName: String,
    coins: List<CryptoModel>,
    onCoinClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 92.dp)
    ) {
        FavoritesHeader(userName)
        Spacer(Modifier.height(20.dp))
        FavoriteMarketSummary(coins)
        Spacer(Modifier.height(18.dp))
        Text("Takip Ettiklerin", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Favoriye aldığın kripto paraların anlık durumu", color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(10.dp))

        if (coins.isEmpty()) {
            EmptyFavoritesState()
        } else {
            FavoriteListHeader()
            coins.forEach { coin ->
                FavoriteCoinRow(
                    coin = coin,
                    prices = coin.priceHistory7d.takeLast(24),
                    onCoinClick = { onCoinClick(coin.id) },
                    onToggleFavorite = { onToggleFavorite(coin.id) }
                )
            }
        }
    }
}

@Composable
private fun FavoritesHeader(userName: String) {
    Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.favorites_title), color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.favorites_empty_desc), color = Muted, fontSize = 13.sp)
        }
        Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(PanelColor).border(1.dp, PanelBorder, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.NotificationsNone, contentDescription = stringResource(R.string.detail_notifications_desc), tint = Color.White, modifier = Modifier.size(19.dp))
        }
        Spacer(Modifier.width(10.dp))
        Box(modifier = Modifier.size(38.dp).border(1.dp, Purple, CircleShape).background(Color(0xFF1C2840), CircleShape), contentAlignment = Alignment.Center) {
            Text(userName.firstOrNull()?.uppercase() ?: "K", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FavoriteMarketSummary(coins: List<CryptoModel>) {
    val averageChange = coins.map { it.priceChangePercentage24h }.average().takeUnless { it.isNaN() } ?: 0.0
    val bestCoin = coins.maxByOrNull { it.priceChangePercentage24h }
    val worstCoin = coins.minByOrNull { it.priceChangePercentage24h }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SummaryCard(stringResource(R.string.favorites_summary_avg), Modifier.weight(1.12f)) {
            val isPositive = averageChange >= 0
            Text(String.format(Locale.US, "%s%.2f%%", if (isPositive) "+" else "", averageChange), color = if (isPositive) Green else Red, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Favorilerinin performansı", color = Muted, fontSize = 10.sp, maxLines = 1)
        }
        SummaryCard("Takipte", Modifier.weight(.82f)) {
            Text(coins.size.toString(), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("kripto para", color = Muted, fontSize = 10.sp)
        }
        SummaryCard("Günün Hareketi", Modifier.weight(1.15f)) {
            if (bestCoin == null || worstCoin == null) {
                Text("-", color = Muted, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Veri bekleniyor", color = Muted, fontSize = 10.sp)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, null, tint = Green, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(bestCoin.symbol.uppercase(), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(String.format(Locale.US, "+%.2f%%", bestCoin.priceChangePercentage24h), color = Green, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                if (worstCoin != bestCoin) Text("En düşük: ${worstCoin.symbol.uppercase()}", color = Muted, fontSize = 9.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun SummaryCard(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = modifier.height(112.dp).border(1.dp, PanelBorder, RoundedCornerShape(16.dp)), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PanelColor)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, color = Muted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            content()
        }
    }
}

@Composable
private fun FavoriteListHeader() {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)) {
        Text(stringResource(R.string.column_name), color = Muted, fontSize = 10.sp, modifier = Modifier.weight(1.15f))
        Text(stringResource(R.string.column_price), color = Muted, fontSize = 10.sp, modifier = Modifier.weight(.9f), textAlign = TextAlign.Center)
        Text(stringResource(R.string.column_change), color = Muted, fontSize = 10.sp, modifier = Modifier.width(70.dp), textAlign = TextAlign.Center)
        Text(stringResource(R.string.column_chart), color = Muted, fontSize = 10.sp, modifier = Modifier.width(58.dp), textAlign = TextAlign.Center)
        Spacer(Modifier.width(28.dp))
    }
}

@Composable
private fun FavoriteCoinRow(
    coin: CryptoModel,
    prices: List<Double>,
    onCoinClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val isPositive = coin.priceChangePercentage24h >= 0

    Column(modifier = Modifier.fillMaxWidth().clickable(onClick = onCoinClick).padding(horizontal = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1.15f), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(model = coin.imageUrl, contentDescription = coin.name, modifier = Modifier.size(30.dp).clip(CircleShape).background(PanelBorder.copy(alpha = .3f)))
                Spacer(Modifier.width(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Text(coin.name, color = Color.White, fontSize = 12.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(coin.symbol.uppercase(), color = Muted, fontSize = 9.sp, lineHeight = 11.sp)
                        Spacer(Modifier.width(6.dp))
                        coin.marketCapRank?.let { Text("#$it", color = Muted, fontSize = 9.sp, lineHeight = 11.sp) }
                    }
                }
            }

            Column(modifier = Modifier.weight(.9f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(String.format(Locale.US, "$%,.2f", coin.currentPrice), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            }

            Row(modifier = Modifier.width(70.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Icon(if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward, null, tint = if (isPositive) Green else Red, modifier = Modifier.size(11.dp))
                Text(String.format(Locale.US, "%.2f%%", abs(coin.priceChangePercentage24h)), color = if (isPositive) Green else Red, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }

            Box(modifier = Modifier.width(58.dp).height(24.dp).padding(horizontal = 5.dp)) {
                FavoriteSparkline(prices, if (isPositive) Green else Red, Modifier.fillMaxSize())
            }

            IconButton(onClick = onToggleFavorite, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Star, contentDescription = stringResource(R.string.remove_favorite), tint = Purple, modifier = Modifier.size(18.dp))
            }
        }

        Box(Modifier.fillMaxWidth().height(1.dp).background(PanelBorder.copy(alpha = .38f)))
    }
}

@Composable
private fun FavoriteSparkline(prices: List<Double>, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        if (prices.size < 2) return@Canvas
        val minPrice = prices.minOrNull() ?: return@Canvas
        val maxPrice = prices.maxOrNull() ?: return@Canvas
        val range = (maxPrice - minPrice).takeIf { it > 0.0 } ?: 1.0
        val path = Path()

        prices.forEachIndexed { index, price ->
            val x = size.width * index / (prices.lastIndex.coerceAtLeast(1))
            val normalized = ((price - minPrice) / range).toFloat()
            val y = size.height * (1f - normalized)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color.copy(alpha = .22f), style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round))
        drawPath(path, color, style = Stroke(width = 1.25.dp.toPx(), cap = StrokeCap.Round))
        drawCircle(color, radius = 1.8.dp.toPx(), center = Offset(size.width, size.height * (1f - ((prices.last() - minPrice) / range).toFloat())))
    }
}

@Composable
private fun EmptyFavoritesState() {
    Card(modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder, RoundedCornerShape(16.dp)), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PanelColor)) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp, horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Purple, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(10.dp))
            Text(stringResource(R.string.favorites_empty_short), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(stringResource(R.string.favorites_empty_subtext), color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp))
        }
    }
}

private class MockFavoritesViewModel(val state: FavoritesUiState) : IFavoritesViewModel {
    override val uiState: StateFlow<FavoritesUiState> = MutableStateFlow(state)
    override fun toggleFavorite(coinId: String) = Unit
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FavoritesScreenPreview() {
    val sampleCoins = listOf(
        CryptoModel("bitcoin", "btc", "Bitcoin", "", 67823.54, 1.34e12, 1, 2.35),
        CryptoModel("ethereum", "eth", "Ethereum", "", 3523.18, 4.23e11, 2, 3.02),
        CryptoModel("solana", "sol", "Solana", "", 174.35, 8.12e10, 3, 6.21)
    )
    FavoritesScreen("Berat", {}, viewModel = MockFavoritesViewModel(FavoritesUiState.Success(sampleCoins, sampleCoins.map { it.id })))
}
