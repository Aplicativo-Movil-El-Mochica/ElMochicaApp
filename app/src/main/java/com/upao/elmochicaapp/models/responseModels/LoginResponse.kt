package com.upao.elmochicaapp.models.responseModels

data class LoginResponse(
    val token: String // El backend debe devolver el token en una propiedad llamada `token`
)
