package com.example.currencyconverter.network.repo

import com.example.currencyconverter.models.CurrencyResponse
import com.example.currencyconverter.network.api.CurrencyApi
import com.example.currencyconverter.utils.AppResult
import javax.inject.Inject

class CurrencyRepository @Inject constructor(private val currencyApi: CurrencyApi) {
    suspend fun getRates(baseCode: String): AppResult<CurrencyResponse> {
        return try {
            val response = currencyApi.getRates(baseCode = baseCode)
            val result = response.body()
            if(response.isSuccessful && result != null) {
                AppResult.Success(result)
            } else {
                AppResult.Error(response.message())
            }
        } catch(e: Exception) {
            AppResult.Error(e.message ?: "An error occured")
        }
    }}