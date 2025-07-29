package com.example.watchapp.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.watchapp.R

class QrLoginActivity : ComponentActivity() {
    
    private lateinit var textViewBack: TextView
    private lateinit var textViewTime: TextView
    private lateinit var buttonConfirm: Button
    
    companion object {
        private const val PREFS_NAME = "LoginPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_login)
        
        initializeViews()
        setupListeners()
        updateTime()
    }
    
    private fun initializeViews() {
        textViewBack = findViewById(R.id.text_view_back)
        textViewTime = findViewById(R.id.text_view_time)
        buttonConfirm = findViewById(R.id.button_confirm)
    }
    
    private fun setupListeners() {
        // Geri butonu
        textViewBack.setOnClickListener {
            finish()
        }
        
        // Onayla butonu
        buttonConfirm.setOnClickListener {
            // Giriş durumunu kaydet
            saveLoginStatus(true)
            
            // Giriş başarılı simülasyonu
            Toast.makeText(this, "Giriş başarılı!", Toast.LENGTH_SHORT).show()
            
            // Ana sayfaya geç
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
    
    private fun updateTime() {
        // Gerçek uygulamada burada güncel saat alınır
        textViewTime.text = "10:18"
    }
    
    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }
} 