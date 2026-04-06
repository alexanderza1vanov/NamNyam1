package com.example.namnyam.ui.client

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.ProductDto
import com.example.namnyam.data.repository.ProductRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class RestaurantDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository(application)

    var state: ((UiState<List<ProductDto>>) -> Unit)? = null

    fun loadProducts(restaurantId: Long) {
        state?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val data = repository.getProductsByRestaurant(restaurantId)
                state?.invoke(UiState.Success(data))
            } catch (e: Exception) {
                state?.invoke(UiState.Error(e.message ?: "Ошибка загрузки меню"))
            }
        }
    }
}