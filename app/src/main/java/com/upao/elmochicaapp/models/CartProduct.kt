package com.upao.elmochicaapp.models

// CartProduct.kt
data class CartProduct(
    val id: String,
    val productName: String,
    var amount: Int,
    var priceUnit: Int, // Precio unitario del producto
    val availability: Int
)

