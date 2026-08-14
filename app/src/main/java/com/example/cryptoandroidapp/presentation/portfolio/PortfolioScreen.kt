package com.example.cryptoandroidapp.presentation.portfolio

import com.example.cryptoandroidapp.common.formatCryptoAmount

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.cryptoandroidapp.domain.model.CryptoModel
import com.example.cryptoandroidapp.ui.theme.CryptoAndroidAppTheme
import com.example.cryptoandroidapp.presentation.home.Background
import com.example.cryptoandroidapp.presentation.home.Muted
import com.example.cryptoandroidapp.presentation.home.PanelBorder
import com.example.cryptoandroidapp.presentation.home.PanelColor
import com.example.cryptoandroidapp.presentation.home.Purple
import com.example.cryptoandroidapp.presentation.home.Red
import androidx.compose.ui.res.stringResource
import com.example.cryptoandroidapp.R
import java.util.Locale

@Composable
fun PortfolioScreen(
    userName: String,
    modifier: Modifier = Modifier,
    viewModel: IPortfolioViewModel = hiltViewModel<PortfolioViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddAssetDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Purple.copy(alpha = 0.12f), Background, Background)
                )
            )
    ) {
        when (val state = uiState) {
            is PortfolioUiState.Loading -> {
                Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.CircularProgressIndicator(color = Purple)
                }
            }
            is PortfolioUiState.Error -> {
                Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(R.string.error_prefix, state.message), color = Color.White, fontSize = 15.sp)
                }
            }
            is PortfolioUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(top = 12.dp, bottom = 140.dp)
                ) {
                    PortfolioHeader()
                    Spacer(Modifier.height(8.dp))
                    
                    // Bölüm 1: Portföy Performansı ve Zaman Filtresi
                    PortfolioPerformanceCard(state = state, onRangeSelected = viewModel::selectTimeRange)
                    
                    HorizontalDivider(color = PanelBorder.copy(alpha = 0.45f), modifier = Modifier.padding(vertical = 12.dp))
                    
                    // Bölüm 2: Kripto Dağılımı Donut Grafiği
                    CryptoAllocationCard(assets = state.userAssets, totalText = state.portfolioTotalText)
                    
                    HorizontalDivider(color = PanelBorder.copy(alpha = 0.45f), modifier = Modifier.padding(vertical = 12.dp))
                    
                    // Bölüm 3: Portföy Varlıkları Listesi
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.portfolio_assets_title), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        androidx.compose.material3.TextButton(
                            onClick = { showAddAssetDialog = true },
                            colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = Purple)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(stringResource(R.string.add_asset), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (state.rawUserAssets.isEmpty()) {
                        Text(
                            text = stringResource(R.string.portfolio_empty_desc),
                            color = Muted,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                        )
                    } else {
                        state.userAssets.forEach { asset ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = asset.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(38.dp).clip(CircleShape).background(PanelBorder.copy(alpha = 0.3f))
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = asset.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text(text = asset.amount.formatCryptoAmount(asset.symbol), color = Muted, fontSize = 12.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = String.format(Locale.US, "$%,.2f", asset.totalValueUsd), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = stringResource(R.string.avg_cost_prefix, String.format(Locale.US, "$%,.2f", asset.averageCostUsd)),
                                        color = Muted,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = String.format(Locale.US, "%s$%,.2f (%s%.2f%%)", if (asset.pnlUsd >= 0) "+" else "-", kotlin.math.abs(asset.pnlUsd), if (asset.pnlPercent >= 0) "+" else "-", kotlin.math.abs(asset.pnlPercent)),
                                        color = if (asset.pnlUsd >= 0) Color(0xFF4ADE80) else Red,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                IconButton(onClick = { viewModel.deleteAsset(asset.coinId) }) {
                                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.btn_delete), tint = Red.copy(alpha = 0.75f))
                                }
                            }
                            HorizontalDivider(color = PanelBorder.copy(alpha = 0.3f))
                        }
                    }
                }

                // Varlık Ekleme Diyalog Penceresi
                if (showAddAssetDialog) {
                    var selectedCoin by remember { mutableStateOf<CryptoModel?>(state.allCoins.firstOrNull()) }
                    var amountText by remember { mutableStateOf("") }
                    var costText by remember { mutableStateOf(selectedCoin?.currentPrice?.toString().orEmpty()) }
                    var isDropdownExpanded by remember { mutableStateOf(false) }

                    androidx.compose.ui.window.Dialog(onDismissRequest = { showAddAssetDialog = false }) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = PanelColor),
                            modifier = Modifier.fillMaxWidth().padding(16.dp).border(1.dp, PanelBorder, RoundedCornerShape(20.dp))
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Text(stringResource(R.string.add_asset), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(16.dp))
                                
                                Text(stringResource(R.string.select_crypto), color = Muted, fontSize = 12.sp)
                                Spacer(Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Background, RoundedCornerShape(10.dp))
                                        .border(1.dp, PanelBorder, RoundedCornerShape(10.dp))
                                        .clickable { isDropdownExpanded = !isDropdownExpanded }
                                        .padding(14.dp)
                                ) {
                                    Text(
                                        text = selectedCoin?.let { "${it.name} (${it.symbol.uppercase()})" } ?: stringResource(R.string.select_placeholder),
                                        color = Color.White
                                    )
                                }
                                
                                if (isDropdownExpanded) {
                                    Spacer(Modifier.height(8.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Background),
                                        modifier = Modifier.fillMaxWidth().height(150.dp).border(1.dp, PanelBorder, RoundedCornerShape(10.dp))
                                    ) {
                                        androidx.compose.foundation.lazy.LazyColumn {
                                            items(state.allCoins) { coin ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            selectedCoin = coin
                                                            isDropdownExpanded = false
                                                        }
                                                        .padding(12.dp)
                                                ) {
                                                    Text(text = "${coin.name} (${coin.symbol.uppercase()})", color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                Spacer(Modifier.height(16.dp))
                                Text(stringResource(R.string.amount_label), color = Muted, fontSize = 12.sp)
                                Spacer(Modifier.height(6.dp))
                                androidx.compose.material3.TextField(
                                    value = amountText,
                                    onValueChange = { amountText = it },
                                    placeholder = { Text("0.0", color = Muted) },
                                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                                        focusedContainerColor = Background,
                                        unfocusedContainerColor = Background,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder, RoundedCornerShape(10.dp)),
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                    )
                                )

                                Spacer(Modifier.height(14.dp))
                                Text(stringResource(R.string.unit_buy_cost), color = Muted, fontSize = 12.sp)
                                Spacer(Modifier.height(6.dp))
                                androidx.compose.material3.TextField(
                                    value = costText,
                                    onValueChange = { costText = it },
                                    placeholder = { Text("0.0", color = Muted) },
                                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                                        focusedContainerColor = Background, unfocusedContainerColor = Background,
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder, RoundedCornerShape(10.dp)),
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                                    )
                                )
                                
                                Spacer(Modifier.height(24.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    androidx.compose.material3.TextButton(onClick = { showAddAssetDialog = false }) {
                                        Text(stringResource(R.string.btn_cancel), color = Muted)
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    androidx.compose.material3.Button(
                                        onClick = {
                                            val coin = selectedCoin
                                            val amount = amountText.toDoubleOrNull()
                                            val price = costText.toDoubleOrNull() ?: coin?.currentPrice ?: 0.0
                                            if (coin != null && amount != null && amount > 0.0) {
                                                viewModel.addAsset(coin.id, coin.symbol, coin.name, amount, price)
                                                showAddAssetDialog = false
                                            }
                                        },
                                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Purple)
                                    ) {
                                        Text(stringResource(R.string.btn_add), color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PortfolioHeader() {
    Column(Modifier.statusBarsPadding()) {
        Text(stringResource(R.string.nav_portfolio), color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.tab_portfolio_desc), color = Muted, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

private class MockPortfolioViewModel(
    val state: PortfolioUiState
) : IPortfolioViewModel {
    override val uiState: StateFlow<PortfolioUiState> = MutableStateFlow(state)
    override fun addAsset(coinId: String, symbol: String, name: String, amount: Double, costPerUnitUsd: Double) {}
    override fun deleteAsset(coinId: String) {}
    override fun selectTimeRange(days: String) {}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PortfolioScreenPreview() {
    val mockState = PortfolioUiState.Success(
        portfolioTotalText = "$42,750.00",
        portfolioChangeText = "+$1,600.00  (▲ 3.88%)",
        isPortfolioPositive = true,
        totalCostUsd = 38_500.00,
        unrealizedPnlUsd = 4_250.00,
        unrealizedPnlPercent = 11.04,
        userAssets = listOf(
            UserAssetItem("bitcoin", "Bitcoin", "btc", 0.50, 32735.00, 65470.00, 60000.00, 2735.00, 9.12, ""),
            UserAssetItem("ethereum", "Ethereum", "eth", 2.00, 7280.00, 3640.00, 3100.00, 1080.00, 17.42, ""),
            UserAssetItem("solana", "Solana", "sol", 10.00, 2735.00, 273.50, 250.00, 235.00, 9.40, "")
        ),
        rawUserAssets = emptyList(),
        allCoins = emptyList()
    )
    CryptoAndroidAppTheme {
        Box(modifier = Modifier.fillMaxSize().background(Background)) {
            PortfolioScreen(
                userName = "Berat",
                viewModel = MockPortfolioViewModel(mockState)
            )
        }
    }
}
