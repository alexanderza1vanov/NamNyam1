package com.example.namnyam.ui.courier

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.data.repository.CourierRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class CourierHomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CourierRepository(application.applicationContext)

    var ordersState: ((UiState<List<OrderDto>>) -> Unit)? = null
    var actionState: ((UiState<OrderDto>) -> Unit)? = null

    private var ordersLoaded = false

    fun loadInitial() {
        loadOrders(force = true)
    }

    fun refreshAll() {
        loadOrders(force = true)
    }

    fun loadOrders(force: Boolean = false) {
        if (ordersLoaded && !force) return

        ordersState?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val orders = repository.getCourierOrders()
                    .sortedByDescending { it.id }

                ordersLoaded = true
                ordersState?.invoke(UiState.Success(orders))
            } catch (e: Exception) {
                ordersState?.invoke(
                    UiState.Error(e.message ?: "Не удалось загрузить доставки")
                )
            }
        }
    }

    fun pickUpOrder(orderId: Long) {
        updateOrder { repository.pickUpOrder(orderId) }
    }

    fun moveOnTheWay(orderId: Long) {
        updateOrder { repository.moveOnTheWay(orderId) }
    }

    fun deliverOrder(orderId: Long) {
        updateOrder { repository.deliverOrder(orderId) }
    }

    fun failOrder(orderId: Long) {
        updateOrder { repository.failOrder(orderId) }
    }

    private fun updateOrder(request: suspend () -> OrderDto) {
        actionState?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val order = request()
                actionState?.invoke(UiState.Success(order))
                loadOrders(force = true)
            } catch (e: Exception) {
                actionState?.invoke(
                    UiState.Error(e.message ?: "Не удалось обновить статус доставки")
                )
            }
        }
    }

    fun getStatusText(status: String): String {
        return when (status) {
            "READY_FOR_DELIVERY" -> "Готов к выдаче"
            "PICKED_UP" -> "Забран"
            "ON_THE_WAY" -> "В пути"
            "DELIVERED" -> "Доставлен"
            "DELIVERY_FAILED" -> "Доставка не удалась"
            "CANCELLED" -> "Отменён"
            else -> status
        }
    }
}