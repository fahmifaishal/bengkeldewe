package com.negarfahmifaishal.bengkeldewe.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negarfahmifaishal.bengkeldewe.data.model.Booking
import com.negarfahmifaishal.bengkeldewe.data.repository.BookingRepository
import com.negarfahmifaishal.bengkeldewe.datastore.AuthPreferences
import com.negarfahmifaishal.bengkeldewe.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: BookingRepository = BookingRepository(application)
) : AndroidViewModel(application) {

    private val authPreferences = AuthPreferences(application)

    private val _uiState = MutableStateFlow<UiState<List<Booking>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Booking>>> = _uiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        observeUserEmail()
    }

    private fun observeUserEmail() {
        viewModelScope.launch {
            authPreferences.emailFlow.collect { email ->
                if (email.isNotEmpty()) {
                    fetchBookings(email)
                } else {
                    _uiState.value = UiState.Success(emptyList())
                }
            }
        }
    }

    fun getBookings() {
        viewModelScope.launch {
            val email = authPreferences.emailFlow.first()
            if (email.isNotEmpty()) {
                fetchBookings(email)
            } else {
                _uiState.value = UiState.Success(emptyList())
            }
        }
    }

    private suspend fun fetchBookings(email: String) {
        _uiState.value = UiState.Loading
        try {
            val bookings = repository.getBookings()
            val filteredBookings = bookings.filter { it.userId == email || it.userId == "123" }
            _uiState.value = UiState.Success(filteredBookings)
        } catch (e: Exception) {
            _uiState.value = UiState.Error(e.message ?: "Terjadi kesalahan")
        }
    }

    fun deleteBooking(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.deleteBooking(id)
                getBookings() // Refresh list
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Gagal menghapus booking")
            }
        }
    }

    fun refreshBookings() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val email = authPreferences.emailFlow.first()
            if (email.isNotEmpty()) {
                try {
                    val bookings = repository.getBookings()
                    val filteredBookings = bookings.filter { it.userId == email || it.userId == "123" }
                    _uiState.value = UiState.Success(filteredBookings)
                } catch (e: Exception) {
                    _uiState.value = UiState.Error(e.message ?: "Terjadi kesalahan")
                }
            } else {
                _uiState.value = UiState.Success(emptyList())
            }
            _isRefreshing.value = false
        }
    }
}
