package com.example.namnyam.data.repository

import com.example.namnyam.data.remote.api.NamNyamApi
import com.example.namnyam.data.remote.dto.CreateAddressRequest
import com.example.namnyam.data.remote.dto.DeliveryAddressDto

class AddressRepository(
    private val api: NamNyamApi
) {

    suspend fun getMyAddresses(): Result<List<DeliveryAddressDto>> {
        return try {
            Result.success(api.getMyAddresses())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAddress(
        request: CreateAddressRequest
    ): Result<DeliveryAddressDto> {
        return try {
            Result.success(api.createAddress(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAddress(
        addressId: Long
    ): Result<Unit> {
        return try {
            val response = api.deleteAddress(addressId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Не удалось удалить адрес"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}