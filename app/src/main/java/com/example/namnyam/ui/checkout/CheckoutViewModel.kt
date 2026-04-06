package com.example.namnyam.ui.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.CreateOrderItemRequest
import com.example.namnyam.data.remote.dto.CreateOrderRequest
import com.example.namnyam.data.remote.dto.DeliveryAddressDto
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.data.repository.AddressRepository
import com.example.namnyam.data.repository.OrderRepository
import com.example.namnyam.ui.client.CartItemUi
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val addressRepository: AddressRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _addressesState =
        MutableLiveData<UiState<List<DeliveryAddressDto>>>(UiState.Idle)
    val addressesState: LiveData<UiState<List<DeliveryAddressDto>>> = _addressesState

    private val _createOrderState =
        MutableLiveData<UiState<OrderDto>>(UiState.Idle)
    val createOrderState: LiveData<UiState<OrderDto>> = _createOrderState

    fun loadAddresses() {
        _addressesState.value = UiState.Loading

        viewModelScope.launch {
            val result = addressRepository.getMyAddresses()
            _addressesState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Не удалось загрузить адреса") }
            )
        }
    }

    fun createOrder(
        restaurantId: Long,
        addressId: Long,
        comment: String?,
        cartItems: List<CartItemUi>
    ) {
        _createOrderState.value = UiState.Loading

        val request = CreateOrderRequest(
            restaurantId = restaurantId,
            addressId = addressId,
            comment = comment?.takeIf { it.isNotBlank() },
            cartItems = cartItems.map {
                CreateOrderItemRequest(
                    productId = it.productId,
                    quantity = it.quantity
                )
            }
        )

        viewModelScope.launch {
            val result = orderRepository.createOrder(request)
            _createOrderState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Не удалось оформить заказ") }
            )
        }
    }

    fun resetCreateOrderState() {
        _createOrderState.value = UiState.Idle
    }
}