package com.negarfahmifaishal.bengkeldewe.ui.screens.add_booking

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negarfahmifaishal.bengkeldewe.data.model.Booking
import com.negarfahmifaishal.bengkeldewe.data.remote.ApiConfig
import com.negarfahmifaishal.bengkeldewe.data.repository.BookingRepository
import com.negarfahmifaishal.bengkeldewe.datastore.AuthPreferences
import com.negarfahmifaishal.bengkeldewe.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

class AddBookingViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: BookingRepository = BookingRepository(application)
) : AndroidViewModel(application) {

    private val authPreferences = AuthPreferences(application)

    private val _addBookingState = MutableStateFlow<UiState<Booking>?>(null)
    val addBookingState: StateFlow<UiState<Booking>?> = _addBookingState

    fun addBooking(
        namaMotor: String,
        keluhan: String,
        tanggalBooking: String,
        imageUri: Uri?
    ) {
        if (namaMotor.isBlank() || keluhan.isBlank() || tanggalBooking.isBlank() || imageUri == null) {
            _addBookingState.value = UiState.Error("Semua field dan foto harus diisi")
            return
        }

        viewModelScope.launch {
            _addBookingState.value = UiState.Loading
            try {
                // 1. Convert Uri to MultipartBody.Part
                val contentResolver = getApplication<Application>().contentResolver
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes == null) {
                    _addBookingState.value = UiState.Error("Gagal membaca file gambar")
                    return@launch
                }

                val requestFile = bytes.toRequestBody("image/*".toMediaTypeOrNull(), 0, bytes.size)
                val imagePart = MultipartBody.Part.createFormData("image", "motor.jpg", requestFile)

                // 2. Upload to ImgBB
                val apiKey = ApiConfig.IMGBB_API_KEY
                if (apiKey == "YOUR_IMGBB_API_KEY" || apiKey.isBlank()) {
                    _addBookingState.value = UiState.Error("Harap konfigurasi IMGBB_API_KEY di ApiConfig.kt")
                    return@launch
                }
                
                val imgbbResponse = repository.uploadImage(apiKey, imagePart)
                val imageUrl = imgbbResponse.data.url

                // 3. Save to MockAPI (using user's email as userId)
                val userEmail = authPreferences.emailFlow.first()
                val newBooking = Booking(
                    userId = userEmail,
                    namaMotor = namaMotor,
                    keluhan = keluhan,
                    tanggalBooking = tanggalBooking,
                    imageUrl = imageUrl
                )
                val savedBooking = repository.createBooking(newBooking)
                _addBookingState.value = UiState.Success(savedBooking)
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: ""
                _addBookingState.value = UiState.Error("HTTP ${e.code()}: $errorBody")
            } catch (e: Exception) {
                _addBookingState.value = UiState.Error(e.message ?: "Terjadi kesalahan saat menyimpan data")
            }
        }
    }

    fun resetState() {
        _addBookingState.value = null
    }
}
