package com.minsikhein_bj01lr.mealmate.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun setEmail(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun setPassword(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun login() {
        // Logic that updates state
    }

    // Simulate login logic
    fun validateAndLogin(): Boolean {
        // In real app, you would call Firebase or API
        // For now, we simulate a successful login after user types "test@test.com"
        if (state.value.email == "test@test.com" && state.value.password == "password") {
            _state.update {
                it.copy(isAuthenticated = true)
            }
            return true
        }

        return false
    }
}