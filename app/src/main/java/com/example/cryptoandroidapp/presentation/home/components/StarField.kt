package com.example.cryptoandroidapp.presentation.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun StarField() = Canvas(Modifier.fillMaxSize()) {
    listOf(
        0.05f to 0.08f,
        0.14f to 0.18f,
        0.42f to 0.10f,
        0.7f to 0.21f,
        0.83f to 0.06f,
        0.94f to 0.36f,
        0.28f to 0.44f
    ).forEach { (x, y) ->
        drawCircle(
            color = Color(0xFF7895FF).copy(alpha = 0.35f),
            radius = 1.5f,
            center = Offset(size.width * x, size.height * y)
        )
    }
}
