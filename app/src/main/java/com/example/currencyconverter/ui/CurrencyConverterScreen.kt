package com.example.currencyconverter.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.currencyconverter.utils.CurrencyEvent

@Composable
fun CurrencyConverterScreen(viewModel: CurrencyViewModel) {
    val event by viewModel.conversion.collectAsState()
    var amount by remember { mutableStateOf("") }
    var fromCurrency by remember { mutableStateOf("EUR") }
    var toCurrency by remember { mutableStateOf("USD") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Currency Converter",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "From")
                CurrencyDropdown(viewModel.currencyList, selectedCurrency = fromCurrency) {
                    fromCurrency = it
                }
            }
            Column {
                Text(text = "To")
                CurrencyDropdown(viewModel.currencyList, selectedCurrency = toCurrency) {
                    toCurrency = it
                }
            }
        }

        Button(
            onClick = {
                viewModel.convert(amount, fromCurrency, toCurrency)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Convert")
        }

        when (event) {
            is CurrencyEvent.Success -> {
                val result = (event as CurrencyEvent.Success).resultText
                Text(text = result, color = Color.Black)
            }
            is CurrencyEvent.Failure -> {
                val error = (event as CurrencyEvent.Failure).errorText
                Text(text = error, color = Color.Red)
            }
            is CurrencyEvent.Loading -> {
                CircularProgressIndicator()
            }
            else -> {}
        }
    }
}

@Composable
fun CurrencyDropdown(
    currencies: List<String>,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(selectedCurrency)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}
