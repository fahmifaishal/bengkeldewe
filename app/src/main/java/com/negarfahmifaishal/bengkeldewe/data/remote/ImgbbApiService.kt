package com.negarfahmifaishal.bengkeldewe.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class ImgbbResponse(
    val data: ImgbbData,
    val success: Boolean,
    val status: Int
)

@JsonClass(generateAdapter = true)
data class ImgbbData(
    @Json(name = "url") val url: String
)

interface ImgbbApiService {
    @Multipart
    @POST("1/upload")
    suspend fun uploadImage(
        @Query("key") apiKey: String,
        @Part image: MultipartBody.Part
    ): ImgbbResponse
}
