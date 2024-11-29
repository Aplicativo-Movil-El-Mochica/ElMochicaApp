package com.upao.elmochicaapp.models.requestModels


data class PaymentRequest(
    val userId: String,
    val amount: Int, // Monto en centavos
    val currency: String,
    val orderId: String,
    val cardNumber: String,
    val expirationDate: String,
    val cvc: String,
    val cardholderName: String,
    val email: String,
    val address: String
)
