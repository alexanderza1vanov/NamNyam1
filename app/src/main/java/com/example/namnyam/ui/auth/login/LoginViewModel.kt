package com.example.namnyam.ui.auth.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.namnyam.data.remote.dto.AuthResponseDto
import com.example.namnyam.data.repository.AuthRepository
import com.example.namnyam.utils.UiState
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    var state: ((UiState<AuthResponseDto>) -> Unit)? = null

    fun login(email: String, password: String) {
        state?.invoke(UiState.Loading)

        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                state?.invoke(UiState.Success(response))
            } catch (e: Exception) {
                state?.invoke(UiState.Error(e.message ?: "Ошибка входа"))
            }
        }
    }
}