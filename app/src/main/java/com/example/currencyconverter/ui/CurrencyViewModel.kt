package com.example.currencyconverter.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.network.repo.CurrencyRepository
import com.example.currencyconverter.utils.AppResult
import com.example.currencyconverter.utils.CurrencyEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class CurrencyViewModel @Inject constructor(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion
    val amount = mutableStateOf("")
    private val _rateText = MutableStateFlow("")
    val rateText: StateFlow<String> = _rateText
    val currencyList = listOf(
        "AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AUD",
        "AWG", "AZN", "BAM", "BBD", "BDT", "BGN", "BHD", "BIF",
        "BMD", "BND", "BOB", "BRL", "BSD", "BTC", "BTN", "BWP",
        "BYN", "BYR", "BZD", "CAD", "CDF", "CHF", "CLF", "CLP",
        "CNY", "CNH", "COP", "CRC", "CUC", "CUP", "CVE", "CZK",
        "DJF", "DKK", "DOP", "DZD", "EGP", "ERN", "ETB", "EUR",
        "FJD", "FKP", "GBP", "GEL", "GGP", "GHS", "GIP", "GMD",
        "GNF", "GTQ", "GYD", "HKD", "HNL", "HRK", "HTG", "HUF",
        "IDR", "ILS", "IMP", "INR", "IQD", "IRR", "ISK", "JEP",
        "JMD", "JOD", "JPY", "KES", "KGS", "KHR", "KMF", "KPW",
        "KRW", "KWD", "KYD", "KZT", "LAK", "LBP", "LKR", "LRD",
        "LSL", "LTL", "LVL", "LYD", "MAD", "MDL", "MGA", "MKD",
        "MMK", "MNT", "MOP", "MRU", "MUR", "MVR", "MWK", "MXN",
        "MYR", "MZN", "NAD", "NGN", "NIO", "NOK", "NPR", "NZD",
        "OMR", "PAB", "PEN", "PGK", "PHP", "PKR", "PLN", "PYG",
        "QAR", "RON", "RSD", "RUB", "RWF", "SAR", "SBD", "SCR",
        "SDG", "SEK", "SGD", "SHP", "SLE", "SLL", "SOS", "SRD",
        "STD", "SVC", "SYP", "SZL", "THB", "TJS", "TMT", "TND",
        "TOP", "TRY", "TTD", "TWD", "TZS", "UAH", "UGX", "USD",
        "UYU", "UZS", "VES", "VND", "VUV", "WST", "XAF", "XAG",
        "XAU", "XCD", "XDR", "XOF", "XPF", "YER", "ZAR", "ZMK",
        "ZMW", "ZWL"
    )

    fun convert(fromCurrency: String, toCurrency: String) {
        val fromAmount = amount.value.toFloatOrNull()
        if (fromAmount == null) {
            _conversion.value = CurrencyEvent.Failure("Not a valid amount")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _conversion.value = CurrencyEvent.Loading

            when (val ratesResponse = currencyRepository.getRates(fromCurrency)) {
                is AppResult.Error -> {
                    _conversion.value = CurrencyEvent.Failure(ratesResponse.message ?: "Unknown error")
                    _rateText.value = ""
                }
                is AppResult.Success -> {
                    val rates = ratesResponse.data!!.conversionRates
                    val rate = rates[toCurrency]
                    if (rate == null) {
                        _conversion.value = CurrencyEvent.Failure("Unexpected error: rate not found")
                        _rateText.value = ""
                    } else {
                        val convertedCurrency = round(fromAmount * rate * 100) / 100
                        _conversion.value = CurrencyEvent.Success(resultText = "$convertedCurrency")
                        _rateText.value = "1 $fromCurrency = ${"%.4f".format(rate)} $toCurrency"
                    }
                }
            }
        }
    }

    fun updateAmount(input: String){
        amount.value = input.filter { it.isDigit() }
    }
}
