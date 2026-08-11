package com.example.cryptoandroidapp.domain.use_case.auth

import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.UserEntity
import com.example.cryptoandroidapp.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<Resource<UserEntity>> {
        return authRepository.login(email, password)
    }
}