package com.example.watchapp.presentation.data.model

data class BillPaymentRequestDTO(
    val billerCode: String,
    val subscriberNumber: String,
    val amount: Double
)