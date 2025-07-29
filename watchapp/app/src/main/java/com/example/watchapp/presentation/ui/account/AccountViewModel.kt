package com.example.watchapp.presentation.ui.account

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchapp.presentation.data.model.*
import com.example.watchapp.presentation.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI'ın durumunu temsil eden bir data class
data class AccountUiState(
    val isLoading: Boolean = false,
    val accountData: AccountDTO? = null,
    val error: String? = null
)

class AccountViewModel : ViewModel() {

    // UI'ın dinleyeceği durum (state)
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    init {
        // ViewModel oluşturulduğunda veriyi otomatik olarak çek
        fetchAccountDetails()
    }

    fun fetchAccountDetails() {
        // Yükleniyor durumunu ayarla
        _uiState.value = AccountUiState(isLoading = true)

        viewModelScope.launch {
            try {
                // API isteğini at
                val response = RetrofitClient.apiService.getAccountDetails()
                // Başarılı olursa durumu güncelle
                _uiState.value = AccountUiState(isLoading = false, accountData = response)
            } catch (e: Exception) {
                // Hata olursa durumu güncelle
                _uiState.value = AccountUiState(isLoading = false, error = e.message)
                Log.e(TAG, "Hesap detayları çekilirken hata oluştu!", e)
            }
        }
    }
}