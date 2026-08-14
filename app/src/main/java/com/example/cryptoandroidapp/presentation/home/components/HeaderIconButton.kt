package com.example.cryptoandroidapp.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.cryptoandroidapp.presentation.home.PanelBorder
import com.example.cryptoandroidapp.presentation.home.PanelColor

/**
 * Üst bar başlıklarında (Header) arama, bildirim ve ayarlar için kullanılan ortak yuvarlak cam efektli ikon butonu.
 */
@Composable
fun HeaderIconButton(
    icon: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit = {},
    size: Dp = 36.dp,
    iconSize: Dp = 18.dp,
    tint: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(PanelColor)
            .border(1.dp, PanelBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}
