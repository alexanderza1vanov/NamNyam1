package com.example.namnyam.data.repository

import android.content.Context
import com.example.namnyam.data.remote.dto.AuthResponseDto
import com.example.namnyam.data.remote.dto.LoginRequestDto
import com.example.namnyam.data.remote.dto.RegisterRequestDto
import com.example.namnyam.data.remote.network.RetrofitProvider
import com.example.namnyam.data.storage.TokenManager

class AuthRepository(context: Context) {

    private val appContext = context.applicationContext
    private val api = RetrofitProvider.getApi(appContext)
    private val tokenManager = TokenManager(appContext)

    suspend fun login(email: String, password: String): AuthResponseDto {
        val response = api.login(
            LoginRequestDto(
                email = email,
                password = password
            )
        )

        tokenManager.saveToken(response.token)
        tokenManager.saveUserRole(response.user.role)
        return response
    }

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: String
    ): AuthResponseDto {
        val response = api.register(
            RegisterRequestDto(
                name = name,
                email = email,
                phone = phone,
                password = password,
                role = role
            )
        )

        tokenManager.saveToken(response.token)
        tokenManager.saveUserRole(response.user.role)
        return response
    }

    fun getSavedToken(): String? = tokenManager.getToken()

    fun getSavedRole(): String? = tokenManager.getUserRole()

    fun logout() {
        tokenManager.clear()
    }
}