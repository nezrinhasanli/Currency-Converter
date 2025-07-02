package com.example.currencyconverter.network.api

import com.example.currencyconverter.models.CurrencyResponse
import com.example.currencyconverter.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyApi {
    @GET("{apiKey}/latest/{base}")
    suspend fun getRates(
        @Path("apiKey") apiKey: String = Constants.API_KEY,
        @Path("base") baseCode: String
    ): Response<CurrencyResponse>
}