package com.example.cryptoandroidapp.presentation.crypto_detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoandroidapp.domain.model.CryptoDetailModel
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailMuted
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailPurple
import com.example.cryptoandroidapp.presentation.crypto_detail.DetailText

import androidx.compose.ui.res.stringResource
import com.example.cryptoandroidapp.R

@Composable
fun AboutCoinSection(detail: CryptoDetailModel) {
    val description = remember(detail.description) { detail.description.replace(Regex("<[^>]*>"), "").trim() }
    var isExpanded by rememberSaveable(detail.id) { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().padding(vertical = 22.dp)) {
        Text(stringResource(R.string.detail_overview), color = DetailText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(
            text = if (description.isBlank()) stringResource(R.string.detail_no_description) else description,
            color = DetailMuted,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            maxLines = if (isExpanded) Int.MAX_VALUE else 4,
            overflow = if (isExpanded) TextOverflow.Clip else TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 14.dp)
        )
        if (description.isNotBlank()) {
            Text(
                text = if (isExpanded) stringResource(R.string.detail_show_less) else stringResource(R.string.detail_show_more),
                color = DetailPurple,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 14.dp).clickable { isExpanded = !isExpanded }
            )
        }
    }
}
