package com.example.namnyam.ui.client

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.RestaurantDto
import com.example.namnyam.data.repository.RestaurantRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class ClientHomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RestaurantRepository(application)

    var state: ((UiState<List<RestaurantDto>>) -> Unit)? = null

    private var cachedRestaurants: List<RestaurantDto> = emptyList()
    private var isLoaded = false

    fun loadRestaurants(forceRefresh: Boolean = false) {
        if (isLoaded && !forceRefresh) {
            state?.invoke(UiState.Success(cachedRestaurants))
            return
        }

        state?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val restaurants = repository.getRestaurants()
                cachedRestaurants = restaurants
                isLoaded = true
                state?.invoke(UiState.Success(restaurants))
            } catch (e: Exception) {
                state?.invoke(UiState.Error(e.message ?: "Ошибка загрузки ресторанов"))
            }
        }
    }
}