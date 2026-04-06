package com.example.namnyam.ui.checkout

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.namnyam.data.remote.network.RetrofitProvider
import com.example.namnyam.data.repository.AddressRepository
import com.example.namnyam.data.repository.OrderRepository

class CheckoutViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitProvider.getApi(context)

        return CheckoutViewModel(
            addressRepository = AddressRepository(api),
            orderRepository = OrderRepository(api)
        ) as T
    }
}