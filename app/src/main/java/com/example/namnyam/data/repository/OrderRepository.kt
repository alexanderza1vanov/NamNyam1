package com.example.namnyam.data.repository

import com.example.namnyam.data.remote.api.NamNyamApi
import com.example.namnyam.data.remote.dto.CreateOrderRequest
import com.example.namnyam.data.remote.dto.OrderDto

class OrderRepository(
    private val api: NamNyamApi
) {

    suspend fun createOrder(
        request: CreateOrderRequest
    ): Result<OrderDto> {
        return try {
            Result.success(api.createOrder(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}