package com.example.namnyam.data.remote.dto

data class OrderDto(
    val id: Long,
    val userId: Long,
    val restaurantId: Long,
    val restaurantName: String = "",
    val restaurantAddress: String = "",
    val courierId: Long? = null,
    val status: String,
    val deliveryAddress: String,
    val comment: String? = null,
    val deliveryFee: Double,
    val totalPrice: Double,
    val createdAt: String,
    val items: List<OrderItemDto> = emptyList()
)