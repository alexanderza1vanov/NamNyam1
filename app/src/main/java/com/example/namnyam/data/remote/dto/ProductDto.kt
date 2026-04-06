package com.example.namnyam.data.remote.dto

data class ProductDto(
    val id: Long,
    val restaurantId: Long,
    val categoryId: Long?,
    val name: String,
    val description: String?,
    val price: Double,
    val weightGrams: Int?,
    val ingredients: String?,
    val imageUrl: String?,
    val isAvailable: Boolean
)