package com.example.currencyconverter.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.currencyconverter.R
import com.example.currencyconverter.utils.CurrencyEvent

@Composable
fun CurrencyConverterScreen(viewModel: CurrencyViewModel) {
    val event by viewModel.conversion.collectAsState()
    val context = LocalContext.current
    var fromCurrency by remember { mutableStateOf("EUR") }
    var toCurrency by remember { mutableStateOf("USD") }
    val resultState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Currency Converter",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                CurrencyDropdown(viewModel.currencyList, selectedCurrency = fromCurrency) {
                    fromCurrency = it
                }
            OutlinedTextField(
                value = viewModel.amount.value,
                onValueChange = { viewModel.updateAmount(it) },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Box(
            modifier = Modifier
                .background(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                )
                .align(Alignment.CenterHorizontally)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_swap),
                contentDescription = "Swap Currencies",
                tint = Color.White,
                modifier = Modifier
                    .clickable(onClick = {
                        val temp = fromCurrency
                        fromCurrency = toCurrency
                        toCurrency = temp
                    })
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CurrencyDropdown(viewModel.currencyList, selectedCurrency = toCurrency) {
                toCurrency = it
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(53.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Text(
                    text = resultState.value.ifEmpty { "0.00" },
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp),
                    color = if(resultState.value.isEmpty()) Color.Black.copy(alpha = 0.5f) else Color.Black
                )
            }
        }

        Button(
            onClick = { viewModel.convert(fromCurrency, toCurrency) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            when (event) {
                is CurrencyEvent.Success -> {
                    val result = (event as CurrencyEvent.Success).resultText
                    resultState.value = result
                    Text("Convert")
                }
                is CurrencyEvent.Failure -> {
                    val error = (event as CurrencyEvent.Failure).errorText
                    resultState.value = ""
                    Text("Convert")
                    LaunchedEffect(error) {
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    }
                }
                is CurrencyEvent.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                }
                else -> { Text("Convert") }
            }
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
