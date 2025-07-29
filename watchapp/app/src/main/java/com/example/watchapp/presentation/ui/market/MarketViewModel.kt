package com.example.watchapp.presentation.ui.market


import android.content.ContentValues.TAG
import android.util.Log
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
    val cryptos: List<CryptoDTO> = emptyList(), // YENİ ALAN
    val error: String? = null
)

class MarketViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MarketUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // ViewModel ilk oluşturulduğunda verileri çekmeye devam etsin
        fetchMarketData()
    }

    // Bu fonksiyonu public yapalım ki UI'dan "yenile" butonu ile çağırılabilsin
    fun fetchMarketData() {
        _uiState.value = MarketUiState(isLoading = true)
        Log.d(TAG, "Tüm piyasa verileri çekiliyor...")
        viewModelScope.launch {
            try {
                // Üç API çağrısını da aynı anda yapalım (paralel)
                // Bu daha hızlı bir kullanıcı deneyimi sağlar
                val stocksResponse = RetrofitClient.apiService.getStocks()
                Log.i(TAG, "Hisse senetleri çekildi: ${stocksResponse.size} adet.")

                val ratesResponse = RetrofitClient.apiService.getExchangeRates()
                Log.i(TAG, "Döviz kurları çekildi: ${ratesResponse.size} adet.")

                val cryptosResponse = RetrofitClient.apiService.getCryptos()
                Log.i(TAG, "Kripto paralar çekildi: ${cryptosResponse.size} adet.")

                _uiState.value = MarketUiState(
                    isLoading = false,
                    stocks = stocksResponse,
                    exchangeRates = ratesResponse,
                    cryptos = cryptosResponse
                )
                Log.i(TAG, "Tüm piyasa verileri başarıyla çekildi ve UI güncellendi.")
            } catch (e: Exception) {
                _uiState.value = MarketUiState(isLoading = false, error = e.message)
                Log.e(TAG, "Piyasa verileri çekilirken bir hata oluştu!", e)
            }
        }
    }
}