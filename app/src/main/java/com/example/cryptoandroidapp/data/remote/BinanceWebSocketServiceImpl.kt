package com.example.cryptoandroidapp.data.remote

import android.util.Log
import com.example.cryptoandroidapp.data.model.BinanceTickerDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class BinanceCombinedStreamDto(
    @SerialName("data")
    val data: BinanceTickerDto
)

@Singleton
class BinanceWebSocketServiceImpl @Inject constructor(
    private val client: OkHttpClient
) : IBinanceWebSocketService {

    private val json = Json { ignoreUnknownKeys = true }
    private val _tickerFlow = MutableSharedFlow<BinanceTickerDto>(replay = 1)
    override val tickerFlow: SharedFlow<BinanceTickerDto> = _tickerFlow.asSharedFlow()

    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun connect(symbols: List<String>) {
        if (webSocket != null) return

        val streamsPath = symbols.map { "${it.lowercase()}usdt@ticker" }.joinToString("/")
        val url = "wss://stream.binance.com/stream?streams=$streamsPath"

        Log.d("BinanceWS", "WebSocket URL'sine bağlanılıyor: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("BinanceWS", "Dinamik WebSocket Bağlantısı Başarıyla Açıldı!")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val wrapper = json.decodeFromString<BinanceCombinedStreamDto>(text)

                    scope.launch {
                        _tickerFlow.emit(wrapper.data)
                    }
                } catch (e: Exception) {
                    Log.e("BinanceWS", "JSON Ayrıştırma Hatası: ${e.localizedMessage}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("BinanceWS", "WebSocket Bağlantı Hatası: ${t.localizedMessage}")

                this@BinanceWebSocketServiceImpl.webSocket = null

                scope.launch {
                    kotlinx.coroutines.delay(5000)
                    connect(symbols)
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("BinanceWS", "WebSocket Bağlantısı Kapatıldı.")
            }
        })
    }

    override fun disconnect() {
        webSocket?.close(1000, "Uygulama kapatıldı")
        webSocket = null
    }
}
