package com.example.sportapp.presentation.settings

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.sportapp.core.i18n.AppLanguage
import com.example.sportapp.core.i18n.AppStrings
import com.example.sportapp.core.i18n.PlStrings
import com.example.sportapp.presentation.workout.DataLayerManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ScreenBehavior {
    KEEP_SCREEN_ON, AMBIENT, SYSTEM
}

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataLayerManagerProvider: Provider<DataLayerManager>
) {
    private val gson = Gson()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private val CLOCK_COLOR_KEY = longPreferencesKey("clock_color")
        private val HEALTH_DATA_KEY = stringPreferencesKey("health_data")
        private val SCREEN_BEHAVIOR_KEY = stringPreferencesKey("screen_behavior")
        
        private val WATCH_STATS_WIDGETS_KEY = stringPreferencesKey("watch_stats_widgets")
        private val WATCH_STATS_PERIOD_KEY = stringPreferencesKey("watch_stats_period")
        private val WATCH_STATS_CUSTOM_DAYS_KEY = intPreferencesKey("watch_stats_custom_days")
        private val APP_LANGUAGE_KEY = stringPreferencesKey("app_language")
        
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
            val clockColorValue = preferences[CLOCK_COLOR_KEY]
            val clockColor = if (clockColorValue == -1L) null else clockColorValue?.let { Color(it.toULong()) } ?: Color.Red
            val healthDataJson = preferences[HEALTH_DATA_KEY]
            val healthData = if (healthDataJson != null) {
                gson.fromJson(healthDataJson, HealthData::class.java)
            } else {
                HealthData()
            }
            val screenBehavior = preferences[SCREEN_BEHAVIOR_KEY]?.let { ScreenBehavior.valueOf(it) } ?: ScreenBehavior.KEEP_SCREEN_ON

            // We use PlStrings as a reference for default labels for persistence logic
            val defaultWidgets = getDefaultWidgets(PlStrings)

            val watchStatsWidgetsJson = preferences[WATCH_STATS_WIDGETS_KEY]
            val watchStatsWidgets = if (watchStatsWidgetsJson != null) {
                try {
                    val type = object : TypeToken<List<WidgetItem>>() {}.type
                    val decoded: List<WidgetItem>? = gson.fromJson(watchStatsWidgetsJson, type)
                    if (decoded.isNullOrEmpty()) {
                        defaultWidgets
                    } else {
                        // Merging logic
                        val currentIds = defaultWidgets.map { it.id }.toSet()
                        val filtered = decoded.filter { it.id in currentIds }.toMutableList()
                        val missing = defaultWidgets.filter { def -> filtered.none { it.id == def.id } }
                        if (missing.isNotEmpty()) {
                            filtered.addAll(missing)
                        }
                        filtered
                    }
                } catch (e: Exception) {
                    defaultWidgets
                }
            } else {
                defaultWidgets
            }
            val watchStatsPeriod = preferences[WATCH_STATS_PERIOD_KEY]?.let { ReportingPeriod.valueOf(it) } ?: ReportingPeriod.WEEK
            val watchStatsCustomDays = preferences[WATCH_STATS_CUSTOM_DAYS_KEY] ?: 7
            val appLanguage = preferences[APP_LANGUAGE_KEY]?.let { AppLanguage.valueOf(it) } ?: AppLanguage.POLISH

            UserSettings(
                clockColor, healthData, screenBehavior,
                watchStatsWidgets, watchStatsPeriod, watchStatsCustomDays,
                appLanguage
            )
        }

    fun getDefaultWidgets(strings: AppStrings) = listOf(
        WidgetItem("count", strings.activityCount),
        WidgetItem("calories", strings.totalCalories),
        WidgetItem("distanceGps", strings.distanceGps),
        WidgetItem("distanceSteps", strings.distanceSteps),
        WidgetItem("ascent", strings.ascent),
        WidgetItem("descent", strings.descent),
        WidgetItem("steps", strings.allSteps)
    )

    private fun triggerSync() {
        scope.launch {
            try {
                dataLayerManagerProvider.get().syncAll()
            } catch (e: Exception) {
                // Handle potential circular dependency or initialization issues
            }
        }
    }

    suspend fun saveWatchStatsSettings(widgets: List<WidgetItem>, period: ReportingPeriod, customDays: Int) {
        context.dataStore.edit { preferences ->
            preferences[WATCH_STATS_WIDGETS_KEY] = gson.toJson(widgets)
            preferences[WATCH_STATS_PERIOD_KEY] = period.name
            preferences[WATCH_STATS_CUSTOM_DAYS_KEY] = customDays
        }
        triggerSync()
    }

    suspend fun saveClockColor(color: Color?) {
        context.dataStore.edit { preferences ->
            preferences[CLOCK_COLOR_KEY] = color?.value?.toLong() ?: -1L
        }
        triggerSync()
    }

    suspend fun saveHealthData(data: HealthData) {
        context.dataStore.edit { preferences ->
            preferences[HEALTH_DATA_KEY] = gson.toJson(data)
        }
        triggerSync()
    }

    suspend fun saveScreenBehavior(behavior: ScreenBehavior) {
        context.dataStore.edit { preferences ->
            preferences[SCREEN_BEHAVIOR_KEY] = behavior.name
        }
        triggerSync()
    }

    suspend fun saveAppLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[APP_LANGUAGE_KEY] = language.name
        }
        triggerSync()
    }
}

data class UserSettings(
    val clockColor: Color?,
    val healthData: HealthData,
    val screenBehavior: ScreenBehavior,
    val watchStatsWidgets: List<WidgetItem>,
    val watchStatsPeriod: ReportingPeriod,
    val watchStatsCustomDays: Int,
    val appLanguage: AppLanguage = AppLanguage.POLISH
)
