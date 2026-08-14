package com.example.cryptoandroidapp.common

sealed class Resource<out T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<out T>(data: T) : Resource<T>(data)
    class Error<out T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<out T>(data: T? = null) : Resource<T>(data)
}

// Resource, API veya veri işlemlerinden dönen sonuçların durumunu yönetmek için kullanılan sealed class yapısıdır.
// İşlemin başarılı (Success), hatalı (Error) veya devam ediyor (Loading) olduğunu tek bir yapı üzerinden temsil eder.
// Success başarılı işlem sonucundaki veriyi, Error hata mesajını ve varsa mevcut veriyi, Loading ise işlem sırasında elde bulunan veriyi taşıyabilir.
// Bu sayede ViewModel ve UI katmanlarında veri durumlarını daha düzenli ve güvenli bir şekilde yönetmemizi sağlar.