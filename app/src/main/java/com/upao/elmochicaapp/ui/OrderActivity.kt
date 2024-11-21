package com.upao.elmochicaapp.ui



import com.upao.elmochicaapp.com.upao.elmochicaapp.models.IziPaySignatureGenerator
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.upao.elmochicaapp.R
import com.upao.elmochicaapp.com.upao.elmochicaapp.ui.PayCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class OrderActivity : BaseActivity(), PayCallback {

    private lateinit var sectionRecojo: LinearLayout
    private lateinit var sectionDelivery: LinearLayout
    private lateinit var radioGroupTipoPedido: RadioGroup
    private lateinit var direccionEntrega: EditText
    private lateinit var referenciaDomicilio: EditText
    private lateinit var confirmButton: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        setupToolbar()
        initializeViews()
        handleRadioGroupSelection()
        setupConfirmButton()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar2)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun initializeViews() {
        sectionRecojo = findViewById(R.id.section_recojo)
        sectionDelivery = findViewById(R.id.section_delivery)
        radioGroupTipoPedido = findViewById(R.id.radio_group_tipo_pedido)
        direccionEntrega = findViewById(R.id.et_direccion_entrega)
        referenciaDomicilio = findViewById(R.id.et_referencia_domicilio)
        confirmButton = findViewById(R.id.btn_hacer_pedido)
    }

    private fun handleRadioGroupSelection() {
        radioGroupTipoPedido.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_recojo -> {
                    sectionRecojo.visibility = View.VISIBLE
                    sectionDelivery.visibility = View.GONE
                }
                R.id.radio_delivery -> {
                    sectionRecojo.visibility = View.GONE
                    sectionDelivery.visibility = View.VISIBLE
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupConfirmButton() {
        confirmButton.setOnClickListener { validateAndStartPayment() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateAndStartPayment() {
        val userId = getUserId() ?: run {
            Toast.makeText(this, "ID de usuario no encontrado. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
            return
        }

        val address = direccionEntrega.text.toString()
        val reference = referenciaDomicilio.text.toString().takeIf { it.isNotEmpty() }

        if (address.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa la dirección de entrega", Toast.LENGTH_SHORT).show()
            return
        }

        // Generar los parámetros de pago
        val amount = "2800" // Monto en centavos (por ejemplo, 28.00 soles = 2800 centavos)
        val iziPaySignatureGenerator = IziPaySignatureGenerator()
        val paymentParams = iziPaySignatureGenerator.generatePaymentParams(amount)

        // Lanzar el formulario de pago con los parámetros generados
        launchPaymentForm(paymentParams)
    }

    private var hasRedirected = false
    private var hasRegeneratedSignature = false

    private fun launchPaymentForm(paymentParams: Map<String, String>) {
        try {
            Log.d("OrderActivity", "Iniciando el pago con parámetros: $paymentParams")

            val paymentUrl = "https://secure.micuentaweb.pe/vads-payment/entry.silentInit.a"
            val webView = findViewById<WebView>(R.id.webView)
            webView.visibility = View.VISIBLE
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.d("WebViewClient", "onPageFinished URL: $url")

                    // Evitar redirecciones múltiples o doble procesamiento
                    if (hasRedirected) {
                        Log.d("WebViewClient", "Redirección ya realizada, no se procesa nuevamente.")
                        return
                    }

                    view?.evaluateJavascript(
                        "(function() { return document.body.innerText; })();"
                    ) { response ->
                        try {
                            // Convertir la respuesta en un JSON o texto para procesar
                            val jsonResponseString = response
                                .replace("\\\"", "\"") // Reemplazar caracteres escapados
                                .trim('"') // Eliminar comillas externas si existen
                            Log.d("WebViewClient", "Respuesta capturada (procesada): $jsonResponseString")

                            // Paso 1: Procesar la firma esperada
                            if (!hasRegeneratedSignature && jsonResponseString.contains("Cadena de caracteres esperada")) {
                                val signaturePattern = "Cadena de caracteres esperada \\(UTF-8\\) : \\[(.*?)\\]"
                                val regex = Regex(signaturePattern)
                                val matchResult = regex.find(jsonResponseString)

                                if (matchResult != null) {
                                    val expectedSignature = matchResult.groupValues[1]
                                    Log.d("OrderActivity", "Firma esperada capturada: $expectedSignature")

                                    // Regenerar los parámetros de pago con la nueva firma
                                    val updatedPaymentParams = paymentParams.toMutableMap().apply {
                                        put("signature", expectedSignature)
                                    }

                                    // Actualizar la bandera de regeneración
                                    hasRegeneratedSignature = true

                                    // Relanzar el formulario con la nueva firma
                                    launchPaymentForm(updatedPaymentParams)
                                    return@evaluateJavascript // Terminar aquí para evitar seguir procesando
                                }
                            }

                            // Paso 2: Procesar el redirect_url
                            val jsonResponse = JSONObject(jsonResponseString)

                            if (jsonResponse.has("redirect_url")) {
                                val redirectUrl = jsonResponse.getString("redirect_url")
                                Log.d("OrderActivity", "URL de redirección capturada: $redirectUrl")

                                // Configurar la bandera para evitar redirección múltiple
                                hasRedirected = true

                                // Redirigir automáticamente al redirect_url
                                view.post {
                                    view.loadUrl(redirectUrl)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("WebViewClient", "Error procesando la respuesta JSON: ${e.message}")
                        }
                    }
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    url?.let {
                        Log.d("WebViewClient", "shouldOverrideUrlLoading URL: $it")
                        if (it.contains("success")) {
                            onSuccess("transactionId") // Manejar éxito
                        } else if (it.contains("error") || it.contains("refused") || it.contains("cancel")) {
                            onFailure("Error en el pago")
                        } else {
                            view?.loadUrl(it) // Cargar redirección
                        }
                    }
                    return true
                }
            }

            val paymentHtml = StringBuilder().apply {
                append("<html><body onload=\"document.forms['paymentForm'].submit()\">")
                append("<form id=\"paymentForm\" action=\"$paymentUrl\" method=\"post\">")
                paymentParams.forEach { (key, value) ->
                    append("<input type=\"hidden\" name=\"$key\" value=\"$value\" />")
                }
                append("</form></body></html>")
            }.toString()

            webView.loadData(paymentHtml, "text/html", "UTF-8")

        } catch (e: Exception) {
            Log.e("OrderActivity", "Error al iniciar el pago: ${e.message}")
            Toast.makeText(this, "Error al iniciar el pago", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(transactionId: String) {
        Log.d("OrderActivity", "Pago exitoso con ID: $transactionId")
        Toast.makeText(this, "Pago exitoso", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(message: String) {
        Log.e("OrderActivity", "Error en el pago: $message")
        Toast.makeText(this, "Error en el pago: $message", Toast.LENGTH_SHORT).show()
    }

    override fun onCancel() {
        Log.d("OrderActivity", "El usuario canceló el pago")
        Toast.makeText(this, "Pago cancelado", Toast.LENGTH_SHORT).show()
    }
}
