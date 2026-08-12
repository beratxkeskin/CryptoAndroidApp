package com.example.cryptoandroidapp.domain.use_case.portfolio

import com.example.cryptoandroidapp.domain.model.CryptoModel
import com.example.cryptoandroidapp.domain.model.UserPortfolioAsset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * [CalculatePortfolioUseCase] sınıfının portföy hesaplama matematiğini test eden Unit Test sınıfı.
 * Herhangi bir Android cihazına ihtiyaç duymadan saf Kotlin ile milisaniyeler içinde çalışır.
 */
class CalculatePortfolioUseCaseTest {

    // Test edeceğimiz UseCase nesnesi
    private lateinit var useCase: CalculatePortfolioUseCase

    @Before
    fun setUp() {
        // Her @Test fonksiyonu çalışmadan önce UseCase nesnemizi taze olarak başlatıyoruz (Arrange)
        useCase = CalculatePortfolioUseCase()
    }

    @Test
    fun `portfoy hesabi dogru toplam dolar degerini ve kar durumunu dondurmelidir`() {
        // 1. GIVEN (Girdi Verilerini Hazırla - Arrange)
        val sampleCoins = listOf(
            CryptoModel(
                id = "ethereum",
                symbol = "eth",
                name = "Ethereum",
                imageUrl = "",
                currentPrice = 2000.0,
                marketCap = 200_000_000.0,
                marketCapRank = 2,
                priceChangePercentage24h = 5.0 // %5 Yükselmiş
            ),
            CryptoModel(
                id = "bitcoin",
                symbol = "btc",
                name = "Bitcoin",
                imageUrl = "",
                currentPrice = 60000.0,
                marketCap = 1_000_000_000.0,
                marketCapRank = 1,
                priceChangePercentage24h = 2.0 // %2 Yükselmiş
            )
        )

        val samplePortfolio = listOf(
            UserPortfolioAsset(
                coinId = "ethereum",
                symbol = "ETH",
                amount = 10.0, // 10 * 2000 = $20,000
                addedPriceUsd = 1900.0,
                timestamp = 0L
            ),
            UserPortfolioAsset(
                coinId = "bitcoin",
                symbol = "BTC",
                amount = 0.5, // 0.5 * 60000 = $30,000
                addedPriceUsd = 58000.0,
                timestamp = 0L
            )
        )

        // 2. WHEN (Test Edilecek Fonksiyonu Çalıştır - Act)
        val result = useCase(samplePortfolio, sampleCoins)

        // 3. THEN (Çıktıyı Doğrula - Assert)
        // Toplam Değer: 20.000 + 30.000 = 50.000 USD olmalı
        assertEquals(50000.0, result.totalValueUsd, 0.01)

        // İki coin de yükseldiği için portföy kârda (true) olmalı
        assertTrue(result.isPositive)
    }

    @Test
    fun `bos portfoy verildiginde toplam deger 0 dondurmelidir`() {
        // 1. GIVEN (Boş Portföy)
        val sampleCoins = listOf(
            CryptoModel(
                id = "bitcoin",
                symbol = "btc",
                name = "Bitcoin",
                imageUrl = "",
                currentPrice = 60000.0,
                marketCap = 1_000_000_000.0,
                marketCapRank = 1,
                priceChangePercentage24h = 2.0
            )
        )
        val emptyPortfolio = emptyList<UserPortfolioAsset>()

        // 2. WHEN
        val result = useCase(emptyPortfolio, sampleCoins)

        // 3. THEN
        assertEquals(0.0, result.totalValueUsd, 0.001)
        assertEquals(0.0, result.totalChangeUsd, 0.001)
        assertTrue(result.isPositive)
    }
}
