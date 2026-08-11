package com.example.cryptoandroidapp.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.presentation.home.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ProfileScreen(onLogout: () -> Unit, modifier: Modifier = Modifier, viewModel: IProfileViewModel = hiltViewModel<ProfileViewModel>()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var nameDialog by remember { mutableStateOf(false) }
    var infoDialog by remember { mutableStateOf<String?>(null) }
    var languageDialog by remember { mutableStateOf(false) }
    var notificationsOn by remember { mutableStateOf(true) }
    var darkThemeOn by remember { mutableStateOf(true) }

    state.message?.let { message ->
        AlertDialog(onDismissRequest = viewModel::clearMessage, confirmButton = { TextButton(viewModel::clearMessage) { Text("Tamam") } }, title = { Text("Profil") }, text = { Text(message) })
    }
    if (nameDialog) NameDialog(state.userName, onDismiss = { nameDialog = false }, onSave = { viewModel.updateDisplayName(it); nameDialog = false })
    if (languageDialog) LanguageDialog { languageDialog = false }
    infoDialog?.let { title -> InfoDialog(title) { infoDialog = null }
    }

    Column(
        modifier = modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp).padding(top = 20.dp, bottom = 96.dp)
    ) {
        ProfileHeader()
        Spacer(Modifier.height(22.dp))
        HeroCard(state, onEdit = { nameDialog = true })
        Spacer(Modifier.height(20.dp))
        SettingsGroup("Hesap Yönetimi") {
            SettingsRow(Icons.Default.Person, "Kişisel Bilgiler", "Profil bilgilerini görüntüle ve güncelle.", onClick = { nameDialog = true })
            SettingsRow(Icons.Default.Security, "Güvenlik", "Şifre sıfırlama bağlantısı gönder.", onClick = viewModel::sendPasswordResetEmail)
            SettingsRow(Icons.Default.Notifications, "Bildirim Tercihleri", "Fiyat uyarıları ve bildirim ayarları.", trailing = { Switch(notificationsOn, { notificationsOn = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Purple)) })
            SettingsRow(Icons.Default.History, "İşlem Geçmişi", "Geçmiş işlemlerini görüntüle.", onClick = { infoDialog = "İşlem Geçmişi" }, showDivider = false)
        }
        Spacer(Modifier.height(16.dp))
        SettingsGroup("Uygulama") {
            SettingsRow(Icons.Default.Palette, "Tema", if (darkThemeOn) "Koyu tema aktif" else "Açık tema aktif", trailing = { Switch(darkThemeOn, { darkThemeOn = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Purple)) })
            SettingsRow(Icons.Default.Language, "Dil", "Türkçe / English", onClick = { languageDialog = true })
            SettingsRow(Icons.Default.HelpOutline, "Yardım & Destek", "Sık sorulan sorular ve destek.", onClick = { infoDialog = "Yardım & Destek" })
            SettingsRow(Icons.Default.Info, "Hakkında", "Uygulama bilgileri ve lisanslar.", value = "v1.0.0", onClick = { infoDialog = "Hakkında" }, showDivider = false)
        }
        Spacer(Modifier.height(20.dp))
        OutlinedButton(
            onClick = { viewModel.logout(); onLogout() }, modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Red), border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Red.copy(alpha = .45f)))
        ) { Icon(Icons.Default.ExitToApp, null); Spacer(Modifier.width(10.dp)); Text("Çıkış Yap", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
    }
}

@Composable private fun ProfileHeader() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Column { Text("Profilim", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold); Text("Hesabını yönet, ayarlarını kişiselleştir.", color = Muted, fontSize = 14.sp) }
        Row { HeaderIcon(Icons.Default.Settings); Spacer(Modifier.width(8.dp)); HeaderIcon(Icons.Default.Notifications) }
    }
}
@Composable private fun HeaderIcon(icon: androidx.compose.ui.graphics.vector.ImageVector) = Surface(shape = CircleShape, color = PanelColor, border = androidx.compose.foundation.BorderStroke(1.dp, PanelBorder), modifier = Modifier.size(46.dp)) { Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = Color(0xFFB9B3D5)) } }

