package com.example.cryptoandroidapp.domain.repository

import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {

    // E-posta ve şifre ile kayıt oluşturur
    fun signUp(email: String, password: String, displayName: String): Flow<Resource<UserEntity>>

    // E-posta ve şifre ile mevcut kullanıcıya giriş yaptırır
    fun login(email: String, password: String): Flow<Resource<UserEntity>>

    // Mevcut oturumu kapatır (Çıkış Yap)
    fun logout()

    // Kullanıcının o an giriş yapmış olup olmadığını döner
    fun isUserLoggedIn(): Boolean

    // Giriş yapmış kullanıcının saf domain bilgilerini döner
    fun getCurrentUser(): UserEntity?

    // Kullanıcı görünen adını günceller
    suspend fun updateDisplayName(displayName: String): Resource<Unit>

    // Şifre sıfırlama e-postası gönderir
    suspend fun sendPasswordResetEmail(): Resource<Unit>
}
