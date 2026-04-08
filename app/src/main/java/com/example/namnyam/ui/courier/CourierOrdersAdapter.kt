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
            binding.tvOrderNumber.text = "Заказ #${order.id}"
            binding.tvStatus.text = getStatusText(order)
            binding.tvRestaurant.text = buildRestaurantText(order)
            binding.tvCreatedAt.text = formatCreatedAt(order.createdAt)
            binding.tvAddress.text = "Доставка: ${order.deliveryAddress}"
            binding.tvItems.text = if (order.items.isEmpty()) {
                "Состав заказа недоступен"
            } else {
                order.items.joinToString(", ") { item ->
                    "${item.productName} × ${item.quantity}"
                }
            }
            binding.tvPrice.text = "Сумма: ${formatMoney(order.totalPrice)}"

            val mainActionText = getMainActionText(order)
            if (mainActionText == null) {
                binding.btnMainAction.visibility = View.GONE
            } else {
                binding.btnMainAction.visibility = View.VISIBLE
                binding.btnMainAction.text = mainActionText
            }

            val canFail = order.status in setOf(
                "ASSIGNED_TO_COURIER",
                "PICKED_UP",
                "ON_THE_WAY"
            )
            binding.btnSecondaryAction.visibility = if (canFail) View.VISIBLE else View.GONE

            binding.root.setOnClickListener { onOrderClick(order) }
            binding.btnMainAction.setOnClickListener { onMainActionClick(order) }
            binding.btnSecondaryAction.setOnClickListener { onSecondaryActionClick(order) }
        }

        private fun getMainActionText(order: OrderDto): String? {
            return when {
                order.status == "READY_FOR_DELIVERY" && order.courierId == null -> "Взять заказ"
                order.status == "ASSIGNED_TO_COURIER" -> "Забрать из ресторана"
                order.status == "PICKED_UP" -> "В путь"
                order.status == "ON_THE_WAY" -> "Завершить доставку"
                else -> null
            }
        }

        private fun getStatusText(order: OrderDto): String {
            return when {
                order.status == "READY_FOR_DELIVERY" && order.courierId == null -> "Свободен для курьера"
                order.status == "ASSIGNED_TO_COURIER" -> "Взят курьером"
                order.status == "PICKED_UP" -> "Забран из ресторана"
                order.status == "ON_THE_WAY" -> "В пути"
                order.status == "DELIVERED" -> "Доставлен"
                order.status == "FAILED_DELIVERY" -> "Доставка не удалась"
                order.status == "CANCELED" -> "Отменён"
                else -> order.status
            }
        }

        private fun buildRestaurantText(order: OrderDto): String {
            return when {
                order.restaurantName.isNotBlank() && order.restaurantAddress.isNotBlank() -> {
                    "Ресторан: ${order.restaurantName}, ${order.restaurantAddress}"
                }
                order.restaurantName.isNotBlank() -> {
                    "Ресторан: ${order.restaurantName}"
                }
                else -> {
                    "Ресторан #${order.restaurantId}"
                }
            }
        }

        private fun formatCreatedAt(createdAt: String): String {
            return createdAt
                .replace("T", " ")
                .replace("Z", "")
        }

        private fun formatMoney(value: Double): String {
            return String.format(Locale.getDefault(), "%.0f ₽", value)
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