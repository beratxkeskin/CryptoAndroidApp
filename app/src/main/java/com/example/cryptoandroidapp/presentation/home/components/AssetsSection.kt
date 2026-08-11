package com.example.cryptoandroidapp.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.presentation.home.Green
import com.example.cryptoandroidapp.presentation.home.Muted
import com.example.cryptoandroidapp.presentation.home.PanelBorder
import com.example.cryptoandroidapp.presentation.home.PanelColor
import com.example.cryptoandroidapp.presentation.home.Purple
import com.example.cryptoandroidapp.presentation.home.Red
import com.example.cryptoandroidapp.presentation.home.UserAsset

@Composable
fun AssetsSection(assets: List<UserAsset>, onAddAsset: () -> Unit) {
    SectionTitle(stringResource(R.string.my_assets_title), stringResource(R.string.view_all))
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(end = 4.dp)
    ) {
        items(assets, key = { it.symbol }) { asset ->
            AssetCard(asset)
        }
        item {
            AddAssetCard(onAddAsset)
        }
    }
}

@Composable
private fun AssetCard(asset: UserAsset) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        modifier = Modifier
            .width(165.dp)
            .height(155.dp)
            .border(1.dp, PanelBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            // Header Row: Logo + İsim Üstte, Sembol (ETH) ve Rozet (▼ 1.25%) Yan Yana Altta!
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = asset.imageUrl,
                    contentDescription = asset.name,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(asset.colorHex).copy(alpha = 0.2f))
                )

                Spacer(Modifier.width(8.dp))

                Column(Modifier.fillMaxWidth()) {
                    // Satır 1: Tam Kripto İsmi (Ethereum, Solana vb. - Tam Genişlik)
                    Text(
                        text = asset.name,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(2.dp))

                    // Satır 2: Solda Sembol (ETH), Sağda Değişim Rozeti
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = asset.symbol,
                            color = Muted,
                            fontSize = 11.sp,
                            maxLines = 1
                        )

                        Spacer(Modifier.weight(1f))

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (asset.isPositive) Green.copy(alpha = 0.15f) else Red.copy(alpha = 0.15f),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = asset.changeText,
                                color = if (asset.isPositive) Green else Red,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Satır 3: Coin Miktarı (Örn: 10 ETH)
            Text(
                text = asset.amountText,
                color = Muted,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Satır 4: Dolar Değeri (Örn: $18,926.30)
            Text(
                text = asset.valueText,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun AddAssetCard(onAddAsset: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        modifier = Modifier
            .width(135.dp)
            .height(155.dp)
            .border(1.dp, PanelBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable(onClick = onAddAsset),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = Purple.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Purple.copy(alpha = 0.5f)),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = stringResource(R.string.add_asset),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = stringResource(R.string.add_asset_hint),
                color = Muted,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0B0F19
)
@Composable
private fun AssetsSectionPreview() {
    val sampleAssets = listOf(
        UserAsset(
            name = "Ethereum",
            symbol = "ETH",
            amountText = "10 ETH",
            valueText = "$18,926.30",
            changeText = "▼ 1.25%",
            isPositive = false,
            colorHex = 0xFF8496C7,
            imageUrl = ""
        ),
        UserAsset(
            name = "Solana",
            symbol = "SOL",
            amountText = "5 SOL",
            valueText = "$381.45",
            changeText = "▲ 4.50%",
            isPositive = true,
            colorHex = 0xFF906CFF,
            imageUrl = ""
        )
    )

    Surface(
        color = Color(0xFF0B0F19),
        modifier = Modifier.padding(16.dp)
    ) {
        AssetsSection(
            assets = sampleAssets,
            onAddAsset = {}
        )
    }
}
