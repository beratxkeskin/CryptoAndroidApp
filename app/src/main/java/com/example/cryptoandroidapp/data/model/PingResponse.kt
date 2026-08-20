package com.example.cryptoandroidapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PingResponse(
    @SerialName("gecko_says")
    val geckoSays: String
)

// servisin erişilebilir olup olmadığını denemek için kullanıyoruz