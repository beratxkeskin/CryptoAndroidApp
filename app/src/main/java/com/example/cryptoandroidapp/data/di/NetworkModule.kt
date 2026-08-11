package com.example.cryptoandroidapp.data.di

import com.example.cryptoandroidapp.data.remote.CoinGeckoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {  //OkHttpClient, uygulamanın internete bağlanırken kullandığı ana ağ aracıdır.
        // Interface'i anonim nesne (object : Dns) olarak türetiyoruz
        val ipv4Dns = object : okhttp3.Dns {
            override fun lookup(hostname: String): List<java.net.InetAddress> {
                return okhttp3.Dns.SYSTEM.lookup(hostname).filterIsInstance<java.net.Inet4Address>()
            }
        }

        return OkHttpClient.Builder()
            .dns(ipv4Dns)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true // API'den gelen ve bizim modelimizde olmayan ekstra alanları yok sayar (çökmeyi önler).
            coerceInputValues = true // JSON'daki eksik veya null değerleri Kotlin'deki varsayılan değerlere güvenle eşitler.
        }
        return Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideCoinGeckoApi(retrofit: Retrofit): CoinGeckoApi {
        return retrofit.create(CoinGeckoApi::class.java)
    }
}