package com.example.cryptoandroidapp.data.repository

import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.UserEntity
import com.example.cryptoandroidapp.domain.repository.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : IAuthRepository {

    override fun signUp(email: String, password: String, displayName: String): Flow<Resource<UserEntity>> = flow {
        emit(Resource.Loading())
        try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()
            val user = result.user
            if (user != null) {
                // Firebase profilini isim-soyisim ile güncelle
                val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                    this.displayName = displayName
                }
                user.updateProfile(profileUpdates).await()

                emit(Resource.Success(user.toUserEntity()))
            } else {
                emit(Resource.Error("Kullanıcı oluşturuldu ancak bilgiler alınamadı."))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Kayıt işlemi sırasında bir hata oluştu."))
        }
    }

    override fun login(email: String, password: String): Flow<Resource<UserEntity>> = flow {
        emit(Resource.Loading())
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                emit(Resource.Success(user.toUserEntity()))
            } else {
                emit(Resource.Error("Giriş yapıldı ancak kullanıcı bulunamadı."))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Giriş yapılırken bir hata oluştu."))
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun getCurrentUser(): UserEntity? {
        return firebaseAuth.currentUser?.toUserEntity()
    }

    override suspend fun updateDisplayName(displayName: String): Resource<Unit> = try {
        val user = firebaseAuth.currentUser ?: return Resource.Error("Oturum açmış kullanıcı bulunamadı.")
        user.updateProfile(com.google.firebase.auth.userProfileChangeRequest { this.displayName = displayName }).await()
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "İsim güncellenemedi.")
    }

    override suspend fun sendPasswordResetEmail(): Resource<Unit> = try {
        val email = firebaseAuth.currentUser?.email ?: return Resource.Error("Bu hesap için e-posta adresi bulunamadı.")
        firebaseAuth.sendPasswordResetEmail(email).await()
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Şifre sıfırlama e-postası gönderilemedi.")
    }

    private fun FirebaseUser.toUserEntity(): UserEntity = UserEntity(
        uid = uid,
        email = email.orEmpty(),
        displayName = displayName.orEmpty(),
        creationTimestamp = metadata?.creationTimestamp ?: System.currentTimeMillis()
    )
}
