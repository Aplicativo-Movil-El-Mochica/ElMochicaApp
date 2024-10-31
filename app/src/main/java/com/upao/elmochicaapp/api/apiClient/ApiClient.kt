package com.upao.elmochicaapp.api.apiClient

import com.upao.elmochicaapp.api.apiEndpoints.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://13.58.72.89:8080/" // Reemplaza con la URL base de tu API

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Convierte JSON a objetos Kotlin usando Gson
            .build()
            .create(ApiService::class.java)
    }
}