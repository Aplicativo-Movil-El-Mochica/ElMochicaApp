package com.upao.elmochicaapp.models

import com.izipay.izipay_pw_sdk.data.model.BillingPaymentIzipay
import com.izipay.izipay_pw_sdk.data.model.OrderPaymentIzipay
import com.izipay.izipay_pw_sdk.data.model.ShippingPaymentIzipay
import com.izipay.izipay_pw_sdk.data.model.TokenPaymentIzipay
import com.izipay.izipay_pw_sdk.data.model.AppearencePaymentIzipay
import java.io.Serializable

data class ConfigRequest(
    var environment: String?,
    var action: String?,
    var publicKey: String?,
    var transactionId: String?,
    var merchantCode: String?,
    var facilitatorCode: String?,
    var order: OrderPaymentIzipay?,
    var token: TokenPaymentIzipay?,
    var billing: BillingPaymentIzipay?,
    var shipping: ShippingPaymentIzipay?,
    var appearance: AppearencePaymentIzipay?,
    var urlIPN: String?
) : Serializable
