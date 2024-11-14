// CartAdapter.kt
package com.upao.elmochicaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.upao.elmochicaapp.R
import com.upao.elmochicaapp.models.CartProduct

class CartAdapter(
    private val cartProducts: List<CartProduct>,
    private val onIncrease: (CartProduct) -> Unit,
    private val onDecrease: (CartProduct) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.product_image)
        val productName: TextView = view.findViewById(R.id.product_name)
        val productPrice: TextView = view.findViewById(R.id.product_price)
        val productAmount: TextView = view.findViewById(R.id.product_amount)
        val decreaseButton: ImageButton = view.findViewById(R.id.decrease_button)
        val increaseButton: ImageButton = view.findViewById(R.id.increase_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartProducts[position]

        holder.productName.text = product.productName
        holder.productPrice.text = "S/ ${product.priceUnit * product.amount}" // Calcula el precio total en tiempo real
        holder.productAmount.text = product.amount.toString()

        holder.decreaseButton.setOnClickListener {
            if (product.amount > 1) {
                onDecrease(product)
            }
        }

        holder.increaseButton.setOnClickListener {
            onIncrease(product)
        }
    }

    override fun getItemCount(): Int = cartProducts.size
}
