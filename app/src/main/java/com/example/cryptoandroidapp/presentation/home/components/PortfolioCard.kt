package com.example.cryptoandroidapp.presentation.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.presentation.home.*

/**
 * Ana ekrandaki gelişmiş, etkileşimli ve Neon ışımalı Toplam Portföy Değeri Kartı.
 *
 * Özellikler:
 * 1. 👁️ Canlı Bakiye Gizle / Göster (Eye / Privacy Toggle)
 * 2. 🟢🔴 Kar / Zarar durumuna göre dinamik Neon Kenar Işıması (Neon Glow Border)
 * 3. 📈 İnteraktif Zaman Filtresi Seçimi (1D, 1W, 1M, 3M, 1Y, ALL)
 * 4. 🔢 Bakiye değişiminde akıcı sayı sayma animasyonu (AnimatedContent)
 * 5. 🚀 Sağ üstte "Portföyü Yönet ➔" doğrudan erişim kısayolu
 */
@Composable
fun PortfolioCard(
    totalText: String,
    changeText: String,
    isPositive: Boolean,
    onNavigateToPortfolio: () -> Unit = {}
) {
    var isBalanceHidden by remember { mutableStateOf(false) }
    var selectedTimeIndex by remember { mutableStateOf(0) }

    // Kar/Zarar durumuna göre dinamik Neon Kenar Işıması
    val glowColor by animateColorAsState(
        targetValue = if (isPositive) Green.copy(alpha = 0.55f) else Red.copy(alpha = 0.55f),
        animationSpec = tween(durationMillis = 500),
        label = "portfolio_glow"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.2.dp, glowColor),
                RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            PanelColor.copy(alpha = 0.98f),
                            if (isPositive) Green.copy(alpha = 0.05f) else Red.copy(alpha = 0.05f),
                            PanelColor.copy(alpha = 0.92f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // 1. ÜST BAR: Başlık + Gizlilik İkonu + Yönet Kısayolu
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.portfolio_title),
                    color = Muted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.width(6.dp))

                // 👁️ Canlı Gizlilik Butonu (Eye Toggle)
                IconButton(
                    onClick = { isBalanceHidden = !isBalanceHidden },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Balance Privacy",
                        tint = if (isBalanceHidden) Purple else Muted,
                        modifier = Modifier.size(17.dp)
                    )
                }

                Spacer(Modifier.weight(1f))

                // 🚀 Portföyü Yönet Kısayolu
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onNavigateToPortfolio)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.portfolio_all_assets),
                        color = Purple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // 2. 🔢 CANLI BAKİYE RAKAMI (Sayaç Animasyonlu veya Gizli)
            AnimatedContent(
                targetState = if (isBalanceHidden) "$ • • • • •" else totalText,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                label = "balance_anim"
            ) { displayTotal ->
                Text(
                    text = displayTotal,
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            // 3. CANLI DEĞİŞİM ROZETİ VE 24S FİLTRE KAPSÜLÜ
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPositive) Green.copy(alpha = 0.15f) else Red.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isBalanceHidden) "+••••• (▲ ••%)" else changeText,
                        color = if (isPositive) Green else Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(Modifier.width(10.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF25314B)
                ) {
                    Text(
                        text = "${stringResource(R.string.time_24h)} ⌄",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // 4. İNTERAKTİF ÇİZGİ GRAFİK
            val points = remember(selectedTimeIndex, isPositive) {
                when (selectedTimeIndex) {
                    0 -> if (isPositive) listOf(0.3f, 0.45f, 0.38f, 0.65f, 0.55f, 0.85f) else listOf(0.85f, 0.65f, 0.7f, 0.45f, 0.5f, 0.25f)
                    1 -> if (isPositive) listOf(0.2f, 0.35f, 0.5f, 0.4f, 0.7f, 0.9f) else listOf(0.9f, 0.7f, 0.75f, 0.5f, 0.4f, 0.15f)
                    2 -> if (isPositive) listOf(0.4f, 0.3f, 0.6f, 0.55f, 0.75f, 0.95f) else listOf(0.95f, 0.75f, 0.8f, 0.6f, 0.4f, 0.3f)
                    3 -> if (isPositive) listOf(0.25f, 0.4f, 0.35f, 0.68f, 0.6f, 0.88f) else listOf(0.88f, 0.65f, 0.72f, 0.48f, 0.55f, 0.22f)
                    4 -> if (isPositive) listOf(0.15f, 0.3f, 0.45f, 0.6f, 0.8f, 0.98f) else listOf(0.98f, 0.8f, 0.65f, 0.5f, 0.35f, 0.1f)
                    else -> listOf(0.3f, 0.5f, 0.4f, 0.7f, 0.6f, 0.9f)
                }
            }

            LineChart(
                points = points,
                color = if (isPositive) Green else Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(86.dp)
                    .padding(top = 10.dp, bottom = 4.dp)
            )

            // 5. İNTERAKTİF ZAMAN FİLTRESİ BUTONLARI (1D, 1W, 1M, 3M, 1Y, ALL)
            val timeFrames = listOf(
                stringResource(R.string.time_1d),
                stringResource(R.string.time_1w),
                stringResource(R.string.time_1m),
                stringResource(R.string.time_3m),
                stringResource(R.string.time_1y),
                stringResource(R.string.time_all)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                timeFrames.forEachIndexed { index, label ->
                    val isSelected = selectedTimeIndex == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Purple else Color.Transparent)
                            .clickable { selectedTimeIndex = index }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else Muted,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
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
