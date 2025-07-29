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

class DovizKuruActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doviz_kuru)
        
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
        
        // EUR kartı
        findViewById<CardView>(R.id.card_eur).setOnClickListener {
            showCurrencyDetails("EUR", "Euro", "89,908322", "0,11%", "🇪🇺", false)
        }
        
        // CNY kartı
        findViewById<CardView>(R.id.card_cny).setOnClickListener {
            showCurrencyDetails("CNY", "Çin Yuanı", "13,882171", "0,02%", "🇨🇳", true)
        }
        
        // GBP kartı
        findViewById<CardView>(R.id.card_gbp).setOnClickListener {
            showCurrencyDetails("GBP", "İngiliz Sterlini", "115,548566", "1,73%", "🇬🇧", true)
        }
        
        // CAD kartı
        findViewById<CardView>(R.id.card_cad).setOnClickListener {
            showCurrencyDetails("CAD", "Kanada Doları", "22,1268506", "1,73%", "🇨🇦", true)
        }
    }
    
    private fun updateTime() {
        val timeTextView = findViewById<TextView>(R.id.tv_time)
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        timeTextView.text = currentTime
    }
    
    private fun showCurrencyDetails(code: String, name: String, rate: String, change: String, flag: String, isPositive: Boolean) {
        val changeColor = if (isPositive) "Yeşil (Artış)" else "Kırmızı (Düşüş)"
        val currencyInfo = """
            Döviz: $code - $name
            Kuru: $rate TL
            Değişim: $change ($changeColor)
            Bayrak: $flag
            
            Bu döviz hakkında detaylı bilgi almak ister misiniz?
        """.trimIndent()
        
        AlertDialog.Builder(this)
            .setTitle("$code Döviz Detayları")
            .setMessage(currencyInfo)
            .setPositiveButton("Al") { _, _ ->
                showBuyCurrencyDialog(code, name, rate)
            }
            .setNegativeButton("Sat") { _, _ ->
                showSellCurrencyDialog(code, name, rate)
            }
            .setNeutralButton("Tamam") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showBuyCurrencyDialog(code: String, name: String, rate: String) {
        AlertDialog.Builder(this)
            .setTitle("Döviz Al")
            .setMessage("$code ($name) dövizini $rate TL kuruyla almak istiyor musunuz?")
            .setPositiveButton("Al") { _, _ ->
                Toast.makeText(this, "$code dövizi başarıyla alındı!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showSellCurrencyDialog(code: String, name: String, rate: String) {
        AlertDialog.Builder(this)
            .setTitle("Döviz Sat")
            .setMessage("$code ($name) dövizini $rate TL kuruyla satmak istiyor musunuz?")
            .setPositiveButton("Sat") { _, _ ->
                Toast.makeText(this, "$code dövizi başarıyla satıldı!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
} 