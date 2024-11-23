// CartAdapter.kt
package com.upao.elmochicaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.upao.elmochicaapp.R
import com.upao.elmochicaapp.models.CartProduct

class CartAdapter(
    private val cartProducts: List<CartProduct>,
    private val onIncrease: (CartProduct) -> Unit,
    private val onDecrease: (CartProduct) -> Unit,
    private val onDelete: (CartProduct) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.product_image)
        val productName: TextView = view.findViewById(R.id.product_name)
        val productPrice: TextView = view.findViewById(R.id.product_price)
        val productAmount: TextView = view.findViewById(R.id.product_amount)
        val decreaseButton: ImageButton = view.findViewById(R.id.decrease_button)
        val increaseButton: ImageButton = view.findViewById(R.id.increase_button)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartProducts[position]

        holder.productName.text = product.productName
        holder.productPrice.text = "S/ ${product.priceUnit * product.amount}" // Calcula el precio total
        holder.productAmount.text = product.amount.toString()

        // Cargar la imagen usando Glide
        Glide.with(holder.itemView.context)
            .load(product.imageUrl) // URL de la imagen
            .placeholder(R.drawable.placeholder_image) // Imagen de carga
            .error(R.drawable.error_image) // Imagen de error
            .into(holder.productImage)

        holder.decreaseButton.setOnClickListener {
            if (product.amount > 1) {
                onDecrease(product)
            }
        }

        holder.increaseButton.setOnClickListener {
            onIncrease(product)
        }

        holder.deleteButton.setOnClickListener {
            onDelete(product)
        }
    }


    override fun getItemCount(): Int = cartProducts.size
}
