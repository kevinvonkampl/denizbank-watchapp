package com.example.watchapp.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.watchapp.R

class BillPaymentActivity : ComponentActivity() {
    
    private lateinit var textViewBack: TextView
    private lateinit var textViewTime: TextView
    private lateinit var buttonPayPhone: Button
    private lateinit var buttonPayInternet: Button
    private lateinit var buttonPayGas: Button
    private lateinit var buttonPayElectricity: Button
    private lateinit var buttonPayWater: Button
    private lateinit var buttonPayTv: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_payment)
        
        initializeViews()
        setupListeners()
        updateTime()
    }
    
    private fun initializeViews() {
        textViewBack = findViewById(R.id.text_view_back)
        textViewTime = findViewById(R.id.text_view_time)
        buttonPayPhone = findViewById(R.id.button_pay_phone)
        buttonPayInternet = findViewById(R.id.button_pay_internet)
        buttonPayGas = findViewById(R.id.button_pay_gas)
        buttonPayElectricity = findViewById(R.id.button_pay_electricity)
        buttonPayWater = findViewById(R.id.button_pay_water)
        buttonPayTv = findViewById(R.id.button_pay_tv)
    }
    
    private fun setupListeners() {
        // Geri butonu
        textViewBack.setOnClickListener {
            finish()
        }
        
        // Telefon faturası ödeme
        buttonPayPhone.setOnClickListener {
            processPayment("Telefon Faturası")
        }
        
        // İnternet faturası ödeme
        buttonPayInternet.setOnClickListener {
            processPayment("İnternet")
        }
        
        // Doğalgaz faturası ödeme
        buttonPayGas.setOnClickListener {
            processPayment("Doğalgaz")
        }
        
        // Elektrik faturası ödeme
        buttonPayElectricity.setOnClickListener {
            processPayment("Elektrik Faturası")
        }
        
        // Su faturası ödeme
        buttonPayWater.setOnClickListener {
            processPayment("Su Faturası")
        }
        
        // TV faturası ödeme
        buttonPayTv.setOnClickListener {
            processPayment("TV Faturası")
        }
    }
    
    private fun processPayment(billType: String) {
        // Simüle edilmiş ödeme işlemi
        Toast.makeText(this, "$billType ödeme işlemi başlatılıyor...", Toast.LENGTH_SHORT).show()
        
        // Gerçek uygulamada burada API çağrısı yapılır
        // BillPaymentRequestDTO ile backend'e gönderilir
        
        // Başarılı ödeme simülasyonu
        Toast.makeText(this, "$billType ödemesi başarıyla tamamlandı!", Toast.LENGTH_LONG).show()
    }
    
    private fun updateTime() {
        // Gerçek uygulamada burada güncel saat alınır
        textViewTime.text = "10:18"
    }
} 