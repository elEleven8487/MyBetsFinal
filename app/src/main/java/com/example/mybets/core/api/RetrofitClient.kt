package com.example.mybets.core.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://v3.football.api-sports.io/"

    val apiService: ApiSportsService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Convierte el JSON a tus Data Classes
            .build()
            .create(ApiSportsService::class.java)
    }
}