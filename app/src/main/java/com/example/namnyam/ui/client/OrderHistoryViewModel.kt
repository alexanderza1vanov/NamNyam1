package com.example.namnyam.ui.client

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.data.remote.network.RetrofitProvider
import com.example.namnyam.data.repository.OrderRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class OrdersHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val orderRepository = OrderRepository(
        RetrofitProvider.getApi(application)
    )

    var state: ((UiState<List<OrderDto>>) -> Unit)? = null

    fun loadOrders() {
        state?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val result = orderRepository.getMyOrders()

                result
                    .onSuccess { orders ->
                        val sortedOrders = orders.sortedByDescending { order ->
                            order.id
                        }
                        state?.invoke(UiState.Success(sortedOrders))
                    }
                    .onFailure { e ->
                        state?.invoke(
                            UiState.Error(e.message ?: "Не удалось загрузить историю заказов")
                        )
                    }

            } catch (e: Exception) {
                state?.invoke(
                    UiState.Error(e.message ?: "Не удалось загрузить историю заказов")
                )
            }
        }
    }

    fun getStatusText(status: String): String {
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

    fun formatCreatedAt(createdAt: String): String {
        return createdAt
            .replace("T", " ")
            .replace("Z", "")
    }

    fun getItemsText(order: OrderDto): String {
        return order.items.joinToString(", ") {
            "${it.productName} × ${it.quantity}"
        }
    }
}