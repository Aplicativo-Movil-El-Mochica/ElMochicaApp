package com.upao.elmochicaapp.models.requestModels

data class FormtokenRequest(
    val userId: String,
    val amount: Int,
    val currency: String, // Envía el precio unitario correctamente al agregar el producto
    val orderId: String
)
