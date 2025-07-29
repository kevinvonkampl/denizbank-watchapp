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

class KriptoActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kripto)
        
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
        
        // BTC kartı
        findViewById<CardView>(R.id.card_btc).setOnClickListener {
            showCryptoDetails("BTC", "Bitcoin", "$66,66", "6,6%", "$666666", "$6.6666")
        }
        
        // ETH kartı
        findViewById<CardView>(R.id.card_eth).setOnClickListener {
            showCryptoDetails("ETH", "Ethereum", "$64,99", "6,6%", "$666666", "$6.6666")
        }
    }
    
    private fun updateTime() {
        val timeTextView = findViewById<TextView>(R.id.tv_time)
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        timeTextView.text = currentTime
    }
    
    private fun showCryptoDetails(symbol: String, name: String, price: String, change: String, marketCap: String, volume: String) {
        val cryptoInfo = """
            Kripto Para: $symbol - $name
            Fiyat: $price
            Değişim: $change
            Piyasa Değeri: $marketCap
            Hacim: $volume
            
            Bu kripto para hakkında detaylı bilgi almak ister misiniz?
        """.trimIndent()
        
        AlertDialog.Builder(this)
            .setTitle("$symbol Kripto Detayları")
            .setMessage(cryptoInfo)
            .setPositiveButton("Al") { _, _ ->
                showBuyCryptoDialog(symbol, name, price)
            }
            .setNegativeButton("Sat") { _, _ ->
                showSellCryptoDialog(symbol, name, price)
            }
            .setNeutralButton("Tamam") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showBuyCryptoDialog(symbol: String, name: String, price: String) {
        AlertDialog.Builder(this)
            .setTitle("Kripto Para Al")
            .setMessage("$symbol ($name) kripto parasını $price fiyatından almak istiyor musunuz?")
            .setPositiveButton("Al") { _, _ ->
                Toast.makeText(this, "$symbol kripto parası başarıyla alındı!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showSellCryptoDialog(symbol: String, name: String, price: String) {
        AlertDialog.Builder(this)
            .setTitle("Kripto Para Sat")
            .setMessage("$symbol ($name) kripto parasını $price fiyatından satmak istiyor musunuz?")
            .setPositiveButton("Sat") { _, _ ->
                Toast.makeText(this, "$symbol kripto parası başarıyla satıldı!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
} 