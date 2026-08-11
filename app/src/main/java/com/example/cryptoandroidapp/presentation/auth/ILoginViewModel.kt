package com.example.cryptoandroidapp.presentation.auth

import kotlinx.coroutines.flow.StateFlow

interface ILoginViewModel {
    val state: StateFlow<LoginUiState>
    fun login(email: String, password: String)
    fun resetState()
}
