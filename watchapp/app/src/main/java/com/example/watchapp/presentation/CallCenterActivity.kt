package com.example.watchapp.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import com.example.watchapp.R
import java.text.SimpleDateFormat
import java.util.*

class CallCenterActivity : ComponentActivity() {
    
    private val callCenterNumber = "08502220800"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_center)
        
        setupViews()
        setupClickListeners()
        updateTime()
    }
    
    private fun setupViews() {
        // Saat güncellemesi
        updateTime()
    }
    
    private fun setupClickListeners() {
        // Geri butonu
        findViewById<TextView>(R.id.btn_back).setOnClickListener {
            finish()
        }
        
        // Arama butonu
        findViewById<CardView>(R.id.btn_call).setOnClickListener {
            makeCall()
        }
        
        // Telefon numarasına tıklama
        findViewById<TextView>(R.id.tv_phone_number).setOnClickListener {
            makeCall()
        }
    }
    
    private fun updateTime() {
        val timeTextView = findViewById<TextView>(R.id.tv_time)
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        timeTextView.text = currentTime
    }
    
    private fun makeCall() {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$callCenterNumber")
            startActivity(intent)
            
            Toast.makeText(this, "Çağrı Merkezi aranıyor...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Arama başlatılamadı", Toast.LENGTH_SHORT).show()
        }
    }
} 