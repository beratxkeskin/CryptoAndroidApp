package com.example.cryptoandroidapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.use_case.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel(), ILoginViewModel {

    private val _state = MutableStateFlow(LoginUiState())
    override val state: StateFlow<LoginUiState> = _state.asStateFlow()

    override fun login(email: String, password: String) {
        viewModelScope.launch { //ViewModel yaşadığı sürece çalışan coroutine alanıdır
            loginUseCase(email, password).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = LoginUiState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = LoginUiState(isSuccess = true)
                    }
                    is Resource.Error -> {
                        _state.value = LoginUiState(error = result.message)
                    }
                }
            }
        }
    }

    override fun resetState() {
        _state.value = LoginUiState()
    }
}