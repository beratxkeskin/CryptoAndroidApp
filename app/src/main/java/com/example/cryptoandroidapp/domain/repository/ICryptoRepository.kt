package com.example.cryptoandroidapp.domain.repository

import com.example.cryptoandroidapp.common.Resource
import com.example.cryptoandroidapp.domain.model.CryptoModel
import com.example.cryptoandroidapp.domain.model.GlobalMarketData
import com.example.cryptoandroidapp.domain.model.CryptoDetailModel
import com.example.cryptoandroidapp.domain.model.CandleModel
import kotlinx.coroutines.flow.Flow

interface ICryptoRepository {

    // Kripto paraların listesini (canlı fiyat güncellemeleriyle birlikte) Flow olarak döner
    fun getCryptoList(): Flow<Resource<List<CryptoModel>>>

    // Küresel piyasa verilerini (toplam değer, hacim, btc dominansı) REST ile döner
    suspend fun getGlobalMarketData(): Resource<GlobalMarketData>

    // Belirli bir coinin detaylarını çeker
    suspend fun getCoinDetail(id: String): Resource<CryptoDetailModel>

    // Grafik çizimi için mum verisi listesini çeker
    suspend fun getCoinOhlc(id: String, days: String): Resource<List<CandleModel>>

    // Canlı fiyat akışını (WebSocket) Domain katmanına aktarır
    fun getCryptoPriceStream(symbol: String): Flow<Double>
}