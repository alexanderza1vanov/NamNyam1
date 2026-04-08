package com.example.namnyam.data.repository

import android.content.Context
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.data.remote.network.RetrofitProvider

class CourierRepository(context: Context) {

    private val api = RetrofitProvider.getApi(context.applicationContext)

    suspend fun getAvailableOrders(): List<OrderDto> {
        return api.getAvailableCourierOrders()
    }

    suspend fun getCourierOrders(): List<OrderDto> {
        return api.getCourierOrders()
    }

    suspend fun getCourierOrderById(orderId: Long): OrderDto {
        return api.getCourierOrderById(orderId)
    }

    suspend fun takeOrder(orderId: Long): OrderDto {
        return api.takeCourierOrder(orderId)
    }

    suspend fun pickUpOrder(orderId: Long): OrderDto {
        return api.pickUpCourierOrder(orderId)
    }

    suspend fun moveOnTheWay(orderId: Long): OrderDto {
        return api.moveCourierOrderOnTheWay(orderId)
    }

    suspend fun deliverOrder(orderId: Long): OrderDto {
        return api.deliverCourierOrder(orderId)
    }

    suspend fun failOrder(orderId: Long): OrderDto {
        return api.failCourierOrder(orderId)
    }
}