package com.upao.elmochicaapp.com.upao.elmochicaapp.ui

interface PayCallback {
    fun onSuccess(transactionId: String)
    fun onFailure(errorMessage: String)
    fun onCancel()
}