@Composable private fun HeroCard(state: ProfileUiState, onEdit: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = PanelColor.copy(alpha = .96f)), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder.copy(alpha = .65f), RoundedCornerShape(20.dp))) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    Image(painterResource(R.drawable.profile_avatar_astronaut), null, Modifier.size(86.dp).clip(CircleShape).border(2.dp, Purple, CircleShape), contentScale = ContentScale.Crop)
                    Surface(onClick = onEdit, shape = CircleShape, color = Purple, modifier = Modifier.size(30.dp).align(Alignment.BottomEnd)) { Icon(Icons.Default.Edit, null, Modifier.padding(7.dp), tint = Color.White) }
                }
                Spacer(Modifier.width(16.dp)); Column(Modifier.weight(1f)) {
                    Text(state.userName, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text(state.userEmail, color = Muted, fontSize = 13.sp)
                }
                Icon(Icons.Default.ChevronRight, null, tint = Color.White)
            }
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth()) {
                Stat(Modifier.weight(1f), Icons.Default.AccountBalanceWallet, "Portföy Değeri", state.portfolioValue, state.portfolioChange, state.isPortfolioPositive)
                Stat(Modifier.weight(1f), Icons.Default.Star, "Favori Varlık", state.favoritesCount.toString(), "Takip ediliyor")
                Stat(Modifier.weight(1f), Icons.Default.CalendarToday, "Üyelik Başlangıcı", state.memberSince, state.membershipAge)
            }
        }
    }
}
@Composable private fun Stat(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, caption: String, positive: Boolean = true) = Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) { Surface(shape = CircleShape, color = Color.White.copy(alpha = .06f), modifier = Modifier.size(32.dp)) { Icon(icon, null, Modifier.padding(8.dp), tint = Color(0xFFACA8D1)) }; Spacer(Modifier.height(8.dp)); Text(label, color = Muted, fontSize = 10.sp); Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1); Text(caption, color = if (positive) Green else Red, fontSize = 10.sp, maxLines = 1) }

@Composable private fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) = Card(colors = CardDefaults.cardColors(containerColor = PanelColor.copy(alpha = .94f)), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder.copy(alpha = .6f), RoundedCornerShape(20.dp))) { Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) { Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 6.dp)); content() } }
@Composable private fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, value: String? = null, trailing: @Composable (() -> Unit)? = null, onClick: () -> Unit = {}, showDivider: Boolean = true) { Column { Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) { Surface(shape = CircleShape, color = Color.White.copy(alpha = .055f), modifier = Modifier.size(42.dp)) { Icon(icon, null, Modifier.padding(10.dp), tint = Color(0xFFB8B5DA)) }; Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium); Text(subtitle, color = Muted, fontSize = 12.sp) }; if (value != null) Text(value, color = Muted, fontSize = 12.sp); if (trailing != null) trailing() else Icon(Icons.Default.ChevronRight, null, tint = Color.White) }; if (showDivider) HorizontalDivider(color = PanelBorder.copy(alpha = .45f)) } }

@Composable private fun NameDialog(currentName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) { var name by remember { mutableStateOf(currentName) }; AlertDialog(onDismissRequest = onDismiss, title = { Text("Kişisel Bilgiler") }, text = { OutlinedTextField(name, { name = it }, label = { Text("Ad soyad") }, singleLine = true) }, dismissButton = { TextButton(onDismiss) { Text("İptal") } }, confirmButton = { TextButton({ onSave(name) }) { Text("Kaydet") } }) }
@Composable private fun LanguageDialog(onDismiss: () -> Unit) { AlertDialog(onDismissRequest = onDismiss, title = { Text("Uygulama dili") }, text = { Text("Tercih ettiğiniz dili seçin.") }, dismissButton = { TextButton({ androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(androidx.core.os.LocaleListCompat.forLanguageTags("tr")); onDismiss() }) { Text("Türkçe") } }, confirmButton = { TextButton({ androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(androidx.core.os.LocaleListCompat.forLanguageTags("en")); onDismiss() }) { Text("English") } }) }
@Composable private fun InfoDialog(title: String, onDismiss: () -> Unit) { val text = when (title) { "Yardım & Destek" -> "Sık sorulan sorular için destek merkezimiz yakında burada olacak."; "Dil" -> "Dil seçimi uygulama ayarlarından Türkçe veya English olarak değiştirilebilir."; "İşlem Geçmişi" -> "İşlem geçmişi altyapısı henüz bağlanmadı."; else -> "$title ekranı yakında kullanıma açılacak." }; AlertDialog(onDismissRequest = onDismiss, title = { Text(title) }, text = { Text(text) }, confirmButton = { TextButton(onDismiss) { Text("Tamam") } }) }

private class MockProfileViewModel(state: ProfileUiState) : IProfileViewModel { override val uiState: StateFlow<ProfileUiState> = MutableStateFlow(state); override fun logout() {}; override fun updateDisplayName(displayName: String) {}; override fun sendPasswordResetEmail() {}; override fun clearMessage() {} }
