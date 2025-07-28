package com.example.watchapp.presentation

import android.app.AlertDialog
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import com.example.watchapp.R
import java.text.SimpleDateFormat
import java.util.*

class CurrencyBuySellActivity : ComponentActivity() {
    
    private var currentCurrency = "USD"
    private var currentRate = 18.829
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_buy_sell)
        
        setupViews()
        setupClickListeners()
        updateTime()
    }
    
    private fun setupViews() {
        // Saat güncellemesi
        updateTime()
        
        // Döviz değerini güncelle
        updateCurrencyDisplay()
    }
    
    private fun setupClickListeners() {
        // Geri butonu
        findViewById<TextView>(R.id.btn_back).setOnClickListener {
            finish()
        }
        
        // Döviz kodu seçimi
        findViewById<TextView>(R.id.tv_currency_code).setOnClickListener {
            showCurrencySelection()
        }
        
        // Al butonu
        findViewById<CardView>(R.id.btn_buy).setOnClickListener {
            showBuyDialog()
        }
        
        // Sat butonu
        findViewById<CardView>(R.id.btn_sell).setOnClickListener {
            showSellDialog()
        }
    }
    
    private fun updateTime() {
        val timeTextView = findViewById<TextView>(R.id.tv_time)
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        timeTextView.text = currentTime
    }
    
    private fun updateCurrencyDisplay() {
        val currencyValueTextView = findViewById<TextView>(R.id.tv_currency_value)
        val currencyCodeTextView = findViewById<TextView>(R.id.tv_currency_code)
        
        currencyValueTextView.text = "$${String.format("%.3f", currentRate)}"
        currencyCodeTextView.text = currentCurrency
    }
    
    private fun showCurrencySelection() {
        val currencies = arrayOf("USD", "EUR", "GBP", "JPY", "TRY")
        
        AlertDialog.Builder(this)
            .setTitle("Döviz Seçin")
            .setItems(currencies) { _, which ->
                currentCurrency = currencies[which]
                // Farklı dövizler için farklı oranlar
                currentRate = when (currentCurrency) {
                    "USD" -> 18.829
                    "EUR" -> 20.456
                    "GBP" -> 23.789
                    "JPY" -> 0.125
                    "TRY" -> 1.0
                    else -> 18.829
                }
                updateCurrencyDisplay()
                Toast.makeText(this, "$currentCurrency seçildi", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showBuyDialog() {
        AlertDialog.Builder(this)
            .setTitle("Döviz Al")
            .setMessage("$currentCurrency almak istediğinizden emin misiniz?\n\nMevcut Kur: $${String.format("%.3f", currentRate)}")
            .setPositiveButton("Al") { _, _ ->
                // Burada gerçek alım işlemi yapılabilir
                Toast.makeText(this, "$currentCurrency alım işlemi başarılı!", Toast.LENGTH_LONG).show()
                finish()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showSellDialog() {
        AlertDialog.Builder(this)
            .setTitle("Döviz Sat")
            .setMessage("$currentCurrency satmak istediğinizden emin misiniz?\n\nMevcut Kur: $${String.format("%.3f", currentRate)}")
            .setPositiveButton("Sat") { _, _ ->
                // Burada gerçek satış işlemi yapılabilir
                Toast.makeText(this, "$currentCurrency satış işlemi başarılı!", Toast.LENGTH_LONG).show()
                finish()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
} 