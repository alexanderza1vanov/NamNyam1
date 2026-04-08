package com.example.namnyam.data.remote.dto

data class CreateProductRequest(
    val restaurantId: Long,
    val categoryId: Long? = null,
    val name: String,
    val description: String? = null,
    val price: Double,
    val weightGrams: Int? = null,
    val ingredients: String? = null,
    val imageUrl: String? = null,
    val isAvailable: Boolean = true
)