package com.example.namnyam.ui.client

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.data.repository.OrderRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class OrderDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val orderRepository = OrderRepository(application)

    var state: ((UiState<OrderDto>) -> Unit)? = null

    fun loadOrder(orderId: Long) {
        state?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val order = orderRepository.getOrderById(orderId)
                state?.invoke(UiState.Success(order))
            } catch (e: Exception) {
                state?.invoke(
                    UiState.Error(e.message ?: "Не удалось загрузить заказ")
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

    fun getStatusDescription(status: String): String {
        return when (status) {
            "CREATED" -> "Ресторан получил ваш заказ"
            "CONFIRMED" -> "Заказ подтверждён и скоро начнёт готовиться"
            "COOKING" -> "На кухне уже готовят ваши блюда"
            "READY_FOR_DELIVERY" -> "Заказ готов и ждёт курьера"
            "ASSIGNED_TO_COURIER" -> "Назначен курьер"
            "PICKED_UP" -> "Курьер уже забрал заказ"
            "ON_THE_WAY" -> "Курьер едет к вам"
            "DELIVERED" -> "Заказ успешно доставлен"
            "CANCELLED" -> "Заказ был отменён"
            else -> "Статус заказа обновлён"
        }
    }

    fun formatCreatedAt(createdAt: String): String {
        return createdAt
            .replace("T", " ")
            .replace("Z", "")
    }

    fun getProgressStep(status: String): Int {
        return when (status) {
            "CREATED" -> 1
            "CONFIRMED" -> 2
            "COOKING" -> 3
            "READY_FOR_DELIVERY" -> 4
            "ASSIGNED_TO_COURIER" -> 5
            "PICKED_UP" -> 6
            "ON_THE_WAY" -> 7
            "DELIVERED" -> 8
            else -> 1
        }
    }
}