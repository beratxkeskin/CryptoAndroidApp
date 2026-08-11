package com.example.cryptoandroidapp.domain.use_case.auth

import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.UserEntity
import com.example.cryptoandroidapp.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    // operator fun invoke, bu sınıfın bir fonksiyon gibi çağrılabilmesini sağlar (örn: signUpUseCase(email, pass))
    operator fun invoke(email: String, password: String, displayName: String): Flow<Resource<UserEntity>> {
        return authRepository.signUp(email, password, displayName)
    }
}