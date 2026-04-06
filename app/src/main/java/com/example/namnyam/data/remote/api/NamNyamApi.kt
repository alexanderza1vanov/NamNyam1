package com.example.namnyam.data.remote.api

import com.example.namnyam.data.remote.dto.AuthResponseDto
import com.example.namnyam.data.remote.dto.LoginRequestDto
import com.example.namnyam.data.remote.dto.ProductDto
import com.example.namnyam.data.remote.dto.RegisterRequestDto
import com.example.namnyam.data.remote.dto.RestaurantDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
}