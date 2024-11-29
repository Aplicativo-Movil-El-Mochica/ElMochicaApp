package com.upao.elmochicaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.upao.elmochicaapp.R
import com.upao.elmochicaapp.databinding.ItemOrderDetailBinding
import com.upao.elmochicaapp.models.OrderDetail

class OrderDetailAdapter(private val orderDetails: List<OrderDetail>) : RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail, parent, false)
        return OrderDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        val orderDetail = orderDetails[position]

        // Cargar la imagen del producto usando Glide
        Glide.with(holder.itemView.context)
            .load(orderDetail.imageUrl)
            .into(holder.productImage)

        // Asignar los demás datos
        holder.productName.text = orderDetail.productName
        holder.productQuantity.text = "Cantidad: ${orderDetail.amount}"
    }

    override fun getItemCount(): Int = orderDetails.size

    class OrderDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productQuantity: TextView = itemView.findViewById(R.id.product_quantity)
    }
}



