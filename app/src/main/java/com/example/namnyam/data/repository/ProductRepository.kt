package com.example.namnyam.data.repository

import android.content.Context
import com.example.namnyam.data.remote.dto.ProductDto
import com.example.namnyam.data.remote.network.RetrofitProvider

class ProductRepository(context: Context) {

    private val api = RetrofitProvider.getApi(context.applicationContext)

    suspend fun getProductsByRestaurant(restaurantId: Long): List<ProductDto> {
        return api.getProductsByRestaurant(restaurantId)
    }
}