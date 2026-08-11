package com.example.cryptoandroidapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cryptoandroidapp.domain.repository.IAuthRepository
import com.example.cryptoandroidapp.presentation.auth.LoginScreen
import com.example.cryptoandroidapp.presentation.auth.SignUpScreen
import com.example.cryptoandroidapp.presentation.home.HomeScreen
import com.example.cryptoandroidapp.presentation.home.HomeViewModel
import com.example.cryptoandroidapp.presentation.crypto_detail.CryptoDetailScreen
import com.example.cryptoandroidapp.ui.theme.CryptoAndroidAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var authRepository: IAuthRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()
        fetchFcmToken()

        setContent {
            CryptoAndroidAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    
                    // Bildirime tıklandığında gelen intent extra'sını kontrol ediyoruz
                    val notificationCoinId = intent?.getStringExtra("coinId")
                    androidx.compose.runtime.LaunchedEffect(notificationCoinId) {
                        notificationCoinId?.let { coinId ->
                            navController.navigate("detail/$coinId")
                        }
                    }

                    val startDestination = if (authRepository.isUserLoggedIn()) "home" else "login"
                    // Sekme seçimi oturumluk bir arayüz durumudur. Bunu SavedState'a
                    // yazmak, uygulama yeniden açıldığında kullanıcıyı eski bir alt
                    // ekrana (ör. Portföy) geri döndürüp ana akışın kaybolmuş gibi
                    // görünmesine neden oluyordu. Yeni uygulama oturumu her zaman
                    // gerçek ana ekrandan başlar.
                    var selectedHomeTab by remember { mutableStateOf("home") }

                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("login") {
                            LoginScreen(
                                onNavigateToSignUp = { navController.navigate("signup") },
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("signup") {
                            SignUpScreen(
                                onNavigateToLogin = { navController.navigate("login") },
                                onSignUpSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("signup") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            val homeViewModel: HomeViewModel = hiltViewModel()
                            HomeScreen(
                                userName = authRepository.getCurrentUser()?.displayName ?: "Kullanıcı",
                                viewModel = homeViewModel,
                                selectedTab = selectedHomeTab,
                                onTabSelected = { selectedHomeTab = it },
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onCoinClick = { coinId ->
                                    navController.navigate("detail/$coinId")
                                }
                            )
                        }
                        composable(
                            route = "detail/{coinId}",
                            deepLinks = listOf(
                                androidx.navigation.navDeepLink { uriPattern = "cryptoapp://detail/{coinId}" }
                            ),
                            arguments = listOf(navArgument("coinId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val coinId = backStackEntry.arguments?.getString("coinId") ?: "bitcoin"
                            CryptoDetailScreen(
                                coinId = coinId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun fetchFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "FCM token alımı başarısız", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM", "Mevcut Cihaz FCM Token: $token")
        }
    }
}
