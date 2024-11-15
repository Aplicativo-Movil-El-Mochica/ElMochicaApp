package com.upao.elmochicaapp.models.requestModels

data class AddressRequest(
    val address: String,
    val reference: String?,
    val userId: String
)