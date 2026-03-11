package com.example.sportapp.presentation.settings

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.maps.android.compose.MapType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val gson = Gson()

    companion object {
        private val MAP_TYPE_KEY = stringPreferencesKey("map_type")
        private val CLOCK_COLOR_KEY = longPreferencesKey("clock_color")
        private val HEALTH_DATA_KEY = stringPreferencesKey("health_data")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val mapType = preferences[MAP_TYPE_KEY]?.let { MapType.valueOf(it) } ?: MapType.NORMAL
            val clockColorValue = preferences[CLOCK_COLOR_KEY]
            val clockColor = if (clockColorValue == -1L) null else clockColorValue?.let { Color(it.toULong()) } ?: Color.Red
            val healthDataJson = preferences[HEALTH_DATA_KEY]
            val healthData = if (healthDataJson != null) {
                gson.fromJson(healthDataJson, HealthData::class.java)
            } else {
                HealthData()
            }

            UserSettings(mapType, clockColor, healthData)
        }

    suspend fun saveMapType(type: MapType) {
        context.dataStore.edit { preferences ->
            preferences[MAP_TYPE_KEY] = type.name
        }
    }

    suspend fun saveClockColor(color: Color?) {
        context.dataStore.edit { preferences ->
            preferences[CLOCK_COLOR_KEY] = color?.value?.toLong() ?: -1L
        }
    }

    suspend fun saveHealthData(data: HealthData) {
        context.dataStore.edit { preferences ->
            preferences[HEALTH_DATA_KEY] = gson.toJson(data)
        }
    }
}

data class UserSettings(
    val mapType: MapType,
    val clockColor: Color?,
    val healthData: HealthData
)
