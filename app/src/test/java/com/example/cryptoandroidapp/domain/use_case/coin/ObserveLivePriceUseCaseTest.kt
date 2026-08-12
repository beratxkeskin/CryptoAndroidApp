package com.example.cryptoandroidapp.domain.use_case.coin

import app.cash.turbine.test
import com.example.cryptoandroidapp.domain.repository.ICryptoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * [ObserveLivePriceUseCase] sınıfının canlı fiyat akışını (Flow) doğru dinleyip dinlemediğini test eder.
 * MockK ile sahte repository, Turbine ile Flow akış testi yapılır.
 */
class ObserveLivePriceUseCaseTest {

    // MockK ile sahte (taklit) CryptoRepository oluşturuyoruz
    private val cryptoRepository: ICryptoRepository = mockk()

    // Test edilecek UseCase
    private lateinit var useCase: ObserveLivePriceUseCase

    @Before
    fun setUp() {
        useCase = ObserveLivePriceUseCase(cryptoRepository)
    }

    @Test
    fun `verilen sembol icin canlı fiyat akisini dondurmelidir`() = runTest {
        // 1. GIVEN (Arrange)
        val symbol = "btc"
        val expectedPriceStream = flowOf(60000.0, 60150.0, 60200.0)

        // MockK kuralı: repository.getCryptoPriceStream("btc") çağrıldığında bu 3 fiyatlık akışı döndür!
        every { cryptoRepository.getCryptoPriceStream(symbol) } returns expectedPriceStream

        // 2. WHEN & 3. THEN (Act & Assert - Turbine ile Flow Testi)
        useCase(symbol).test {
            // Flow'dan sırayla gelecek 3 fiyatı doğruluyoruz
            assertEquals(60000.0, awaitItem(), 0.001)
            assertEquals(60150.0, awaitItem(), 0.001)
            assertEquals(60200.0, awaitItem(), 0.001)

            // Akışın bittiğini onaylıyoruz
            awaitComplete()
        }

        // Repository metodunun tam olarak "btc" parametresiyle 1 kez çağrıldığını doğruluyoruz
        verify(exactly = 1) { cryptoRepository.getCryptoPriceStream(symbol) }
    }
}