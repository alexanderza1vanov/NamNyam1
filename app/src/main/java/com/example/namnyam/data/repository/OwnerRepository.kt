package com.example.namnyam.data.repository

import android.content.Context
import com.example.namnyam.data.remote.dto.CreateRestaurantRequest
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.data.remote.dto.RestaurantDto
import com.example.namnyam.data.remote.network.RetrofitProvider

class OwnerRepository(context: Context) {

    private val api = RetrofitProvider.getApi(context.applicationContext)

    suspend fun getMyRestaurant(): RestaurantDto {
        return api.getMyRestaurant()
    }

    suspend fun createRestaurant(request: CreateRestaurantRequest): RestaurantDto {
        return api.createRestaurant(request)
    }

    suspend fun getOwnerOrders(): List<OrderDto> {
        return api.getOwnerOrders()
    }

    suspend fun confirmOrder(orderId: Long): OrderDto {
        return api.confirmOwnerOrder(orderId)
    }

    suspend fun startCooking(orderId: Long): OrderDto {
        return api.startCookingOwnerOrder(orderId)
    }

    suspend fun markReady(orderId: Long): OrderDto {
        return api.markReadyOwnerOrder(orderId)
    }

    suspend fun cancelOrder(orderId: Long): OrderDto {
        return api.cancelOwnerOrder(orderId)
    }
}