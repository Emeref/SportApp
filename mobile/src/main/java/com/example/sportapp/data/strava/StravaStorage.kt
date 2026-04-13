package com.example.sportapp.data.strava

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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

    val accessToken: Flow<String?> = context.stravaDataStore.data.map { it[ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = context.stravaDataStore.data.map { it[REFRESH_TOKEN] }
    val expiresAt: Flow<Long?> = context.stravaDataStore.data.map { it[EXPIRES_AT] }

    suspend fun saveTokens(accessToken: String, refreshToken: String, expiresAt: Long) {
        context.stravaDataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            prefs[REFRESH_TOKEN] = refreshToken
            prefs[EXPIRES_AT] = expiresAt
        }
    }

    suspend fun clearTokens() {
        context.stravaDataStore.edit { it.clear() }
    }
}
