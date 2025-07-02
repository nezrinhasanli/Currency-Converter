package com.example.currencyconverter.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrencyResponse(
    @Json(name = "base_code")
    val baseCode: String,
    @Json(name = "conversion_rates")
    val conversionRates: Map<String, Double>,
    @Json(name = "documentation")
    val documentation: String,
    @Json(name = "result")
    val result: String,
    @Json(name = "terms_of_use")
    val termsOfUse: String,
    @Json(name = "time_last_update_unix")
    val timeLastUpdateUnix: Int,
    @Json(name = "time_last_update_utc")
    val timeLastUpdateUtc: String,
    @Json(name = "time_next_update_unix")
    val timeNextUpdateUnix: Int,
    @Json(name = "time_next_update_utc")
    val timeNextUpdateUtc: String
)