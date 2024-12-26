package com.dicoding.picodiploma.loginwithanimation.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import kotlinx.coroutines.launch

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun register(
        name: String,
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val message = userRepository.registerAccount(name, email, password)
                onResult(true, message)
            } catch (e: Exception) {
                onResult(false, e.message ?: "Registration failed")
            }
        }
    }
}