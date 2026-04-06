package com.example.namnyam.data.remote.api

import com.example.namnyam.data.remote.dto.AddressDto
import com.example.namnyam.data.remote.dto.AuthResponseDto
import com.example.namnyam.data.remote.dto.CreateAddressRequest
import com.example.namnyam.data.remote.dto.CreateAddressRequestDto
import com.example.namnyam.data.remote.dto.CreateOrderRequest
import com.example.namnyam.data.remote.dto.CreateOrderRequestDto
import com.example.namnyam.data.remote.dto.DeliveryAddressDto
import com.example.namnyam.data.remote.dto.LoginRequestDto
import com.example.namnyam.data.remote.dto.OrderDto
import com.example.namnyam.data.remote.dto.ProductDto
import com.example.namnyam.data.remote.dto.RegisterRequestDto
import com.example.namnyam.data.remote.dto.RestaurantDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Response

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

    // Addresses
//    @GET("addresses")
//    suspend fun getAddresses(): List<AddressDto>
//
//    @POST("addresses")
//    suspend fun createAddress(
//        @Body body: CreateAddressRequestDto
//    ): AddressDto

    // Orders
//    @POST("orders")
//    suspend fun createOrder(
//        @Body body: CreateOrderRequestDto
//    ): OrderDto

    @GET("orders/my")
    suspend fun getMyOrders(): List<OrderDto>

    @GET("orders/{orderId}")
    suspend fun getOrderById(
        @Path("orderId") orderId: Long
    ): OrderDto

    // Owner
//    @GET("owner/orders")
//    suspend fun getOwnerOrders(): List<OrderDto>
//
//    // Courier
//    @GET("courier/orders")
//    suspend fun getCourierOrders(): List<OrderDto>

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
}