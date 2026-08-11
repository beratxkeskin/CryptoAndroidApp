package com.example.cryptoandroidapp.presentation.auth

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
//LoginUiState bir bilgi kutusudur. Bu nedenle data class kullanılmıştır.