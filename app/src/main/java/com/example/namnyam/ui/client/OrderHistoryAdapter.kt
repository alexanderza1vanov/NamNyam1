package com.example.namnyam.ui.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.databinding.ItemOrderHistoryBinding

class OrdersHistoryAdapter(
    private val onOrderClick: (OrderDto) -> Unit,
    private val statusFormatter: (String) -> String,
    private val dateFormatter: (String) -> String,
    private val itemsFormatter: (OrderDto) -> String
) : RecyclerView.Adapter<OrdersHistoryAdapter.OrderHistoryViewHolder>() {

    private val items = mutableListOf<OrderDto>()

    fun submitList(newItems: List<OrderDto>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class OrderHistoryViewHolder(
        private val binding: ItemOrderHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderDto) {
            binding.tvOrderNumber.text = "Заказ №${order.id}"
            binding.tvStatus.text = statusFormatter(order.status)
            binding.tvDate.text = dateFormatter(order.createdAt)
            binding.tvItems.text = itemsFormatter(order)
            binding.tvTotal.text = "${order.totalPrice.toInt()} ₽"

            binding.root.setOnClickListener {
                onOrderClick(order)
            }
        }
    }
}