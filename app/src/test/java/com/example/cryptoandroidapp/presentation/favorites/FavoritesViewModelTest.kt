package com.example.cryptoandroidapp.presentation.favorites

import com.example.cryptoandroidapp.data.repository.FakeAuthRepository
import com.example.cryptoandroidapp.data.repository.FakeCryptoRepository
import com.example.cryptoandroidapp.data.repository.FakeFavoritesRepository
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
class FavoritesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeFavoritesRepository = FakeFavoritesRepository()
    private val fakeCryptoRepository = FakeCryptoRepository()
    private val fakeAuthRepository = FakeAuthRepository()

    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `favori coinler yüklendiğinde Success durumunda favori listesi donmelidir`() = runTest {
        // GIVEN
        viewModel = FavoritesViewModel(
            favoritesRepository = fakeFavoritesRepository,
            cryptoRepository = fakeCryptoRepository,
            authRepository = fakeAuthRepository
        )

        testDispatcher.scheduler.advanceUntilIdle()

        // THEN
        val currentState = viewModel.uiState.value
        assertTrue(currentState is FavoritesUiState.Success)

        val successState = currentState as FavoritesUiState.Success
        // FakeFavoritesRepository varsayılan 2 adet favori (bitcoin, ethereum) içeriyordu
        assertEquals(2, successState.favoriteCoins.size)
        assertEquals("Bitcoin", successState.favoriteCoins[0].name)
    }

    @Test
    fun `toggleFavorite cagirildiginda coin favorilerden cikarilmalidir`() = runTest {
        // GIVEN
        viewModel = FavoritesViewModel(
            favoritesRepository = fakeFavoritesRepository,
            cryptoRepository = fakeCryptoRepository,
            authRepository = fakeAuthRepository
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // WHEN (Bitcoin'i favorilerden çıkarıyoruz)
        viewModel.toggleFavorite("bitcoin")
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN
        val currentState = viewModel.uiState.value as FavoritesUiState.Success
        assertEquals(1, currentState.favoriteCoins.size)
        assertEquals("Ethereum", currentState.favoriteCoins[0].name)
    }
}