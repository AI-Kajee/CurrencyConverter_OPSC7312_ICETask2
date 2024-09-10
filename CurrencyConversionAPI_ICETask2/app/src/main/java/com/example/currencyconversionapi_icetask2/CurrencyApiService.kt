package com.example.currencyconversionapi_icetask2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("v2/currency/convert")
    fun getExchangeRate(
        @Query("api_key") apiKey: String,
        @Query("from") fromCurrency: String,
        @Query("to") toCurrency: String,
        @Query("amount") amount: Double
    ): Call<CurrencyResponse>
}

