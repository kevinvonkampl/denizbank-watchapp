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

class NearestAtmActivity : ComponentActivity() {
    
    private var selectedAtm = "Genel Müdürlük"
    private val atmList = listOf(
        "Genel Müdürlük",
        "Esentepe", 
        "Mecidiyeköy",
        "Levent"
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearest_atm)
        
        setupViews()
        setupClickListeners()
        updateTime()
    }
    
    private fun setupViews() {
        // Saat güncellemesi
        updateTime()
        
        // İlk ATM'yi seçili olarak işaretle
        updateSelection()
    }
    
    private fun setupClickListeners() {
        // Genel Müdürlük
        findViewById<CardView>(R.id.card_genel_mudurluk).setOnClickListener {
            selectAtm("Genel Müdürlük")
        }
        
        // Esentepe
        findViewById<CardView>(R.id.card_esentepe).setOnClickListener {
            selectAtm("Esentepe")
        }
        
        // Mecidiyeköy
        findViewById<CardView>(R.id.card_mecidiyekoy).setOnClickListener {
            selectAtm("Mecidiyeköy")
        }
        
        // Levent
        findViewById<CardView>(R.id.card_levent).setOnClickListener {
            selectAtm("Levent")
        }
    }
    
    private fun updateTime() {
        val timeTextView = findViewById<TextView>(R.id.tv_time)
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        timeTextView.text = currentTime
    }
    
    private fun selectAtm(atmName: String) {
        selectedAtm = atmName
        updateSelection()
        
        // ATM seçimi hakkında bilgi göster
        showAtmInfo(atmName)
    }
    
    private fun updateSelection() {
        // Tüm ATM kartlarının border'ını kaldır
        val cards = listOf(
            findViewById<CardView>(R.id.card_genel_mudurluk),
            findViewById<CardView>(R.id.card_esentepe),
            findViewById<CardView>(R.id.card_mecidiyekoy),
            findViewById<CardView>(R.id.card_levent)
        )
        
        cards.forEach { card ->
            card.background = null
        }
        
        // Seçili ATM'ye border ekle
        val selectedCard = when (selectedAtm) {
            "Genel Müdürlük" -> findViewById<CardView>(R.id.card_genel_mudurluk)
            "Esentepe" -> findViewById<CardView>(R.id.card_esentepe)
            "Mecidiyeköy" -> findViewById<CardView>(R.id.card_mecidiyekoy)
            "Levent" -> findViewById<CardView>(R.id.card_levent)
            else -> findViewById<CardView>(R.id.card_genel_mudurluk)
        }
        
        selectedCard.setBackgroundResource(R.drawable.selected_atm_border)
    }
    
    private fun showAtmInfo(atmName: String) {
        val atmInfo = when (atmName) {
            "Genel Müdürlük" -> "DenizBank Genel Müdürlük ATM'si\nAdres: İstanbul, Türkiye\nMesafe: 0.5 km"
            "Esentepe" -> "DenizBank Esentepe ATM'si\nAdres: Esentepe, İstanbul\nMesafe: 1.2 km"
            "Mecidiyeköy" -> "DenizBank Mecidiyeköy ATM'si\nAdres: Mecidiyeköy, İstanbul\nMesafe: 2.1 km"
            "Levent" -> "DenizBank Levent ATM'si\nAdres: Levent, İstanbul\nMesafe: 3.5 km"
            else -> "ATM bilgisi bulunamadı"
        }
        
        AlertDialog.Builder(this)
            .setTitle("$atmName ATM Bilgileri")
            .setMessage(atmInfo)
            .setPositiveButton("Tamam") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Navigasyon") { _, _ ->
                // Burada harita uygulaması açılabilir
                Toast.makeText(this, "Navigasyon başlatılıyor...", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Randevu Al") { _, _ ->
                showAppointmentConfirmation(atmName)
            }
            .show()
    }
    
    private fun showAppointmentConfirmation(atmName: String) {
        // Rastgele sıra numarası oluştur (1-100 arası)
        val queueNumber = (1..100).random()
        
        AlertDialog.Builder(this)
            .setTitle("Randevu Alındı !")
            .setMessage("👍\n\nSıra No: $queueNumber")
            .setPositiveButton("Tamam") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
} 