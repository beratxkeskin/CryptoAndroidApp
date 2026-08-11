package com.example.cryptoandroidapp.presentation.profile

import kotlinx.coroutines.flow.StateFlow

interface IProfileViewModel {
    val uiState: StateFlow<ProfileUiState>
    fun logout()
    fun updateDisplayName(displayName: String)
    fun sendPasswordResetEmail()
    fun clearMessage()
}
