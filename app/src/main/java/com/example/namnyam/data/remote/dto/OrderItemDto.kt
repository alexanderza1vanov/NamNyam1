package com.example.namnyam.data.remote.dto

data class OrderItemDto(
    val id: Long,
    val productId: Long?,
    val productName: String,
    val productPrice: Double,
    val quantity: Int,
    val totalPrice: Double
)