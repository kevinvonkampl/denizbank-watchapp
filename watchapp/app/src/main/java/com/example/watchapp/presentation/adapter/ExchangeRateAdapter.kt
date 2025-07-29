package com.example.watchapp.presentation.adapter

// ... StockAdapter'a çok benzer bir yapı...
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watchapp.R
import com.example.watchapp.presentation.data.model.ExchangeRateDTO

class ExchangeRateAdapter(private val onItemClick: (ExchangeRateDTO) -> Unit) :
    ListAdapter<ExchangeRateDTO, ExchangeRateAdapter.RateViewHolder>(RateDiffCallback()) {

    class RateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pairTextView: TextView = itemView.findViewById(R.id.rate_pair)
        private val rateTextView: TextView = itemView.findViewById(R.id.rate_value)
        fun bind(rate: ExchangeRateDTO) {
            pairTextView.text = rate.currencyPair
            rateTextView.text = String.format("%.4f", rate.rate)
        }
    }
    // ... onCreateViewHolder ve onBindViewHolder ...
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exchange_rate, parent, false)
        return RateViewHolder(view)
    }
    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        val rate = getItem(position)
        holder.bind(rate)
        holder.itemView.setOnClickListener { onItemClick(rate) }
    }
}

class RateDiffCallback : DiffUtil.ItemCallback<ExchangeRateDTO>() {
    override fun areItemsTheSame(oldItem: ExchangeRateDTO, newItem: ExchangeRateDTO): Boolean = oldItem.currencyPair == newItem.currencyPair
    override fun areContentsTheSame(oldItem: ExchangeRateDTO, newItem: ExchangeRateDTO): Boolean = oldItem == newItem
}