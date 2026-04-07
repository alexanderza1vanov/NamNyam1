package com.example.namnyam.ui.owner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.databinding.ItemOwnerOrderBinding
import java.util.Locale

class OwnerOrdersAdapter(
    private val onOrderClick: (OrderDto) -> Unit,
    private val onConfirmClick: (OrderDto) -> Unit,
    private val onCookingClick: (OrderDto) -> Unit,
    private val onReadyClick: (OrderDto) -> Unit,
    private val onCancelClick: (OrderDto) -> Unit
) : ListAdapter<OrderDto, OwnerOrdersAdapter.OwnerOrderViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerOrderViewHolder {
        val binding = ItemOwnerOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OwnerOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OwnerOrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OwnerOrderViewHolder(
        private val binding: ItemOwnerOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderDto) {
            binding.tvOrderId.text = "Заказ #${order.id}"
            binding.tvStatus.text = getStatusText(order.status)
            binding.tvCreatedAt.text = order.createdAt
                .replace("T", " ")
                .replace("Z", "")
            binding.tvTotal.text = "Сумма: ${formatMoney(order.totalPrice)}"
            binding.tvAddress.text = "Адрес: ${order.deliveryAddress}"

            binding.tvItems.text = if (order.items.isEmpty()) {
                "Состав заказа недоступен"
            } else {
                order.items.joinToString(", ") { item ->
                    "${item.productName} × ${item.quantity}"
                }
            }

            val canConfirm = order.status == "CREATED"
            val canCooking = order.status == "CONFIRMED"
            val canReady = order.status == "COOKING"
            val canCancel = order.status == "CREATED" ||
                    order.status == "CONFIRMED" ||
                    order.status == "COOKING"

            binding.btnConfirm.visibility = if (canConfirm) View.VISIBLE else View.GONE
            binding.btnCooking.visibility = if (canCooking) View.VISIBLE else View.GONE
            binding.btnReady.visibility = if (canReady) View.VISIBLE else View.GONE
            binding.btnCancel.visibility = if (canCancel) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                onOrderClick(order)
            }

            binding.btnConfirm.setOnClickListener {
                onConfirmClick(order)
            }

            binding.btnCooking.setOnClickListener {
                onCookingClick(order)
            }

            binding.btnReady.setOnClickListener {
                onReadyClick(order)
            }

            binding.btnCancel.setOnClickListener {
                onCancelClick(order)
            }
        }

        private fun getStatusText(status: String): String {
            return when (status) {
                "CREATED" -> "Создан"
                "CONFIRMED" -> "Подтверждён"
                "COOKING" -> "Готовится"
                "READY_FOR_DELIVERY" -> "Готов к выдаче"
                "ASSIGNED_TO_COURIER" -> "Передан курьеру"
                "PICKED_UP" -> "Курьер забрал заказ"
                "ON_THE_WAY" -> "В пути"
                "DELIVERED" -> "Доставлен"
                "CANCELLED" -> "Отменён"
                else -> status
            }
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