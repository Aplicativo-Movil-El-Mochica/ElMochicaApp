package com.upao.elmochicaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.upao.elmochicaapp.R
import com.upao.elmochicaapp.models.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onAddToCart: (Product) -> Unit // Callback para agregar al carrito
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var filteredProducts = products.toList()

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        filteredProducts = products.toList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredProducts = if (query.isEmpty()) {
            products
        } else {
            products.filter {
                it.productName.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.product_name)
        val productDescription: TextView = view.findViewById(R.id.product_description)
        val productPrice: TextView = view.findViewById(R.id.product_price)
        val addButton: ImageButton = view.findViewById(R.id.add_button) // Botón de agregar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = filteredProducts[position]
        holder.productName.text = product.productName
        holder.productDescription.text = product.description
        holder.productPrice.text = "S/ ${product.price}"

        // Configurar el botón de agregar para que llame al callback con el producto
        holder.addButton.setOnClickListener {
            Toast.makeText(holder.itemView.context, "${product.productName} agregado", Toast.LENGTH_SHORT).show()
            onAddToCart(product)
        }
    }

    override fun getItemCount(): Int = filteredProducts.size
}
