package com.example.namnyam.data.remote.dto

data class AddressDto(
    val id: Long,
    val userId: Long,
    val title: String,
    val addressLine: String,
    val entrance: String?,
    val floor: String?,
    val apartment: String?,
    val comment: String?,
    val isDefault: Boolean
)