package com.example.namnyam.data.remote.dto

data class CreateOrderRequest(
    val restaurantId: Long,
    val addressId: Long,
    val comment: String = "",
    val items: List<CreateOrderItemRequest>
)

data class CreateOrderItemRequest(
    val productId: Long,
    val quantity: Int
)