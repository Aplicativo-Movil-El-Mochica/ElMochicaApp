package com.upao.elmochicaapp.models

data class Product(
    val productName: String,
    val description: String,
    val price: Double,
    val image: Int // Puedes agregar un campo para el ID del recurso de imagen
)
