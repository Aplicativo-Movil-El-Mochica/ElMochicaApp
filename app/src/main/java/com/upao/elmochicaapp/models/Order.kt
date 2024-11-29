package com.upao.elmochicaapp.models

data class Order(
    val id: String,
    val userId: String,
    val orderDate: String,
    val total: Double,
    val orderStatus: String,
    val statusCounter: Boolean,
    val details: List<OrderDetail> // Aquí se encuentra la lista de productos en el pedido
)