package com.upao.elmochicaapp.api.apiClient

import com.upao.elmochicaapp.api.apiEndpoints.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://3.142.238.253:8080/" // Reemplaza con la URL base de tu API

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Convierte JSON a objetos Kotlin usando Gson
            .build()
            .create(ApiService::class.java)
    }


    private const val BASE_URL2 = "http://3.142.238.253:3000/" // Reemplaza con la URL base de tu API

    val apiService2: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL2)
            .addConverterFactory(GsonConverterFactory.create()) // Convierte JSON a objetos Kotlin usando Gson
            .build()
            .create(ApiService::class.java)
    }
}