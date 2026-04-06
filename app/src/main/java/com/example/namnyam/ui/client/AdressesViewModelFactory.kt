package com.example.namnyam.ui.client

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.namnyam.data.remote.network.RetrofitProvider
import com.example.namnyam.data.repository.AddressRepository

class AdressesViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitProvider.namNyamApi
        return AdressesViewModel(AddressRepository(api)) as T
    }
}