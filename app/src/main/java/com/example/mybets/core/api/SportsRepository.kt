package com.example.mybets.core.api

import android.util.Log

class SportsRepository {

    private val apiKey = "9bfff2b90b678cb90ca57c7e3719af76"


    suspend fun probarConexion(): ApiSportsResponse {
        return RetrofitClient.apiService.getPartidosDelDia(
            apiKey = apiKey,
            fecha = "2026-06-29"
        )
    }
}