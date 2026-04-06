package com.example.namnyam.data.remote.dto

data class RestaurantDto(
    val id: Long,
    val ownerId: Long,
    val name: String,
    val description: String?,
    val address: String,
    val phone: String,
    val cuisineType: String?,
    val imageUrl: String?,
    val isOpen: Boolean,
    val deliveryTimeMin: Int?,
    val deliveryFee: Double,
    val minOrderAmount: Double
)