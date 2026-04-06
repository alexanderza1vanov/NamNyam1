package com.example.namnyam.data.cart

class CartManager private constructor() {

    private val items = mutableListOf<CartItem>()

    fun getItems(): List<CartItem> = items.toList()

    fun add(item: CartItem) {
        val index = items.indexOfFirst { it.productId == item.productId }
        if (index >= 0) {
            val old = items[index]
            items[index] = old.copy(quantity = old.quantity + 1)
        } else {
            items.add(item)
        }
    }

    fun increase(productId: Long) {
        val index = items.indexOfFirst { it.productId == productId }
        if (index >= 0) {
            val old = items[index]
            items[index] = old.copy(quantity = old.quantity + 1)
        }
    }

    fun decrease(productId: Long) {
        val index = items.indexOfFirst { it.productId == productId }
        if (index >= 0) {
            val old = items[index]
            if (old.quantity <= 1) {
                items.removeAt(index)
            } else {
                items[index] = old.copy(quantity = old.quantity - 1)
            }
        }
    }

    fun remove(productId: Long) {
        items.removeAll { it.productId == productId }
    }

    fun clear() {
        items.clear()
    }

    fun getTotalPrice(): Double = items.sumOf { it.totalPrice() }

    fun getTotalCount(): Int = items.sumOf { it.quantity }

    companion object {
        @Volatile
        private var INSTANCE: CartManager? = null

        fun getInstance(): CartManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CartManager().also { INSTANCE = it }
            }
        }
    }
}