package com.example.cryptoandroidapp.presentation.auth

data class SignUpUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)