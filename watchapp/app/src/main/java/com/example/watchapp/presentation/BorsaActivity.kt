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

class BorsaActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borsa)
        
        setupViews()
        setupClickListeners()
        updateTime()
    }
    
    private fun setupViews() {
        // Saat güncellemesi
        updateTime()
    }
    
    private fun setupClickListeners() {
        // Back butonu
        findViewById<TextView>(R.id.tv_back).setOnClickListener {
            finish()
        }
        
        // MSFT kartı
        findViewById<CardView>(R.id.card_msft).setOnClickListener {
            showStockDetails("MSFT", "$66,66", "6,6%", "$6.6666")
        }
        
        // AMZN kartı
        findViewById<CardView>(R.id.card_amzn).setOnClickListener {
            showStockDetails("AMZN", "$64,99", "6,6%", "$6.6666")
        }
        
        // DHIB kartı
        findViewById<CardView>(R.id.card_dhib).setOnClickListener {
            showStockDetails("DHIB", "$64,99", "6,6%", "$6.6666")
        }
    }
    
    private fun updateTime() {
        val timeTextView = findViewById<TextView>(R.id.tv_time)
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        timeTextView.text = currentTime
    }
    
    private fun showStockDetails(symbol: String, price: String, change: String, volume: String) {
        val stockInfo = """
            Hisse: $symbol
            Fiyat: $price
            Değişim: $change
            Hacim: $volume
            
            Bu hisse senedi hakkında detaylı bilgi almak ister misiniz?
        """.trimIndent()
        
        AlertDialog.Builder(this)
            .setTitle("$symbol Hisse Detayları")
            .setMessage(stockInfo)
            .setPositiveButton("Al") { _, _ ->
                showBuyStockDialog(symbol, price)
            }
            .setNegativeButton("Sat") { _, _ ->
                showSellStockDialog(symbol, price)
            }
            .setNeutralButton("Tamam") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showBuyStockDialog(symbol: String, price: String) {
        AlertDialog.Builder(this)
            .setTitle("Hisse Al")
            .setMessage("$symbol hissesini $price fiyatından almak istiyor musunuz?")
            .setPositiveButton("Al") { _, _ ->
                Toast.makeText(this, "$symbol hissesi başarıyla alındı!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showSellStockDialog(symbol: String, price: String) {
        AlertDialog.Builder(this)
            .setTitle("Hisse Sat")
            .setMessage("$symbol hissesini $price fiyatından satmak istiyor musunuz?")
            .setPositiveButton("Sat") { _, _ ->
                Toast.makeText(this, "$symbol hissesi başarıyla satıldı!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
} 