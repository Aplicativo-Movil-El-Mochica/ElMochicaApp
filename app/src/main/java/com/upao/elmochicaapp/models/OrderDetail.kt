package com.upao.elmochicaapp.models

data class OrderDetail(
    val id: String,
    val productName: String,
    val amount: Int,
    val priceTotal: Double,
    val priceUnit: Double,
    val availability: Int,
    val imageUrl: String? = null
)

