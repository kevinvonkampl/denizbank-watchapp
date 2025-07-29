/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.watchapp.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import com.example.watchapp.R

class MainActivity : ComponentActivity() {
    
    private lateinit var textViewLogout: TextView
    
    companion object {
        private const val PREFS_NAME = "LoginPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupMenuClickListeners()
        setupLogoutListener()
    }
    
    private fun setupMenuClickListeners() {
        // Kripto
        findViewById<CardView>(R.id.card_kripto).setOnClickListener {
            val intent = Intent(this, KriptoActivity::class.java)
            startActivity(intent)
        }
        
        // Kredi Kartlarım
        findViewById<CardView>(R.id.card_kredi_kartlarim).setOnClickListener {
            val intent = Intent(this, CreditCardsActivity::class.java)
            startActivity(intent)
        }
        
        // Borsa
        findViewById<CardView>(R.id.card_borsa).setOnClickListener {
            val intent = Intent(this, BorsaActivity::class.java)
            startActivity(intent)
        }
        
        // Para Çek
        findViewById<CardView>(R.id.card_para_cek).setOnClickListener {
            val intent = Intent(this, WithdrawMoneyActivity::class.java)
            startActivity(intent)
        }
        
        // Döviz Kuru
        findViewById<CardView>(R.id.card_doviz_kuru).setOnClickListener {
            val intent = Intent(this, DovizKuruActivity::class.java)
            startActivity(intent)
        }
        
        // En Yakın ATM
        findViewById<CardView>(R.id.card_en_yakin_atm).setOnClickListener {
            val intent = Intent(this, NearestAtmActivity::class.java)
            startActivity(intent)
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
        
        // Fatura Ödeme
        findViewById<CardView>(R.id.card_fatura_odeme).setOnClickListener {
            val intent = Intent(this, BillPaymentActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun initializeViews() {
        textViewLogout = findViewById(R.id.tv_logout)
    }
    
    private fun setupLogoutListener() {
        textViewLogout.setOnClickListener {
            // Giriş durumunu temizle
            clearLoginStatus()
            
            // Hoş geldiniz ekranına geri dön
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
    
    private fun clearLoginStatus() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.apply()
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}