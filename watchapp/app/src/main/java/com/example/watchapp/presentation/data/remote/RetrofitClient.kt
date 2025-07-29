package com.example.watchapp.presentation.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // ÇOK ÖNEMLİ: Bilgisayarının yerel IP adresini buraya yaz.
    // Windows'ta 'ipconfig', Mac/Linux'ta 'ifconfig' ile bulabilirsin.
    // Emülatör ve bilgisayarın aynı Wi-Fi ağında olmalı.
    private const val BASE_URL = "http://127.0.0.1:8080/"

    val apiService: WatchAppApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WatchAppApiService::class.java)
    }
}