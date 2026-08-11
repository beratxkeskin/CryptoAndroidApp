package com.example.cryptoandroidapp.data.remote

import com.example.cryptoandroidapp.data.model.CoinDto
import com.example.cryptoandroidapp.data.model.PingResponse
import com.example.cryptoandroidapp.data.model.CoinGeckoGlobalResponse
import com.example.cryptoandroidapp.data.model.CoinDetailResponseDto
import com.example.cryptoandroidapp.data.model.CoinMarketChartResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoApi {

    @GET("api/v3/ping")
    suspend fun ping(): PingResponse
    // ping, “servis cevap veriyor mu?” kontrolüdür.


    // Kripto para piyasa listesini çeken gerçek endpoint
    @GET("api/v3/coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 250,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = true
    ): List<CoinDto>


    // Küresel piyasa verilerini çeken endpoint
    @GET("api/v3/global")
    suspend fun getGlobalData(): CoinGeckoGlobalResponse

    // 1. COIN DETAY UCU: Coinin açıklamasını, 24 saatlik en yüksek/en düşük vb. piyasa değerlerini çeker
    @GET("api/v3/coins/{id}")
    suspend fun getCoinDetail(
        @Path("id") id: String,
        @Query("localization") localization: Boolean = true,
        @Query("tickers") tickers: Boolean = false,
        @Query("market_data") marketData: Boolean = true,
        @Query("community_data") communityData: Boolean = false,
        @Query("developer_data") developerData: Boolean = false,
        @Query("sparkline") sparkline: Boolean = false
    ): CoinDetailResponseDto

    // 2. OHLC (MUM) GRAFİK UCU: Seçilen gün aralığına göre (1, 7, 30 gün vb.) mum koordinatlarını çeker
    @GET("api/v3/coins/{id}/ohlc")
    suspend fun getCoinOhlc(
        @Path("id") id: String,
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("days") days: String // "1", "7", "30", "365" vb.
    ): List<List<Double>>

    @GET("api/v3/coins/{id}/market_chart")
    suspend fun getCoinMarketChart(
        @Path("id") id: String,
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("days") days: String
    ): CoinMarketChartResponseDto
}
