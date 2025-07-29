package com.example.watchapp.presentation.data.remote

import com.example.watchapp.presentation.data.model.*
import retrofit2.Response
import retrofit2.http.*

// Backend'deki tüm endpoint'lerin Retrofit karşılıkları
interface WatchAppApiService {

    @GET("api/v1/account")
    suspend fun getAccountDetails(): AccountDTO

    @GET("api/v1/stocks")
    suspend fun getStocks(): List<StockDTO>

    @GET("api/v1/exchange-rates")
    suspend fun getExchangeRates(): List<ExchangeRateDTO>

    @POST("api/v1/withdraw/initiate")
    suspend fun initiateWithdrawal(@Query("amount") amount: Double): Response<Void>

    @POST("api/v1/withdraw/confirm/{txId}")
    suspend fun confirmWithdrawal(@Path("txId") txId: String): Response<Void>

    @POST("api/v1/feedback")
    suspend fun submitFeedback(@Body feedbackRequest: FeedbackRequestDTO): Response<Void>

    @GET("api/v1/ibans")
    suspend fun getSavedIbans(): List<SavedIbanDTO>

    @POST("api/v1/ibans")
    suspend fun addIban(@Body newIban: SavedIbanDTO): SavedIbanDTO

    @DELETE("api/v1/ibans/{ibanId}")
    suspend fun deleteIban(@Path("ibanId") ibanId: Long): Response<Void>

    @POST("api/v1/exchange/execute")
    suspend fun executeExchange(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ): Response<Void>

    @POST("api/v1/appointments/create")
    suspend fun createAppointment(@Query("phoneNumber") phoneNumber: String): Response<String>

    @GET("api/v1/support/atms")
    suspend fun getAtms(): List<AtmLocationDTO>

    @GET("api/v1/support/contact")
    suspend fun getContact(): ContactInfoDTO

    @POST("api/v1/payment/qr/generate")
    suspend fun generateQrCode(@Query("amount") amount: Double): QrCodeDTO

    @POST("api/v1/bills/pay")
    suspend fun payBill(@Body billPaymentRequest: BillPaymentRequestDTO): Response<String>

    @GET("api/v1/cryptos")
    suspend fun getCryptos(): List<CryptoDTO>
}