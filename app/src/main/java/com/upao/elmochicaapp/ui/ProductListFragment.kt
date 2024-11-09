package com.upao.elmochicaapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upao.elmochicaapp.R
import com.upao.elmochicaapp.adapters.ProductAdapter
import com.upao.elmochicaapp.api.apiClient.ApiClient
import com.upao.elmochicaapp.models.Product
import kotlinx.coroutines.launch

class ProductListFragment : Fragment() {

    private lateinit var category: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getString("CATEGORY") ?: "ENTRADAS"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_list, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_products)
        recyclerView.layoutManager = LinearLayoutManager(context)
        productAdapter = ProductAdapter(emptyList()) { product ->
            // Lógica para agregar al carrito
        }
        recyclerView.adapter = productAdapter

        // Llama a la API para obtener los productos de la categoría
        fetchProductsByCategory()

        return view
    }

    private fun fetchProductsByCategory() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService2.getProductsByCategory(category)
                if (response.isSuccessful) {
                    val products = response.body() ?: emptyList()
                    productAdapter.updateProducts(products)
                } else {
                    Toast.makeText(context, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun filterProducts(query: String) {
        productAdapter.filter(query)
    }

    companion object {
        fun newInstance(category: String): ProductListFragment {
            val fragment = ProductListFragment()
            val args = Bundle()
            args.putString("CATEGORY", category)
            fragment.arguments = args
            return fragment
        }
    }
}

