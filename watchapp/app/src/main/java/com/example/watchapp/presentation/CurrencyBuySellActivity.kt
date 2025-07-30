package com.example.watchapp.presentation

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.example.watchapp.R
import com.example.watchapp.presentation.data.model.ExchangeRateDTO
import com.example.watchapp.presentation.data.remote.RetrofitClient
import com.example.watchapp.presentation.ui.market.MarketViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Bu Activity hem veri gösterip hem işlem yaptığı için kendi küçük ViewModel'i olabilir.
class ExchangeActionViewModel : ViewModel() {
    private val _exchangeResult = MutableStateFlow<String?>(null)
    val exchangeResult = _exchangeResult.asStateFlow()

    fun executeExchange(from: String, to: String, amount: Double) {
        viewModelScope.launch {
            try {
                // Sadece göstermelik bir miktar (100 birim) ile işlem yapıyoruz.
                // Gerçekte bu miktar kullanıcıdan alınır.
                RetrofitClient.apiService.executeExchange(from, to, 100.0)
                _exchangeResult.value = "$from -> $to işlemi başarılı!"
            } catch (e: Exception) {
                _exchangeResult.value = "İşlem başarısız: ${e.message}"
            }
        }
    }
}


class CurrencyBuySellActivity : ComponentActivity() {

    // Veri çekmek için MarketViewModel
    private val marketViewModel: MarketViewModel by viewModels()
    // İşlem yapmak için ActionViewModel
    private val actionViewModel: ExchangeActionViewModel by viewModels()

    // UI Elemanları
    private lateinit var currencyValueTextView: TextView
    private lateinit var currencyCodeTextView: TextView
    private lateinit var currencySelector: LinearLayout
    private lateinit var buyButton: Button
    private lateinit var sellButton: Button
    private lateinit var loadingIndicator: ProgressBar

    // Durum değişkenleri
    private var allRates: List<ExchangeRateDTO> = emptyList()
    private var selectedRate: ExchangeRateDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_buy_sell)

        setupViews()
        setupClickListeners()
        observeViewModels()
        updateTime()
    }

    private fun setupViews() {
        currencyValueTextView = findViewById(R.id.tv_currency_value)
        currencyCodeTextView = findViewById(R.id.tv_currency_code)
        currencySelector = findViewById(R.id.currency_selector)
        buyButton = findViewById(R.id.btn_buy)
        sellButton = findViewById(R.id.btn_sell)
        loadingIndicator = findViewById(R.id.loading_indicator)
        findViewById<TextView>(R.id.btn_back).setOnClickListener { finish() }
    }

    private fun setupClickListeners() {
        currencySelector.setOnClickListener {
            showCurrencySelection()
        }

        buyButton.setOnClickListener {
            selectedRate?.let {
                // USD -> EUR alımı gibi düşünelim. "from" USD, "to" diğer para birimi.
                val fromCurrency = it.currencyPair.substringAfter("/")
                val toCurrency = it.currencyPair.substringBefore("/")
                actionViewModel.executeExchange(fromCurrency, toCurrency, 100.0)
            }
        }

        sellButton.setOnClickListener {
            selectedRate?.let {
                // EUR -> USD satımı gibi düşünelim.
                val fromCurrency = it.currencyPair.substringBefore("/")
                val toCurrency = it.currencyPair.substringAfter("/")
                actionViewModel.executeExchange(fromCurrency, toCurrency, 100.0)
            }
        }
    }

    private fun observeViewModels() {
        // 1. Piyasa verilerini dinle
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                marketViewModel.uiState.collect { state ->
                    loadingIndicator.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    if (state.exchangeRates.isNotEmpty()) {
                        allRates = state.exchangeRates
                        // Başlangıçta ilk kuru seçili yap
                        if (selectedRate == null) {
                            selectedRate = allRates.first()
                            updateCurrencyDisplay()
                        }
                    }

                    state.error?.let {
                        Toast.makeText(this@CurrencyBuySellActivity, "Hata: $it", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // 2. Al/Sat işlem sonucunu dinle
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                actionViewModel.exchangeResult.collect { result ->
                    result?.let {
                        Toast.makeText(this@CurrencyBuySellActivity, it, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateCurrencyDisplay() {
        selectedRate?.let {
            currencyValueTextView.text = String.format("$%.3f", it.rate)
            currencyCodeTextView.text = it.currencyPair.substringBefore("/")
        }
    }

    private fun showCurrencySelection() {
        if (allRates.isEmpty()) {
            Toast.makeText(this, "Döviz kurları yükleniyor...", Toast.LENGTH_SHORT).show()
            return
        }

        val currencyPairs = allRates.map { it.currencyPair }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Döviz Seçin")
            .setItems(currencyPairs) { _, which ->
                selectedRate = allRates[which]
                updateCurrencyDisplay()
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun updateTime() {
        findViewById<TextView>(R.id.tv_time).text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
}