package com.example.cryptoandroidapp.presentation.portfolio

import com.example.cryptoandroidapp.data.repository.FakeAuthRepository
import com.example.cryptoandroidapp.data.repository.FakeCryptoRepository
import com.example.cryptoandroidapp.data.repository.FakePortfolioRepository
import com.example.cryptoandroidapp.domain.use_case.portfolio.CalculatePortfolioUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class PortfolioViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakePortfolioRepository = FakePortfolioRepository()
    private val fakeCryptoRepository = FakeCryptoRepository()
    private val fakeAuthRepository = FakeAuthRepository()
    private val calculatePortfolioUseCase = CalculatePortfolioUseCase()

    private lateinit var viewModel: PortfolioViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `portfoy verileri yuklendiginde Success durumuna gecmeli ve varliklari listelemelidir`() = runTest {
        // GIVEN (ViewModel Başlatılıyor)
        viewModel = PortfolioViewModel(
            portfolioRepository = fakePortfolioRepository,
            cryptoRepository = fakeCryptoRepository,
            authRepository = fakeAuthRepository,
            calculatePortfolioUseCase = calculatePortfolioUseCase
        )

        testDispatcher.scheduler.advanceUntilIdle()

        // THEN (Doğrulama)
        val currentState = viewModel.uiState.value
        assertTrue(currentState is PortfolioUiState.Success)

        val successState = currentState as PortfolioUiState.Success
        // FakePortfolioRepository içinde 1 adet BTC (0.5 miktar) vardı
        assertEquals(1, successState.userAssets.size)
        assertEquals("BTC", successState.userAssets[0].symbol)
        assertEquals(0.5, successState.userAssets[0].amount, 0.001)
    }
}