package com.example.watchapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watchapp.R
import com.example.watchapp.presentation.data.model.AtmLocationDTO

class AtmAdapter(private val onItemClick: (AtmLocationDTO) -> Unit) :
    ListAdapter<AtmLocationDTO, AtmAdapter.AtmViewHolder>(AtmDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AtmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_atm, parent, false)
        return AtmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AtmViewHolder, position: Int) {
        val atm = getItem(position)
        holder.bind(atm)
        holder.itemView.setOnClickListener { onItemClick(atm) }
    }

    class AtmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.atm_name)
        private val addressTextView: TextView = itemView.findViewById(R.id.atm_address)

        fun bind(atm: AtmLocationDTO) {
            nameTextView.text = atm.name
            addressTextView.text = atm.address
        }
    }
}

class AtmDiffCallback : DiffUtil.ItemCallback<AtmLocationDTO>() {
    override fun areItemsTheSame(oldItem: AtmLocationDTO, newItem: AtmLocationDTO): Boolean = oldItem.name == newItem.name
    override fun areContentsTheSame(oldItem: AtmLocationDTO, newItem: AtmLocationDTO): Boolean = oldItem == newItem
}