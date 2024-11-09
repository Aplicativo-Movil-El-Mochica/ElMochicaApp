package com.upao.elmochicaapp.api.apiEndpoints

import com.upao.elmochicaapp.models.Product
import com.upao.elmochicaapp.models.User
import com.upao.elmochicaapp.models.requestModels.CartItemRequest
import com.upao.elmochicaapp.models.requestModels.LoginRequest
import com.upao.elmochicaapp.models.responseModels.LoginResponse
import com.upao.elmochicaapp.models.responseModels.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("/user/register")
    suspend fun registerUser(@Body user: User): Response<Void> // O la respuesta que devuelva tu API

    @POST("/user/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse> // Login y obtención del JWT

    @GET("/user/search/{dni}")
    suspend fun getUserByDni(
        @Path("dni") dni: Int,
        @Header("Authorization") token: String
    ): Response<UserResponse>

    @GET("/api/products/filterByCategory/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): Response<List<Product>>

    @POST("/api/cart/aggproduct")
    suspend fun addProductToCart(
        @Body cartItem: CartItemRequest
    ): Response<Void>


}