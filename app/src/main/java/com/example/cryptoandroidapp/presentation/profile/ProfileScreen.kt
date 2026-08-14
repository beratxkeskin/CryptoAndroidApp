package com.example.cryptoandroidapp.presentation.profile

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.presentation.home.*
import com.example.cryptoandroidapp.presentation.home.components.HeaderIconButton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: IProfileViewModel = hiltViewModel<ProfileViewModel>()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var nameDialog by remember { mutableStateOf(false) }
    var infoDialog by remember { mutableStateOf<String?>(null) }
    var languageDialog by remember { mutableStateOf(false) }
    var notificationsOn by remember { mutableStateOf(true) }
    var darkThemeOn by remember { mutableStateOf(true) }

    state.message?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::clearMessage,
            confirmButton = { TextButton(viewModel::clearMessage) { Text(stringResource(R.string.btn_cancel)) } },
            title = { Text(stringResource(R.string.nav_profile)) },
            text = { Text(message) }
        )
    }

    if (nameDialog) NameDialog(state.userName, onDismiss = { nameDialog = false }, onSave = { viewModel.updateDisplayName(it); nameDialog = false })
    if (languageDialog) LanguageDialog { languageDialog = false }
    infoDialog?.let { title -> InfoDialog(title) { infoDialog = null } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp, bottom = 140.dp)
    ) {
        ProfileHeader()
        Spacer(Modifier.height(22.dp))
        HeroCard(state, onEdit = { nameDialog = true })
        Spacer(Modifier.height(20.dp))
        SettingsGroup(stringResource(R.string.tab_profile_title)) {
            SettingsRow(
                icon = Icons.Default.Person,
                title = stringResource(R.string.name_placeholder),
                subtitle = stringResource(R.string.profile_info_sub),
                onClick = { nameDialog = true }
            )
            SettingsRow(
                icon = Icons.Default.Security,
                title = stringResource(R.string.security),
                subtitle = stringResource(R.string.forgot_password),
                onClick = viewModel::sendPasswordResetEmail
            )
            SettingsRow(
                icon = Icons.Default.Notifications,
                title = stringResource(R.string.detail_notifications_desc),
                subtitle = stringResource(R.string.notifications_sub),
                trailing = { Switch(notificationsOn, { notificationsOn = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Purple)) }
            )
            SettingsRow(
                icon = Icons.Default.History,
                title = stringResource(R.string.transaction_history),
                subtitle = stringResource(R.string.transaction_history_sub),
                onClick = { infoDialog = "transaction_history" },
                showDivider = false
            )
        }
        Spacer(Modifier.height(16.dp))
        SettingsGroup(stringResource(R.string.app_settings)) {
            SettingsRow(
                icon = Icons.Default.Palette,
                title = stringResource(R.string.theme),
                subtitle = stringResource(R.string.theme_dark_active),
                trailing = { Switch(checked = true, onCheckedChange = null, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Purple)) }
            )
            SettingsRow(
                icon = Icons.Default.Language,
                title = stringResource(R.string.app_language_title),
                subtitle = "Türkçe / English",
                onClick = { languageDialog = true }
            )
            SettingsRow(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                title = stringResource(R.string.help_support),
                subtitle = stringResource(R.string.help_support_sub),
                onClick = { infoDialog = "help_support" }
            )
            SettingsRow(
                icon = Icons.Default.Info,
                title = stringResource(R.string.about),
                subtitle = stringResource(R.string.about_sub),
                value = "v1.0.0",
                onClick = { infoDialog = "about" },
                showDivider = false
            )
        }
        Spacer(Modifier.height(20.dp))
        OutlinedButton(
            onClick = { viewModel.logout(); onLogout() },
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Red),
            border = BorderStroke(1.dp, Red.copy(alpha = .45f))
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
            Spacer(Modifier.width(10.dp))
            Text(stringResource(R.string.btn_logout), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun ProfileHeader() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.nav_profile), color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.tab_profile_desc), color = Muted, fontSize = 13.sp)
        }
    }
}

