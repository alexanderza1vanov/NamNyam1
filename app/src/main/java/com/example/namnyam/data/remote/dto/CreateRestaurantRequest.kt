package com.example.namnyam.data.remote.dto

data class CreateRestaurantRequest(
    val name: String,
    val description: String? = null,
    val address: String,
    val phone: String,
    val cuisineType: String? = null,
    val imageUrl: String? = null,
    val deliveryTimeMin: Int? = null,
    val deliveryFee: Double = 0.0,
    val minOrderAmount: Double = 0.0
)