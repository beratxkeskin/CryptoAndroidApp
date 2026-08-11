package com.example.cryptoandroidapp.presentation.auth

import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.cryptoandroidapp.R
@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit,
    viewModel: ISignUpViewModel = hiltViewModel<SignUpViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SignUpScreenContent(
        state = state,
        onNavigateToLogin = onNavigateToLogin,
        onSignUpSuccess = {
            onSignUpSuccess()
            viewModel.resetState() // Navigasyon sonrasında state'i sıfırlıyoruz
        },
        onSignUp = { email, password, name ->
            viewModel.signUp(email, password, name)
        },
        onErrorShown = {
            viewModel.resetState() // Hata gösterildikten sonra hata durumunu temizliyoruz
        }
    )
}

@Composable
fun SignUpScreenContent(
    state: SignUpUiState,
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit,
    onSignUp: (String, String, String) -> Unit,
    onErrorShown: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var passwordConfirmVisible by remember { mutableStateOf(false) }
    var acceptTerms by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var termsError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            Toast.makeText(context, context.getString(R.string.success_register), Toast.LENGTH_SHORT).show()
            onSignUpSuccess()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            onErrorShown()
        }
    }

    val backgroundColor = Color(0xFF0F172A)
    val borderColor = Color(0xFF1E293B)
    val primaryColor = Color(0xFF4F46E5) // Button gradient start
    val secondaryColor = Color(0xFF7C3AED) // Button gradient end
    val textPrimary = Color.White
    val textSecondary = Color(0xFF94A3B8)
    val textFieldBackground = Color(0xFF0B1120).copy(alpha = 0.6f) // Darker, slightly transparent

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = primaryColor,
        unfocusedBorderColor = borderColor,
        focusedContainerColor = textFieldBackground,
        unfocusedContainerColor = textFieldBackground,
        focusedTextColor = textPrimary,
        unfocusedTextColor = textPrimary,
        cursorColor = primaryColor,
        focusedLeadingIconColor = textSecondary,
        unfocusedLeadingIconColor = textSecondary,
        focusedTrailingIconColor = textSecondary,
        unfocusedTrailingIconColor = textSecondary,
    )

    Box(
        modifier = Modifier.fillMaxSize().background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.login_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Arka planı biraz karartmak için yarı saydam siyah katman (Overlay/Scrim)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f)) // Push down to center below background texts

            Text(
                text = stringResource(R.string.register_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = stringResource(R.string.register_subtitle),
                fontSize = 13.sp,
                color = textSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // AD SOYAD
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                placeholder = { Text(stringResource(R.string.name_placeholder), color = textSecondary, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = stringResource(R.string.name_placeholder), modifier = Modifier.size(20.dp)) },
                isError = nameError != null,
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // E-POSTA
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                placeholder = { Text(stringResource(R.string.email_placeholder), color = textSecondary, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = stringResource(R.string.email_placeholder), modifier = Modifier.size(20.dp)) },
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // ŞİFRE
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                placeholder = { Text(stringResource(R.string.password_placeholder), color = textSecondary, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = stringResource(R.string.password_placeholder), modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // ŞİFRE TEKRARI
            OutlinedTextField(
                value = passwordConfirm,
                onValueChange = { passwordConfirm = it; passwordError = null },
                placeholder = { Text(stringResource(R.string.password_placeholder), color = textSecondary, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = stringResource(R.string.password_placeholder), modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    val image = if (passwordConfirmVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordConfirmVisible = !passwordConfirmVisible }) {
                        Icon(imageVector = image, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                },
                visualTransformation = if (passwordConfirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                singleLine = true
            )
            if (passwordError != null || emailError != null || nameError != null) {
                Text(
                    text = passwordError ?: emailError ?: nameError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // KULLANIM KOŞULLARI
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = acceptTerms,
                    onCheckedChange = { acceptTerms = it; termsError = null },
                    modifier = Modifier.offset(x = (-12).dp, y = (-12).dp), // Adjust checkbox padding
                    colors = CheckboxDefaults.colors(
                        checkedColor = primaryColor,
                        uncheckedColor = borderColor,
                        checkmarkColor = Color.White
                    )
                )
                Column(modifier = Modifier.offset(x = (-16).dp)) {
                    Text(
                        text = androidx.compose.ui.text.buildAnnotatedString {
                            val prefix = stringResource(R.string.terms_prefix)
                            val termsLink = stringResource(R.string.terms_link)
                            val andText = stringResource(R.string.terms_and)
                            val privacyLink = stringResource(R.string.privacy_link)
                            val suffix = stringResource(R.string.terms_suffix)
                            
                            append(prefix)
                            
                            val startTerms = length
                            append(termsLink)
                            addStyle(
                                style = androidx.compose.ui.text.SpanStyle(color = Color(0xFF818CF8)),
                                start = startTerms, end = length
                            )
                            
                            append(andText)
                            
                            val startPrivacy = length
                            append(privacyLink)
                            addStyle(
                                style = androidx.compose.ui.text.SpanStyle(color = Color(0xFF818CF8)),
                                start = startPrivacy, end = length
                            )
                            
                            append(suffix)
                        },
                        color = textSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    if (termsError != null) {
                        Text(
                            text = termsError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // KAYIT OL BUTONU
            Button(
                onClick = {
                    val emailPattern = android.util.Patterns.EMAIL_ADDRESS
                    if (name.isBlank()) nameError = context.getString(R.string.err_name_empty)
                    else if (email.isBlank() || !emailPattern.matcher(email).matches()) emailError = context.getString(R.string.err_email_invalid)
                    else if (password.isBlank() || password.length < 6) passwordError = context.getString(R.string.err_password_short)
                    else if (password != passwordConfirm) passwordError = context.getString(R.string.err_password_mismatch)
                    else if (!acceptTerms) termsError = context.getString(R.string.err_terms_not_accepted)
                    else {
                        onSignUp(email, password, name)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(primaryColor, secondaryColor)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.btn_register), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // VEYA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = borderColor)
                Text(
                    text = stringResource(R.string.or_text),
                    color = textSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = borderColor)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // GOOGLE
            OutlinedButton(
                onClick = { /* Google SignUp */ },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, borderColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFF0B1120).copy(alpha = 0.4f),
                    contentColor = textPrimary
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = "Google",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = stringResource(R.string.continue_with_google), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // APPLE
            OutlinedButton(
                onClick = { /* Apple SignUp */ },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, borderColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFF0B1120).copy(alpha = 0.4f),
                    contentColor = textPrimary
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_apple_filled),
                    contentDescription = "Apple",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = stringResource(R.string.continue_with_apple), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.weight(1f)) // Push bottom text to the bottom

            // GİRİŞ EKRANINA YÖNLENDİRME METNİ
            TextButton(onClick = onNavigateToLogin, modifier = Modifier.padding(bottom = 32.dp)) {
                Text(text = stringResource(R.string.already_have_account), color = textSecondary, fontSize = 13.sp)
                Text(text = stringResource(R.string.btn_login), color = Color(0xFF818CF8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(color = primaryColor)
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    com.example.cryptoandroidapp.ui.theme.CryptoAndroidAppTheme {
        SignUpScreenContent(
            state = SignUpUiState(),
            onNavigateToLogin = {},
            onSignUpSuccess = {},
            onSignUp = { _, _, _ -> },
            onErrorShown = {}
        )
    }
}

