package com.example.watchapp.presentation

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.watchapp.R
import com.example.watchapp.presentation.ui.support.SupportViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AppointmentActivity : AppCompatActivity() {

    // Randevu işlemini yapacak olan SupportViewModel'i kullanıyoruz
    private val viewModel: SupportViewModel by viewModels()

    // UI Elemanları
    private lateinit var locationNameTextView: TextView
    private lateinit var locationAddressTextView: TextView
    private lateinit var createAppointmentButton: Button
    private lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)

        // Bir önceki ekrandan gelen ATM bilgilerini al
        val atmName = intent.getStringExtra("ATM_NAME") ?: "Bilinmeyen Şube"
        val atmAddress = intent.getStringExtra("ATM_ADDRESS") ?: "Adres bulunamadı"

        setupViews()

        // Gelen bilgileri ekrana yaz
        locationNameTextView.text = atmName
        locationAddressTextView.text = atmAddress

        setupClickListeners()
        observeViewModel()
        updateTime()
    }

    private fun setupViews() {
        locationNameTextView = findViewById(R.id.tv_location_name)
        locationAddressTextView = findViewById(R.id.tv_location_address)
        createAppointmentButton = findViewById(R.id.btn_create_appointment)
        loadingIndicator = findViewById(R.id.loading_indicator)
        findViewById<TextView>(R.id.tv_back).setOnClickListener { finish() }
    }

    private fun setupClickListeners() {
        createAppointmentButton.setOnClickListener {
            // Butona basıldığında ViewModel'deki fonksiyonu çağır
            viewModel.createAppointment()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Randevu talebi sırasındaki yüklenme durumunu yönet
                    loadingIndicator.visibility = if (state.isAppointmentLoading) View.VISIBLE else View.GONE
                    createAppointmentButton.isEnabled = !state.isAppointmentLoading

                    // Başarı mesajı gelirse göster ve ekranı kapat
                    state.appointmentSuccessMessage?.let {
                        Toast.makeText(this@AppointmentActivity, it, Toast.LENGTH_LONG).show()
                        finish() // İşlem bitince bir önceki ekrana dön
                    }

                    // Hata mesajı gelirse göster
                    state.error?.let {
                        Toast.makeText(this@AppointmentActivity, it, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateTime() {
        findViewById<TextView>(R.id.tv_time).text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
}