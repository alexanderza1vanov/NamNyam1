package com.example.namnyam.data.remote.api

import com.example.namnyam.data.remote.dto.AuthResponseDto
import com.example.namnyam.data.remote.dto.CreateAddressRequest
import com.example.namnyam.data.remote.dto.CreateOrderRequest
import com.example.namnyam.data.remote.dto.DeliveryAddressDto
import com.example.namnyam.data.remote.dto.LoginRequestDto
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.data.remote.dto.ProductDto
import com.example.namnyam.data.remote.dto.RegisterRequestDto
import com.example.namnyam.data.remote.dto.RestaurantDto
import retrofit2.Response
import com.example.namnyam.data.remote.dto.CreateRestaurantRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NamNyamApi {

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequestDto
    ): AuthResponseDto

    @POST("auth/register")
    suspend fun register(
        @Body body: RegisterRequestDto
    ): AuthResponseDto

    @GET("restaurants")
    suspend fun getRestaurants(): List<RestaurantDto>

    @GET("restaurants/{restaurantId}/products")
    suspend fun getProductsByRestaurant(
        @Path("restaurantId") restaurantId: Long
    ): List<ProductDto>

    @GET("orders/my")
    suspend fun getMyOrders(): List<OrderDto>

    @GET("orders/{orderId}")
    suspend fun getOrderById(
        @Path("orderId") orderId: Long
    ): OrderDto

    @GET("addresses/my")
    suspend fun getMyAddresses(): List<DeliveryAddressDto>

    @POST("addresses")
    suspend fun createAddress(
        @Body request: CreateAddressRequest
    ): DeliveryAddressDto

    @DELETE("addresses/{id}")
    suspend fun deleteAddress(
        @Path("id") id: Long
    ): Response<Unit>

    @POST("orders")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): OrderDto

    // OWNER

    @GET("restaurants/my")
    suspend fun getMyRestaurant(): RestaurantDto

    @GET("owner/orders")
    suspend fun getOwnerOrders(): List<OrderDto>

    @PUT("owner/orders/{id}/confirm")
    suspend fun confirmOwnerOrder(
        @Path("id") orderId: Long
    ): OrderDto

    @PUT("owner/orders/{id}/cooking")
    suspend fun startCookingOwnerOrder(
        @Path("id") orderId: Long
    ): OrderDto

    @PUT("owner/orders/{id}/ready")
    suspend fun markReadyOwnerOrder(
        @Path("id") orderId: Long
    ): OrderDto

    @PUT("owner/orders/{id}/cancel")
    suspend fun cancelOwnerOrder(
        @Path("id") orderId: Long
    ): OrderDto

    @POST("restaurants")
    suspend fun createRestaurant(
        @Body request: CreateRestaurantRequest
    ): RestaurantDto
}