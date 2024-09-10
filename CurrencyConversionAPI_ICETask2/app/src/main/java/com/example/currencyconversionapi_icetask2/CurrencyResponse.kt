package com.example.currencyconversionapi_icetask2

data class CurrencyResponse(
    val base_currency_code: String,
    val base_currency_name: String,
    val amount: String,
    val updated_date: String,
    val rates: Map<String, RateDetails>,
    val status: String
)




