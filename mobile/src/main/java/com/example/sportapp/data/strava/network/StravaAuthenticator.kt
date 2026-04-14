package com.example.sportapp.data.strava.network

import com.example.sportapp.data.strava.StravaStorage
import com.example.sportapp.data.strava.api.StravaAuthApi
import com.example.sportapp.data.strava.model.StravaTokenResponse
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class StravaAuthenticator @Inject constructor(
    private val storage: StravaStorage,
    private val authApiProvider: Provider<StravaAuthApi>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Only try to refresh if it's a 401
        if (response.code != 401) return null

        return runBlocking {
            val refreshToken = storage.refreshToken.firstOrNull() ?: return@runBlocking null
            
            // Strava Client ID and Secret
            val clientId = "224679"
            val clientSecret = "9727c9a8bab91d5ef598d94baee374668998dde3"

            val authResponse = authApiProvider.get().refreshToken(
                clientId = clientId,
                clientSecret = clientSecret,
                refreshToken = refreshToken
            )

            if (authResponse.isSuccessful && authResponse.body() != null) {
                val newTokens = authResponse.body()!!
                storage.saveTokens(
                    accessToken = newTokens.accessToken,
                    refreshToken = newTokens.refreshToken,
                    expiresAt = newTokens.expiresAt
                )

                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                    .build()
            } else {
                null
            }
        }
    }
}
