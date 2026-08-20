package com.example.cryptoandroidapp.data.remote

import com.example.cryptoandroidapp.data.model.BinanceTickerDto
import kotlinx.coroutines.flow.SharedFlow

//Canlı fiyat hizmeti kullanan bir sınıf, bu üç imkâna sahip olmalıdır.
interface IBinanceWebSocketService {
    val tickerFlow: SharedFlow<BinanceTickerDto>  //yeni fiyat mesajlarını dinleme kanalı
    fun connect(symbols: List<String>)     //Verilen coin sembolleri için bağlantı açma isteği.
    fun disconnect()   //Bağlantıyı kapatma isteği.
}
