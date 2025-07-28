package com.example.watchapp.presentation.ui.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchapp.presentation.data.model.*
import com.example.watchapp.presentation.data.remote.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SupportUiState(
    val isLoading: Boolean = false,
    val atms: List<AtmLocationDTO> = emptyList(),
    val contactInfo: ContactInfoDTO? = null,
    val error: String? = null
)

class SupportViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchSupportData()
    }

    fun fetchSupportData() {
        _uiState.value = SupportUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val atmsResponse = RetrofitClient.apiService.getAtms()
                val contactResponse = RetrofitClient.apiService.getContact()
                _uiState.value = SupportUiState(
                    isLoading = false,
                    atms = atmsResponse,
                    contactInfo = contactResponse
                )
            } catch (e: Exception) {
                _uiState.value = SupportUiState(isLoading = false, error = e.message)
                e.printStackTrace()
            }
        }
    }
}