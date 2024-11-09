package com.upao.elmochicaapp.models.requestModels

data class CartItemRequest(
    val productName: String,
    val amount: Int,
    val price: Double,
    val userId: String
)
