package com.example.watchapp.presentation

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.watchapp.R
import java.text.SimpleDateFormat
import java.util.*

class IbanInfoActivity : ComponentActivity() {
    
    private val vadesizIban = "TR33 0001 0012 3178 4521 6245 06"
    private val vadeliIban = "TR33 0001 0012 3178 4521 6245 06"
    private val vadesizBalance = "8.568,00₺"
    private val vadeliBalance = "8.568,00₺"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iban_info)
        
        setupViews()
        setupClickListeners()
        updateTime()
    }
    
    private fun setupViews() {
        // Saat güncellemesi
        updateTime()
        
        // IBAN ve bakiye bilgilerini güncelle
        updateAccountInfo()
    }
    
    private fun setupClickListeners() {
        // Geri butonu
        findViewById<TextView>(R.id.btn_back).setOnClickListener {
            finish()
        }
        
        // Vadesiz IBAN'a tıklama
        findViewById<TextView>(R.id.tv_iban_vadesiz).setOnClickListener {
            copyIbanToClipboard(vadesizIban, "Vadesiz")
        }
        
        // Vadeli IBAN'a tıklama
        findViewById<TextView>(R.id.tv_iban_vadeli).setOnClickListener {
            copyIbanToClipboard(vadeliIban, "Vadeli")
        }
        
        // Vadesiz bakiye'ye tıklama
        findViewById<TextView>(R.id.tv_balance_vadesiz).setOnClickListener {
            showBalanceDetails("Vadesiz", vadesizBalance)
        }
        
        // Vadeli bakiye'ye tıklama
        findViewById<TextView>(R.id.tv_balance_vadeli).setOnClickListener {
            showBalanceDetails("Vadeli", vadeliBalance)
        }
    }
    
    private fun updateTime() {
        val timeTextView = findViewById<TextView>(R.id.tv_time)
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        timeTextView.text = currentTime
    }
    
    private fun updateAccountInfo() {
        // IBAN numaralarını güncelle
        findViewById<TextView>(R.id.tv_iban_vadesiz).text = vadesizIban
        findViewById<TextView>(R.id.tv_iban_vadeli).text = vadeliIban
        
        // Bakiye bilgilerini güncelle
        findViewById<TextView>(R.id.tv_balance_vadesiz).text = vadesizBalance
        findViewById<TextView>(R.id.tv_balance_vadeli).text = vadeliBalance
    }
    
    private fun copyIbanToClipboard(iban: String, accountType: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("IBAN", iban)
        clipboardManager.setPrimaryClip(clipData)
        
        Toast.makeText(this, "$accountType IBAN kopyalandı", Toast.LENGTH_SHORT).show()
    }
    
    private fun showBalanceDetails(accountType: String, balance: String) {
        AlertDialog.Builder(this)
            .setTitle("$accountType Hesap Detayları")
            .setMessage("""
                Hesap Türü: $accountType
                Bakiye: $balance
                IBAN: ${if (accountType == "Vadesiz") vadesizIban else vadeliIban}
                
                Bu hesap hakkında detaylı bilgi almak için müşteri hizmetlerini arayabilirsiniz.
            """.trimIndent())
            .setPositiveButton("Tamam") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
} 