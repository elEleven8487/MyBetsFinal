package com.example.mybets.core.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiSportsService {

    @GET("fixtures")
    suspend fun getPartidosDelDia(
        @Header("x-apisports-key") apiKey: String,
        @Query("date") fecha: String,
        @Query("timezone") timezone: String = "America/Mexico_City"
    ): ApiSportsResponse
}