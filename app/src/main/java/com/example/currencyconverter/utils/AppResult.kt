package com.example.currencyconverter.utils

sealed class AppResult<T>(val data: T?, val message: String?) {
    class Success<T>(data: T) : AppResult<T>(data, null)
    class Error<T>(message: String) : AppResult<T>(null, message)
}