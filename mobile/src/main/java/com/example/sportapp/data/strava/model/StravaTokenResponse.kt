package com.example.sportapp.data.strava.model

import com.google.gson.annotations.SerializedName

data class StravaTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_at") val expiresAt: Long,
    @SerializedName("athlete") val athlete: StravaAthlete? = null
)

data class StravaAthlete(
    @SerializedName("id") val id: Long,
    @SerializedName("firstname") val firstname: String,
    @SerializedName("lastname") val lastname: String
)
