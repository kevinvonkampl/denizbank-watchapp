package com.example.watchapp.presentation.ui.iban

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchapp.presentation.data.model.*
import com.example.watchapp.presentation.data.remote.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class IbanUiState(
    val isLoading: Boolean = false,
    val ibans: List<SavedIbanDTO> = emptyList(),
    val successMessage: String? = null,
    val error: String? = null
)

class IbanViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(IbanUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // 1. Mevcut IBAN'ları çek (Bu zaten vardı)
        fetchIbans()

        // 2. Test için yeni bir IBAN ekle
        addIban(nickname = "Otomatik Test IBAN", iban = "TR101010101010101010101010")
    }

    fun fetchIbans() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getSavedIbans()
                _uiState.value = IbanUiState(isLoading = false, ibans = response)
            } catch (e: Exception) {
                _uiState.value = IbanUiState(isLoading = false, error = e.message)
                Log.e(TAG, "Kayıtlı IBAN'lar çekilirken hata oluştu!", e)
            }
        }
    }

    fun addIban(nickname: String, iban: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
        viewModelScope.launch {
            try {
                val newIbanDto = SavedIbanDTO(
                    id = 0,
                    nickname = nickname,
                    iban = iban
                ) // id backend'de üretileceği için 0 yolluyoruz
                RetrofitClient.apiService.addIban(newIbanDto)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "IBAN başarıyla eklendi!"
                )
                fetchIbans() // Listeyi yenile
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                Log.e(TAG, "IBAN eklenirken hata oluştu!", e)
            }
        }
    }

    fun deleteIban(ibanId: Long) { // <<-- TİPİ Long OLARAK DÜZELTTİK
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
        Log.d(TAG, "IBAN siliniyor: ID $ibanId")
        viewModelScope.launch {
            try {
                // Artık tipler eşleştiği için Retrofit çağrısı sorunsuz çalışacak.
                RetrofitClient.apiService.deleteIban(ibanId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "IBAN başarıyla silindi!"
                )
                Log.i(TAG, "IBAN (ID: $ibanId) başarıyla silindi. Liste yenileniyor...")
                fetchIbans() // Listeyi yenile
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                Log.e(TAG, "IBAN silinirken hata oluştu!", e)
            }
        }
    }
}