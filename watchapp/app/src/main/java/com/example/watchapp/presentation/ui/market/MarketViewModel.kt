package com.example.watchapp.presentation.ui.market


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchapp.presentation.data.model.*
import com.example.watchapp.presentation.data.remote.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MarketUiState(
    val isLoading: Boolean = false,
    val stocks: List<StockDTO> = emptyList(),
    val exchangeRates: List<ExchangeRateDTO> = emptyList(),
    val error: String? = null
)

class MarketViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MarketUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchMarketData()
    }

    fun fetchMarketData() {
        _uiState.value = MarketUiState(isLoading = true)
        viewModelScope.launch {
            try {
                // İki API çağrısını aynı anda yapabiliriz (asenkron olarak)
                val stocksResponse = RetrofitClient.apiService.getStocks()
                val ratesResponse = RetrofitClient.apiService.getExchangeRates()

                _uiState.value = MarketUiState(
                    isLoading = false,
                    stocks = stocksResponse,
                    exchangeRates = ratesResponse
                )
            } catch (e: Exception) {
                _uiState.value = MarketUiState(isLoading = false, error = e.message)
                e.printStackTrace()
            }
        }
    }
}