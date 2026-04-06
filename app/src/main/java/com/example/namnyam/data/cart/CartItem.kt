package com.example.namnyam.data.cart

data class CartItem(
    val productId: Long,
    val restaurantId: Long,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val weightGrams: Int?,
    val quantity: Int
) {
    fun totalPrice(): Double = price * quantity
}