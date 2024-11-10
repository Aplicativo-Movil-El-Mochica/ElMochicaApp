package com.upao.elmochicaapp.models

// CartProduct.kt
data class CartProduct(
    val id: String,
    val productName: String,
    var amount: Int,
    val price: Double,
    val availability: Int
)

