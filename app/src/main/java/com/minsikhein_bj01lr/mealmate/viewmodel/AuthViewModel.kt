package com.minsikhein_bj01lr.mealmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.minsikhein_bj01lr.mealmate.data.model.User
import com.minsikhein_bj01lr.mealmate.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Represents various states of authentication
sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser, val dbUser: User) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    // Internal mutable state
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    // External read-only state
    val authState: StateFlow<AuthState> = _authState
    val currentUser: FirebaseUser? get() = auth.currentUser
    val lastLoginTimestamp: Long? get() = currentUser?.metadata?.lastSignInTimestamp
    val creationTimestamp: Long? get() = currentUser?.metadata?.creationTimestamp


    init {
        checkCurrentUser()
    }

    // Check if user is already logged in
    private fun checkCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                _authState.value = AuthState.Loading
                val dbUser = userRepository.getCurrentUser(currentUser.uid)
                if (dbUser != null) {
                    _authState.value = AuthState.Authenticated(currentUser, dbUser)
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            }
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    // Email format validation
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Handle login flow
    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or Password cannot be empty")
            return
        }

        if (!isValidEmail(email)) {
            _authState.value = AuthState.Error("Please enter a valid email address")
            return
        }

        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        viewModelScope.launch {
                            val dbUser = userRepository.getCurrentUser(user.uid)
                            if (dbUser != null) {
                                _authState.value = AuthState.Authenticated(user, dbUser)
                            } else {
                                _authState.value = AuthState.Error("User data not found")
                            }
                        }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    // Handle signup flow
    fun signup(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or Password cannot be empty")
            return
        }

        if (!isValidEmail(email)) {
            _authState.value = AuthState.Error("Please enter a valid email address")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        val defaultName = email.substringBefore("@")
                        viewModelScope.launch {
                            // Save to Firestore if not exists
                            userRepository.createUserIfNotExists(user, defaultName)

                            // Then fetch the created user doc
                            val dbUser = userRepository.getCurrentUser(user.uid)
                            if (dbUser != null) {
                                _authState.value = AuthState.Authenticated(user, dbUser)
                            } else {
                                _authState.value = AuthState.Error("User data not found")
                            }
                        }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "An unknown error occurred")
                }
            }
    }

    // Sign out the user
    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}
