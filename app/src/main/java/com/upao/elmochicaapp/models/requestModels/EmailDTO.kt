package com.upao.elmochicaapp.models.requestModels

data class EmailDTO(
    val nombre: String,
    val dni: String,
    val tipoPedido: String,
    val direccion: String,
    val fechaPedido: String,
    val resumenPedido: List<PedidoItem>,
    val total: Int,
    val destinatario: String
)
