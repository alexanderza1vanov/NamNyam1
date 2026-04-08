package com.example.namnyam.data.repository

import android.content.Context
import com.example.namnyam.data.remote.dto.CreateProductRequest
import com.example.namnyam.data.remote.dto.CreateRestaurantRequest
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.data.remote.dto.ProductDto
import com.example.namnyam.data.remote.dto.RestaurantDto
import com.example.namnyam.data.remote.dto.UpdateProductRequest
import com.example.namnyam.data.remote.dto.UpdateRestaurantRequest
import com.example.namnyam.data.remote.network.RetrofitProvider

class OwnerRepository(context: Context) {

    private val api = RetrofitProvider.getApi(context.applicationContext)

    suspend fun getMyRestaurant(): RestaurantDto {
        return api.getMyRestaurant()
    }

    suspend fun createRestaurant(request: CreateRestaurantRequest): RestaurantDto {
        return api.createRestaurant(request)
    }

    suspend fun updateMyRestaurant(request: UpdateRestaurantRequest): RestaurantDto {
        return api.updateMyRestaurant(request)
    }

    suspend fun openMyRestaurant(): RestaurantDto {
        return api.openMyRestaurant()
    }

    suspend fun closeMyRestaurant(): RestaurantDto {
        return api.closeMyRestaurant()
    }

    suspend fun getRestaurantProducts(restaurantId: Long): List<ProductDto> {
        return api.getProductsByRestaurant(restaurantId)
    }

    suspend fun getProductById(productId: Long): ProductDto {
        return api.getProductById(productId)
    }

    suspend fun createProduct(request: CreateProductRequest): ProductDto {
        return api.createProduct(request)
    }

    suspend fun updateProduct(productId: Long, request: UpdateProductRequest): ProductDto {
        return api.updateProduct(productId, request)
    }

    suspend fun deleteProduct(productId: Long) {
        api.deleteProduct(productId)
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
    suspend fun updateRestaurant(request: UpdateRestaurantRequest): RestaurantDto {
        return api.updateMyRestaurant(request)
    }

    suspend fun openRestaurant(): RestaurantDto {
        return api.openMyRestaurant()
    }

    suspend fun closeRestaurant(): RestaurantDto {
        return api.closeMyRestaurant()
    }

    suspend fun getMyProducts(): List<ProductDto> {
        val restaurant = api.getMyRestaurant()
        return api.getProductsByRestaurant(restaurant.id)
    }
}