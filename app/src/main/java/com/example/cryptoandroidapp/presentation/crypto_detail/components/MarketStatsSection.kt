package com.example.cryptoandroidapp.presentation.crypto_detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.domain.model.CryptoDetailModel
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailGreen
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailMuted
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailRed
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailText
import com.example.cryptoandroidapp.presentation.crypto_detail.compactNumber
import com.example.cryptoandroidapp.presentation.crypto_detail.compactUsd
import com.example.cryptoandroidapp.presentation.crypto_detail.formatUsd
import java.util.Locale

@Composable
fun MarketStatsSection(detail: CryptoDetailModel) {
    Column(Modifier.fillMaxWidth().padding(vertical = 22.dp)) {
        Text(stringResource(R.string.detail_market_stats), color = DetailText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))

        StatRow(
            stringResource(R.string.detail_market_cap), compactUsd(detail.marketCapUsd),
            stringResource(R.string.detail_volume), compactUsd(detail.totalVolumeUsd)
        )
        StatRow(
            stringResource(R.string.detail_high), formatUsd(detail.high24hUsd),
            stringResource(R.string.detail_low), formatUsd(detail.low24hUsd)
        )
        StatRow(
            stringResource(R.string.detail_market_rank), detail.marketCapRank?.let { "#$it" } ?: "—",
            stringResource(R.string.detail_circulating_supply), supplyText(detail.circulatingSupply, detail.symbol)
        )
        StatRow(
            stringResource(R.string.detail_max_supply), supplyText(detail.maxSupply, detail.symbol),
            stringResource(R.string.detail_ath), detail.allTimeHighUsd?.let(::formatUsd) ?: "—"
        )
        StatRow(
            stringResource(R.string.detail_ath_change), percentageText(detail.allTimeHighChangePercentage),
            stringResource(R.string.detail_change_7d), percentageText(detail.priceChangePercentage7d),
            leftValueColor = percentageColor(detail.allTimeHighChangePercentage),
            rightValueColor = percentageColor(detail.priceChangePercentage7d)
        )
        StatRow(
            stringResource(R.string.detail_change_30d), percentageText(detail.priceChangePercentage30d),
            stringResource(R.string.detail_change_24h), percentageText(detail.priceChangePercentage24h),
            leftValueColor = percentageColor(detail.priceChangePercentage30d),
            rightValueColor = percentageColor(detail.priceChangePercentage24h)
        )
    }
}

@Composable
private fun StatRow(
    leftLabel: String,
    leftValue: String,
    rightLabel: String,
    rightValue: String,
    leftValueColor: Color = DetailText,
    rightValueColor: Color = DetailText
) {
    Row(Modifier.fillMaxWidth().padding(bottom = 22.dp)) {
        StatItem(leftLabel, leftValue, Modifier.weight(1f), leftValueColor)
        StatItem(rightLabel, rightValue, Modifier.weight(1f), rightValueColor)
    }
}

@Composable
private fun StatItem(label: String, value: String, modifier: Modifier, valueColor: Color) = Column(modifier) {
    Text(label, color = DetailMuted, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    Text(
        value,
        color = valueColor,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 7.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

private fun supplyText(value: Double?, symbol: String): String =
    value?.let { "${compactNumber(it)} ${symbol.uppercase(Locale.ROOT)}" } ?: "—"

private fun percentageText(value: Double?): String =
    value?.let { String.format(Locale.US, "%+.2f%%", it) } ?: "—"

private fun percentageColor(value: Double?): Color = when {
    value == null -> DetailText
    value >= 0 -> DetailGreen
    else -> DetailRed
}
