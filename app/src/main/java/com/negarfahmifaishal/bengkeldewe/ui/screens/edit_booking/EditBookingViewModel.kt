package com.negarfahmifaishal.bengkeldewe.ui.screens.edit_booking

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negarfahmifaishal.bengkeldewe.data.model.Booking
import com.negarfahmifaishal.bengkeldewe.data.remote.ApiConfig
import com.negarfahmifaishal.bengkeldewe.data.repository.BookingRepository
import com.negarfahmifaishal.bengkeldewe.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

class EditBookingViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: BookingRepository = BookingRepository(application)
) : AndroidViewModel(application) {

    private val _loadState = MutableStateFlow<UiState<Booking>>(UiState.Loading)
    val loadState: StateFlow<UiState<Booking>> = _loadState

    private val _updateState = MutableStateFlow<UiState<Booking>?>(null)
    val updateState: StateFlow<UiState<Booking>?> = _updateState

    fun getBookingById(id: String) {
        viewModelScope.launch {
            _loadState.value = UiState.Loading
            try {
                val booking = repository.getBookingById(id)
                _loadState.value = UiState.Success(booking)
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: ""
                _loadState.value = UiState.Error("HTTP ${e.code()}: $errorBody")
            } catch (e: Exception) {
                _loadState.value = UiState.Error(e.message ?: "Gagal memuat data booking")
            }
        }
    }

    fun updateBooking(
        id: String,
        userId: String,
        namaMotor: String,
        keluhan: String,
        tanggalBooking: String,
        currentImageUrl: String,
        newImageUri: Uri?
    ) {
        if (namaMotor.isBlank() || keluhan.isBlank() || tanggalBooking.isBlank()) {
            _updateState.value = UiState.Error("Semua field harus diisi")
            return
        }

        viewModelScope.launch {
            _updateState.value = UiState.Loading
            try {
                var finalImageUrl = currentImageUrl

                // 1. If user selected a new image, upload to ImgBB first
                if (newImageUri != null) {
                    val contentResolver = getApplication<Application>().contentResolver
                    val inputStream: InputStream? = contentResolver.openInputStream(newImageUri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes == null) {
                        _updateState.value = UiState.Error("Gagal membaca file gambar baru")
                        return@launch
                    }

                    val requestFile = bytes.toRequestBody("image/*".toMediaTypeOrNull(), 0, bytes.size)
                    val imagePart = MultipartBody.Part.createFormData("image", "motor_edit.jpg", requestFile)

                    val apiKey = ApiConfig.IMGBB_API_KEY
                    if (apiKey == "YOUR_IMGBB_API_KEY" || apiKey.isBlank()) {
                        _updateState.value = UiState.Error("Harap konfigurasi IMGBB_API_KEY di ApiConfig.kt")
                        return@launch
                    }

                    val imgbbResponse = repository.uploadImage(apiKey, imagePart)
                    finalImageUrl = imgbbResponse.data.url
                }

                // 2. Perform PUT request
                val updatedBooking = Booking(
                    id = id,
                    userId = userId,
                    namaMotor = namaMotor,
                    keluhan = keluhan,
                    tanggalBooking = tanggalBooking,
                    imageUrl = finalImageUrl
                )
                val result = repository.updateBooking(id, updatedBooking)
                _updateState.value = UiState.Success(result)
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: ""
                _updateState.value = UiState.Error("HTTP ${e.code()}: $errorBody")
            } catch (e: Exception) {
                _updateState.value = UiState.Error(e.message ?: "Gagal memperbarui data booking")
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = null
    }
}
