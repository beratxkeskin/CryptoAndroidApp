package com.example.cryptoandroidapp.presentation.portfolio

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoandroidapp.presentation.home.Muted
import com.example.cryptoandroidapp.presentation.home.PanelBorder
import com.example.cryptoandroidapp.presentation.home.components.Panel
import java.util.Locale

private val allocationColors = listOf(
    Color(0xFF7C4DFF), Color(0xFF3C67F4), Color(0xFF1FB6B7), Color(0xFFFF981F), Color(0xFFFFC107)
)

@Composable
fun CryptoAllocationCard(assets: List<UserAssetItem>, totalText: String) = Panel {
    Text("Kripto Dağılımı", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Text("Varlıklarınızın güncel değer dağılımı", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
    Spacer(Modifier.height(16.dp))
    if (assets.isEmpty()) {
        Text("Dağılımı görmek için portföyünüze varlık ekleyin.", color = Muted, fontSize = 13.sp, modifier = Modifier.padding(vertical = 16.dp))
    } else {
        val portfolioValue = assets.sumOf { it.totalValueUsd }.takeIf { it > 0.0 } ?: 1.0
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
                AllocationDonut(assets)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(totalText, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Toplam", color = Muted, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.width(18.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                assets.sortedByDescending { it.totalValueUsd }.take(5).forEachIndexed { index, asset ->
                    AllocationLegend(asset, allocationColors[index % allocationColors.size], portfolioValue)
                }
            }
        }
    }
}

@Composable
private fun AllocationDonut(assets: List<UserAssetItem>) {
    val total = assets.sumOf { it.totalValueUsd }.takeIf { it > 0.0 } ?: 1.0
    Canvas(Modifier.size(150.dp)) {
        val stroke = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Butt)
        drawCircle(PanelBorder.copy(alpha = .45f), style = stroke)
        var start = -90f
        assets.sortedByDescending { it.totalValueUsd }.take(5).forEachIndexed { index, asset ->
            val sweep = (asset.totalValueUsd / total * 360.0).toFloat()
            drawArc(allocationColors[index % allocationColors.size], start, (sweep - 2f).coerceAtLeast(0f), false, Offset.Zero, size, style = stroke)
            start += sweep
        }
    }
}

@Composable
private fun AllocationLegend(asset: UserAssetItem, color: Color, portfolioValue: Double) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(10.dp)) { drawCircle(color) }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(asset.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
            Text(asset.symbol.uppercase(), color = Muted, fontSize = 10.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(String.format(Locale.US, "$%,.0f", asset.totalValueUsd), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(String.format(Locale.US, "%.1f%%", asset.totalValueUsd / portfolioValue * 100), color = Muted, fontSize = 10.sp)
        }
    }
}
