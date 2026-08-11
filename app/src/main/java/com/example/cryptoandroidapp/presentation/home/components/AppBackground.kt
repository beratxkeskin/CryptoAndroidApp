package com.example.cryptoandroidapp.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.presentation.home.Background
import com.example.cryptoandroidapp.presentation.home.Purple

/** A quiet, reusable backdrop: visual atmosphere without competing with market data. */
@Composable
fun AppBackground(
    mood: AppBackgroundMood,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Purple.copy(alpha = if (mood == AppBackgroundMood.Portfolio) 0.18f else 0.10f),
                            Color.Transparent
                        )
                    )
                )
        )

        if (mood == AppBackgroundMood.Portfolio) {
            Image(
                painter = painterResource(R.drawable.portfolio_orbit_ambient),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
                    .align(Alignment.TopCenter)
                    .alpha(0.20f)
            )
        }
    }
}

enum class AppBackgroundMood { Home, Markets, Portfolio, Favorites, Profile }
