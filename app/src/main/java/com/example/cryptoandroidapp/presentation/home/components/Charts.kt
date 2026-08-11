package com.example.cryptoandroidapp.presentation.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val portfolioPoints = listOf(.18f, .27f, .31f, .38f, .28f, .34f, .49f, .57f, .45f, .36f, .55f, .63f, .51f, .58f, .69f, .62f, .74f, .82f)

val sparkPointsPositive = listOf(.35f, .48f, .42f, .58f, .52f, .68f, .62f, .78f, .72f, .85f)
val sparkPointsNegative = listOf(.85f, .72f, .78f, .62f, .68f, .52f, .58f, .42f, .48f, .35f)

@Composable
fun LineChart(points: List<Float>, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val path = Path()
        val paddingY = 4.dp.toPx()
        val availableWidth = size.width
        val availableHeight = size.height - (paddingY * 2)

        points.forEachIndexed { index, point ->
            val x = availableWidth * index / (points.size - 1)
            val y = paddingY + (availableHeight * (1f - point))
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color.copy(alpha = 0.18f), style = Stroke(5.dp.toPx(), cap = StrokeCap.Round))
        drawPath(path, color, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
        drawCircle(color, 3.5f.dp.toPx(), Offset(size.width, paddingY + (availableHeight * (1f - points.last()))))
    }
}

@Composable
fun SparkLine(color: Color, isPositive: Boolean = true, modifier: Modifier = Modifier) {
    val points = if (isPositive) sparkPointsPositive else sparkPointsNegative
    LineChart(points, color, modifier)
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0B0F19
)
@Composable
private fun LineChartPreview() {
    Box(
        modifier = Modifier
            .size(width = 360.dp, height = 220.dp)
            .background(Color(0xFF0B0F19))
            .padding(24.dp)
    ) {
        LineChart(
            points = portfolioPoints,
            color = Color(0xFF7C5CFC),
            modifier = Modifier.fillMaxSize()
        )
    }
}