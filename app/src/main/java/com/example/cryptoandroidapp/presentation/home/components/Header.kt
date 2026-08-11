package com.example.cryptoandroidapp.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoandroidapp.presentation.home.Muted
import com.example.cryptoandroidapp.presentation.home.Purple
import androidx.compose.ui.res.stringResource
import com.example.cryptoandroidapp.R

@Composable
fun Header(userName: String) {
    Row(
        modifier = Modifier.statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Brush.linearGradient(listOf(Purple, Color(0xFF9B7BFF)))),
            contentAlignment = Alignment.Center
        ) {
            Text("C", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.NotificationsNone,
            contentDescription = stringResource(R.string.detail_notifications_desc),
            tint = Muted,
            modifier = Modifier.size(19.dp)
        )
        Spacer(Modifier.width(11.dp))
        Box(
            modifier = Modifier
                .size(29.dp)
                .border(1.dp, Purple, CircleShape)
                .background(Color(0xFF1C2840), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.firstOrNull()?.uppercase() ?: "K",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
    Spacer(Modifier.height(12.dp))
    Text(stringResource(R.string.greeting_evening), color = Muted, fontSize = 12.sp)
    Text(userName, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Text(stringResource(R.string.header_subtitle), color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 3.dp))
}
