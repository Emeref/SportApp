package com.example.sportapp.presentation.settings

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
        private val AUTO_CENTER_DELAY_KEY = intPreferencesKey("auto_center_delay")
        private val SHOW_ROUTE_KEY = booleanPreferencesKey("show_route")
        private val ROUTE_COLOR_KEY = longPreferencesKey("route_color")
        
        val Orange = Color(0xFFFFA500)
        val Transparent = Color.Transparent
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
            val autoCenterDelay = preferences[AUTO_CENTER_DELAY_KEY] ?: 5 // Default 5 seconds
            val showRoute = preferences[SHOW_ROUTE_KEY] ?: true
            val routeColorValue = preferences[ROUTE_COLOR_KEY] ?: Orange.toArgb().toLong()
            val routeColor = if (routeColorValue == -1L) Transparent else Color(routeColorValue.toULong())

            UserSettings(mapType, clockColor, healthData, autoCenterDelay, showRoute, routeColor)
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

    suspend fun saveAutoCenterDelay(delaySeconds: Int) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_CENTER_DELAY_KEY] = delaySeconds
        }
    }

    suspend fun saveShowRoute(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_ROUTE_KEY] = show
        }
    }

    suspend fun saveRouteColor(color: Color) {
        context.dataStore.edit { preferences ->
            preferences[ROUTE_COLOR_KEY] = if (color == Transparent) -1L else color.value.toLong()
        }
    }
}

data class UserSettings(
    val mapType: MapType,
    val clockColor: Color?,
    val healthData: HealthData,
    val autoCenterDelay: Int,
    val showRoute: Boolean,
    val routeColor: Color
)
