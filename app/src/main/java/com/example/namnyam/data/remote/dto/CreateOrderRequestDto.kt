package com.example.namnyam.data.remote.dto

data class CreateOrderRequestDto(
    val restaurantId: Long,
    val addressId: Long,
    val comment: String?,
    val items: List<CreateOrderItemRequestDto>
)