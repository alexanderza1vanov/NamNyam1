package com.example.namnyam.ui.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.CreateProductRequest
import com.example.namnyam.data.remote.dto.ProductDto
import com.example.namnyam.data.remote.dto.UpdateProductRequest
import com.example.namnyam.data.repository.OwnerRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class EditProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OwnerRepository(application.applicationContext)

    var productState: ((UiState<ProductDto>) -> Unit)? = null
    var saveState: ((UiState<ProductDto>) -> Unit)? = null

    fun loadProduct(productId: Long) {
        productState?.invoke(UiState.Loading)
        viewModelScope.launch {
            try {
                val product = repository.getProductById(productId)
                productState?.invoke(UiState.Success(product))
            } catch (e: Exception) {
                productState?.invoke(
                    UiState.Error(e.message ?: "Не удалось загрузить блюдо")
                )
            }
        }
    }

    fun createProduct(
        name: String,
        description: String?,
        price: Double,
        weightGrams: Int?,
        ingredients: String?,
        imageUrl: String?,
        isAvailable: Boolean
    ) {
        saveState?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val restaurant = repository.getMyRestaurant()
                val product = repository.createProduct(
                    CreateProductRequest(
                        restaurantId = restaurant.id,
                        name = name,
                        description = description,
                        price = price,
                        weightGrams = weightGrams,
                        ingredients = ingredients,
                        imageUrl = imageUrl,
                        isAvailable = isAvailable
                    )
                )
                saveState?.invoke(UiState.Success(product))
            } catch (e: Exception) {
                saveState?.invoke(
                    UiState.Error(e.message ?: "Не удалось создать блюдо")
                )
            }
        }
    }

    fun updateProduct(
        productId: Long,
        name: String,
        description: String?,
        price: Double,
        weightGrams: Int?,
        ingredients: String?,
        imageUrl: String?,
        isAvailable: Boolean
    ) {
        saveState?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val product = repository.updateProduct(
                    productId,
                    UpdateProductRequest(
                        name = name,
                        description = description,
                        price = price,
                        weightGrams = weightGrams,
                        ingredients = ingredients,
                        imageUrl = imageUrl,
                        isAvailable = isAvailable
                    )
                )
                saveState?.invoke(UiState.Success(product))
            } catch (e: Exception) {
                saveState?.invoke(
                    UiState.Error(e.message ?: "Не удалось обновить блюдо")
                )
            }
        }
    }
}