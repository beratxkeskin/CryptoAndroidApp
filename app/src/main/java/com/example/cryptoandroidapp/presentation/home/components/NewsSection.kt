package com.example.cryptoandroidapp.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.presentation.home.Muted

import androidx.compose.ui.res.stringResource

@Composable
fun NewsSection() = Panel {
    SectionTitle(stringResource(R.string.news_title), stringResource(R.string.all_news))
    Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.news_crypto_orbit),
            contentDescription = "News image",
            modifier = Modifier
                .size(width = 112.dp, height = 72.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.news_sample_title),
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 19.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.news_sample_source),
                color = Muted,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 7.dp)
            )
        }
    }
}
