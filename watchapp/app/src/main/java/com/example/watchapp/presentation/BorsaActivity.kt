package com.example.watchapp.presentation

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.watchapp.R
import com.example.watchapp.presentation.data.model.StockDTO
import com.example.watchapp.presentation.ui.market.MarketViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class BorsaActivity : AppCompatActivity() {

    private val viewModel: MarketViewModel by viewModels()
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var timeTextView: TextView
    private lateinit var stockListContainer: LinearLayout
    private val stockCardHolders = mutableListOf<StockCardHolder>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borsa)
        setupViews()
        observeViewModel()
        findViewById<TextView>(R.id.tv_back).setOnClickListener { finish() }
        updateTime()
    }

    private fun setupViews() {
        loadingIndicator = findViewById(R.id.loading_indicator)
        timeTextView = findViewById(R.id.tv_time)
        stockListContainer = findViewById(R.id.stock_list_container)

        val cardIds = listOf(R.id.card_stock_1, R.id.card_stock_2, R.id.card_stock_3, R.id.card_stock_4, R.id.card_stock_5)
        val infoIds = listOf(R.id.stock_info_1, R.id.stock_info_2, R.id.stock_info_3, R.id.stock_info_4, R.id.stock_info_5)
        val subInfoIds = listOf(R.id.stock_subinfo_1, R.id.stock_subinfo_2, R.id.stock_subinfo_3, R.id.stock_subinfo_4, R.id.stock_subinfo_5)
        val graphIds = listOf(R.id.stock_graph_1, R.id.stock_graph_2, R.id.stock_graph_3, R.id.stock_graph_4, R.id.stock_graph_5)
        val priceIds = listOf(R.id.stock_price_1, R.id.stock_price_2, R.id.stock_price_3, R.id.stock_price_4, R.id.stock_price_5)
        val changeIds = listOf(R.id.stock_change_1, R.id.stock_change_2, R.id.stock_change_3, R.id.stock_change_4, R.id.stock_change_5)

        for (i in cardIds.indices) {
            stockCardHolders.add(StockCardHolder(
                card = findViewById(cardIds[i]),
                info = findViewById(infoIds[i]),
                subInfo = findViewById(subInfoIds[i]),
                graph = findViewById(graphIds[i]),
                price = findViewById(priceIds[i]),
                change = findViewById(changeIds[i])
            ))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    loadingIndicator.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    if (state.stocks.isNotEmpty()) {
                        stockListContainer.visibility = View.VISIBLE
                        updateUiWithStockData(state.stocks)
                    }
                    state.error?.let {
                        Toast.makeText(this@BorsaActivity, "Hata: $it", Toast.LENGTH_LONG).show()
                        stockListContainer.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun updateUiWithStockData(stocks: List<StockDTO>) {
        val count = min(stocks.size, stockCardHolders.size)
        for (i in 0 until count) {
            val stockData = stocks[i]
            val holder = stockCardHolders[i]
            val isChangePositive = stockData.change >= 0

            val changePercentage = if (stockData.price > 0) (stockData.change / stockData.price) * 100 else 0.0

            holder.info.text = stockData.symbol
            holder.subInfo.text = String.format("%.2f%%", changePercentage)
            holder.price.text = String.format("$%.2f", stockData.price)
            holder.change.text = String.format("%.2f", stockData.change)

            val color = if (isChangePositive) getColor(R.color.green) else getColor(R.color.red)
            holder.subInfo.setTextColor(color)
            holder.change.setTextColor(color)
            holder.graph.setImageResource(if (isChangePositive) R.drawable.ic_graph_up else R.drawable.ic_graph_down)

            holder.card.setOnClickListener {
                showStockDetails(stockData.symbol, stockData.price, stockData.change)
            }
        }
    }

    private fun updateTime() {
        timeTextView.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    private fun showStockDetails(symbol: String, price: Double, change: Double) {
        val stockInfo = "Hisse: $symbol\nFiyat: $${String.format("%.2f", price)}\nDeğişim: ${String.format("%.2f", change)}"
        AlertDialog.Builder(this)
            .setTitle("$symbol Detayları")
            .setMessage(stockInfo)
            .setPositiveButton("Al", null)
            .setNegativeButton("Sat", null)
            .setNeutralButton("Kapat", null)
            .show()
    }

    data class StockCardHolder(
        val card: CardView,
        val info: TextView,
        val subInfo: TextView,
        val graph: ImageView,
        val price: TextView,
        val change: TextView
    )
}