package com.negarfahmifaishal.bengkeldewe.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiConfig {

    private const val BASE_URL =
        "https://6a3ad393917c7b14c74e20a8.mockapi.io/api/v1/"

    const val IMGBB_API_KEY = "5dc6daf19124d53b4a189f7a2a425df5"

    val apiService: BookingApiService by lazy {

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                MoshiConverterFactory.create(moshi)
            )
            .build()
            .create(BookingApiService::class.java)
    }

    val imgbbApiService: ImgbbApiService by lazy {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.imgbb.com/")
            .addConverterFactory(
                MoshiConverterFactory.create(moshi)
            )
            .build()
            .create(ImgbbApiService::class.java)
    }
}