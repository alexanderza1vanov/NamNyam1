package com.example.namnyam.ui.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.CreateAddressRequest
import com.example.namnyam.data.remote.dto.DeliveryAddressDto
import com.example.namnyam.data.repository.AddressRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class AddAddressViewModel(
    private val repository: AddressRepository
) : ViewModel() {

    private val _state =
        MutableLiveData<UiState<DeliveryAddressDto>>(UiState.Idle)
    val state: LiveData<UiState<DeliveryAddressDto>> = _state

    fun createAddress(
        title: String,
        addressLine: String,
        entrance: String?,
        floor: String?,
        apartment: String?,
        comment: String?,
        isDefault: Boolean
    ) {
        _state.value = UiState.Loading

        val request = CreateAddressRequest(
            title = title,
            addressLine = addressLine,
            entrance = entrance?.takeIf { it.isNotBlank() },
            floor = floor?.takeIf { it.isNotBlank() },
            apartment = apartment?.takeIf { it.isNotBlank() },
            comment = comment?.takeIf { it.isNotBlank() },
            isDefault = isDefault
        )

        viewModelScope.launch {
            val result = repository.createAddress(request)
            _state.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Не удалось добавить адрес") }
            )
        }
    }

    fun reset() {
        _state.value = UiState.Idle
    }
}