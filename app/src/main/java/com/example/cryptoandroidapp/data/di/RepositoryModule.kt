package com.example.cryptoandroidapp.data.di

import com.example.cryptoandroidapp.data.repository.AuthRepositoryImpl
import com.example.cryptoandroidapp.domain.repository.IAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.example.cryptoandroidapp.data.repository.CryptoRepositoryImpl
import com.example.cryptoandroidapp.domain.repository.ICryptoRepository
import com.example.cryptoandroidapp.data.repository.PortfolioRepositoryImpl
import com.example.cryptoandroidapp.domain.repository.IPortfolioRepository
import com.example.cryptoandroidapp.data.repository.FavoritesRepositoryImpl
import com.example.cryptoandroidapp.domain.repository.IFavoritesRepository

//Repository'ler uygulamadaki veri yönetim merkezleridir.
// @Module -> Bu işaret Hilt'e şunu söyler: “Bu sınıfta nesneleri nasıl vereceğine dair kurallar bulunuyor.”
// @InstallIn -> Bu satır iki soruya cevap verir:
// 1. Bu kurallar hangi Hilt alanında geçerli?  2.Hazırlanan nesneler ne kadar uzun süre kullanılabilir?
// SingletonComponent, uygulama açık kaldığı sürece yaşayan en geniş Hilt alanıdır.
// abstract kelimesi “bu sınıftan doğrudan normal nesne üretme” anlamına gelir.
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds // -> “Bu interface istenirse, bu sınıfı kullan.”
    @Singleton // -> Singleton burada “uygulama açıkken aynı ortak örneği kullan” anlamına gelir.
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): IAuthRepository //AuthRepositoryImpl Firebase ile konuşur.
    // “Birisi IAuthRepository isterse, ona AuthRepositoryImpl ver. Uygulama açık kaldığı sürece bunu ortak bir örnek olarak kullan.”
    // - IAuthRepository: Sözleşme, yani ne yapılabileceğini söyleyen interface.
    // - AuthRepositoryImpl: Gerçek işi yapan sınıf, yani Firebase'e bağlanan implementasyon.


    @Binds
    @Singleton
    abstract fun bindCryptoRepository(
        cryptoRepositoryImpl: CryptoRepositoryImpl
    ): ICryptoRepository


    @Binds
    @Singleton
    abstract fun bindBinanceWebSocketService(
        binanceWebSocketServiceImpl: com.example.cryptoandroidapp.data.remote.BinanceWebSocketServiceImpl
    ): com.example.cryptoandroidapp.data.remote.IBinanceWebSocketService

    @Binds
    @Singleton
    abstract fun bindPortfolioRepository(
        portfolioRepositoryImpl: PortfolioRepositoryImpl
    ): IPortfolioRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(
        favoritesRepositoryImpl: FavoritesRepositoryImpl
    ): IFavoritesRepository
}