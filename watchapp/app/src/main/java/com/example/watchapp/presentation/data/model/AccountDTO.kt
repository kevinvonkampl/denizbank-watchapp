package com.example.watchapp.presentation.data.model

data class AccountDTO(
    val iban: String,
    val balance: Double,
    val lastThreeTransactions: List<TransactionDTO>
)