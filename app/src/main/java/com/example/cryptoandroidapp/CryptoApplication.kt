package com.example.cryptoandroidapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CryptoApplication : Application() {
    // Uygulama başladığında ilk çalışacak sınıf burasıdır.
}