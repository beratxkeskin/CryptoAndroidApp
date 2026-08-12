package com.example.cryptoandroidapp.data.repository

import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.UserEntity
import com.example.cryptoandroidapp.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Firebase Auth sunucusuna erişmeden Unit Testlerde sahte giriş/kayıt işlemleri yapmamızı sağlayan Fake Repository.
 */
class FakeAuthRepository : IAuthRepository {

    // Test senaryolarında oturum açmış varsayılan kullanıcı (JVM Çakışmasını önlemek için mockUser adı verildi)
    var mockUser: UserEntity? = UserEntity("user_123", "test@crypto.com", "Berat Keskin")

    // Test esnasında hata simülasyonu yapmak için kullanılan bayraklar
    var shouldReturnError = false
    var errorMessage = "Kimlik doğrulama hatası"

    override fun login(email: String, password: String): Flow<Resource<UserEntity>> {
        if (shouldReturnError) {
            return flowOf(Resource.Error(errorMessage))
        }
        val user = UserEntity("user_123", email, "Berat Keskin")
        mockUser = user
        return flowOf(Resource.Success(user))
    }

    override fun signUp(email: String, password: String, displayName: String): Flow<Resource<UserEntity>> {
        if (shouldReturnError) {
            return flowOf(Resource.Error(errorMessage))
        }
        val user = UserEntity("user_new_123", email, displayName)
        mockUser = user
        return flowOf(Resource.Success(user))
    }

    override fun logout() {
        mockUser = null
    }

    override fun isUserLoggedIn(): Boolean {
        return mockUser != null
    }

    override fun getCurrentUser(): UserEntity? {
        return mockUser
    }

    override suspend fun updateDisplayName(displayName: String): Resource<Unit> {
        if (shouldReturnError) return Resource.Error(errorMessage)
        mockUser = mockUser?.copy(displayName = displayName)
        return Resource.Success(Unit)
    }

    override suspend fun sendPasswordResetEmail(): Resource<Unit> {
        if (shouldReturnError) return Resource.Error(errorMessage)
        return Resource.Success(Unit)
    }
}