package com.example.namnyam.data.remote.dto

data class CreateOrderRequest(
    val restaurantId: Long,
    val addressId: Long,
    val comment: String? = null,
    val cartItems: List<CreateOrderItemRequest>
)

data class CreateOrderItemRequest(
    val productId: Long,
    val quantity: Int
)