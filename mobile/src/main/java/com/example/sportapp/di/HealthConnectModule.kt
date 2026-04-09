package com.example.sportapp.di

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import com.example.sportapp.healthconnect.HealthConnectManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HealthConnectModule {

    @Provides
    @Singleton
    fun provideHealthConnectClient(@ApplicationContext context: Context): HealthConnectClient {
        return HealthConnectClient.getOrCreate(context)
    }

    // HealthConnectManager uses constructor injection, so we don't strictly need a @Provides here
    // unless we want to control its instantiation. Hilt will find the @Inject constructor.
}
