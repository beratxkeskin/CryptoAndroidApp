package com.example.cryptoandroidapp.presentation.crypto_detail.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cryptoandroidapp.domain.model.CryptoDetailModel
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailBorder
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailGreen
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailMuted
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailRed
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailText
import com.example.cryptoandroidapp.presentation.crypto_detail.formatUsd
import com.example.cryptoandroidapp.presentation.crypto_detail.formatUsdChange
import java.util.Locale

import androidx.compose.ui.res.stringResource
import com.example.cryptoandroidapp.R

@Composable
fun DetailTopBar(onBackClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(64.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.detail_back), tint = DetailMuted)
        }
        Spacer(Modifier.weight(1f))
        val topBarActions = listOf(
            Icons.Default.StarBorder to stringResource(R.string.detail_favorites_desc),
            Icons.Default.NotificationsNone to stringResource(R.string.detail_notifications_desc)
        )
        topBarActions.forEach { (icon, description) ->
            Surface(
                shape = CircleShape,
                color = Color.Transparent,
                modifier = Modifier.padding(start = 10.dp).size(44.dp).border(1.dp, DetailBorder, CircleShape)
            ) {
                Icon(icon, description, tint = DetailMuted, modifier = Modifier.padding(11.dp))
            }
        }
    }
}

@Composable
fun CoinOverview(detail: CryptoDetailModel) {
    val positive = detail.priceChangePercentage24h >= 0
    val changeColor = if (positive) DetailGreen else DetailRed
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(detail.imageUrl, "${detail.name} logo", Modifier.size(56.dp).clip(CircleShape))
        Spacer(Modifier.width(14.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(detail.name, color = DetailText, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(9.dp))
                Text(detail.symbol.uppercase(), color = DetailMuted, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Text(
                text = detail.marketCapRank?.let { stringResource(R.string.detail_rank_prefix, it.toString()) } ?: stringResource(R.string.detail_no_rank),
                color = DetailMuted,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
    Text(formatUsd(detail.currentPriceUsd), color = DetailText, fontSize = 35.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 24.dp))
    Text(
        text = "${if (positive) "+" else ""}${formatUsdChange(detail.currentPriceUsd, detail.priceChangePercentage24h)} (${if (positive) "+" else ""}${String.format(Locale.US, "%.2f", detail.priceChangePercentage24h)}%)",
        color = changeColor,
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 6.dp)
    )
}
