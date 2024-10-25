package com.upao.elmochicaapp.api.apiEndpoints

import com.upao.elmochicaapp.models.Product
import com.upao.elmochicaapp.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("/user/register")
    suspend fun registerUser(@Body user: User): Response<Void> // O la respuesta que devuelva tu API

    @GET("/productos/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): List<Product>

}