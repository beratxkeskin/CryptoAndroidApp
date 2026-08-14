package com.example.cryptoandroidapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.UserPortfolioAsset
import com.example.cryptoandroidapp.domain.repository.IAuthRepository
import com.example.cryptoandroidapp.domain.repository.ICryptoRepository
import com.example.cryptoandroidapp.domain.repository.IFavoritesRepository
import com.example.cryptoandroidapp.domain.repository.IPortfolioRepository
import com.example.cryptoandroidapp.domain.use_case.portfolio.CalculatePortfolioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ProfileUiState(
    val userName: String = "Kullanıcı",
    val userEmail: String = "",
    val portfolioValue: String = "$0.00",
    val portfolioChange: String = "%0.00",
    val isPortfolioPositive: Boolean = true,
    val favoritesCount: Int = 0,
    val memberSince: String = "—",
    val membershipAge: String = "",
    val message: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val portfolioRepository: IPortfolioRepository,
    private val favoritesRepository: IFavoritesRepository,
    private val cryptoRepository: ICryptoRepository,
    private val calculatePortfolioUseCase: CalculatePortfolioUseCase
) : ViewModel(), IProfileViewModel {
    private val _uiState = MutableStateFlow(ProfileUiState())
    override val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init { loadProfile() }

    private fun loadProfile() {
        val user = authRepository.getCurrentUser() ?: return
        val createdAt = user.creationTimestamp
        val currentLocale = Locale.getDefault()
        _uiState.value = _uiState.value.copy(
            userName = user.displayName.ifBlank { if (currentLocale.language == "tr") "Kullanıcı" else "User" },
            userEmail = user.email,
            memberSince = SimpleDateFormat("dd MMM yyyy", currentLocale).format(Date(createdAt)),
            membershipAge = membershipAge(createdAt)
        )
        viewModelScope.launch {
            combine(
                portfolioRepository.getUserPortfolio(user.uid),
                favoritesRepository.getUserFavorites(user.uid),
                cryptoRepository.getCryptoList()
            ) { portfolio, favorites, coins -> Triple(portfolio, favorites, coins) }
                .collect { (portfolioResult, favoritesResult, coinsResult) ->
                    val rawAssets = (portfolioResult as? Resource.Success)?.data.orEmpty()
                    val coins = (coinsResult as? Resource.Success)?.data.orEmpty()

                    val calcResult = calculatePortfolioUseCase(rawAssets, coins)

                    _uiState.value = _uiState.value.copy(
                        portfolioValue = String.format(Locale.US, "$%,.2f", calcResult.totalValueUsd),
                        portfolioChange = String.format(Locale.US, "%+.2f%%", calcResult.totalChangePercent),
                        isPortfolioPositive = calcResult.isPositive,
                        favoritesCount = (favoritesResult as? Resource.Success)?.data?.size ?: 0
                    )
                }
        }
    }

    override fun updateDisplayName(displayName: String) {
        if (displayName.isBlank()) return
        val isTurkish = Locale.getDefault().language == "tr"
        viewModelScope.launch {
            when (val result = authRepository.updateDisplayName(displayName.trim())) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    userName = displayName.trim(),
                    message = if (isTurkish) "Profil adı güncellendi." else "Profile name updated."
                )
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    message = result.message ?: if (isTurkish) "İsim güncellenemedi." else "Name update failed."
                )
                else -> Unit
            }
        }
    }

    override fun sendPasswordResetEmail() {
        val isTurkish = Locale.getDefault().language == "tr"
        viewModelScope.launch {
            _uiState.value = when (val result = authRepository.sendPasswordResetEmail()) {
                is Resource.Success -> _uiState.value.copy(
                    message = if (isTurkish) "Şifre sıfırlama bağlantısı e-posta adresinize gönderildi." else "Password reset link sent to your email."
                )
                is Resource.Error -> _uiState.value.copy(
                    message = result.message ?: if (isTurkish) "E-posta gönderilemedi." else "Email failed to send."
                )
                else -> _uiState.value
            }
        }
    }

    override fun clearMessage() { _uiState.value = _uiState.value.copy(message = null) }
    override fun logout() = authRepository.logout()

    private fun membershipAge(timestamp: Long): String {
        val days = ((System.currentTimeMillis() - timestamp) / 86_400_000L).coerceAtLeast(0)
        val isTurkish = Locale.getDefault().language == "tr"
        return when {
            days >= 365 -> if (isTurkish) "${days / 365} yıl" else "${days / 365} yr"
            days >= 30 -> if (isTurkish) "${days / 30} ay" else "${days / 30} mo"
            else -> if (isTurkish) "$days gün" else "$days d"
        }
    }
}
