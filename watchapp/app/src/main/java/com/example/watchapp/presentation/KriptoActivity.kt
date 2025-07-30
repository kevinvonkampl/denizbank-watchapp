package com.example.watchapp.presentation

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.watchapp.R
import com.example.watchapp.presentation.data.model.CryptoDTO
import com.example.watchapp.presentation.ui.market.MarketViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class KriptoActivity : ComponentActivity() {

    private val viewModel: MarketViewModel by viewModels()
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var timeTextView: TextView
    private lateinit var cryptoListContainer: LinearLayout

    private val cryptoCardHolders = mutableListOf<CryptoCardHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kripto)

        setupViews()
        observeViewModel()

        findViewById<TextView>(R.id.tv_back).setOnClickListener { finish() }
        updateTime()
    }

    private fun setupViews() {
        loadingIndicator = findViewById(R.id.loading_indicator)
        timeTextView = findViewById(R.id.tv_time)
        cryptoListContainer = findViewById(R.id.crypto_list_container)

        cryptoCardHolders.add(CryptoCardHolder(
            card = findViewById(R.id.card_crypto_1),
            info = findViewById(R.id.crypto_info_1),
            subInfo = findViewById(R.id.crypto_subinfo_1),
            graph = findViewById(R.id.crypto_graph_1),
            price = findViewById(R.id.crypto_price_1),
            change = findViewById(R.id.crypto_change_1)
        ))
        cryptoCardHolders.add(CryptoCardHolder(
            card = findViewById(R.id.card_crypto_2),
            info = findViewById(R.id.crypto_info_2),
            subInfo = findViewById(R.id.crypto_subinfo_2),
            graph = findViewById(R.id.crypto_graph_2),
            price = findViewById(R.id.crypto_price_2),
            change = findViewById(R.id.crypto_change_2)
        ))
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    loadingIndicator.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    if (state.cryptos.isNotEmpty()) {
                        cryptoListContainer.visibility = View.VISIBLE
                        updateUiWithCryptoData(state.cryptos)
                    }

                    state.error?.let {
                        Toast.makeText(this@KriptoActivity, "Hata: $it", Toast.LENGTH_LONG).show()
                        cryptoListContainer.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun updateUiWithCryptoData(cryptos: List<CryptoDTO>) {
        val count = min(cryptos.size, cryptoCardHolders.size)

        for (i in 0 until count) {
            val cryptoData = cryptos[i]
            val holder = cryptoCardHolders[i]
            val isPositive = cryptoData.priceChangePercentage24h >= 0
            val priceFormatter = DecimalFormat("$#,##0.00")

            holder.info.text = "${cryptoData.symbol.uppercase()} ${String.format("%.2f%%", cryptoData.priceChangePercentage24h)}"
            holder.subInfo.text = cryptoData.name
            holder.price.text = priceFormatter.format(cryptoData.currentPrice)
            holder.change.text = String.format("%.2f", cryptoData.currentPrice * (cryptoData.priceChangePercentage24h / 100))

            val color = if (isPositive) getColor(R.color.green) else getColor(R.color.red)
            holder.info.setTextColor(color)
            holder.graph.setImageResource(if (isPositive) R.drawable.ic_graph_up else R.drawable.ic_graph_down)

            holder.card.setOnClickListener {
                showCryptoDetails(cryptoData)
            }
        }
    }

    private fun updateTime() {
        timeTextView.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    private fun showCryptoDetails(crypto: CryptoDTO) {
        val priceFormatter = DecimalFormat("$#,##0.00")
        val cryptoInfo = "Kripto: ${crypto.name} (${crypto.symbol.uppercase()})\n" +
                "Fiyat: ${priceFormatter.format(crypto.currentPrice)}\n" +
                "24s Değişim: ${String.format("%.2f%%", crypto.priceChangePercentage24h)}"

        AlertDialog.Builder(this)
            .setTitle("${crypto.name} Detayları")
            .setMessage(cryptoInfo)
            .setPositiveButton("Al", null)
            .setNegativeButton("Sat", null)
            .setNeutralButton("Kapat", null)
            .show()
    }

    data class CryptoCardHolder(
        val card: CardView,
        val info: TextView,
        val subInfo: TextView,
        val graph: ImageView,
        val price: TextView,
        val change: TextView
    )
}