package com.example.watchapp.presentation

import android.app.AlertDialog
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.watchapp.R
import java.text.SimpleDateFormat
import java.util.*

class CreditCardsActivity : ComponentActivity() {
    
    private val cardNumber = "**** **** **** 2345"
    private val expiryDate = "02/30"
    private val mainBalance = "9.923,23 ₺"
    private val availableBalance = "12.000,00₺"
    private val overdraftLimit = "2.000,00 ₺"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_cards)
        
        setupViews()
        setupClickListeners()
        updateTime()
    }
    
    private fun setupViews() {
        // Saat güncellemesi
        updateTime()
        
        // Kart bilgilerini güncelle
        updateCardInfo()
    }
    
    private fun setupClickListeners() {
        // Geri butonu
        findViewById<TextView>(R.id.btn_back).setOnClickListener {
            finish()
        }
        
        // Ana bakiye'ye tıklama
        findViewById<TextView>(R.id.tv_main_balance).setOnClickListener {
            showBalanceDetails()
        }
        
        // Kullanılabilir bakiye'ye tıklama
        findViewById<TextView>(R.id.tv_available_balance).setOnClickListener {
            showAvailableBalanceDetails()
        }
        
        // Overdraft limit'e tıklama
        findViewById<TextView>(R.id.tv_overdraft_limit).setOnClickListener {
            showOverdraftDetails()
        }
    }
    
    private fun updateTime() {
        val timeTextView = findViewById<TextView>(R.id.tv_time)
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        timeTextView.text = currentTime
    }
    
    private fun updateCardInfo() {
        // Kart bilgileri zaten XML'de tanımlı
        // Burada dinamik güncellemeler yapılabilir
    }
    
    private fun showBalanceDetails() {
        AlertDialog.Builder(this)
            .setTitle("Kart Bakiyesi")
            .setMessage("""
                Ana Bakiye: $mainBalance
                Kullanılabilir: $availableBalance
                Overdraft Limit: $overdraftLimit
                
                Bu kartın detaylı hareketlerini görmek için mobil uygulamayı kullanabilirsiniz.
            """.trimIndent())
            .setPositiveButton("Tamam") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showAvailableBalanceDetails() {
        AlertDialog.Builder(this)
            .setTitle("Kullanılabilir Bakiye")
            .setMessage("""
                Kullanılabilir Bakiye: $availableBalance
                
                Bu tutar, kartınızla yapabileceğiniz işlemler için kullanılabilir tutardır.
                Ana bakiyenizden farklı olabilir.
            """.trimIndent())
            .setPositiveButton("Tamam") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showOverdraftDetails() {
        AlertDialog.Builder(this)
            .setTitle("Overdraft Limit")
            .setMessage("""
                Overdraft Limit: $overdraftLimit
                
                Bu tutar, ana bakiyenizin üzerinde kullanabileceğiniz ek kredi limitidir.
                Bu limiti aşmamaya dikkat edin.
            """.trimIndent())
            .setPositiveButton("Tamam") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
} 