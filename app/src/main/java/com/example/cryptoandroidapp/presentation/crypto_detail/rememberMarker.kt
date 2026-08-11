package com.example.cryptoandroidapp.presentation.crypto_detail

import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarker

@Composable
fun rememberMarker(): CartesianMarker {
    val indicator = rememberShapeComponent(
        fill = Fill(DetailPurple),
        shape = CircleShape,
        strokeFill = Fill(DetailBackground),
        strokeThickness = 2.dp
    )
    return rememberDefaultCartesianMarker(
        label = rememberTextComponent(
            style = TextStyle(color = Color.White)
        ),
        indicator = { indicator },
        indicatorSize = 10.dp,
        guideline = rememberAxisGuidelineComponent()
    )
}
