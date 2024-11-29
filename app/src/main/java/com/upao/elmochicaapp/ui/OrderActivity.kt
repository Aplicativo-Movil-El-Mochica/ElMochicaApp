package com.upao.elmochicaapp.ui

import android.content.Context
import android.content.Intent

import android.os.Build
import android.os.Bundle
import android.util.Log
import java.time.LocalDate
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.upao.elmochicaapp.R
import com.upao.elmochicaapp.api.apiClient.ApiClient
import com.upao.elmochicaapp.models.CartProduct
import com.upao.elmochicaapp.models.requestModels.EmailDTO
import com.upao.elmochicaapp.models.requestModels.PedidoItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class OrderActivity : AppCompatActivity(){

    private lateinit var sectionDelivery: LinearLayout
    private lateinit var radioGroupTipoPedido: RadioGroup
    private lateinit var direccionEntrega: EditText
    private lateinit var referenciaDomicilio: EditText
    private lateinit var confirmButton: Button
    private lateinit var productsContainer: LinearLayout

    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        setupToolbar()
        initializeViews()
        handleRadioGroupSelection()
        setupConfirmButton()

        // Llama a esta función para cargar los productos
        loadProductsFromCart()
        loadPaymentFragment()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar2)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun initializeViews() {
        sectionDelivery = findViewById(R.id.section_delivery)
        radioGroupTipoPedido = findViewById(R.id.radio_group_tipo_pedido)
        direccionEntrega = findViewById(R.id.et_direccion_entrega)
        referenciaDomicilio = findViewById(R.id.et_referencia_domicilio)
        confirmButton = findViewById(R.id.btn_hacer_pedido)
        productsContainer = findViewById(R.id.products_container)

    }


    private fun handleRadioGroupSelection() {
        // Dirección predeterminada para "Recojo en restaurante"
        val direccionRecojo = "Calle Sta. Mariana 146 Urb. La Merced"

        radioGroupTipoPedido.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_recojo -> {
                    // Configuración para "Recojo en restaurante"
                    direccionEntrega.setText(direccionRecojo) // Fijar dirección
                    direccionEntrega.isEnabled = false // Desactivar edición
                    referenciaDomicilio.setText("") // Limpiar referencia
                    referenciaDomicilio.isEnabled = false // Desactivar referencia
                }
                R.id.radio_delivery -> {
                    // Configuración para "Delivery a domicilio"
                    direccionEntrega.setText("") // Limpiar campo
                    direccionEntrega.isEnabled = true // Habilitar edición
                    referenciaDomicilio.setText("") // Limpiar referencia
                    referenciaDomicilio.isEnabled = true // Habilitar edición
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupConfirmButton() {
        confirmButton.setOnClickListener {
            lifecycleScope.launch {
            validateAndStartPayment() // Llama a la función suspend desde una coroutine
        } }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun validateAndStartPayment() {
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

        val fechaPedido = LocalDate.now().toString()

        val emailDTO = EmailDTO(
            nombre = getName() ?: "Nombre desconocido",   // Reemplazar con el nombre real del usuario
            dni = getUserDni().toString(),       // Reemplazar con el DNI real del usuario
            tipoPedido = if (radioGroupTipoPedido.checkedRadioButtonId == R.id.radio_recojo) "Recojo en restaurante" else "Delivery a domicilio",
            direccion = address,
            fechaPedido = fechaPedido,  // Reemplazar con la fecha actual o la que corresponda
            resumenPedido = loadProductsFromCart(),  // Aquí puedes obtener el resumen de los productos del carrito
            total = getTotalAmount(userId).toInt(),  // Calcula el total
            destinatario = getCorreo() ?: "juan089207@gmail.com"  // Reemplazar con el correo del destinatario
        )

        Log.d("OrderActivity", "Email por enviar: ${emailDTO}")

        val token = "Bearer " + getJwtToken()  // Asegúrate de que el token esté configurado correctamente
        Log.d("OrderActivity", "JWT: ${token}")

        lifecycleScope.launch {
            try {
                    val response = ApiClient.apiService.sendVoucher(token = token, emailDTO = emailDTO)
                    if (response.isSuccessful) {
                        Log.d("OrderActivity", "Voucher enviado con éxito")
                        Toast.makeText(this@OrderActivity, "Voucher enviado con éxito", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@OrderActivity, MyOrderActivity::class.java)
                        startActivity(intent)
                    } else if (response.code() == 403) {
                        Toast.makeText(this@OrderActivity, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@OrderActivity, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@OrderActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadPaymentFragment() {
        // Inicia una Coroutine en el hilo principal
        CoroutineScope(Dispatchers.Main).launch {
            val userId = getUserId()  // Obtén el userId
            if (userId != null) {
                // Espera de forma asíncrona para obtener el total
                val totalAmount = getTotalAmount(userId)  // Obtén el total con la cuota de envío

                // Crear un nuevo Bundle con los valores que queremos pasar al fragmento
                val bundle = Bundle().apply {
                    putString("userId", userId)  // Pasa el userId
                    putDouble("totalAmount", totalAmount)  // Pasa el totalAmount
                }

                // Crear una instancia del PaymentFragment
                val paymentFragment = PaymentFragment().apply {
                    arguments = bundle
                }

                // Reemplazar el fragmento en el contenedor
                supportFragmentManager.beginTransaction()
                    .replace(R.id.paymentFragmentContainer, paymentFragment)
                    .commit()
            } else {
                Toast.makeText(this@OrderActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadProductsFromCart() : List<PedidoItem> {
        val userId = getUserId()
        val pedidoItems = mutableListOf<PedidoItem>()
        if (userId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = ApiClient.apiService3.getCartProducts(userId)
                    if (response.isSuccessful) {
                        val cartProducts = response.body() ?: emptyList()

                        // Convertir cada producto del carrito a PedidoItem
                        cartProducts.forEach { product ->
                            val pedidoItem = PedidoItem(
                                nombre = product.productName,  // Nombre del producto
                                cantidad = product.amount,     // Cantidad del producto
                                precio = product.priceUnit * product.amount  // Precio total del producto
                            )
                            pedidoItems.add(pedidoItem)
                        }

                        withContext(Dispatchers.Main) {
                            updateProductsContainer(cartProducts)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@OrderActivity, "Error al obtener los productos del carrito", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@OrderActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
        }

        return pedidoItems
    }

    private fun updateProductsContainer(products: List<CartProduct>) {
        productsContainer.removeAllViews() // Limpia el contenedor antes de añadir productos

        var subtotal = 0.0 // Variable para calcular el subtotal

        // Agregar cada producto al contenedor
        for (product in products) {
            val productView = layoutInflater.inflate(R.layout.item_product_summary, productsContainer, false)

            val productNameTextView = productView.findViewById<TextView>(R.id.tv_product_name)
            val productPriceTextView = productView.findViewById<TextView>(R.id.tv_product_price)

            // Mostrar nombre del producto con la cantidad entre paréntesis
            productNameTextView.text = "${product.productName} (${product.amount})"

            // Calcular el precio total del producto según la cantidad
            val totalPrice = product.priceUnit * product.amount
            productPriceTextView.text = "S/ $totalPrice.00"

            subtotal += totalPrice // Acumular precio total en el subtotal
            productsContainer.addView(productView) // Añadir la vista del producto al contenedor
        }

        // Añadir cuota de envío como un item separado
        val shippingView = layoutInflater.inflate(R.layout.item_product_summary, productsContainer, false)
        val shippingNameTextView = shippingView.findViewById<TextView>(R.id.tv_product_name)
        val shippingPriceTextView = shippingView.findViewById<TextView>(R.id.tv_product_price)

        shippingNameTextView.text = "Cuota de envío"
        shippingPriceTextView.text = "S/ 5.00"

        subtotal += 5.0 // Sumar la cuota de envío al subtotal

        productsContainer.addView(shippingView) // Añadir la cuota de envío al contenedor

        // Actualizar el subtotal en la vista
        val subtotalTextView = findViewById<TextView>(R.id.tv_total)
        subtotalTextView.text = "Total a pagar\nS/ $subtotal.00"
    }


    private suspend fun getTotalAmount(userId: String): Double {
        return try {
            // Llamada a la API para obtener el subtotal
            val response = ApiClient.apiService3.getSubtotal(userId)

            if (response.isSuccessful) {
                // Si la respuesta es exitosa, obtener el subtotal
                val subtotal = response.body() ?: 0.0
                // Calcular el total sumando la cuota de envío (5 soles)
                val total = subtotal.toDouble() + 5.0
                total
            } else {
                // Si ocurre un error en la API, retornar 0.0
                0.0
            }
        } catch (e: Exception) {
            // En caso de error de conexión, retornar 0.0
            0.0
        }
    }



    protected fun getUserId(): String? {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("USER_ID", null)
    }

    private fun getJwtToken(): String? {
        // Implementa la lógica para obtener el JWT almacenado en SharedPreferences
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("JWT_TOKEN", null)
    }

    private fun getName(): String? {
        // Implementa la lógica para obtener el JWT almacenado en SharedPreferences
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("NAME", null)
    }

    private fun getCorreo(): String? {
        // Implementa la lógica para obtener el JWT almacenado en SharedPreferences
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("CORREO", null)
    }

    private fun getUserDni(): Int {
        // Devuelve el DNI del usuario almacenado
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getInt("USER_DNI", 0)
    }
}