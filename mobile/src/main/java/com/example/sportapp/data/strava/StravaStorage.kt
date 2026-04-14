package com.example.sportapp.data.strava

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.stravaDataStore: DataStore<Preferences> by preferencesDataStore(name = "strava_prefs")

@Singleton
class StravaStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    private val EXPIRES_AT = longPreferencesKey("expires_at")
    private val ATHLETE_NAME = stringPreferencesKey("athlete_name")

    val accessToken: Flow<String?> = context.stravaDataStore.data.map { it[ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = context.stravaDataStore.data.map { it[REFRESH_TOKEN] }
    val expiresAt: Flow<Long?> = context.stravaDataStore.data.map { it[EXPIRES_AT] }
    val athleteName: Flow<String?> = context.stravaDataStore.data.map { it[ATHLETE_NAME] }
    
    val isConnected: Flow<Boolean> = accessToken.map { it != null }

    suspend fun saveTokens(accessToken: String, refreshToken: String, expiresAt: Long, athleteName: String? = null) {
        context.stravaDataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            prefs[REFRESH_TOKEN] = refreshToken
            prefs[EXPIRES_AT] = expiresAt
            athleteName?.let { prefs[ATHLETE_NAME] = it }
        }
    }

    suspend fun clearTokens() {
        context.stravaDataStore.edit { it.clear() }
    }
}
