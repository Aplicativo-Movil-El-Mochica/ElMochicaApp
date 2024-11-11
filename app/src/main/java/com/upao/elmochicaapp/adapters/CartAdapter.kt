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

        // Asegúrate de calcular siempre usando el precio unitario
        val unitPrice = product.price / product.amount

        // Configura la vista con los datos del producto
        holder.productName.text = product.productName
        holder.productPrice.text = "S/ ${unitPrice * product.amount}" // Calcula el precio total usando el precio unitario
        holder.productAmount.text = product.amount.toString()

        // Configura los botones de aumentar y disminuir cantidad
        holder.decreaseButton.setOnClickListener {
            if (product.amount > 1) {
                product.amount -= 1
                holder.productAmount.text = product.amount.toString()
                holder.productPrice.text = "S/ ${unitPrice * product.amount}" // Actualiza el precio total basado en la cantidad
                onDecrease(product) // Notifica el cambio al backend
            }
        }

        holder.increaseButton.setOnClickListener {
            product.amount += 1
            holder.productAmount.text = product.amount.toString()
            holder.productPrice.text = "S/ ${unitPrice * product.amount}" // Actualiza el precio total basado en la cantidad
            onIncrease(product) // Notifica el cambio al backend
        }
    }


    override fun getItemCount(): Int = cartProducts.size
}
