package com.example.cryptoandroidapp.domain.use_case.auth

import app.cash.turbine.test
import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.data.repository.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        fakeAuthRepository = FakeAuthRepository()
        loginUseCase = LoginUseCase(fakeAuthRepository)
    }

    @Test
    fun `gecerli bilgiler girildiginde basarili kullanici dondurmelidir`() = runTest {
        // 1. GIVEN (Doğru Bilgiler)
        val email = "test@crypto.com"
        val password = "password123"

        // 2. WHEN & 3. THEN
        loginUseCase(email, password).test {
            val item = awaitItem()
            assertTrue(item is Resource.Success)
            assertEquals("test@crypto.com", (item as Resource.Success).data?.email)
            awaitComplete()
        }
    }

    @Test
    fun `repository hata dondugunde Resource Error iletmelidir`() = runTest {
        // 1. GIVEN (Hata Simülasyonu)
        fakeAuthRepository.shouldReturnError = true
        fakeAuthRepository.errorMessage = "Hatalı e-posta veya şifre"

        // 2. WHEN & 3. THEN
        loginUseCase("test@crypto.com", "password123").test {
            val item = awaitItem()
            assertTrue(item is Resource.Error)
            assertEquals("Hatalı e-posta veya şifre", (item as Resource.Error).message)
            awaitComplete()
        }
    }
}