package com.upao.elmochicaapp.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upao.elmochicaapp.R
import com.upao.elmochicaapp.adapters.OrderDetailAdapter
import com.upao.elmochicaapp.api.apiClient.ApiClient
import com.upao.elmochicaapp.models.OrderDetail
import com.upao.elmochicaapp.models.Order
import com.upao.elmochicaapp.models.requestModels.StatusRequest
import kotlinx.coroutines.launch

class MyOrderActivity : BaseActivity() {

    private var handler: Handler? = null
    private var updateTimeRunnable: Runnable? = null
    private var currentOrder: Order? = null

    private lateinit var orderItemsRecyclerView: RecyclerView
    private lateinit var orderTimeTextView: TextView
    private lateinit var orderDetailAdapter: OrderDetailAdapter
    private val orderDetails = mutableListOf<OrderDetail>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_order)

        orderItemsRecyclerView = findViewById(R.id.order_items_recycler)
        orderItemsRecyclerView.layoutManager = LinearLayoutManager(this)
        orderTimeTextView = findViewById(R.id.order_time)

        val userId = getUserId() ?: ""
        fetchOrderDetails(userId)
    }

    private fun fetchOrderDetails(userId: String) {
        lifecycleScope.launch {
            try {
                // Obtener la lista de órdenes
                val response = ApiClient.apiService3.getOrder(userId)
                if (response.isSuccessful) {
                    val orderList = response.body() ?: emptyList()
                    val order = orderList.firstOrNull()

                    // Si existe el pedido y tiene detalles
                    if (order != null && order.details.isNotEmpty()) {
                        currentOrder = order
                        val orderDetailList = order.details

                        // Actualiza el RecyclerView con los detalles de la orden
                        val updatedOrderDetails = orderDetailList.map { orderDetail ->
                            val encodedProductName = orderDetail.productName.replace(" ", "%20")
                            val imageUrl = "https://pacohunter.alwaysdata.net/uploads/$encodedProductName.jpg"
                            orderDetail.copy(imageUrl = imageUrl)
                        }

                        orderDetails.clear()
                        orderDetails.addAll(updatedOrderDetails)
                        orderDetailAdapter = OrderDetailAdapter(orderDetails)
                        orderItemsRecyclerView.adapter = orderDetailAdapter

                        if (order.statusCounter == false) {
                            // Si statusCounter es false, iniciar el contador
                            val startTime = System.currentTimeMillis()  // Guardamos la hora de inicio
                            saveStartTime(order.id, startTime)  // Guardamos la hora en SharedPreferences

                            // Actualizar el statusCounter en la base de datos
                            updateOrderStatusCounter(order.id)

                            // Actualizamos la UI con el tiempo restante
                            startUpdatingTime(order.id)
                        } else {
                            // Si statusCounter es true, continuar con el proceso existente
                            val startTime = getSavedStartTime(order.id)
                            if (startTime != -1L) {
                                // Calcular el tiempo restante
                                startUpdatingTime(order.id)
                            }
                        }
                    } else {
                        Toast.makeText(this@MyOrderActivity, "No se encontró el pedido o no tiene detalles.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MyOrderActivity, "Error al obtener detalles del pedido", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MyOrderActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startUpdatingTime(orderId: String) {
        // Obtener el tiempo de inicio desde SharedPreferences
        val startTime = getSavedStartTime(orderId)
        if (startTime != -1L) {
            // Creamos un Handler y un Runnable para actualizar el tiempo cada segundo
            handler = Handler(mainLooper)
            updateTimeRunnable = object : Runnable {
                override fun run() {
                    val timeRemaining = calculateRemainingTime(startTime)
                    updateTimeTextView(timeRemaining)

                    // Si el tiempo restante es mayor a 0, seguir actualizando
                    if (timeRemaining > 0) {
                        handler?.postDelayed(this, 1000)  // Actualizamos cada 1 segundo
                    } else {
                        // Si ya pasó el tiempo, actualizamos el estado de la orden
                        updateOrderStatus(orderId)
                        // Eliminar la hora de inicio de SharedPreferences para evitar conflictos
                        removeStartTimeFromSharedPrefs(orderId)
                    }
                }
            }

            // Iniciar la actualización
            updateTimeRunnable?.run()
        }
    }

    private fun updateTimeTextView(timeRemaining: Int) {
        val minutes = timeRemaining / 60
        val seconds = timeRemaining % 60
        orderTimeTextView.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun calculateRemainingTime(startTime: Long): Int {
        val elapsedTime = System.currentTimeMillis() - startTime  // Tiempo transcurrido desde el inicio en milisegundos
        val timeRemaining = (5 * 60 * 1000 - elapsedTime) / 1000  // Calculamos el tiempo restante en segundos

        return if (timeRemaining > 0) {
            timeRemaining.toInt()  // Si todavía queda tiempo
        } else {
            0  // Si ya pasó el tiempo, devolvemos 0
        }
    }

    private fun updateOrderStatus(orderId: String) {
        lifecycleScope.launch {
            try {
                val request = StatusRequest(newStatus = "EN_PROCESO")
                val response = ApiClient.apiService3.actualizarStatus(orderId, request)
                if (response.isSuccessful) {
                    Toast.makeText(this@MyOrderActivity, "Estado actualizado a EN_PROCESO", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MyOrderActivity, "No se pudo actualizar el estado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MyOrderActivity, "Error al actualizar el estado de la orden: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateOrderStatusCounter(orderId: String) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService3.actualizarCounter(orderId)
                if (response.isSuccessful) {
                    // Status counter actualizado a true
                } else {
                    // Error al actualizar el status counter
                }
            } catch (e: Exception) {
                // Error en la conexión
            }
        }
    }

    private fun saveStartTime(orderId: String, startTime: Long) {
        val sharedPreferences = getSharedPreferences("orderPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong(orderId, startTime)
        editor.apply()
    }

    private fun getSavedStartTime(orderId: String): Long {
        val sharedPreferences = getSharedPreferences("orderPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getLong(orderId, -1)  // Si no existe, devolvemos -1
    }

    private fun removeStartTimeFromSharedPrefs(orderId: String) {
        val sharedPreferences = getSharedPreferences("orderPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(orderId)  // Eliminamos la hora de inicio
        editor.apply()
    }

    override fun onPause() {
        super.onPause()
        // Detener el Handler cuando la actividad se pausa
        handler?.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        // Si la actividad se reanuda, reiniciar la actualización del tiempo
        currentOrder?.let { order ->
            val startTime = getSavedStartTime(order.id)
            if (startTime != -1L) {
                startUpdatingTime(order.id)
            }
        }
    }
}
