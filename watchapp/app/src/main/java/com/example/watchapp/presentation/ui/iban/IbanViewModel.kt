package com.example.watchapp.presentation.ui.iban

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
        fetchIbans()
    }

    fun fetchIbans() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getSavedIbans()
                _uiState.value = IbanUiState(isLoading = false, ibans = response)
            } catch (e: Exception) {
                _uiState.value = IbanUiState(isLoading = false, error = e.message)
                e.printStackTrace()
            }
        }
    }

    fun addIban(nickname: String, iban: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
        viewModelScope.launch {
            try {
                val newIbanDto = SavedIbanDTO(id = 0, nickname = nickname, iban = iban) // id backend'de üretileceği için 0 yolluyoruz
                RetrofitClient.apiService.addIban(newIbanDto)
                _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "IBAN başarıyla eklendi!")
                fetchIbans() // Listeyi yenile
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                e.printStackTrace()
            }
        }
    }

    fun deleteIban(ibanId: Long) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.deleteIban(ibanId)
                _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "IBAN başarıyla silindi!")
                fetchIbans() // Listeyi yenile
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                e.printStackTrace()
            }
        }
    }
}