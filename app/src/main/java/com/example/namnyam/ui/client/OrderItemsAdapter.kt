package com.example.namnyam.ui.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.namnyam.data.remote.dto.OrderItemDto
import com.example.namnyam.databinding.ItemOrderProductBinding

class OrderItemsAdapter : RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder>() {

    private val items = mutableListOf<OrderItemDto>()

    fun submitList(newItems: List<OrderItemDto>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class OrderItemViewHolder(
        private val binding: ItemOrderProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderItemDto) {
            binding.tvProductName.text = item.productName
            binding.tvProductQuantity.text = "${item.quantity} шт."
            binding.tvProductPrice.text = "${item.productPrice.toInt()} ₽"
            binding.tvProductTotal.text = "${item.totalPrice.toInt()} ₽"
        }
    }
}