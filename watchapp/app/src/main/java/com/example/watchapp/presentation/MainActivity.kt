/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.watchapp.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import com.example.watchapp.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Menü kartlarına tıklama olaylarını ekle
        setupMenuClickListeners()
    }
    
    private fun setupMenuClickListeners() {
        // Hesabım
        findViewById<CardView>(R.id.card_hesabim).setOnClickListener {
            showToast("Hesabım seçildi")
        }
        
        // Kredi Kartlarım
        findViewById<CardView>(R.id.card_kredi_kartlarim).setOnClickListener {
            showToast("Kredi Kartlarım seçildi")
        }
        
        // Borsa
        findViewById<CardView>(R.id.card_borsa).setOnClickListener {
            showToast("Borsa seçildi")
        }
        
        // Para Çek
        findViewById<CardView>(R.id.card_para_cek).setOnClickListener {
            showToast("Para Çek seçildi")
        }
        
        // Döviz Kuru
        findViewById<CardView>(R.id.card_doviz_kuru).setOnClickListener {
            showToast("Döviz Kuru seçildi")
        }
        
        // En Yakın ATM
        findViewById<CardView>(R.id.card_en_yakin_atm).setOnClickListener {
            showToast("En Yakın ATM seçildi")
        }
        
        // Çağrı Merkezi
        findViewById<CardView>(R.id.card_cagri_merkezi).setOnClickListener {
            val intent = Intent(this, CallCenterActivity::class.java)
            startActivity(intent)
        }
        
        // Döviz Al-Sat
        findViewById<CardView>(R.id.card_doviz_al_sat).setOnClickListener {
            val intent = Intent(this, CurrencyBuySellActivity::class.java)
            startActivity(intent)
        }
        
        // IBAN Bilgileri
        findViewById<CardView>(R.id.card_iban_bilgileri).setOnClickListener {
            val intent = Intent(this, IbanInfoActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}