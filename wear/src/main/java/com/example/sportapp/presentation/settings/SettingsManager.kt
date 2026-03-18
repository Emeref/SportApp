package com.example.sportapp.presentation.settings

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.sportapp.presentation.settings.ReportingPeriod
import com.example.sportapp.presentation.settings.WidgetItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.compose.MapType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ScreenBehavior {
    KEEP_SCREEN_ON, AMBIENT, SYSTEM
}

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
        private val SCREEN_BEHAVIOR_KEY = stringPreferencesKey("screen_behavior")
        
        private val WATCH_STATS_WIDGETS_KEY = stringPreferencesKey("watch_stats_widgets")
        private val WATCH_STATS_PERIOD_KEY = stringPreferencesKey("watch_stats_period")
        private val WATCH_STATS_CUSTOM_DAYS_KEY = intPreferencesKey("watch_stats_custom_days")
        
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
            val autoCenterDelay = preferences[AUTO_CENTER_DELAY_KEY] ?: 5
            val showRoute = preferences[SHOW_ROUTE_KEY] ?: true
            val routeColorValue = preferences[ROUTE_COLOR_KEY] ?: Orange.toArgb().toLong()
            val routeColor = if (routeColorValue == -1L) Transparent else Color(routeColorValue.toULong())
            val screenBehavior = preferences[SCREEN_BEHAVIOR_KEY]?.let { ScreenBehavior.valueOf(it) } ?: ScreenBehavior.KEEP_SCREEN_ON

            val watchStatsWidgetsJson = preferences[WATCH_STATS_WIDGETS_KEY]
            val watchStatsWidgets = if (watchStatsWidgetsJson != null) {
                try {
                    val type = object : TypeToken<List<WidgetItem>>() {}.type
                    gson.fromJson<List<WidgetItem>>(watchStatsWidgetsJson, type) ?: defaultWatchStatsWidgets
                } catch (e: Exception) {
                    defaultWatchStatsWidgets
                }
            } else {
                defaultWatchStatsWidgets
            }
            val watchStatsPeriod = preferences[WATCH_STATS_PERIOD_KEY]?.let { ReportingPeriod.valueOf(it) } ?: ReportingPeriod.WEEK
            val watchStatsCustomDays = preferences[WATCH_STATS_CUSTOM_DAYS_KEY] ?: 7

            UserSettings(
                mapType, clockColor, healthData, autoCenterDelay, showRoute, routeColor, screenBehavior,
                watchStatsWidgets, watchStatsPeriod, watchStatsCustomDays
            )
        }

    private val defaultWatchStatsWidgets = listOf(
        WidgetItem("count", "Liczba aktywności"),
        WidgetItem("calories", "Spalone kalorie"),
        WidgetItem("distanceGps", "Dystans (GPS)"),
        WidgetItem("distanceSteps", "Dystans (kroki)"),
        WidgetItem("ascent", "Przewyższenia w górę"),
        WidgetItem("descent", "Przewyższenia w dół"),
        WidgetItem("steps", "Wszystkie kroki")
    )

    suspend fun saveWatchStatsSettings(widgets: List<WidgetItem>, period: ReportingPeriod, customDays: Int) {
        context.dataStore.edit { preferences ->
            preferences[WATCH_STATS_WIDGETS_KEY] = gson.toJson(widgets)
            preferences[WATCH_STATS_PERIOD_KEY] = period.name
            preferences[WATCH_STATS_CUSTOM_DAYS_KEY] = customDays
        }
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

    suspend fun saveScreenBehavior(behavior: ScreenBehavior) {
        context.dataStore.edit { preferences ->
            preferences[SCREEN_BEHAVIOR_KEY] = behavior.name
        }
    }
}

data class UserSettings(
    val mapType: MapType,
    val clockColor: Color?,
    val healthData: HealthData,
    val autoCenterDelay: Int,
    val showRoute: Boolean,
    val routeColor: Color,
    val screenBehavior: ScreenBehavior,
    val watchStatsWidgets: List<WidgetItem>,
    val watchStatsPeriod: ReportingPeriod,
    val watchStatsCustomDays: Int
)
