package com.example.cryptoandroidapp.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.presentation.home.Muted
import com.example.cryptoandroidapp.presentation.home.PanelBorder
import com.example.cryptoandroidapp.presentation.home.Purple

@Composable
fun BottomNavigation(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) = Card(
    modifier = modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 16.dp, vertical = 10.dp)
        .border(1.dp, PanelBorder, RoundedCornerShape(16.dp)),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1427).copy(alpha = .97f)),
    shape = RoundedCornerShape(16.dp)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavItem(Icons.Default.Home, stringResource(R.string.nav_home), selectedTab == "home") { onTabSelected("home") }
        NavItem(Icons.AutoMirrored.Filled.TrendingUp, stringResource(R.string.nav_markets), selectedTab == "markets") { onTabSelected("markets") }
        NavItem(Icons.Default.PieChart, stringResource(R.string.nav_portfolio), selectedTab == "portfolio") { onTabSelected("portfolio") }
        NavItem(Icons.Default.Star, stringResource(R.string.nav_favorites), selectedTab == "favorites") { onTabSelected("favorites") }
        NavItem(Icons.Default.Person, stringResource(R.string.nav_profile), selectedTab == "profile") { onTabSelected("profile") }
    }
}

@Composable
private fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.clickable(onClick = onClick)
) {
    Icon(icon, label, tint = if (selected) Purple else Muted, modifier = Modifier.size(25.dp))
    Text(label, color = if (selected) Purple else Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
}

@Preview(showBackground = true)
@Composable
private fun BottomNavigationPreview() {
    var selectedTab by remember { mutableStateOf("home") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF020916))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        BottomNavigation(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
    }
}
