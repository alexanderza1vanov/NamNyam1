package com.example.namnyam.data.repository

import android.content.Context
import com.example.namnyam.data.remote.dto.RestaurantDto
import com.example.namnyam.data.remote.network.RetrofitProvider

class RestaurantRepository(context: Context) {

    private val api = RetrofitProvider.getApi(context.applicationContext)

    suspend fun getRestaurants(): List<RestaurantDto> {
        return api.getRestaurants()
    }
}