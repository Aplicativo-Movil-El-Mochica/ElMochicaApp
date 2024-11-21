package com.upao.elmochicaapp.com.upao.elmochicaapp.models

import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

class IziPaySignatureGenerator {

    private val secretKey = "Gf2RJSniMkeN0z8g9NoEivUTfCb8ivXw9BLil9wIcg7G4"  // Reemplazar con la clave correcta

    @RequiresApi(Build.VERSION_CODES.O)
    fun generatePaymentParams(amount: String): Map<String, String> {
        val vadsTransId = generateRandomTransId()
        val vadsTransDate = generateCurrentTransDate()

        val params = mutableMapOf(
            "vads_action_mode" to "INTERACTIVE",
            "vads_amount" to amount,
            "vads_ctx_mode" to "TEST",
            "vads_currency" to "604", // Código para PEN (Soles peruanos)
            "vads_language" to "ES",
            "vads_page_action" to "PAYMENT",
            "vads_payment_config" to "SINGLE",
            "vads_site_id" to "60242485", // Cambiar por tu ID de tienda correcto
            "vads_trans_date" to vadsTransDate,
            "vads_trans_id" to vadsTransId,
            "vads_version" to "V2",
        )

        // Generar la firma
        val signature = generateSignature(params)
        //  val signature = "1fgJ2mca68bydExOUT5VvpcKVKNCU285tw4gKkhPRXc="
        params["signature"] = signature

        return params
    }

    private fun generateRandomTransId(): String {
        return Random.nextInt(100000, 999999).toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateCurrentTransDate(): String {
        val now = java.time.Instant.now()
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            .withZone(java.time.ZoneOffset.UTC)
        return formatter.format(now)
    }

    private fun generateSignature(params: Map<String, String>): String {
        // Filtrar solo los parámetros que empiezan con "vads_" y ordenarlos alfabéticamente
        val sortedParams = params.filterKeys { it.startsWith("vads_") }.toSortedMap()

        // Concatenar los valores de los parámetros separados por "+"
        val concatenatedValues = StringBuilder()
        sortedParams.forEach { (_, value) ->
            concatenatedValues.append(value).append("+")
        }

        // Eliminar el último "+" sobrante
        concatenatedValues.setLength(concatenatedValues.length - 1)

        // Agregar la clave secreta al final, separada por "+"
        concatenatedValues.append("+").append(secretKey)

        // Imprimir la cadena concatenada para ver qué se está enviando a la firma (debug)
        Log.d("IziPaySignatureGenerator", "Cadena concatenada para la firma: $concatenatedValues")

        // Generar el hash HMAC-SHA-256
        val hmacSha256 = "HmacSHA256"
        val keySpec = SecretKeySpec(secretKey.toByteArray(StandardCharsets.UTF_8), hmacSha256)
        val mac = Mac.getInstance(hmacSha256)
        mac.init(keySpec)

        val hash = mac.doFinal(concatenatedValues.toString().toByteArray(StandardCharsets.UTF_8))

        // Codificar la firma en Base64
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }
}
