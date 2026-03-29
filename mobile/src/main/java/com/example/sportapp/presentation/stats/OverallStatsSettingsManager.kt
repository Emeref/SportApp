package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.sportapp.core.i18n.AppStrings
import com.example.sportapp.presentation.settings.WidgetItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.overallStatsDataStore: DataStore<Preferences> by preferencesDataStore(name = "overall_stats_settings")

class OverallStatsSettingsManager(private val context: Context) {
    private val gson = Gson()

    companion object {
        private val WIDGETS_JSON = stringPreferencesKey("widgets_json")
        private val CHARTS_JSON = stringPreferencesKey("charts_json")
        
        fun getDefaultWidgets(strings: AppStrings) = listOf(
            WidgetItem("count", strings.activityCount),
            WidgetItem("calories", strings.totalCalories),
            WidgetItem("distanceGps", strings.distanceGpsLabel),
            WidgetItem("distanceSteps", strings.distanceStepsLabel),
            WidgetItem("ascent", strings.totalAscentLabel),
            WidgetItem("descent", strings.totalDescentLabel),
            WidgetItem("steps", strings.allSteps),
            WidgetItem("avg_cadence", strings.avgCadence),
            WidgetItem("max_speed", strings.maxSpeed),
            WidgetItem("max_altitude", strings.maxAltitude),
            WidgetItem("max_elevation_gain", strings.maxElevationGain),
            WidgetItem("max_distance", strings.maxDistance),
            WidgetItem("max_duration", strings.maxDuration),
            WidgetItem("max_calories", strings.maxCalories),
            WidgetItem("max_avg_cadence", strings.maxAvgAvgCadence),
            WidgetItem("max_avg_speed", strings.maxAvgSpeed)
        )

        fun getDefaultCharts(strings: AppStrings) = listOf(
            WidgetItem("calories", strings.totalCalories),
            WidgetItem("distanceGps", strings.distanceGpsLabel),
            WidgetItem("distanceSteps", strings.distanceStepsLabel),
            WidgetItem("ascent", strings.totalAscentLabel),
            WidgetItem("descent", strings.totalDescentLabel),
            WidgetItem("steps", strings.steps),
            WidgetItem("avg_cadence", strings.avgCadence),
            WidgetItem("maxPressure", strings.maxPressureLabel),
            WidgetItem("minPressure", strings.minPressureLabel),
            WidgetItem("bestPace1km", strings.bestPace1kmLabel)
        )
    }

    fun getWidgetsFlow(strings: AppStrings): Flow<List<WidgetItem>> = context.overallStatsDataStore.data.map { preferences ->
        val widgetsJson = preferences[WIDGETS_JSON]
        decodeList(widgetsJson, getDefaultWidgets(strings))
    }

    fun getChartsFlow(strings: AppStrings): Flow<List<WidgetItem>> = context.overallStatsDataStore.data.map { preferences ->
        val chartsJson = preferences[CHARTS_JSON]
        decodeList(chartsJson, getDefaultCharts(strings))
    }

    private fun decodeList(json: String?, default: List<WidgetItem>): List<WidgetItem> {
        if (json == null) return default
        return try {
            val type = object : TypeToken<List<WidgetItem>>() {}.type
            val decoded: List<WidgetItem> = gson.fromJson(json, type) ?: return default
            
            if (decoded.isEmpty()) {
                default
            } else {
                // Re-map labels from the current locale for existing IDs
                val currentIdsMap = default.associate { it.id to it.label }
                val filtered = decoded.filter { it.id in currentIdsMap.keys }.map {
                    it.copy(label = currentIdsMap[it.id] ?: it.label)
                }.toMutableList()

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
