package com.example.namnyam.ui.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.DeliveryAddressDto
import com.example.namnyam.data.repository.AddressRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class AdressesViewModel(
    private val repository: AddressRepository
) : ViewModel() {

    private val _state =
        MutableLiveData<UiState<List<DeliveryAddressDto>>>(UiState.Idle)
    val state: LiveData<UiState<List<DeliveryAddressDto>>> = _state

    fun loadAddresses() {
        _state.value = UiState.Loading

        viewModelScope.launch {
            val result = repository.getMyAddresses()
            _state.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Не удалось загрузить адреса") }
            )
        }
    }

    fun deleteAddress(addressId: Long) {
        viewModelScope.launch {
            repository.deleteAddress(addressId)
            loadAddresses()
        }
    }
}