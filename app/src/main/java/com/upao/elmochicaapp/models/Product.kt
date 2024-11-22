package com.upao.elmochicaapp.models

data class Product(
    val productName: String,
    val description: String,
    val price: Int,
    val imageUrl: String // Puedes agregar un campo para el ID del recurso de imagen
)
