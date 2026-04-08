package com.example.namnyam.ui.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.ProductDto
import com.example.namnyam.data.repository.OwnerRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class OwnerProductsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OwnerRepository(application.applicationContext)

    var productsState: ((UiState<List<ProductDto>>) -> Unit)? = null
    var deleteState: ((UiState<Unit>) -> Unit)? = null

    fun loadInitial() {
        loadProducts()
    }

    fun refresh() {
        loadProducts()
    }

    fun loadProducts() {
        productsState?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val restaurant = repository.getMyRestaurant()
                val products = repository.getRestaurantProducts(restaurant.id)
                productsState?.invoke(UiState.Success(products))
            } catch (e: Exception) {
                productsState?.invoke(
                    UiState.Error(e.message ?: "Не удалось загрузить блюда")
                )
            }
        }
    }

    fun deleteProduct(productId: Long) {
        deleteState?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                repository.deleteProduct(productId)
                deleteState?.invoke(UiState.Success(Unit))
                loadProducts()
            } catch (e: Exception) {
                deleteState?.invoke(
                    UiState.Error(e.message ?: "Не удалось удалить блюдо")
                )
            }
        }
    }
}