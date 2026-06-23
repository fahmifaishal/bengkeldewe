package com.negarfahmifaishal.bengkeldewe.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings")
    suspend fun getAllBookings(): List<BookingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<BookingEntity>)

    @Query("DELETE FROM bookings")
    suspend fun deleteAllBookings()

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteBookingById(id: String)
}
