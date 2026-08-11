package com.example.cryptoandroidapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.use_case.auth.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel(), ISignUpViewModel {

    // Sadece ViewModel içinden güncellenebilen özel (private) durum akışı
    private val _state = MutableStateFlow(SignUpUiState())
    // Ekranın (UI) sadece okuyabileceği, değiştiremeyeceği salt-okunur durum akışı
    override val state: StateFlow<SignUpUiState> = _state.asStateFlow()

    override fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            signUpUseCase(email, password, displayName).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = SignUpUiState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = SignUpUiState(isSuccess = true)
                    }
                    is Resource.Error -> {
                        _state.value = SignUpUiState(error = result.message)
                    }
                }
            }
        }
    }

    override fun resetState() {
        _state.value = SignUpUiState()
    }
}