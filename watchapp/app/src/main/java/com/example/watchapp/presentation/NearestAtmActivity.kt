package com.example.watchapp.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.example.watchapp.R
import com.example.watchapp.presentation.adapter.AtmAdapter
import com.example.watchapp.presentation.ui.support.SupportViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NearestAtmActivity : ComponentActivity() {
    private val viewModel: SupportViewModel by viewModels()
    private lateinit var atmRecyclerView: WearableRecyclerView
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
            val intent = Intent(this, AppointmentActivity::class.java).apply {
                putExtra("ATM_NAME", atm.name)
                putExtra("ATM_ADDRESS", atm.address)
            }
            startActivity(intent)
        }

        atmRecyclerView.apply {
            isEdgeItemsCenteringEnabled = true
            layoutManager = WearableLinearLayoutManager(this@NearestAtmActivity)
            adapter = atmAdapter
        }
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