package com.example.watchapp.presentation

import android.app.AlertDialog
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import com.example.watchapp.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class WithdrawMoneyActivity : ComponentActivity() {
    
    private var currentAmount = 0.0
    private val stepAmount = 100.0 // Her tıklamada 100 TL artır/azalt
    private val maxAmount = 10000.0 // Maksimum çekim tutarı
    private val minAmount = 0.0 // Minimum çekim tutarı
    
    private lateinit var amountTextView: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw_money)
        
        setupViews()
        setupClickListeners()
        updateTime()
    }
    
    private fun setupViews() {
        // Saat güncellemesi
        updateTime()
        
        // Tutar gösterimi
        amountTextView = findViewById(R.id.tv_amount)
        updateAmountDisplay()
    }
    
    private fun setupClickListeners() {
        // Eksi butonu
        findViewById<CardView>(R.id.btn_minus).setOnClickListener {
            decreaseAmount()
        }
        
        // Artı butonu
        findViewById<CardView>(R.id.btn_plus).setOnClickListener {
            increaseAmount()
        }
        
        // Çek butonu
        findViewById<CardView>(R.id.btn_withdraw).setOnClickListener {
            withdrawMoney()
        }
    }
    
    private fun updateTime() {
        val timeTextView = findViewById<TextView>(R.id.tv_time)
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        timeTextView.text = currentTime
    }
    
    private fun increaseAmount() {
        if (currentAmount < maxAmount) {
            currentAmount += stepAmount
            updateAmountDisplay()
        } else {
            Toast.makeText(this, "Maksimum çekim tutarına ulaştınız", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun decreaseAmount() {
        if (currentAmount > minAmount) {
            currentAmount -= stepAmount
            updateAmountDisplay()
        } else {
            Toast.makeText(this, "Minimum tutar 0 TL'dir", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun updateAmountDisplay() {
        val formatter = DecimalFormat("#,##0.00")
        val formattedAmount = formatter.format(currentAmount)
        amountTextView.text = "$formattedAmount ₺"
    }
    
    private fun withdrawMoney() {
        if (currentAmount <= 0) {
            Toast.makeText(this, "Lütfen bir tutar seçin", Toast.LENGTH_SHORT).show()
            return
        }
        
        AlertDialog.Builder(this)
            .setTitle("Para Çekme Onayı")
            .setMessage("""
                Çekmek istediğiniz tutar: ${DecimalFormat("#,##0.00").format(currentAmount)} ₺
                
                Bu işlemi onaylıyor musunuz?
            """.trimIndent())
            .setPositiveButton("Çek") { _, _ ->
                processWithdrawal()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun processWithdrawal() {
        // Burada gerçek para çekme işlemi yapılabilir
        Toast.makeText(this, "Para çekme işlemi başarılı!", Toast.LENGTH_LONG).show()
        
        // İşlem sonrası tutarı sıfırla
        currentAmount = 0.0
        updateAmountDisplay()
        
        // Ana sayfaya dön
        finish()
    }
} 