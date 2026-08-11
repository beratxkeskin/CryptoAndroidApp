package com.example.cryptoandroidapp.presentation.auth

import kotlinx.coroutines.flow.StateFlow

interface ISignUpViewModel {
    val state: StateFlow<SignUpUiState>
    fun signUp(email: String, password: String, displayName: String)
    fun resetState()
}
