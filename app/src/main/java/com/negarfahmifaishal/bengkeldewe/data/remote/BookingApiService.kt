package com.negarfahmifaishal.bengkeldewe.data.remote

import com.negarfahmifaishal.bengkeldewe.data.model.Booking
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BookingApiService {

    @GET("bookings")
    suspend fun getBookings(): List<Booking>

    @GET("bookings/{id}")
    suspend fun getBookingById(
        @Path("id") id: String
    ): Booking

    @POST("bookings")
    suspend fun createBooking(
        @Body booking: com.negarfahmifaishal.bengkeldewe.data.model.BookingRequest
    ): Booking

    @PUT("bookings/{id}")
    suspend fun updateBooking(
        @Path("id") id: String,
        @Body booking: com.negarfahmifaishal.bengkeldewe.data.model.BookingRequest
    ): Booking

    @DELETE("bookings/{id}")
    suspend fun deleteBooking(
        @Path("id") id: String
    )
}