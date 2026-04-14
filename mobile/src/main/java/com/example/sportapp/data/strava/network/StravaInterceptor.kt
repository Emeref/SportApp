package com.example.sportapp.data.strava.network

import com.example.sportapp.data.strava.StravaStorage
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class StravaInterceptor @Inject constructor(
    private val storage: StravaStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking {
            storage.accessToken.firstOrNull()
        }

        val request = chain.request().newBuilder()
        if (accessToken != null) {
            request.addHeader("Authorization", "Bearer $accessToken")
        }
        
        return chain.proceed(request.build())
    }
}
