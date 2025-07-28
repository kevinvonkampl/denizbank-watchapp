//package com.example.watchapp.presentation.ui.account
//
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.runBlocking
//import org.junit.Assert.*
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class AccountViewModelTest {
//
//    @Test
//    fun fetchAccountDetails_whenBackendIsLive_updatesStateWithData() {
//        // Bu testin çalışması için backend'in çalışıyor ve RetrofitClient'taki
//        // IP adresinin doğru olması gerektiğini unutma.
//
//        // Test edilecek ViewModel'ı oluştur.
//        val viewModel = AccountViewModel()
//
//        // Coroutine'i senkronize bir şekilde çalıştırarak testin bitmesini bekle.
//        runBlocking {
//            // uiState'in isLoading=false ve accountData'sı dolu olan ilk durumunu yakala ve bekle.
//            // Bu, API cevabı gelene kadar testin beklemesini sağlar.
//            val finalState = try {
//                viewModel.uiState.first { !it.isLoading && (it.accountData != null || it.error != null) }
//            } catch (e: NoSuchElementException) {
//                fail("ViewModel durumu hiçbir zaman güncellenmedi. API'den cevap gelmedi veya zaman aşımına uğradı.")
//                return@runBlocking
//            }
//
//            // Sonuçları doğrula (Assertion)
//            println("Gelen Hata (varsa): ${finalState.error}")
//            println("Gelen IBAN: ${finalState.accountData?.iban}")
//
//            assertNull("Hata mesajı null olmalı, yani bir hata olmamalı.", finalState.error)
//            assertNotNull("Hesap verisi null gelmemeli.", finalState.accountData)
//            assertNotNull("IBAN null gelmemeli.", finalState.accountData?.iban)
//            assertTrue("Bakiye pozitif olmalı.", (finalState.accountData?.balance ?: 0.0) >= 0)
//        }
//    }
//}