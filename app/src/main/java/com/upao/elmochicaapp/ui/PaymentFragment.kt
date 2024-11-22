package com.upao.elmochicaapp.com.upao.elmochicaapp.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.upao.elmochicaapp.R
import com.upao.elmochicaapp.api.apiClient.ApiClient
import com.upao.elmochicaapp.models.requestModels.FormtokenRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class PaymentFragment : Fragment() {

    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar el diseño del fragmento
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el contenedor del formulario
        val sdkFragmentContainer = view.findViewById<FrameLayout>(R.id.sdkFragmentContainer)

        // Asegúrate de que el contenedor no sea nulo
        if (sdkFragmentContainer == null) {
            Log.e("PaymentFragment", "sdkFragmentContainer no encontrado")
            return
        }

        // Inicializar y configurar el WebView
        webView = WebView(requireContext())
        sdkFragmentContainer.addView(webView)

        // Configuración del WebView
        setupWebView()

        // Obtener el userId y totalAmount desde los argumentos
        val userId = arguments?.getString("userId") ?: ""
        val totalAmount = arguments?.getDouble("totalAmount", 0.0) ?: 0.0

        // Generar un orderId aleatorio
        val orderId = generateRandomOrderId()

        // Crear el objeto de solicitud para obtener el formToken
        val formTokenRequest = FormtokenRequest(
            userId = userId,
            amount = (totalAmount * 100).toInt(), // Multiplica por 100 para tener el valor en centavos
            currency = "PEN",
            orderId = orderId
        )

        // Llamar al método para cargar el formulario de pago
        fetchFormTokenAndLoadPaymentForm(formTokenRequest)
    }

    // Función para generar un orderId aleatorio
    private fun generateRandomOrderId(): String {
        return "order-${(100000..999999).random()}"
    }




    private fun setupWebView() {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.allowFileAccess = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webView.webViewClient = WebViewClient()
    }


    private fun fetchFormTokenAndLoadPaymentForm(request: FormtokenRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Llamar al API para obtener el formToken
                val response: Response<Map<String, String>> = ApiClient.apiService3.getFormToken(request)
                if (response.isSuccessful && response.body() != null) {
                    val formToken = response.body()?.get("formtoken")
                    if (formToken != null) {
                        withContext(Dispatchers.Main) {
                            Log.d("PaymentFragment", "FormToken: $formToken")
                            loadPaymentForm(formToken)
                        }
                    } else {
                        // Manejo de error: El formToken no está en la respuesta
                        handleError("FormToken no recibido")
                    }
                } else {
                    // Manejo de error: Respuesta fallida
                    handleError("Error en la solicitud: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handleError("Error en la conexión: ${e.message}")
            }
        }
    }

    private fun loadPaymentForm(formToken: String) {

        val publicKey = "60242485:testpublickey_4tAGI3LtHLJjV4rPLvFDg4jT4PnUvh0vKatUPWz7ApxZM" // Reemplaza con tu clave pública
        val htmlData = """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
        <script type="text/javascript"
            src="https://static.micuentaweb.pe/static/js/krypton-client/V4.0/stable/kr-payment-form.min.js"
            kr-public-key="$publicKey"
            kr-form-token="$formToken"
            kr-hide-payment-types="REGISTER"> <!-- Este cambio oculta el botón de pago -->
        </script>
        <link rel="stylesheet" href="https://static.micuentaweb.pe/static/js/krypton-client/V4.0/ext/neon-reset.css">
        <style>
            .kr-embedded {
                width: 100%;
                margin: 0 auto;
            }
            .flex-container {
                display: flex;
                flex-direction: row;
                justify-content: space-between;
                gap: 10px;
            }
            .kr-payment-button {
                display: none; /* Asegura que el botón de pago no sea visible */
            }
        </style>
    </head>
    <body>
        <div class="kr-embedded" kr-form-token="$formToken">
            <!-- El botón de pago no se mostrará debido al estilo anterior -->
            <button class="kr-payment-button"></button>
            
        </div>
    </body>
    </html>
    """.trimIndent()

        webView.loadDataWithBaseURL("https://static.micuentaweb.pe/", htmlData, "text/html", "UTF-8", null)
    }




    private fun handleError(message: String) {
        // Mostrar un mensaje al usuario con un Toast
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }
}
