package com.example.namnyam.ui.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.RestaurantDto
import com.example.namnyam.data.remote.dto.UpdateRestaurantRequest
import com.example.namnyam.data.repository.OwnerRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class EditRestaurantViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OwnerRepository(application.applicationContext)

    var loadState: ((UiState<RestaurantDto>) -> Unit)? = null
    var saveState: ((UiState<RestaurantDto>) -> Unit)? = null

    fun loadRestaurant() {
        loadState?.invoke(UiState.Loading)
        viewModelScope.launch {
            try {
                val restaurant = repository.getMyRestaurant()
                loadState?.invoke(UiState.Success(restaurant))
            } catch (e: Exception) {
                loadState?.invoke(
                    UiState.Error(e.message ?: "Не удалось загрузить ресторан")
                )
            }
        }
    }

    fun updateRestaurant(request: UpdateRestaurantRequest) {
        saveState?.invoke(UiState.Loading)
        viewModelScope.launch {
            try {
                val updated = repository.updateRestaurant(request)
                saveState?.invoke(UiState.Success(updated))
            } catch (e: Exception) {
                saveState?.invoke(
                    UiState.Error(e.message ?: "Не удалось обновить ресторан")
                )
            }
        }
    }
}