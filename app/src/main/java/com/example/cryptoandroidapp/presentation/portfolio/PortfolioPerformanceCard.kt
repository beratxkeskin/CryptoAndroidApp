package com.example.cryptoandroidapp.presentation.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.presentation.crypto_detail.rememberMarker
import com.example.cryptoandroidapp.presentation.home.Muted
import com.example.cryptoandroidapp.presentation.home.PanelBorder
import com.example.cryptoandroidapp.presentation.home.Purple
import com.example.cryptoandroidapp.presentation.home.Red
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
fun PortfolioPerformanceCard(state: PortfolioUiState.Success, onRangeSelected: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(stringResource(R.string.portfolio_title), color = Muted, fontSize = 14.sp)
        Text(state.portfolioTotalText, color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 3.dp))
        Text(state.portfolioChangeText, color = if (state.isPortfolioPositive) Color(0xFF4ADE80) else Red, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(20.dp))
        Text(stringResource(R.string.portfolio_performance_title), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.portfolio_performance_desc), color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
        Spacer(Modifier.height(14.dp))
        TimeRangeSelector(state.selectedDays, onRangeSelected)
        Spacer(Modifier.height(16.dp))
        CostSummary(state)
        Spacer(Modifier.height(16.dp))
        ChartSummary(state)
        Spacer(Modifier.height(10.dp))
        PortfolioChart(state.chartPoints, state.isChartLoading, state.chartError)
    }
}

@Composable
private fun CostSummary(state: PortfolioUiState.Success) {
    val positive = state.unrealizedPnlUsd >= 0.0
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(stringResource(R.string.portfolio_total_cost), color = Muted, fontSize = 11.sp)
            Text(String.format(Locale.US, "$%,.2f", state.totalCostUsd), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(stringResource(R.string.portfolio_unrealized_pnl), color = Muted, fontSize = 11.sp)
            Text(
                String.format(Locale.US, "%s$%,.2f (%s%.2f%%)", if (positive) "+" else "-", kotlin.math.abs(state.unrealizedPnlUsd), if (positive) "+" else "-", kotlin.math.abs(state.unrealizedPnlPercent)),
                color = if (positive) Color(0xFF4ADE80) else Red, fontSize = 15.sp, fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TimeRangeSelector(selected: String, onSelected: (String) -> Unit) {
    val ranges = listOf(
        "1" to stringResource(R.string.time_24h),
        "7" to stringResource(R.string.time_7d),
        "30" to stringResource(R.string.time_30d),
        "90" to stringResource(R.string.time_90d),
        "365" to stringResource(R.string.time_1y)
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        ranges.forEach { (days, label) ->
            val active = days == selected
            Text(
                text = label.uppercase(Locale.ROOT), textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold, fontSize = 12.sp,
                color = if (active) Color.White else Muted,
                modifier = Modifier.weight(1f).background(if (active) Purple else Color.Transparent, RoundedCornerShape(14.dp))
                    .clickable { onSelected(days) }.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun ChartSummary(state: PortfolioUiState.Success) {
    val first = state.chartPoints.firstOrNull()?.valueUsd
    val last = state.chartPoints.lastOrNull()?.valueUsd
    val change = if (first != null && last != null) last - first else 0.0
    val percent = if (first != null && first != 0.0) change / first * 100 else 0.0
    val positive = change >= 0.0
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
        Column {
            Text(stringResource(R.string.selected_period_value), color = Muted, fontSize = 11.sp)
            Text(last?.let { String.format(Locale.US, "$%,.2f", it) } ?: "—", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            if (first == null) "" else String.format(Locale.US, "%s$%,.2f  (%s%.2f%%)", if (positive) "+" else "-", kotlin.math.abs(change), if (positive) "+" else "-", kotlin.math.abs(percent)),
            color = if (positive) Color(0xFF4ADE80) else Red, fontSize = 13.sp, fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PortfolioChart(points: List<PortfolioChartPoint>, loading: Boolean, error: String?) {
    val producer = remember { CartesianChartModelProducer() }
    val chartPoints = remember(points) { points.mapIndexed { index, point -> index.toDouble() to point } }
    val rangeProvider = remember(chartPoints) {
        object : CartesianLayerRangeProvider {
            override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double =
                if (minY == maxY) minY * .98 else minY - (maxY - minY) * .08
            override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double =
                if (minY == maxY) maxY * 1.02 else maxY + (maxY - minY) * .08
        }
    }
    LaunchedEffect(chartPoints) {
        if (chartPoints.isNotEmpty()) producer.runTransaction {
            lineModel { series(x = chartPoints.map { it.first }, y = chartPoints.map { it.second.valueUsd }) }
        }
    }
    Box(Modifier.fillMaxWidth().height(230.dp)) {
        when {
            loading -> CircularProgressIndicator(color = Purple, modifier = Modifier.align(Alignment.Center))
            error != null -> Text(error, color = Muted, modifier = Modifier.align(Alignment.Center))
            points.isEmpty() -> Text(stringResource(R.string.portfolio_add_asset_chart), color = Muted, modifier = Modifier.align(Alignment.Center))
            else -> {
                val dateFormatter = CartesianValueFormatter { _, value, _ ->
                    val index = value.roundToInt().coerceIn(0, chartPoints.lastIndex)
                    SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(chartPoints[index].second.timestamp))
                }
                val guideline = rememberAxisGuidelineComponent(fill = Fill(PanelBorder.copy(alpha = .45f)), shape = RectangleShape)
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberLineCartesianLayer(rangeProvider = rangeProvider),
                        endAxis = VerticalAxis.rememberEnd(
                            valueFormatter = CartesianValueFormatter { _, value, _ -> compactUsd(value) },
                            label = rememberAxisLabelComponent(style = androidx.compose.ui.text.TextStyle(color = Muted, fontSize = 10.sp)),
                            guideline = guideline, tick = null, itemPlacer = remember { VerticalAxis.ItemPlacer.count(count = { 4 }) }
                        ),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            valueFormatter = dateFormatter,
                            label = rememberAxisLabelComponent(style = androidx.compose.ui.text.TextStyle(color = Muted, fontSize = 10.sp)), guideline = guideline, tick = null
                        ),
                        marker = rememberMarker(), markerController = CartesianMarkerController.rememberShowOnPress(consumeMoveEvents = true)
                    ), modelProducer = producer, scrollState = rememberVicoScrollState(scrollEnabled = false), modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private fun compactUsd(value: Double): String = when {
    value >= 1_000_000 -> String.format(Locale.US, "$%.1fM", value / 1_000_000)
    value >= 1_000 -> String.format(Locale.US, "$%.1fK", value / 1_000)
    else -> String.format(Locale.US, "$%.0f", value)
}
