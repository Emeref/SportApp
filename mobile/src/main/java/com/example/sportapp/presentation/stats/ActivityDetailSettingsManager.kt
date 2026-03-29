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

private val Context.activityDetailDataStore: DataStore<Preferences> by preferencesDataStore(name = "activity_detail_settings")

data class ActivityDetailSettings(
    val visibleCharts: List<WidgetItem>,
    val visibleWidgets: List<WidgetItem>,
    val trackColor: Int
)

class ActivityDetailSettingsManager(private val context: Context) {
    private val gson = Gson()

    companion object {
        private fun getVisibleChartsKey(typeName: String) = stringPreferencesKey("visible_charts_$typeName")
        private fun getVisibleWidgetsKey(typeName: String) = stringPreferencesKey("visible_widgets_$typeName")
        private fun getTrackColorKey(typeName: String) = stringPreferencesKey("track_color_$typeName")

        fun getDefaultCharts(strings: AppStrings) = listOf(
            WidgetItem("map", strings.mapLabel),
            WidgetItem("bpm", "${strings.heartRate} (${strings.bpmUnit.lowercase()})"),
            WidgetItem("kalorie_min", strings.caloriesMin),
            WidgetItem("kroki_min", strings.cadenceSteps),
            WidgetItem("odl_kroki", strings.distanceStepsLabel),
            WidgetItem("predkosc_kroki", strings.speedStepsLabel),
            WidgetItem("gps_dystans", strings.distanceGpsLabel),
            WidgetItem("predkosc", "${strings.speed} (GPS)"),
            WidgetItem("wysokosc", strings.altitude),
            WidgetItem("przewyzszenia_gora", strings.totalAscentLabel),
            WidgetItem("przewyzszenia_dol", strings.totalDescentLabel),
            WidgetItem("pressure", strings.pressure)
        )

        fun getDefaultWidgets(strings: AppStrings) = listOf(
            WidgetItem("duration", strings.durationLabel),
            WidgetItem("max_bpm", strings.maxHeartRateLabel),
            WidgetItem("avg_bpm", strings.avgHeartRateLabel),
            WidgetItem("total_calories", strings.totalCaloriesLabel),
            WidgetItem("max_calories_min", strings.maxCaloriesBurnLabel),
            WidgetItem("avg_pace", strings.avgPaceLabel),
            WidgetItem("max_speed", strings.maxSpeedLabel),
            WidgetItem("max_altitude", strings.maxAltitudeLabel),
            WidgetItem("total_ascent", strings.totalAscentLabel),
            WidgetItem("total_descent", strings.totalDescentLabel),
            WidgetItem("avg_step_length", strings.avgStepLengthLabel),
            WidgetItem("avg_cadence", strings.avgCadenceLabel),
            WidgetItem("max_cadence", strings.maxCadenceLabel),
            WidgetItem("total_steps", strings.totalStepsLabel),
            WidgetItem("total_distance_gps", strings.distanceGpsLabel),
            WidgetItem("total_distance_steps", strings.distanceStepsLabel),
            WidgetItem("pressure_start", strings.pressureStartLabel),
            WidgetItem("pressure_end", strings.pressureEndLabel),
            WidgetItem("max_pressure", strings.maxPressureLabel),
            WidgetItem("min_pressure", strings.minPressureLabel),
            WidgetItem("best_pace_1km", strings.bestPace1kmLabel)
        )
        
        val DEFAULT_COLOR = 0xFFFF9800.toInt()
    }

    fun getSettingsFlow(typeName: String, strings: AppStrings): Flow<ActivityDetailSettings> = context.activityDetailDataStore.data.map { preferences ->
        val chartsJson = preferences[getVisibleChartsKey(typeName)]
        val charts = decodeList(chartsJson, getDefaultCharts(strings))
        
        val widgetsJson = preferences[getVisibleWidgetsKey(typeName)]
        val widgets = decodeList(widgetsJson, getDefaultWidgets(strings))
        
        val colorHex = preferences[getTrackColorKey(typeName)]
        val color = if (colorHex != null) {
            try {
                colorHex.toLong(16).toInt()
            } catch (e: NumberFormatException) {
                DEFAULT_COLOR
            }
        } else {
            DEFAULT_COLOR
        }

        ActivityDetailSettings(charts, widgets, color)
    }

    private fun decodeList(json: String?, default: List<WidgetItem>): List<WidgetItem> {
        if (json == null) return default
        return try {
            val type = object : TypeToken<List<WidgetItem>>() {}.type
            val decoded: List<WidgetItem> = gson.fromJson(json, type) ?: return default
            if (decoded.isEmpty()) return default
            
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
        } catch (e: Exception) {
            default
        }
    }

    suspend fun saveVisibleCharts(typeName: String, charts: List<WidgetItem>) {
        context.activityDetailDataStore.edit { preferences ->
            preferences[getVisibleChartsKey(typeName)] = gson.toJson(charts)
        }
    }

    suspend fun saveVisibleWidgets(typeName: String, widgets: List<WidgetItem>) {
        context.activityDetailDataStore.edit { preferences ->
            preferences[getVisibleWidgetsKey(typeName)] = gson.toJson(widgets)
        }
    }

    suspend fun saveTrackColor(typeName: String, color: Int) {
        context.activityDetailDataStore.edit { preferences ->
            preferences[getTrackColorKey(typeName)] = Integer.toHexString(color)
        }
    }

    suspend fun deleteSettings(typeName: String) {
        context.activityDetailDataStore.edit { preferences ->
            preferences.remove(getVisibleChartsKey(typeName))
            preferences.remove(getVisibleWidgetsKey(typeName))
            preferences.remove(getTrackColorKey(typeName))
        }
    }
}
