package com.example.cryptoandroidapp.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoandroidapp.presentation.home.Muted
import com.example.cryptoandroidapp.presentation.home.Purple
import com.example.cryptoandroidapp.presentation.home.Green
import com.example.cryptoandroidapp.presentation.home.Red
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import com.example.cryptoandroidapp.R

@Composable
fun PortfolioCard(totalText: String, changeText: String, isPositive: Boolean) = Panel {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(R.string.portfolio_title), color = Muted, fontSize = 15.sp)
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Default.Visibility, null, tint = Muted, modifier = Modifier.size(19.dp))
        Spacer(Modifier.weight(1f))
        Text(stringResource(R.string.portfolio_all_assets), color = Muted, fontSize = 14.sp)
        Icon(Icons.Default.KeyboardArrowDown, null, tint = Muted)
    }

    // Canlı portföy değeri
    Text(totalText, color = Color.White, fontSize = 37.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 15.dp))

    // Canlı portföy yüzdelik ve miktar değişimi
    Row(modifier = Modifier.padding(top = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(changeText, color = if (isPositive) Green else Red, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(12.dp))
        Text(
            text = "24s⌄",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF25314B))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
    LineChart(portfolioPoints, Purple, Modifier.fillMaxWidth().height(82.dp).padding(top = 8.dp))
    
    val timeFrames = listOf(
        stringResource(R.string.time_1d),
        stringResource(R.string.time_1w),
        stringResource(R.string.time_1m),
        stringResource(R.string.time_3m),
        stringResource(R.string.time_1y),
        stringResource(R.string.time_all)
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        timeFrames.forEachIndexed { index, label ->
            Text(
                text = label,
                color = if (index == 0) Color.White else Muted,
                fontSize = 13.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (index == 0) Purple.copy(alpha = .55f) else Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PortfolioCardPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF020916))
            .padding(16.dp)
    ) {
        PortfolioCard(
            totalText = "$42,750.00",
            changeText = "+$1,600.00  (▲ 3.88%)",
            isPositive = true
        )
    }
}
