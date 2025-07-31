package com.example.watchapp.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.watchapp.R

class WelcomeActivity : ComponentActivity() {
    
    private lateinit var textViewTime: TextView
    private lateinit var buttonLogin: Button
    
    companion object {
        private const val PREFS_NAME = "LoginPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_welcome)
        
        initializeViews()
        setupListeners()
        updateTime()
    }
    
    private fun initializeViews() {
        textViewTime = findViewById(R.id.text_view_time)
        buttonLogin = findViewById(R.id.button_login)
    }
    
    private fun setupListeners() {
        buttonLogin.setOnClickListener {
            // QR kod ekranına geç
            val intent = Intent(this, QrLoginActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun updateTime() {
        // Gerçek uygulamada burada güncel saat alınır
        textViewTime.text = "10:18"
    }
    
    private fun isLoggedIn(): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
} 