package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.sportapp.TextsMobilePL
import com.example.sportapp.presentation.settings.WidgetItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Używamy innej nazwy pliku, aby ustawienia były odizolowane
private val Context.overallStatsDataStore: DataStore<Preferences> by preferencesDataStore(name = "overall_stats_settings")

class OverallStatsSettingsManager(private val context: Context) {
    private val gson = Gson()

    companion object {
        private val WIDGETS_JSON = stringPreferencesKey("widgets_json")
        private val CHARTS_JSON = stringPreferencesKey("charts_json")
        
        val DEFAULT_WIDGETS = listOf(
            WidgetItem("count", TextsMobilePL.WIDGET_COUNT),
            WidgetItem("calories", TextsMobilePL.WIDGET_CALORIES),
            WidgetItem("distanceGps", TextsMobilePL.WIDGET_DISTANCE_GPS),
            WidgetItem("distanceSteps", TextsMobilePL.WIDGET_DISTANCE_STEPS),
            WidgetItem("ascent", TextsMobilePL.WIDGET_ASCENT),
            WidgetItem("descent", TextsMobilePL.WIDGET_DESCENT),
            WidgetItem("steps", TextsMobilePL.WIDGET_STEPS),
            WidgetItem("avg_cadence", TextsMobilePL.WIDGET_AVG_CADENCE),
            WidgetItem("max_speed", TextsMobilePL.WIDGET_MAX_SPEED),
            WidgetItem("max_altitude", TextsMobilePL.WIDGET_MAX_ALTITUDE),
            WidgetItem("max_elevation_gain", TextsMobilePL.WIDGET_MAX_ELEVATION_GAIN),
            WidgetItem("max_distance", TextsMobilePL.WIDGET_MAX_DISTANCE),
            WidgetItem("max_duration", TextsMobilePL.WIDGET_MAX_DURATION),
            WidgetItem("max_calories", TextsMobilePL.WIDGET_MAX_CALORIES),
            WidgetItem("max_avg_cadence", TextsMobilePL.WIDGET_MAX_AVG_CADENCE),
            WidgetItem("max_avg_speed", TextsMobilePL.WIDGET_MAX_AVG_SPEED)
        )

        val DEFAULT_CHARTS = listOf(
            WidgetItem("calories", TextsMobilePL.WIDGET_CALORIES),
            WidgetItem("distanceGps", TextsMobilePL.WIDGET_DISTANCE_GPS),
            WidgetItem("distanceSteps", TextsMobilePL.WIDGET_DISTANCE_STEPS),
            WidgetItem("ascent", TextsMobilePL.WIDGET_TOTAL_ASCENT),
            WidgetItem("descent", TextsMobilePL.WIDGET_TOTAL_DESCENT),
            WidgetItem("steps", TextsMobilePL.WIDGET_STEPS),
            WidgetItem("avg_cadence", TextsMobilePL.WIDGET_AVG_CADENCE),
            WidgetItem("maxPressure", TextsMobilePL.WIDGET_MAX_PRESSURE),
            WidgetItem("minPressure", TextsMobilePL.WIDGET_MIN_PRESSURE),
            WidgetItem("bestPace1km", TextsMobilePL.WIDGET_BEST_PACE_1KM)
        )
    }

    val widgetsFlow: Flow<List<WidgetItem>> = context.overallStatsDataStore.data.map { preferences ->
        val widgetsJson = preferences[WIDGETS_JSON]
        decodeList(widgetsJson, DEFAULT_WIDGETS)
    }

    val chartsFlow: Flow<List<WidgetItem>> = context.overallStatsDataStore.data.map { preferences ->
        val chartsJson = preferences[CHARTS_JSON]
        decodeList(chartsJson, DEFAULT_CHARTS)
    }

    private fun decodeList(json: String?, default: List<WidgetItem>): List<WidgetItem> {
        if (json == null) return default
        return try {
            val type = object : TypeToken<List<WidgetItem>>() {}.type
            val decoded: List<WidgetItem>? = gson.fromJson(json, type)
            
            if (decoded.isNullOrEmpty()) {
                default
            } else {
                // Merging logic
                val currentIds = default.map { it.id }.toSet()
                val filtered = decoded.filter { it.id in currentIds }.toMutableList()
                val missing = default.filter { def -> filtered.none { it.id == def.id } }
                if (missing.isNotEmpty()) {
                    filtered.addAll(missing)
                }
                filtered
            }
        } catch (e: Exception) {
            default
        }
    }

    suspend fun saveWidgets(widgets: List<WidgetItem>) {
        context.overallStatsDataStore.edit { preferences ->
            preferences[WIDGETS_JSON] = gson.toJson(widgets)
        }
    }

    suspend fun saveCharts(charts: List<WidgetItem>) {
        context.overallStatsDataStore.edit { preferences ->
            preferences[CHARTS_JSON] = gson.toJson(charts)
        }
    }
}
