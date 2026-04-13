package com.example.sportapp.di

import com.example.sportapp.data.strava.api.StravaAuthApi
import com.example.sportapp.data.strava.api.StravaUploadApi
import com.example.sportapp.data.strava.network.StravaAuthenticator
import com.example.sportapp.data.strava.network.StravaInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StravaNetworkModule {

    private const val STRAVA_BASE_URL = "https://www.strava.com/"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    @Named("StravaAuthClient")
    fun provideStravaAuthClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("StravaApiClient")
    fun provideStravaApiClient(
        loggingInterceptor: HttpLoggingInterceptor,
        stravaInterceptor: StravaInterceptor,
        stravaAuthenticator: StravaAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(stravaInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(stravaAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideStravaAuthApi(
        @Named("StravaAuthClient") okHttpClient: OkHttpClient
    ): StravaAuthApi {
        return Retrofit.Builder()
            .baseUrl(STRAVA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StravaAuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStravaUploadApi(
        @Named("StravaApiClient") okHttpClient: OkHttpClient
    ): StravaUploadApi {
        return Retrofit.Builder()
            .baseUrl(STRAVA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StravaUploadApi::class.java)
    }
}
