package com.example.sportapp.data.strava.network

import com.example.sportapp.BuildConfig
import com.example.sportapp.data.strava.StravaStorage
import com.example.sportapp.data.strava.api.StravaAuthApi
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
            
            val clientId = BuildConfig.STRAVA_CLIENT_ID
            val clientSecret = BuildConfig.STRAVA_CLIENT_SECRET

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
