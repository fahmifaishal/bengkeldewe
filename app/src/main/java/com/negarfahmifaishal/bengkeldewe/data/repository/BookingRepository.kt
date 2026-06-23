package com.negarfahmifaishal.bengkeldewe.data.repository

import android.content.Context
import com.negarfahmifaishal.bengkeldewe.data.model.Booking
import com.negarfahmifaishal.bengkeldewe.data.model.BookingRequest
import com.negarfahmifaishal.bengkeldewe.data.remote.ApiConfig
import com.negarfahmifaishal.bengkeldewe.data.remote.ImgbbResponse
import com.negarfahmifaishal.bengkeldewe.room.AppDatabase
import com.negarfahmifaishal.bengkeldewe.room.BookingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class BookingRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val dao = database.bookingDao()

    suspend fun getBookings(): List<Booking> {
        return try {
            val networkBookings = ApiConfig.apiService.getBookings()
            // Clear old local cache & save new list on IO thread
            withContext(Dispatchers.IO) {
                dao.deleteAllBookings()
                dao.insertBookings(networkBookings.map { BookingEntity.fromBooking(it) })
            }
            networkBookings
        } catch (e: Exception) {
            val cachedBookings = withContext(Dispatchers.IO) {
                dao.getAllBookings()
            }
            if (cachedBookings.isNotEmpty()) {
                cachedBookings.map { it.toBooking() }
            } else {
                throw e
            }
        }
    }

    suspend fun uploadImage(apiKey: String, image: MultipartBody.Part): ImgbbResponse {
        return ApiConfig.imgbbApiService.uploadImage(apiKey, image)
    }

    suspend fun createBooking(booking: Booking): Booking {
        val request = BookingRequest(
            userId = booking.userId,
            namaMotor = booking.namaMotor,
            keluhan = booking.keluhan,
            tanggalBooking = booking.tanggalBooking,
            imageUrl = booking.imageUrl
        )
        val result = ApiConfig.apiService.createBooking(request)
        // Cache locally on IO thread
        withContext(Dispatchers.IO) {
            dao.insertBookings(listOf(BookingEntity.fromBooking(result)))
        }
        return result
    }

    suspend fun getBookingById(id: String): Booking {
        return try {
            ApiConfig.apiService.getBookingById(id)
        } catch (e: Exception) {
            val cached = withContext(Dispatchers.IO) {
                dao.getAllBookings().firstOrNull { it.id == id }
            }
            cached?.toBooking() ?: throw e
        }
    }

    suspend fun updateBooking(id: String, booking: Booking): Booking {
        val request = BookingRequest(
            userId = booking.userId,
            namaMotor = booking.namaMotor,
            keluhan = booking.keluhan,
            tanggalBooking = booking.tanggalBooking,
            imageUrl = booking.imageUrl
        )
        val result = ApiConfig.apiService.updateBooking(id, request)
        withContext(Dispatchers.IO) {
            dao.insertBookings(listOf(BookingEntity.fromBooking(result)))
        }
        return result
    }

    suspend fun deleteBooking(id: String) {
        ApiConfig.apiService.deleteBooking(id)
        withContext(Dispatchers.IO) {
            dao.deleteBookingById(id)
        }
    }

    suspend fun clearCache() {
        withContext(Dispatchers.IO) {
            dao.deleteAllBookings()
        }
    }
}
