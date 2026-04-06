package com.example.namnyam.data.remote.dto


data class RegisterRequestDto(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String
)

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class UserResponseDto(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String,
    val role: String
)

data class AuthResponseDto(
    val message: String,
    val token: String,
    val user: UserResponseDto
)