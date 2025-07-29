package com.example.watchapp.presentation

// SupportViewModel'i kullanarak dinamik hale getirilmiş hali
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watchapp.R
import com.example.watchapp.presentation.adapter.AtmAdapter // Yeni bir adapter gerekecek
import com.example.watchapp.presentation.ui.support.SupportViewModel
import kotlinx.coroutines.launch

class NearestAtmActivity : AppCompatActivity() {
    private val viewModel: SupportViewModel by viewModels()
    private lateinit var atmRecyclerView: RecyclerView
    private lateinit var atmAdapter: AtmAdapter
    private lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearest_atm)

        setupViews()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupViews() {
        atmRecyclerView = findViewById(R.id.atm_recycler_view)
        loadingIndicator = findViewById(R.id.loading_indicator)
        findViewById<TextView>(R.id.tv_back).setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        atmAdapter = AtmAdapter { atm ->
            Toast.makeText(this, "${atm.name} seçildi", Toast.LENGTH_SHORT).show()
        }
        atmRecyclerView.adapter = atmAdapter
        atmRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    loadingIndicator.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    atmAdapter.submitList(state.atms)
                    state.error?.let { Toast.makeText(this@NearestAtmActivity, "Hata: $it", Toast.LENGTH_LONG).show() }
                }
            }
        }
    }
}