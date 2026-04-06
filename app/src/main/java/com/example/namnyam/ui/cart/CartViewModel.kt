package com.example.namnyam.ui.cart

import androidx.lifecycle.ViewModel
import com.example.namnyam.data.cart.CartItem
import com.example.namnyam.data.cart.CartManager

class CartViewModel : ViewModel() {

    private val cartManager = CartManager.getInstance()

    var state: ((List<CartItem>, Double, Int) -> Unit)? = null

    fun loadCart() {
        notifyState()
    }

    fun getItems(): List<CartItem> {
        return cartManager.getItems()
    }

    fun increase(productId: Long) {
        cartManager.increase(productId)
        notifyState()
    }

    fun decrease(productId: Long) {
        cartManager.decrease(productId)
        notifyState()
    }

    fun remove(productId: Long) {
        cartManager.remove(productId)
        notifyState()
    }

    fun clearCart() {
        cartManager.clear()
        notifyState()
    }

    private fun notifyState() {
        state?.invoke(
            cartManager.getItems(),
            cartManager.getTotalPrice(),
            cartManager.getTotalCount()
        )
    }
}