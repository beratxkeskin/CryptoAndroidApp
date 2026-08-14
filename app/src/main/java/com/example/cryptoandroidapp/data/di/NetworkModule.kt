package com.example.cryptoandroidapp.data.di

import com.example.cryptoandroidapp.data.remote.CoinGeckoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.ConnectionSpec
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
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
            level = HttpLoggingInterceptor.Level.HEADERS
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val ipv4Dns = object : okhttp3.Dns {
            override fun lookup(hostname: String): List<java.net.InetAddress> {
                return try {
                    val addresses = okhttp3.Dns.SYSTEM.lookup(hostname)
                    val ipv4 = addresses.filterIsInstance<java.net.Inet4Address>()
                    if (ipv4.isNotEmpty()) ipv4 else addresses
                } catch (e: Exception) {
                    okhttp3.Dns.SYSTEM.lookup(hostname)
                }
            }
        }

        // Modern TLS 1.2 / TLS 1.3 destekli bağlantı spesifikasyonu
        val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
            .build()

        return OkHttpClient.Builder()
            .dns(ipv4Dns)
            .connectionSpecs(listOf(spec, ConnectionSpec.CLEARTEXT))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
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