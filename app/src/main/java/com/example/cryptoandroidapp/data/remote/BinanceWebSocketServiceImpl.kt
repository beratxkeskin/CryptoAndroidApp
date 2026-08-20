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
    //DTO'da olmayan JSON alanlarını görmezden gel
    private val _tickerFlow = MutableSharedFlow<BinanceTickerDto>(replay = 1)
    override val tickerFlow: SharedFlow<BinanceTickerDto> = _tickerFlow.asSharedFlow()

    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun connect(symbols: List<String>) {
        if (webSocket != null || symbols.isEmpty()) return

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
                Log.w("BinanceWS", "WebSocket Ağ / TLS Uyarısı (CoinGecko REST yedekleme aktif): ${t.localizedMessage}")

                this@BinanceWebSocketServiceImpl.webSocket = null

                // TLS / SSL el sıkışma hatası durumunda uygulamayı kilitlenmeye sokmamak için güvenli tekrar deneme
                if (t !is javax.net.ssl.SSLException && t !is java.io.EOFException) {
                    scope.launch {
                        kotlinx.coroutines.delay(15000)
                        if (this@BinanceWebSocketServiceImpl.webSocket == null) {
                            connect(symbols)
                        }
                    }
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("BinanceWS", "WebSocket Bağlantısı Kapatıldı.")
                this@BinanceWebSocketServiceImpl.webSocket = null
            }
        })
    }

    override fun disconnect() {
        webSocket?.close(1000, "Uygulama kapatıldı")
        webSocket = null
    }
}
