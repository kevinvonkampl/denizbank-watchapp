package com.example.watchapp.presentation.adapter

// ... Diğer Adapter'lara çok benzer bir yapı...
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watchapp.R
import com.example.watchapp.presentation.data.model.CryptoDTO

class CryptoAdapter(private val onItemClick: (CryptoDTO) -> Unit) :
    ListAdapter<CryptoDTO, CryptoAdapter.CryptoViewHolder>(CryptoDiffCallback()) {

    class CryptoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.crypto_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.crypto_price)
        private val changeTextView: TextView = itemView.findViewById(R.id.crypto_change_24h)
        fun bind(crypto: CryptoDTO) {
            nameTextView.text = crypto.name
            priceTextView.text = String.format("$%.2f", crypto.currentPrice)
            changeTextView.text = String.format("%.2f%%", crypto.priceChangePercentage24h)
            val changeColor = if (crypto.priceChangePercentage24h >= 0) android.graphics.Color.GREEN else android.graphics.Color.RED
            changeTextView.setTextColor(changeColor)
        }
    }
    // ... onCreateViewHolder ve onBindViewHolder ...
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_crypto, parent, false)
        return CryptoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        val crypto = getItem(position)
        holder.bind(crypto)
        holder.itemView.setOnClickListener { onItemClick(crypto) }
    }
}

class CryptoDiffCallback : DiffUtil.ItemCallback<CryptoDTO>() {
    override fun areItemsTheSame(oldItem: CryptoDTO, newItem: CryptoDTO): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: CryptoDTO, newItem: CryptoDTO): Boolean = oldItem == newItem
}