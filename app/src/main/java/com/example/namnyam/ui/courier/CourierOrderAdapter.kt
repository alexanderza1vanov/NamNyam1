package com.example.namnyam.ui.courier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.databinding.ItemCourierOrderBinding
import java.util.Locale

class CourierOrdersAdapter(
    private val onOrderClick: (OrderDto) -> Unit,
    private val onMainActionClick: (OrderDto) -> Unit,
    private val onSecondaryActionClick: (OrderDto) -> Unit
) : ListAdapter<OrderDto, CourierOrdersAdapter.CourierOrderViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourierOrderViewHolder {
        val binding = ItemCourierOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CourierOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourierOrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CourierOrderViewHolder(
        private val binding: ItemCourierOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderDto) {
            binding.tvOrderNumber.text = "Заказ №${order.id}"
            binding.tvStatus.text = getStatusText(order.status)
            binding.tvAddress.text = order.deliveryAddress
            binding.tvPrice.text = "Сумма: ${formatMoney(order.totalPrice)}"

            binding.root.setOnClickListener {
                onOrderClick(order)
            }

            when (order.status) {
                "READY_FOR_DELIVERY" -> {
                    binding.btnMainAction.visibility = View.VISIBLE
                    binding.btnSecondaryAction.visibility = View.GONE
                    binding.btnMainAction.text = "Забрать"
                    binding.btnMainAction.setOnClickListener { onMainActionClick(order) }
                }

                "PICKED_UP" -> {
                    binding.btnMainAction.visibility = View.VISIBLE
                    binding.btnSecondaryAction.visibility = View.GONE
                    binding.btnMainAction.text = "В путь"
                    binding.btnMainAction.setOnClickListener { onMainActionClick(order) }
                }

                "ON_THE_WAY" -> {
                    binding.btnMainAction.visibility = View.VISIBLE
                    binding.btnSecondaryAction.visibility = View.VISIBLE
                    binding.btnMainAction.text = "Доставлен"
                    binding.btnSecondaryAction.text = "Не удалось"

                    binding.btnMainAction.setOnClickListener { onMainActionClick(order) }
                    binding.btnSecondaryAction.setOnClickListener { onSecondaryActionClick(order) }
                }

                else -> {
                    binding.btnMainAction.visibility = View.GONE
                    binding.btnSecondaryAction.visibility = View.GONE
                }
            }
        }

        private fun formatMoney(value: Double): String {
            return String.format(Locale.getDefault(), "%.0f ₽", value)
        }

        private fun getStatusText(status: String): String {
            return when (status) {
                "READY_FOR_DELIVERY" -> "Готов к выдаче"
                "PICKED_UP" -> "Забран"
                "ON_THE_WAY" -> "В пути"
                "DELIVERED" -> "Доставлен"
                "DELIVERY_FAILED" -> "Не доставлен"
                "CANCELLED" -> "Отменён"
                else -> status
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<OrderDto>() {
        override fun areItemsTheSame(oldItem: OrderDto, newItem: OrderDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderDto, newItem: OrderDto): Boolean {
            return oldItem == newItem
        }
    }
}