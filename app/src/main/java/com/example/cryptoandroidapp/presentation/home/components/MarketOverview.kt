package com.example.cryptoandroidapp.presentation.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.domain.model.CryptoModel
import com.example.cryptoandroidapp.presentation.home.Green
import com.example.cryptoandroidapp.presentation.home.PanelBorder
import com.example.cryptoandroidapp.presentation.home.Purple
import com.example.cryptoandroidapp.presentation.home.Red
import java.util.Locale

data class OverviewItem(
    val coinId: String,
    val symbol: String,
    val name: String,
    val imageUrl: String,
    val changeText: String,
    val isPositive: Boolean
)

@Composable
fun MarketOverview(
    coins: List<CryptoModel> = emptyList(),
    onCoinClick: (String) -> Unit = {}
) = Panel {
    // USD, EUR, TRY veya DAI içeren tüm stablecoin ve itibari paraya sabitli varlıklar filtrelenir (USD1, USDE, USDT, USDC vb.)
    val cryptoOnlyCoins = coins.filterNot { coin ->
        val sym = coin.symbol.uppercase(Locale.ROOT)
        sym.contains("USD") || sym.contains("DAI") || sym.contains("EUR") || sym.contains("TRY")
    }

    // 1. En Çok Yükselenler (24 saat) - 5 Kripto Coin
    val gainers = cryptoOnlyCoins
        .sortedByDescending { it.priceChangePercentage24h }
        .take(5)
        .map { coin ->
            OverviewItem(
                coinId = coin.id,
                symbol = coin.symbol.uppercase(Locale.ROOT),
                name = coin.name,
                imageUrl = coin.imageUrl,
                changeText = String.format(Locale.US, "▲ %.2f%%", coin.priceChangePercentage24h),
                isPositive = true
            )
        }

    // 2. En Çok Düşenler (24 saat) - 5 Kripto Coin
    val losers = cryptoOnlyCoins
        .sortedBy { it.priceChangePercentage24h }
        .take(5)
        .map { coin ->
            OverviewItem(
                coinId = coin.id,
                symbol = coin.symbol.uppercase(Locale.ROOT),
                name = coin.name,
                imageUrl = coin.imageUrl,
                changeText = String.format(Locale.US, "▼ %.2f%%", Math.abs(coin.priceChangePercentage24h)),
                isPositive = false
            )
        }

    // 3. Yüksek Hacimliler - 5 Kripto Coin (USD1, USDE gibi stablecoinler haric)
    val highVolume = cryptoOnlyCoins
        .sortedByDescending { it.totalVolume }
        .take(5)
        .map { coin ->
            OverviewItem(
                coinId = coin.id,
                symbol = coin.symbol.uppercase(Locale.ROOT),
                name = coin.name,
                imageUrl = coin.imageUrl,
                changeText = String.format(Locale.US, "%+.2f%%", coin.priceChangePercentage24h),
                isPositive = coin.priceChangePercentage24h >= 0
            )
        }

    // 4. 7 Günlük Şampiyonlar - 5 Kripto Coin
    val perform7d = cryptoOnlyCoins
        .mapNotNull { coin ->
            val first = coin.priceHistory7d.firstOrNull()
            val last = coin.priceHistory7d.lastOrNull() ?: coin.currentPrice
            if (first != null && first > 0) {
                val pct = ((last - first) / first) * 100.0
                coin to pct
            } else null
        }
        .sortedByDescending { it.second }
        .take(5)
        .map { (coin, pct) ->
            OverviewItem(
                coinId = coin.id,
                symbol = coin.symbol.uppercase(Locale.ROOT),
                name = coin.name,
                imageUrl = coin.imageUrl,
                changeText = String.format(Locale.US, "▲ %.2f%%", pct),
                isPositive = true
            )
        }

    Text(
        text = stringResource(R.string.market_overview_title),
        color = Color.White,
        fontSize = 17.sp,
        fontWeight = FontWeight.Medium
    )

    Spacer(Modifier.height(14.dp))

    // Satır 1: Top Gainers & Top Losers
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OverviewCard(
            icon = "↑",
            title = stringResource(R.string.top_gainers),
            color = Green,
            items = gainers,
            onCoinClick = onCoinClick,
            modifier = Modifier.weight(1f)
        )
        OverviewCard(
            icon = "↓",
            title = stringResource(R.string.top_losers),
            color = Red,
            items = losers,
            onCoinClick = onCoinClick,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(Modifier.height(10.dp))

    // Satır 2: High Volume & 7D Performers
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OverviewCard(
            icon = "⚡",
            title = stringResource(R.string.high_volume),
            color = Purple,
            items = highVolume,
            onCoinClick = onCoinClick,
            modifier = Modifier.weight(1f)
        )
        OverviewCard(
            icon = "🔥",
            title = stringResource(R.string.performers_7d),
            color = Color(0xFFFF9800),
            items = perform7d,
            onCoinClick = onCoinClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun OverviewCard(
    icon: String,
    title: String,
    color: Color,
    items: List<OverviewItem>,
    onCoinClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) = Card(
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1A30)),
    modifier = modifier
        .height(235.dp)
        .border(1.dp, PanelBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
    shape = RoundedCornerShape(14.dp)
) {
    Column(Modifier.padding(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$icon $title",
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(6.dp))

        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onCoinClick(item.coinId) }
                    .padding(vertical = 3.dp, horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.symbol,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = item.symbol,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = item.changeText,
                    color = if (item.isPositive) Green else Red,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}
