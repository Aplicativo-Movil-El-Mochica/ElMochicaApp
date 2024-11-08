package com.upao.elmochicaapp.models.responseModels


data class UserResponse(
    val status: String,
    val data: String // Cambia a String si solo devuelve el nombre en vez de un objeto
)
