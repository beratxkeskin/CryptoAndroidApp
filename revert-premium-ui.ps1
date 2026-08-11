$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $MyInvocation.MyCommand.Path

function Restore-Text([string]$path, [string]$from, [string]$to) {
    $content = [System.IO.File]::ReadAllText($path)
    if (-not $content.Contains($from)) {
        throw "Beklenen tasarım değişikliği bulunamadı: $path"
    }
    [System.IO.File]::WriteAllText($path, $content.Replace($from, $to), [System.Text.UTF8Encoding]::new($false))
}

$home = Join-Path $root 'app\src\main\java\com\example\cryptoandroidapp\presentation\home\HomeScreen.kt'
$commonUi = Join-Path $root 'app\src\main\java\com\example\cryptoandroidapp\presentation\home\components\CommonUI.kt'
$detail = Join-Path $root 'app\src\main\java\com\example\cryptoandroidapp\presentation\crypto_detail\CryptoDetailScreen.kt'

Restore-Text $home @'
import com.example.cryptoandroidapp.presentation.home.components.AssetsSection
import com.example.cryptoandroidapp.presentation.home.components.AppBackground
import com.example.cryptoandroidapp.presentation.home.components.AppBackgroundMood
'@ @'
import com.example.cryptoandroidapp.presentation.home.components.AssetsSection
'@

Restore-Text $home @'
    Box(modifier = modifier.fillMaxSize().background(Background)) {
        AppBackground(
            mood = when (selectedTab) {
                "markets" -> AppBackgroundMood.Markets
                "portfolio" -> AppBackgroundMood.Portfolio
                "favorites" -> AppBackgroundMood.Favorites
                "profile" -> AppBackgroundMood.Profile
                else -> AppBackgroundMood.Home
            }
        )
        StarField()
'@ @'
    Box(modifier = modifier.fillMaxSize().background(Background)) {
        StarField()
'@

Restore-Text $commonUi @'
import androidx.compose.foundation.background
import androidx.compose.foundation.border
'@ @'
import androidx.compose.foundation.border
'@

Restore-Text $commonUi @'
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
'@ @'
import androidx.compose.ui.graphics.Color
'@

Restore-Text $commonUi @'
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PanelColor.copy(alpha = 0.98f), PanelColor.copy(alpha = 0.88f))
                    )
                )
                .padding(12.dp),
            content = content
        )
'@ @'
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(12.dp), content = content)
'@

Restore-Text $detail @'
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
'@ @'
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
'@

Restore-Text $detail @'
    Box(
        modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DetailPurple.copy(alpha = 0.10f), DetailBackground, DetailBackground)
                )
            )
            .statusBarsPadding()
    ) {
'@ @'
    Box(modifier.fillMaxSize().background(DetailBackground).statusBarsPadding()) {
'@

Remove-Item -LiteralPath (Join-Path $root 'app\src\main\java\com\example\cryptoandroidapp\presentation\home\components\AppBackground.kt') -Force
Remove-Item -LiteralPath (Join-Path $root 'app\src\main\res\drawable\portfolio_orbit_ambient.png') -Force
Write-Host 'Premium UI değişiklikleri geri alındı.'
