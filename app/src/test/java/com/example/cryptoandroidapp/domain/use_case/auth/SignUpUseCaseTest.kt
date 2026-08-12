package com.example.cryptoandroidapp.domain.use_case.auth

import app.cash.turbine.test
import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.data.repository.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SignUpUseCaseTest {

    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var signUpUseCase: SignUpUseCase

    @Before
    fun setUp() {
        fakeAuthRepository = FakeAuthRepository()
        signUpUseCase = SignUpUseCase(fakeAuthRepository)
    }

    @Test
    fun `gecerli bilgilerle kayit olundugunda basarili kullanici dondurmelidir`() = runTest {
        // 1. GIVEN (Yeni Kullanıcı Bilgileri)
        val email = "yeni@crypto.com"
        val password = "password123"
        val name = "Berat Keskin"

        // 2. WHEN & 3. THEN (Turbine ile Flow Testi)
        signUpUseCase(email, password, name).test {
            val item = awaitItem()
            assertTrue(item is Resource.Success)

            val user = (item as Resource.Success).data
            assertEquals("yeni@crypto.com", user?.email)
            assertEquals("Berat Keskin", user?.displayName)
            awaitComplete()
        }
    }

    @Test
    fun `kayit esnasinda hata olustugunda Resource Error iletmelidir`() = runTest {
        // 1. GIVEN (E-posta Zaten Kullanımda Hata Simülasyonu)
        fakeAuthRepository.shouldReturnError = true
        fakeAuthRepository.errorMessage = "Bu e-posta adresi zaten kullanımda."

        // 2. WHEN & 3. THEN
        signUpUseCase("yeni@crypto.com", "password123", "Berat Keskin").test {
            val item = awaitItem()
            assertTrue(item is Resource.Error)
            assertEquals("Bu e-posta adresi zaten kullanımda.", (item as Resource.Error).message)
            awaitComplete()
        }
    }
}