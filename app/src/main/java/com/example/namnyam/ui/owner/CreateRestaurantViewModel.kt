package com.example.namnyam.ui.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.CreateRestaurantRequest
import com.example.namnyam.data.remote.dto.RestaurantDto
import com.example.namnyam.data.repository.OwnerRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CreateRestaurantViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OwnerRepository(application.applicationContext)

    var state: ((UiState<RestaurantDto>) -> Unit)? = null

    fun createRestaurant(request: CreateRestaurantRequest) {
        state?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val restaurant = repository.createRestaurant(request)
                state?.invoke(UiState.Success(restaurant))
            } catch (e: Exception) {
                state?.invoke(UiState.Error(mapError(e)))
            }
        }
    }

    private fun mapError(e: Exception): String {
        return when ((e as? HttpException)?.code()) {
            400 -> "Проверьте заполнение полей ресторана"
            403 -> "Только владелец может создать ресторан"
            409 -> "У этого владельца уже есть ресторан"
            else -> e.message ?: "Не удалось создать ресторан"
        }
    }
}