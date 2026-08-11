package com.example.cryptoandroidapp.data.remote

import com.example.cryptoandroidapp.data.model.BinanceTickerDto
import kotlinx.coroutines.flow.SharedFlow

interface IBinanceWebSocketService {
    val tickerFlow: SharedFlow<BinanceTickerDto>
    fun connect(symbols: List<String>)
    fun disconnect()
}
