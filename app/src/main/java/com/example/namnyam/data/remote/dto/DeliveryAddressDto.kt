package com.example.namnyam.data.remote.dto

data class DeliveryAddressDto(
    val id: Long,
    val title: String,
    val addressLine: String,
    val entrance: String? = null,
    val floor: String? = null,
    val apartment: String? = null,
    val comment: String? = null,
    val isDefault: Boolean = false
)