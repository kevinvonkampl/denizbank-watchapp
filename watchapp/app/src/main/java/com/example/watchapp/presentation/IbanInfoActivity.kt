package com.example.watchapp.presentation

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.watchapp.R
import com.example.watchapp.presentation.ui.account.AccountViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class IbanInfoActivity : AppCompatActivity() {
    private val viewModel: AccountViewModel by viewModels()

    // UI Elemanları
    private lateinit var ibanTextView: TextView
    private lateinit var balanceTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var accountCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iban_info)

        setupViews()
        observeViewModel()

        findViewById<TextView>(R.id.btn_back).setOnClickListener { finish() }
        updateTime()
    }

    private fun setupViews() {
        ibanTextView = findViewById(R.id.tv_iban)
        balanceTextView = findViewById(R.id.tv_balance)
        timeTextView = findViewById(R.id.tv_time)
        loadingIndicator = findViewById(R.id.loading_indicator)
        accountCard = findViewById(R.id.account_card_container)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    loadingIndicator.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    state.accountData?.let { account ->
                        // Veri geldiğinde kartı görünür yap
                        accountCard.visibility = View.VISIBLE

                        // Formatter ile para birimini düzgün göster
                        val formatter = DecimalFormat("#,##0.00")
                        val formattedBalance = formatter.format(account.balance)

                        ibanTextView.text = account.iban
                        balanceTextView.text = "$formattedBalance ₺"
                    }

                    state.error?.let {
                        Toast.makeText(this@IbanInfoActivity, "Hata: $it", Toast.LENGTH_LONG).show()
                        // Hata durumunda kartı gizle veya hata mesajı göster
                        accountCard.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun updateTime() {
        timeTextView.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
}