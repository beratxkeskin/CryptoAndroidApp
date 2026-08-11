package com.example.cryptoandroidapp.domain.model

/**
 * Saf Domain Kullanıcı Modeli.
 * Domain katmanının Firebase SDK'sına (FirebaseUser) bağımlı olmasını önler.
 */
data class UserEntity(
    val uid: String,
    val email: String,
    val displayName: String,
    val creationTimestamp: Long = System.currentTimeMillis()
)
