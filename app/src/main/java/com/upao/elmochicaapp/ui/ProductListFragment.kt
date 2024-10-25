package com.upao.elmochicaapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upao.elmochicaapp.R
import com.upao.elmochicaapp.adapters.ProductAdapter
import com.upao.elmochicaapp.api.apiClient.ApiClient
import kotlinx.coroutines.launch

class ProductListFragment : Fragment() {

    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getString("CATEGORY") ?: "Entradas"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_products)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Llamar a la API usando Coroutines
        lifecycleScope.launch {
            try {
                val products = ApiClient.apiService.getProductsByCategory(category)
                recyclerView.adapter = ProductAdapter(products) {
                    // Lógica para agregar al carrito
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Manejar el error de la API
            }
        }
    }
}
