// CartActivity.kt
package com.upao.elmochicaapp.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upao.elmochicaapp.R
import com.upao.elmochicaapp.adapters.CartAdapter
import com.upao.elmochicaapp.api.apiClient.ApiClient
import com.upao.elmochicaapp.models.CartProduct
import com.upao.elmochicaapp.models.requestModels.ModifyCartRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvProductsLabel: TextView
    private lateinit var subtotalTextView: TextView
    private val cartProducts = mutableListOf<CartProduct>()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        recyclerView = findViewById(R.id.recycler_view_cart)
        tvProductsLabel = findViewById(R.id.tv_products_label)
        subtotalTextView = findViewById(R.id.subtotal_amount)
        recyclerView.layoutManager = LinearLayoutManager(this)

        cartAdapter = CartAdapter(cartProducts,
            onIncrease = { product -> modifyProductQuantity(product, "sumar") },
            onDecrease = { product ->
                if (product.amount > 1) {
                    modifyProductQuantity(product, "restar")
                } else {
                    Toast.makeText(this, "La cantidad mínima es 1", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView.adapter = cartAdapter

        val userId = getUserId()
        if (userId != null) {
            fetchCartProducts(userId)
            fetchSubtotal(userId)
        } else {
            Toast.makeText(this, "Error: usuario no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchCartProducts(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService3.getCartProducts(userId)
                if (response.isSuccessful) {
                    val fetchedProducts = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        cartProducts.clear()
                        cartProducts.addAll(fetchedProducts)
                        cartAdapter.notifyDataSetChanged()
                        updateProductsLabel(cartProducts.size)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CartActivity, "Error al obtener el carrito", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CartActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchSubtotal(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService3.getSubtotal(userId)
                if (response.isSuccessful) {
                    val subtotal = response.body() ?: 0
                    withContext(Dispatchers.Main) {
                        subtotalTextView.text = "S/ $subtotal.00"
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CartActivity, "Error al obtener el subtotal", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CartActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateProductsLabel(count: Int) {
        tvProductsLabel.text = "Productos ($count)"
    }

    private fun modifyProductQuantity(product: CartProduct, action: String) {
        // Siempre envía newamount = 1 y usa la acción para indicar si sumamos o restamos
        val request = ModifyCartRequest(cartProductId = product.id, newamount = 1, action = action)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService3.modifyCartProduct(request)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        // Solo después de recibir una respuesta exitosa, ajustamos la cantidad localmente
                        if (action == "sumar") {
                            product.amount += 1
                        } else if (action == "restar" && product.amount > 1) {
                            product.amount -= 1
                        }
                        cartAdapter.notifyDataSetChanged()
                        fetchSubtotal(getUserId() ?: "")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CartActivity, "Error al modificar el carrito", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CartActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
