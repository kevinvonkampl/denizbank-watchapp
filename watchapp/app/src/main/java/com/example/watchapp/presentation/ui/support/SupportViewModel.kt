package com.example.watchapp.presentation.ui.support

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchapp.presentation.data.model.AtmLocationDTO
import com.example.watchapp.presentation.data.model.ContactInfoDTO
import com.example.watchapp.presentation.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI state'ine randevu işlemiyle ilgili alanlar ekleyelim
data class SupportUiState(
    val isLoading: Boolean = false,
    val atms: List<AtmLocationDTO> = emptyList(),
    val contactInfo: ContactInfoDTO? = null,
    val isAppointmentLoading: Boolean = false, // Randevu talebi için ayrı bir yüklenme durumu
    val appointmentSuccessMessage: String? = null,
    val error: String? = null
)

class SupportViewModel : ViewModel() {

    companion object {
        private const val TAG = "SupportViewModel"
    }

    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // 1. Mevcut destek verilerini çek (ATM ve İletişim)
        fetchSupportData()

        // 2. TEST AMAÇLI: Destek ekranı açılır açılmaz randevu oluşturma işlemini tetikle.
        // UYARI: Bu, bu ekran her açıldığında SMS göndermeye çalışacaktır.
        // Gerçek bir uygulamada bu bir butona bağlanmalıdır.
        //createAppointment()
    }

    fun fetchSupportData() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        Log.d(TAG, "Destek verileri (ATM ve İletişim) çekiliyor...")
        viewModelScope.launch {
            try {
                val atmsResponse = RetrofitClient.apiService.getAtms()
                Log.i(TAG, "ATM verileri çekildi: ${atmsResponse.size} adet.")

                val contactResponse = RetrofitClient.apiService.getContact()
                Log.i(TAG, "İletişim bilgisi çekildi: ${contactResponse.department}")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    atms = atmsResponse,
                    contactInfo = contactResponse
                )
                Log.i(TAG, "Tüm destek verileri başarıyla çekildi.")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                Log.e(TAG, "Destek verileri çekilirken hata oluştu!", e)
            }
        }
    }

    /**
     * Sabit bir telefon numarası için randevu talebi oluşturur.
     */
    fun createAppointment() {
        // Randevu işlemi için yüklenme durumunu ayarla
        _uiState.value = _uiState.value.copy(isAppointmentLoading = true, appointmentSuccessMessage = null, error = null)

        // Sabit telefon numarası
        val testPhoneNumber = "+905367330800"

        Log.d(TAG, "Randevu talebi gönderiliyor: $testPhoneNumber")

        viewModelScope.launch {
            try {
                // API isteğini at
                val response = RetrofitClient.apiService.createAppointment(testPhoneNumber)

                // Başarılı olursa durumu güncelle
                val successMessage = response.body() ?: "Randevu talebi alındı."
                _uiState.value = _uiState.value.copy(
                    isAppointmentLoading = false,
                    appointmentSuccessMessage = successMessage
                )
                Log.i(TAG, "Randevu talebi başarılı. Sunucu cevabı: $successMessage")

            } catch (e: Exception) {
                // Hata olursa durumu güncelle
                _uiState.value = _uiState.value.copy(
                    isAppointmentLoading = false,
                    error = "Randevu talebi başarısız: ${e.message}"
                )
                Log.e(TAG, "Randevu talebi oluşturulurken hata oluştu!", e)
            }
        }
    }
}