@Composable
private fun HeroCard(state: ProfileUiState, onEdit: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PanelColor.copy(alpha = .96f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder.copy(alpha = .65f), RoundedCornerShape(20.dp))
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    Image(painterResource(R.drawable.profile_avatar_astronaut), null, Modifier.size(86.dp).clip(CircleShape).border(2.dp, Purple, CircleShape), contentScale = ContentScale.Crop)
                    Surface(onClick = onEdit, shape = CircleShape, color = Purple, modifier = Modifier.size(30.dp).align(Alignment.BottomEnd)) { Icon(Icons.Default.Edit, null, Modifier.padding(7.dp), tint = Color.White) }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(state.userName, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text(state.userEmail, color = Muted, fontSize = 13.sp)
                }
                Icon(Icons.Default.ChevronRight, null, tint = Color.White)
            }
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth()) {
                Stat(Modifier.weight(1f), Icons.Default.AccountBalanceWallet, stringResource(R.string.portfolio_title), state.portfolioValue, state.portfolioChange, state.isPortfolioPositive)
                Stat(Modifier.weight(1f), Icons.Default.Star, stringResource(R.string.nav_favorites), state.favoritesCount.toString(), stringResource(R.string.following))
                Stat(Modifier.weight(1f), Icons.Default.CalendarToday, stringResource(R.string.member_since), state.memberSince, state.membershipAge)
            }
        }
    }
}

@Composable
private fun Stat(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    caption: String,
    positive: Boolean = true
) = Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    Surface(shape = CircleShape, color = Color.White.copy(alpha = .06f), modifier = Modifier.size(32.dp)) {
        Icon(icon, null, Modifier.padding(8.dp), tint = Color(0xFFACA8D1))
    }
    Spacer(Modifier.height(6.dp))
    Box(modifier = Modifier.height(28.dp), contentAlignment = Alignment.Center) {
        Text(
            text = label,
            color = Muted,
            fontSize = 10.sp,
            lineHeight = 11.sp,
            textAlign = TextAlign.Center
        )
    }
    Spacer(Modifier.height(4.dp))
    Text(
        text = value,
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(2.dp))
    Text(
        text = caption,
        color = if (positive) Green else Red,
        fontSize = 10.sp,
        maxLines = 1,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) = Card(colors = CardDefaults.cardColors(containerColor = PanelColor.copy(alpha = .94f)), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder.copy(alpha = .6f), RoundedCornerShape(20.dp))) { Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) { Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 6.dp)); content() } }

@Composable
private fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, value: String? = null, trailing: @Composable (() -> Unit)? = null, onClick: () -> Unit = {}, showDivider: Boolean = true) { Column { Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) { Surface(shape = CircleShape, color = Color.White.copy(alpha = .055f), modifier = Modifier.size(42.dp)) { Icon(icon, null, Modifier.padding(10.dp), tint = Color(0xFFB8B5DA)) }; Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium); Text(subtitle, color = Muted, fontSize = 12.sp) }; if (value != null) Text(value, color = Muted, fontSize = 12.sp); if (trailing != null) trailing() else Icon(Icons.Default.ChevronRight, null, tint = Color.White) }; if (showDivider) HorizontalDivider(color = PanelBorder.copy(alpha = .45f)) } }

@Composable
private fun NameDialog(currentName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) { var name by remember { mutableStateOf(currentName) }; AlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.name_placeholder)) }, text = { OutlinedTextField(name, { name = it }, label = { Text(stringResource(R.string.name_placeholder)) }, singleLine = true) }, dismissButton = { TextButton(onDismiss) { Text(stringResource(R.string.btn_cancel)) } }, confirmButton = { TextButton({ onSave(name) }) { Text(stringResource(R.string.btn_save)) } }) }

@Composable
private fun LanguageDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.app_language_title)) },
        text = { Text(stringResource(R.string.app_language_subtitle)) },
        dismissButton = {
            TextButton(onClick = {
                val appLocales = LocaleListCompat.forLanguageTags("tr")
                AppCompatDelegate.setApplicationLocales(appLocales)
                (context as? Activity)?.recreate()
                onDismiss()
            }) {
                Text("Türkçe")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val appLocales = LocaleListCompat.forLanguageTags("en")
                AppCompatDelegate.setApplicationLocales(appLocales)
                (context as? Activity)?.recreate()
                onDismiss()
            }) {
                Text("English")
            }
        }
    )
}

@Composable
private fun InfoDialog(titleKey: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.about)) },
        text = { Text(titleKey) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_save))
            }
        }
    )
}
