package com.example.cryptoandroidapp.presentation.home

import com.example.cryptoandroidapp.data.repository.FakeCryptoRepository
import com.example.cryptoandroidapp.domain.model.UserEntity
import com.example.cryptoandroidapp.domain.model.UserPortfolioAsset
import com.example.cryptoandroidapp.domain.repository.IAuthRepository
import com.example.cryptoandroidapp.domain.repository.IFavoritesRepository
import com.example.cryptoandroidapp.domain.repository.IPortfolioRepository
import com.example.cryptoandroidapp.domain.use_case.portfolio.CalculatePortfolioUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    // Test Coroutine Dispatcher
    private val testDispatcher = StandardTestDispatcher()

    // Bağımlılıklar
    private val fakeCryptoRepository = FakeCryptoRepository()
    private val portfolioRepository: IPortfolioRepository = mockk()
    private val favoritesRepository: IFavoritesRepository = mockk()
    private val authRepository: IAuthRepository = mockk()
    private val calculatePortfolioUseCase = CalculatePortfolioUseCase()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        // Test ortamında Main thread yerine TestDispatcher kullanıyoruz
        Dispatchers.setMain(testDispatcher)

        // 1. Sahte Kullanıcı Oturumu (Arrange)
        every { authRepository.getCurrentUser() } returns UserEntity(
            uid = "test_user_123",
            email = "test@example.com",
            displayName = "Test User"
        )

        // 2. Sahte Portföy Verisi (1 BTC)
        coEvery { portfolioRepository.getUserPortfolio("test_user_123") } returns flowOf(
            com.example.cryptoandroidapp.common.Resource.Success(
                listOf(
                    UserPortfolioAsset(
                        coinId = "bitcoin",
                        symbol = "BTC",
                        amount = 1.0,
                        addedPriceUsd = 55000.0,
                        timestamp = 0L
                    )
                )
            )
        )

        // 3. Sahte Favoriler Verisi
        coEvery { favoritesRepository.getUserFavorites("test_user_123") } returns flowOf(
            com.example.cryptoandroidapp.common.Resource.Success(listOf("bitcoin"))
        )
    }

    @After
    fun tearDown() {
        // Test bittiğinde Main dispatcher'ı sıfırlıyoruz
        Dispatchers.resetMain()
    }

    @Test
    fun `basarili veri yuklendiginde uiState Success olmali ve portfoy bakiyesini gostermelidir`() = runTest {
        // ViewModel'i başlatıyoruz
        viewModel = HomeViewModel(
            cryptoRepository = fakeCryptoRepository,
            portfolioRepository = portfolioRepository,
            favoritesRepository = favoritesRepository,
            authRepository = authRepository,
            calculatePortfolioUseCase = calculatePortfolioUseCase
        )

        // Asenkron flow işlemlerinin tamamlanması için zamanı ilerletiyoruz
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN (Doğrulama)
        val currentState = viewModel.uiState.value
        assertTrue(currentState is HomeUiState.Success)

        val successState = currentState as HomeUiState.Success
        // Sahte coinlerimizde Bitcoin 60.000$ ve kullanıcının elinde 1 BTC var
        assertEquals("$60,000.00", successState.portfolioTotalText)
        assertEquals(2, successState.coins.size)
        assertEquals(1, successState.favoriteCoins.size)
    }
}