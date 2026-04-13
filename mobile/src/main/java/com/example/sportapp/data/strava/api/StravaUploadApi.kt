package com.example.sportapp.data.strava.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface StravaUploadApi {
    @Multipart
    @POST("api/v3/uploads")
    suspend fun uploadActivity(
        @Part file: MultipartBody.Part,
        @Part("data_type") dataType: RequestBody,
        @Part("activity_type") activityType: RequestBody,
        @Part("description") description: RequestBody? = null,
        @Part("name") name: RequestBody? = null
    ): Response<StravaUploadResponse>
}

data class StravaUploadResponse(
    val id: Long,
    val status: String,
    val error: String?
)
