package com.example.namnyam.data.remote.dto

data class CreateOrderItemRequestDto(
    val productId: Long,
    val quantity: Int
)