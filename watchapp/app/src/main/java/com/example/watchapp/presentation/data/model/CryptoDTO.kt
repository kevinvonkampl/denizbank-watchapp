package com.example.watchapp.presentation.data.model

data class CryptoDTO(
    val id: String,
    val symbol: String,
    val name: String,
    val currentPrice: Double,
    val priceChangePercentage24h: Double
)