package com.example.cryptoandroidapp.presentation.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.domain.model.CryptoModel
import com.example.cryptoandroidapp.presentation.home.Green
import com.example.cryptoandroidapp.presentation.home.Muted
import com.example.cryptoandroidapp.presentation.home.PanelBorder
import com.example.cryptoandroidapp.presentation.home.PanelColor
import com.example.cryptoandroidapp.presentation.home.Red
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun WatchListSection(
    favoriteCoins: List<CryptoModel>,
    favoriteCoinIds: List<String> = emptyList(),
    onCoinClick: (String) -> Unit = {},
    onToggleFavorite: (String) -> Unit = {},
    onViewAllClick: () -> Unit = {}
) = Panel {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.watchlist_title),
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.view_all),
            color = Muted,
            fontSize = 12.sp,
            modifier = Modifier.clickable { onViewAllClick() }
        )
    }

    Spacer(Modifier.height(10.dp))

    if (favoriteCoins.isEmpty()) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1A30)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = Muted,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Henüz takip listenize coin eklemediniz.",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Detay ekranındaki yıldız butonuna basarak favorilerinizi buraya ekleyebilirsiniz.",
                    color = Muted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    } else {
        favoriteCoins.take(5).forEach { coin ->
            WatchListRow(
                coin = coin,
                isFavorite = coin.id in favoriteCoinIds || favoriteCoinIds.isEmpty(),
                onCoinClick = onCoinClick,
                onToggleFavorite = onToggleFavorite
            )
        }
    }
}

@Composable
fun WatchListRow(
    coin: CryptoModel,
    isFavorite: Boolean = true,
    onCoinClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    val priceText = String.format(Locale.US, "$%,.2f", coin.currentPrice)
    val changeText = String.format(
        Locale.US,
        if (coin.priceChangePercentage24h >= 0) "▲ %,.2f%%" else "▼ %,.2f%%",
        Math.abs(coin.priceChangePercentage24h)
    )

    var prevPrice by remember { mutableStateOf(coin.currentPrice) }
    var flashColor by remember { mutableStateOf(Color.Transparent) }

    val animatedColor by animateColorAsState(
        targetValue = flashColor,
        animationSpec = tween(durationMillis = 350),
        label = "price_flash"
    )

    LaunchedEffect(coin.currentPrice) {
        if (coin.currentPrice > prevPrice) {
            flashColor = Green.copy(alpha = 0.25f)
            delay(350)
            flashColor = Color.Transparent
        } else if (coin.currentPrice < prevPrice) {
            flashColor = Red.copy(alpha = 0.25f)
            delay(350)
            flashColor = Color.Transparent
        }
        prevPrice = coin.currentPrice
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(animatedColor)
            .clickable { onCoinClick(coin.id) }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = coin.imageUrl,
            contentDescription = coin.name,
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(PanelBorder.copy(alpha = 0.3f))
        )

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = coin.name,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = coin.symbol.uppercase(Locale.ROOT),
                color = Muted,
                fontSize = 10.sp
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = priceText,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = changeText,
                color = if (coin.priceChangePercentage24h >= 0) Green else Red,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.width(8.dp))

        IconButton(
            onClick = { onToggleFavorite(coin.id) },
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) Color(0xFFFFD700) else Muted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WatchListSectionPreview() {
    val sampleCoins = listOf(
        CryptoModel("bitcoin", "btc", "Bitcoin", "", 67823.54, 1.34e12, 1, 2.35),
        CryptoModel("ethereum", "eth", "Ethereum", "", 3523.18, 4.23e11, 2, -1.02)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF020916))
            .padding(16.dp)
    ) {
        WatchListSection(favoriteCoins = sampleCoins)
    }
}
