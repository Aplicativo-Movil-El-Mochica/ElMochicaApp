package com.upao.elmochicaapp.models.requestModels

data class ModifyCartRequest(
    val cartProductId: String,
    val newamount: Int,
    val action: String
)