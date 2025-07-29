package com.example.watchapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watchapp.R
import com.example.watchapp.presentation.data.model.StockDTO

class StockAdapter(private val onItemClick: (StockDTO) -> Unit) :
    ListAdapter<StockDTO, StockAdapter.StockViewHolder>(StockDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stock, parent, false) // item_stock.xml oluşturulmalı
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val stock = getItem(position)
        holder.bind(stock)
        holder.itemView.setOnClickListener { onItemClick(stock) }
    }

    class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val symbolTextView: TextView = itemView.findViewById(R.id.stock_symbol)
        private val priceTextView: TextView = itemView.findViewById(R.id.stock_price)
        private val changeTextView: TextView = itemView.findViewById(R.id.stock_change)

        fun bind(stock: StockDTO) {
            symbolTextView.text = stock.symbol
            priceTextView.text = String.format("$%.2f", stock.price)
            changeTextView.text = String.format("%.2f", stock.change)
            // Değişime göre renk ayarlanabilir
            val changeColor = if (stock.change >= 0) android.graphics.Color.GREEN else android.graphics.Color.RED
            changeTextView.setTextColor(changeColor)
        }
    }
}

class StockDiffCallback : DiffUtil.ItemCallback<StockDTO>() {
    override fun areItemsTheSame(oldItem: StockDTO, newItem: StockDTO): Boolean = oldItem.symbol == newItem.symbol
    override fun areContentsTheSame(oldItem: StockDTO, newItem: StockDTO): Boolean = oldItem == newItem
}