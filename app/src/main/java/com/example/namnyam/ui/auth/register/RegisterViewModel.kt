package com.example.namnyam.ui.auth.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.AuthResponseDto
import com.example.namnyam.data.repository.AuthRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    var state: ((UiState<AuthResponseDto>) -> Unit)? = null

    fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: String
    ) {
        state?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val response = repository.register(
                    name = name,
                    email = email,
                    phone = phone,
                    password = password,
                    role = role
                )
                state?.invoke(UiState.Success(response))
            } catch (e: Exception) {
                state?.invoke(UiState.Error(e.message ?: "Ошибка регистрации"))
            }
        }
    }
}