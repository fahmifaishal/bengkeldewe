package com.negarfahmifaishal.bengkeldewe.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Booking(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "userId")
    val userId: String,
    @Json(name = "namaMotor")
    val namaMotor: String,
    @Json(name = "keluhan")
    val keluhan: String,
    @Json(name = "tanggalBooking")
    val tanggalBooking: String,
    @Json(name = "imageUrl")
    val imageUrl: String
)

@JsonClass(generateAdapter = true)
data class BookingRequest(
    @Json(name = "userId")
    val userId: String,
    @Json(name = "namaMotor")
    val namaMotor: String,
    @Json(name = "keluhan")
    val keluhan: String,
    @Json(name = "tanggalBooking")
    val tanggalBooking: String,
    @Json(name = "imageUrl")
    val imageUrl: String
)
