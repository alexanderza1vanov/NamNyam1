package com.example.namnyam.ui.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItemUi(
    val productId: Long,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String? = null
) : Parcelable {
    val totalPrice: Double
        get() = price * quantity
}