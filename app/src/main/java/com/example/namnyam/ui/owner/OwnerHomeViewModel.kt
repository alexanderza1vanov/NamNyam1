package com.example.namnyam.ui.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.data.remote.dto.RestaurantDto
import com.example.namnyam.data.repository.OwnerRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class OwnerHomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OwnerRepository(application.applicationContext)

    var restaurantState: ((UiState<RestaurantDto>) -> Unit)? = null
    var ordersState: ((UiState<List<OrderDto>>) -> Unit)? = null
    var actionState: ((UiState<OrderDto>) -> Unit)? = null

    private var restaurantLoaded = false
    private var ordersLoaded = false

    fun loadInitial() {
        if (!restaurantLoaded) {
            loadRestaurant()
        }
        if (!ordersLoaded) {
            loadOrders()
        }
    }

    fun refreshAll() {
        loadRestaurant(force = true)
        loadOrders(force = true)
    }

    fun loadRestaurant(force: Boolean = false) {
        if (restaurantLoaded && !force) return

        restaurantState?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val restaurant = repository.getMyRestaurant()
                restaurantLoaded = true
                restaurantState?.invoke(UiState.Success(restaurant))
            } catch (e: Exception) {
                restaurantState?.invoke(
                    UiState.Error(e.message ?: "Не удалось загрузить ресторан")
                )
            }
        }
    }

    fun loadOrders(force: Boolean = false) {
        if (ordersLoaded && !force) return

        ordersState?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val orders = repository.getOwnerOrders()
                    .sortedByDescending { it.id }

                ordersLoaded = true
                ordersState?.invoke(UiState.Success(orders))
            } catch (e: Exception) {
                ordersState?.invoke(
                    UiState.Error(e.message ?: "Не удалось загрузить заказы ресторана")
                )
            }
        }
    }

    fun confirmOrder(orderId: Long) {
        updateOrder { repository.confirmOrder(orderId) }
    }

    fun startCooking(orderId: Long) {
        updateOrder { repository.startCooking(orderId) }
    }

    fun markReady(orderId: Long) {
        updateOrder { repository.markReady(orderId) }
    }

    fun cancelOrder(orderId: Long) {
        updateOrder { repository.cancelOrder(orderId) }
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
                    UiState.Error(e.message ?: "Не удалось обновить статус заказа")
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
}