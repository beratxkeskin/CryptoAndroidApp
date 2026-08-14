package com.example.cryptoandroidapp.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.presentation.home.PanelColor
import com.example.cryptoandroidapp.presentation.home.Red

/**
 * Uzay ve Finans temalı, süzülen neon cam kapsül (Glassmorphic Floating Capsule) tasarımlı internetsizlik uyarı bileşeni.
 */
@Composable
fun OfflineBanner(
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    // Kırmızı neon noktanın canlı olarak yanıp sönme (Pulse) animasyonu
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_offline")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    AnimatedVisibility(
        visible = !isConnected,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(stiffness = 300f)
        ) + fadeIn(animationSpec = tween(400)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = spring(stiffness = 300f)
        ) + fadeOut(animationSpec = tween(300)),
        modifier = modifier.statusBarsPadding().padding(top = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(PanelColor.copy(alpha = 0.92f))
                .border(1.dp, Red.copy(alpha = 0.6f), RoundedCornerShape(30.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Canlı Yanıp Sönen Kırmızı Parlayan Nokta
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(pulseAlpha)
            ) {
                Row(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Red)
                ) {}
            }

            Spacer(Modifier.width(10.dp))

            Text(
                text = stringResource(R.string.offline_mode_title),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.width(6.dp))

            Text(
                text = stringResource(R.string.offline_mode_subtitle),
                color = Red,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}