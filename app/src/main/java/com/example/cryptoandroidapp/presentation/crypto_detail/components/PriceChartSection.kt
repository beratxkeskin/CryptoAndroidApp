package com.example.cryptoandroidapp.presentation.crypto_detail.components

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.domain.model.CandleModel
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailBorder
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailGreen
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailMuted
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailPurple
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailRed
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailText
import com.example.cryptoandroidapp.presentation.crypto_detail.formatChartPrice
import com.example.cryptoandroidapp.presentation.crypto_detail.formatUsd
import com.example.cryptoandroidapp.presentation.crypto_detail.rememberMarker
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarkerController
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun PriceChartSection(candles: List<CandleModel>, selectedDays: String, onRange: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 22.dp)) {
        Text(stringResource(R.string.detail_price_performance), color = DetailText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.detail_price_performance_subtitle), color = DetailMuted, fontSize = 13.sp, modifier = Modifier.padding(top = 3.dp))
        Spacer(Modifier.height(12.dp))
        TimeRangeSelector(selectedDays, onRange)
        Spacer(Modifier.height(10.dp))
        ChartSummary(candles)
        Spacer(Modifier.height(12.dp))
        PriceChart(candles)
    }
}

@Composable
private fun TimeRangeSelector(selectedDays: String, onRange: (String) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        val ranges = listOf(
            "1" to stringResource(R.string.time_24h),
            "7" to stringResource(R.string.time_7d),
            "30" to stringResource(R.string.time_30d),
            "90" to stringResource(R.string.time_90d),
            "365" to stringResource(R.string.time_1y)
        )
        ranges.forEach { (days, label) ->
            val active = selectedDays == days
            Text(
                text = label,
                color = if (active) DetailText else DetailMuted,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f).background(if (active) DetailPurple else androidx.compose.ui.graphics.Color.Transparent, RoundedCornerShape(14.dp)).clickable { onRange(days) }.padding(vertical = 10.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun ChartSummary(candles: List<CandleModel>) {
    if (candles.isEmpty()) return
    val first = candles.first()
    val last = candles.last()
    val change = if (first.open != 0.0) (last.close - first.open) / first.open * 100 else 0.0
    val positive = change >= 0.0
    val high = candles.maxOf { it.high }
    val low = candles.minOf { it.low }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(stringResource(R.string.detail_last_price), color = DetailMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text(formatUsd(last.close), color = DetailText, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 3.dp))
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(stringResource(R.string.detail_selected_period), color = DetailMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text(
                text = "${if (positive) "+" else ""}${String.format(Locale.US, "%.2f", change)}%",
                color = if (positive) DetailGreen else DetailRed,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
    Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(stringResource(R.string.detail_max_label, formatUsd(high)), color = DetailMuted, fontSize = 12.sp)
        Text(stringResource(R.string.detail_min_label, formatUsd(low)), color = DetailMuted, fontSize = 12.sp)
    }
}

@Composable
private fun PriceChart(candles: List<CandleModel>) {
    val producer = remember { CartesianChartModelProducer() }
    val scrollState = rememberVicoScrollState(scrollEnabled = false)
    val chartPoints = remember(candles) { candles.mapIndexed { index, candle -> ChartPoint(index.toDouble(), candle) } }
    val rangeProvider = remember(chartPoints) {
        object : CartesianLayerRangeProvider {
            override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
                if (!minY.isFinite() || !maxY.isFinite()) return 0.0
                if (minY == maxY) return if (minY > 0) minY * 0.95 else -1.0
                return (minY - (maxY - minY) * 0.05).coerceAtLeast(0.0)
            }
            override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
                if (!minY.isFinite() || !maxY.isFinite()) return 1.0
                if (minY == maxY) return if (maxY > 0) maxY * 1.05 else 1.0
                return maxY + (maxY - minY) * 0.05
            }
        }
    }
    LaunchedEffect(chartPoints) {
        if (chartPoints.isNotEmpty()) producer.runTransaction {
            lineModel { series(x = chartPoints.map { it.x }, y = chartPoints.map { it.candle.close }) }
        }
    }
    Box(Modifier.fillMaxWidth().height(270.dp)) {
        if (candles.isEmpty()) {
            Text(stringResource(R.string.detail_loading), color = DetailMuted, modifier = Modifier.align(Alignment.Center))
        } else {
            val dateFormatter = CartesianValueFormatter { _, value, _ ->
                val index = value.roundToInt().coerceIn(0, chartPoints.lastIndex)
                SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(chartPoints[index].candle.timestamp))
            }
            val guideline = rememberAxisGuidelineComponent(fill = Fill(DetailBorder.copy(alpha = 0.38f)), shape = RectangleShape)
            val priceItemPlacer = remember { VerticalAxis.ItemPlacer.count(count = { 5 }) }
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(rangeProvider = rangeProvider),
                    endAxis = VerticalAxis.rememberEnd(
                        valueFormatter = CartesianValueFormatter { _, value, _ -> formatChartPrice(value) },
                        label = rememberAxisLabelComponent(style = androidx.compose.ui.text.TextStyle(color = DetailMuted, fontSize = 10.sp)),
                        guideline = guideline,
                        tick = null,
                        itemPlacer = priceItemPlacer
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = dateFormatter,
                        label = rememberAxisLabelComponent(style = androidx.compose.ui.text.TextStyle(color = DetailMuted, fontSize = 10.sp)),
                        guideline = guideline,
                        tick = null
                    ),
                    marker = rememberMarker(),
                    markerController = CartesianMarkerController.rememberShowOnPress(
                        consumeMoveEvents = true
                    )
                ),
                modelProducer = producer,
                scrollState = scrollState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private data class ChartPoint(val x: Double, val candle: CandleModel)
