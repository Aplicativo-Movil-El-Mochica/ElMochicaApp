// OrderActivity.kt
package com.upao.elmochicaapp.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import com.upao.elmochicaapp.R
import com.upao.elmochicaapp.api.apiClient.ApiClient
import com.upao.elmochicaapp.models.requestModels.AddressRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderActivity : BaseActivity() {

    private lateinit var sectionRecojo: LinearLayout
    private lateinit var sectionDelivery: LinearLayout
    private lateinit var radioGroupTipoPedido: RadioGroup
    private lateinit var direccionEntrega: EditText
    private lateinit var referenciaDomicilio: EditText
    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        // Configurar la Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Inicializar las vistas
        sectionRecojo = findViewById(R.id.section_recojo)
        sectionDelivery = findViewById(R.id.section_delivery)
        radioGroupTipoPedido = findViewById(R.id.radio_group_tipo_pedido)
        direccionEntrega = findViewById(R.id.et_direccion_entrega)
        referenciaDomicilio = findViewById(R.id.et_referencia_domicilio)
        confirmButton = findViewById(R.id.btn_hacer_pedido)

        // Mostrar/ocultar secciones según la selección del radio button
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

        // Acción al hacer clic en el botón de confirmación
        confirmButton.setOnClickListener {
            sendAddress()
        }
    }

    private fun sendAddress() {
        val userId = getUserId()

        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "ID de usuario no encontrado. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
            return
        }

        val address = direccionEntrega.text.toString()
        val reference = referenciaDomicilio.text.toString().takeIf { it.isNotEmpty() }

        if (address.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa la dirección de entrega", Toast.LENGTH_SHORT).show()
            return
        }

        val addressRequest = AddressRequest(
            address = address,
            reference = reference,
            userId = userId
        )

        // Obtener el token utilizando la función getToken() de BaseActivity
        val token = getToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token no encontrado. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
            return
        }

        // Realizar la llamada con el encabezado de autorización
        val call = ApiClient.apiService.saveAddress("Bearer $token", addressRequest)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@OrderActivity, "Dirección guardada exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("OrderActivity", "Error al guardar la dirección: Código ${response.code()}, mensaje: ${response.message()}")
                    Toast.makeText(this@OrderActivity, "Error al guardar la dirección", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("OrderActivity", "Error de conexión: ${t.message}")
                Toast.makeText(this@OrderActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
