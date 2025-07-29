package com.example.watchapp.presentation

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.watchapp.R
import com.example.watchapp.presentation.data.model.ExchangeRateDTO
import com.example.watchapp.presentation.ui.market.MarketViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class DovizKuruActivity : AppCompatActivity() {

    private val viewModel: MarketViewModel by viewModels()
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var timeTextView: TextView
    private lateinit var currencyListContainer: LinearLayout

    private val currencyCardHolders = mutableListOf<CurrencyCardHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doviz_kuru)

        setupViews()
        observeViewModel()

        findViewById<TextView>(R.id.tv_back).setOnClickListener { finish() }
        updateTime()
    }

    private fun setupViews() {
        loadingIndicator = findViewById(R.id.loading_indicator)
        timeTextView = findViewById(R.id.tv_time)
        currencyListContainer = findViewById(R.id.currency_list_container)

        // XML'deki 3 kartın elemanlarını bulup listeye ekliyoruz
        currencyCardHolders.add(CurrencyCardHolder(
            flag = findViewById(R.id.currency_flag_1),
            code = findViewById(R.id.currency_code_1),
            rate = findViewById(R.id.currency_rate_1),
            change = findViewById(R.id.currency_change_1)
        ))
        currencyCardHolders.add(CurrencyCardHolder(
            flag = findViewById(R.id.currency_flag_2),
            code = findViewById(R.id.currency_code_2),
            rate = findViewById(R.id.currency_rate_2),
            change = findViewById(R.id.currency_change_2)
        ))
        currencyCardHolders.add(CurrencyCardHolder(
            flag = findViewById(R.id.currency_flag_3),
            code = findViewById(R.id.currency_code_3),
            rate = findViewById(R.id.currency_rate_3),
            change = findViewById(R.id.currency_change_3)
        ))
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    loadingIndicator.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    if (state.exchangeRates.isNotEmpty()) {
                        currencyListContainer.visibility = View.VISIBLE
                        updateUiWithRates(state.exchangeRates)
                    }

                    state.error?.let {
                        Toast.makeText(this@DovizKuruActivity, "Hata: $it", Toast.LENGTH_LONG).show()
                        currencyListContainer.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun updateUiWithRates(rates: List<ExchangeRateDTO>) {
        val count = minOf(rates.size, currencyCardHolders.size)
        for (i in 0 until count) {
            val rateData = rates[i]
            val holder = currencyCardHolders[i]

            // API'den değişim oranı gelmediği için simüle ediyoruz
            val simulatedChange = Random.nextDouble(-2.0, 2.0)
            val isPositive = simulatedChange >= 0

            // Verileri ilgili UI elemanlarına yaz
            holder.code.text = rateData.currencyPair.substringBefore("/")
            holder.rate.text = String.format("%.4f", rateData.rate)
            holder.change.text = String.format("%.2f%%", simulatedChange)

            // Bayrağı ayarla
            when (rateData.currencyPair) {
                "EUR/USD" -> holder.flag.setImageResource(R.drawable.ic_flag_eur)
                "GBP/USD" -> holder.flag.setImageResource(R.drawable.ic_flag_gbp)
                else -> holder.flag.setImageResource(R.drawable.ic_flag_usd) // Varsayılan
            }

            // Renkleri ayarla
            val color = if (isPositive) getColor(R.color.green) else getColor(R.color.red)
            holder.change.setTextColor(color)
        }
    }

    private fun updateTime() {
        timeTextView.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    // Her bir kartın UI elemanlarını tutan basit bir data class
    data class CurrencyCardHolder(
        val flag: ImageView,
        val code: TextView,
        val rate: TextView,
        val change: TextView
    )
}