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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.currencyconverter.R
import com.example.currencyconverter.utils.CurrencyEvent

@Composable
fun CurrencyConverterScreen(viewModel: CurrencyViewModel) {
    val event by viewModel.conversion.collectAsState()
    val context = LocalContext.current
    var fromCurrency by remember { mutableStateOf("EUR") }
    var toCurrency by remember { mutableStateOf("USD") }
    val resultState = remember { mutableStateOf("") }
    val rateText by viewModel.rateText.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Currency Converter",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ){
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
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Box(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .size(52.dp)
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
                        .size(80.dp)
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
                        text = resultState.value.ifEmpty { "Converted Amount" },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp),
                        color = if(resultState.value.isEmpty()) Color.Black.copy(alpha = 0.7f) else Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.convert(fromCurrency, toCurrency) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
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

        Spacer(modifier = Modifier.height(16.dp))

        if (rateText.isNotEmpty()) {
            Text(
                text = "Indicative Exchange Rate:",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                text = rateText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Start)
            )
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
    var searchQuery by remember { mutableStateOf("") }

    val filteredCurrencies = currencies.filter {
        it.contains(searchQuery, ignoreCase = true)
    }

    Box {
        Row(
            modifier = Modifier
                .border(1.dp, Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clip(shape = RoundedCornerShape(16.dp))
                .clickable {
                    expanded = true
                    searchQuery = ""
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = selectedCurrency, modifier = Modifier.padding(12.dp))
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.padding(12.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(max = 300.dp)
                .width(200.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search...") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                singleLine = true
            )

            filteredCurrencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }

            if (filteredCurrencies.isEmpty()) {
                Text(
                    text = "No results",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            }
        }
    }
}
