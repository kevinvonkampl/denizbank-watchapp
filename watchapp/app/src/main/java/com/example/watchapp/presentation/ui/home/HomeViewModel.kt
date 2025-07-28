package com.example.watchapp.presentation.ui.home

import androidx.lifecycle.ViewModel

// Ana menü ekranı için ViewModel.
// Şimdilik sadece UI olaylarını (örneğin buton tıklamaları ile navigasyon)
// yönetmek için bir yer tutucu olarak duruyor.
class HomeViewModel : ViewModel() {
    // Örneğin, hangi ekrana gidileceğini yöneten bir state olabilir.
    // val navigationTarget = MutableStateFlow<Screen?>(null)

    fun onAccountClicked() {
        // Navigasyon işlemini tetikle
        // navigationTarget.value = Screen.Account
    }

    // ... diğer buton tıklamaları için metotlar ...
}