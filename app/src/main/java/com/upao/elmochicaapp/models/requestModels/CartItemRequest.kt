package com.upao.elmochicaapp.models.requestModels

data class CartItemRequest(
    val productName: String,
    val amount: Int,
    val priceUnit: Int, // Envía el precio unitario correctamente al agregar el producto
    val userId: String
)

