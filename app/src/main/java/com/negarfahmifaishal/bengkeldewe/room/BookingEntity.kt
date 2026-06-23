package com.negarfahmifaishal.bengkeldewe.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.negarfahmifaishal.bengkeldewe.data.model.Booking

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val namaMotor: String,
    val keluhan: String,
    val tanggalBooking: String,
    val imageUrl: String
) {
    fun toBooking(): Booking {
        return Booking(
            id = id,
            userId = userId,
            namaMotor = namaMotor,
            keluhan = keluhan,
            tanggalBooking = tanggalBooking,
            imageUrl = imageUrl
        )
    }

    companion object {
        fun fromBooking(booking: Booking): BookingEntity {
            return BookingEntity(
                id = booking.id,
                userId = booking.userId,
                namaMotor = booking.namaMotor,
                keluhan = booking.keluhan,
                tanggalBooking = booking.tanggalBooking,
                imageUrl = booking.imageUrl
            )
        }
    }
}
