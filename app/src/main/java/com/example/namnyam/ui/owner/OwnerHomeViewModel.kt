package com.example.namnyam.ui.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.data.remote.dto.RestaurantDto
import com.example.namnyam.data.repository.OwnerRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch
import retrofit2.HttpException

class OwnerHomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OwnerRepository(application.applicationContext)

    var restaurantState: ((UiState<RestaurantDto>) -> Unit)? = null
    var ordersState: ((UiState<List<OrderDto>>) -> Unit)? = null
    var actionState: ((UiState<Unit>) -> Unit)? = null

    private var restaurantLoaded = false
    private var ordersLoaded = false
    private var hasRestaurant = false

    fun loadInitial() {
        loadRestaurant(force = true, loadOrdersAfterSuccess = true)
    }

    fun refreshAll() {
        loadRestaurant(force = true, loadOrdersAfterSuccess = true)
    }

    fun loadRestaurant(
        force: Boolean = false,
        loadOrdersAfterSuccess: Boolean = false
    ) {
        if (restaurantLoaded && !force) return

        restaurantState?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val restaurant = repository.getMyRestaurant()
                hasRestaurant = true
                restaurantLoaded = true
                restaurantState?.invoke(UiState.Success(restaurant))

                if (loadOrdersAfterSuccess) {
                    loadOrders(force = true)
                }
            } catch (e: HttpException) {
                hasRestaurant = false
                restaurantLoaded = false

                if (e.code() == 400) {
                    restaurantState?.invoke(
                        UiState.Error("У вас пока нет ресторана. Создайте его.")
                    )
                    ordersLoaded = true
                    ordersState?.invoke(UiState.Success(emptyList()))
                } else {
                    restaurantState?.invoke(
                        UiState.Error(e.message ?: "Не удалось загрузить ресторан")
                    )
                }
            } catch (e: Exception) {
                hasRestaurant = false
                restaurantState?.invoke(
                    UiState.Error(e.message ?: "Не удалось загрузить ресторан")
                )
            }
        }
    }

    fun loadOrders(force: Boolean = false) {
        if (!hasRestaurant) {
            ordersLoaded = true
            ordersState?.invoke(UiState.Success(emptyList()))
            return
        }

        if (ordersLoaded && !force) return

        ordersState?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val orders = repository.getOwnerOrders().sortedByDescending { it.id }
                ordersLoaded = true
                ordersState?.invoke(UiState.Success(orders))
            } catch (e: Exception) {
                ordersState?.invoke(
                    UiState.Error(e.message ?: "Не удалось загрузить заказы ресторана")
                )
            }
        }
    }

    fun openRestaurant() {
        updateRestaurantState { repository.openMyRestaurant() }
    }

    fun closeRestaurant() {
        updateRestaurantState { repository.closeMyRestaurant() }
    }

    private fun updateRestaurantState(request: suspend () -> RestaurantDto) {
        restaurantState?.invoke(UiState.Loading)
        viewModelScope.launch {
            try {
                val restaurant = request()
                hasRestaurant = true
                restaurantLoaded = true
                restaurantState?.invoke(UiState.Success(restaurant))
            } catch (e: Exception) {
                restaurantState?.invoke(
                    UiState.Error(e.message ?: "Не удалось обновить ресторан")
                )
            }
        }
    }

    fun confirmOrder(orderId: Long) { updateOrder { repository.confirmOrder(orderId) }
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
                request()
                actionState?.invoke(UiState.Success(Unit))
                loadOrders(force = true)
            } catch (e: Exception) {
                actionState?.invoke(
                    UiState.Error(e.message ?: "Не удалось обновить статус заказа")
                )
            }
        }
    }
    fun toggleRestaurantOpenState() {
        actionState?.invoke(UiState.Loading)
        viewModelScope.launch {
            try {
                val restaurant = repository.getMyRestaurant()
                if (restaurant.isOpen) {
                    repository.closeMyRestaurant()
                } else {
                    repository.openMyRestaurant()
                }
                actionState?.invoke(UiState.Success(Unit))
                refreshAll()
            } catch (e: Exception) {
                actionState?.invoke(
                    UiState.Error(e.message ?: "Не удалось изменить статус ресторана")
                )
            }
        }
    }
}