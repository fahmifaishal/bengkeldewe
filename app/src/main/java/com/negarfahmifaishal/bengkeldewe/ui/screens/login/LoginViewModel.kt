package com.negarfahmifaishal.bengkeldewe.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negarfahmifaishal.bengkeldewe.datastore.AuthPreferences
import com.negarfahmifaishal.bengkeldewe.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val authPreferences = AuthPreferences(application)

    private val _loginState = MutableStateFlow<UiState<Unit>?>(null)
    val loginState: StateFlow<UiState<Unit>?> = _loginState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = UiState.Error("Email dan password tidak boleh kosong")
            return
        }

        if (!email.contains("@")) {
            _loginState.value = UiState.Error("Format email tidak valid")
            return
        }

        if (password.length < 6) {
            _loginState.value = UiState.Error("Password minimal harus 6 karakter")
            return
        }

        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                // Extract name prefix from email (e.g. "faishal@gmail.com" -> "Faishal")
                val namePrefix = email.substringBefore("@")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

                // Save session in DataStore
                authPreferences.saveSession(email, namePrefix)
                _loginState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Gagal memproses login")
            }
        }
    }

    fun loginWithGoogle(email: String, name: String, photoUrl: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                authPreferences.saveSession(email, name, photoUrl)
                _loginState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Gagal memproses login Google")
            }
        }
    }

    fun resetState() {
        _loginState.value = null
    }
}
