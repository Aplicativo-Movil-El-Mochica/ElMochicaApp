package com.upao.elmochicaapp.models

data class User(
    val name: String,
    val email: String,
    val dni: Int,
    val password: String,
    val phone: Int,
)
