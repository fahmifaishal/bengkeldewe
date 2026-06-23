package com.negarfahmifaishal.bengkeldewe.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negarfahmifaishal.bengkeldewe.data.repository.BookingRepository
import com.negarfahmifaishal.bengkeldewe.datastore.AuthPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val authPreferences = AuthPreferences(application)
    private val repository = BookingRepository(application)

    val emailFlow: StateFlow<String> = authPreferences.emailFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val nameFlow: StateFlow<String> = authPreferences.nameFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val profilePhotoUrlFlow: StateFlow<String> = authPreferences.profilePhotoUrlFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut: StateFlow<Boolean> = _isLoggedOut

    fun logout() {
        viewModelScope.launch {
            try {
                repository.clearCache()
            } catch (e: Exception) {
                // Ignore or log error if database clear fails
            }
            authPreferences.clearSession()
            _isLoggedOut.value = true
        }
    }
}
