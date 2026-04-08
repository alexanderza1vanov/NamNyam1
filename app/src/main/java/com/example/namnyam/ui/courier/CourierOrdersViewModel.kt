package com.example.namnyam.ui.courier

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.data.repository.CourierRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CourierActionEvent {
    data object Loading : CourierActionEvent
    data class Success(val message: String) : CourierActionEvent
    data class Error(val message: String) : CourierActionEvent
}

class CourierOrdersViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CourierRepository(application.applicationContext)

    private val _availableOrdersState =
        MutableStateFlow<UiState<List<OrderDto>>>(UiState.Idle)
    val availableOrdersState: StateFlow<UiState<List<OrderDto>>> =
        _availableOrdersState.asStateFlow()

    private val _myOrdersState =
        MutableStateFlow<UiState<List<OrderDto>>>(UiState.Idle)
    val myOrdersState: StateFlow<UiState<List<OrderDto>>> =
        _myOrdersState.asStateFlow()

    private val _actionEvents = MutableSharedFlow<CourierActionEvent>(extraBufferCapacity = 1)
    val actionEvents: SharedFlow<CourierActionEvent> = _actionEvents.asSharedFlow()

    private var availableLoaded = false
    private var myLoaded = false

    fun loadAvailableOrders(force: Boolean = false) {
        if (availableLoaded && !force) return

        _availableOrdersState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val orders = repository.getAvailableOrders()
                    .sortedByDescending { it.id }

                availableLoaded = true
                _availableOrdersState.value = UiState.Success(orders)
            } catch (e: Exception) {
                _availableOrdersState.value =
                    UiState.Error(e.message ?: "Не удалось загрузить свободные заказы")
            }
        }
    }

    fun loadMyOrders(force: Boolean = false) {
        if (myLoaded && !force) return

        _myOrdersState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val orders = repository.getCourierOrders()
                    .sortedByDescending { it.id }

                myLoaded = true
                _myOrdersState.value = UiState.Success(orders)
            } catch (e: Exception) {
                _myOrdersState.value =
                    UiState.Error(e.message ?: "Не удалось загрузить мои доставки")
            }
        }
    }

    fun refreshAvailableOrders() {
        loadAvailableOrders(force = true)
    }

    fun refreshMyOrders() {
        loadMyOrders(force = true)
    }

    fun takeOrder(orderId: Long) {
        performAction(
            successMessage = "Заказ взят в доставку"
        ) {
            repository.takeOrder(orderId)
        }
    }

    fun pickUpOrder(orderId: Long) {
        performAction(
            successMessage = "Заказ забран из ресторана"
        ) {
            repository.pickUpOrder(orderId)
        }
    }

    fun moveOnTheWay(orderId: Long) {
        performAction(
            successMessage = "Заказ переведён в путь"
        ) {
            repository.moveOnTheWay(orderId)
        }
    }

    fun deliverOrder(orderId: Long) {
        performAction(
            successMessage = "Заказ доставлен"
        ) {
            repository.deliverOrder(orderId)
        }
    }

    fun failOrder(orderId: Long) {
        performAction(
            successMessage = "Доставка помечена как неудачная"
        ) {
            repository.failOrder(orderId)
        }
    }

    private fun performAction(
        successMessage: String,
        request: suspend () -> OrderDto
    ) {
        _actionEvents.tryEmit(CourierActionEvent.Loading)

        viewModelScope.launch {
            try {
                request()
                _actionEvents.emit(CourierActionEvent.Success(successMessage))
                loadAvailableOrders(force = true)
                loadMyOrders(force = true)
            } catch (e: Exception) {
                _actionEvents.emit(
                    CourierActionEvent.Error(
                        e.message ?: "Не удалось выполнить действие"
                    )
                )
            }
        }
    }
}