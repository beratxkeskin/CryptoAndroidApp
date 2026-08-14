package com.example.cryptoandroidapp.common

import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Beklenmeyen hataları ve istisnaları (Exception) Firebase Crashlytics paneline loglayan yardımcı sınıf.
 */
object CrashlyticsLogger {

    private val crashlytics: FirebaseCrashlytics
        get() = FirebaseCrashlytics.getInstance()

    /**
     * Uygulama çökmeden yakalanan kritik bir hatayı (Non-fatal) Firebase'e raporlar.
     */
    fun logNonFatal(throwable: Throwable, customMessage: String? = null) {
        customMessage?.let { crashlytics.log(it) }
        crashlytics.recordException(throwable)
    }

    /**
     * Hata alan aktif kullanıcının ID'sini tanımlar (Hangi kullanıcının hata aldığını bulmak için).
     */
    fun setUser(userId: String) {
        crashlytics.setUserId(userId)
    }

    /**
     * Hata detayına özel anahtar-değer bilgisi ekler (Örn: "screen" -> "CryptoDetail").
     */
    fun setKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }
}