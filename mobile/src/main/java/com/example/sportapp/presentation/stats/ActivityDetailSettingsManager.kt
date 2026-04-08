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

        val DEFAULT_CHARTS = listOf(
            WidgetItem("map", TextsMobilePL.DETAIL_MAP),
            WidgetItem("bpm", TextsMobilePL.DETAIL_HEART_RATE),
            WidgetItem("kalorie_min", TextsMobilePL.SENSOR_CALORIES_MIN),
            WidgetItem("kroki_min", TextsMobilePL.SENSOR_STEPS_MIN),
            WidgetItem("avg_step_length_over_time", TextsMobilePL.SENSOR_AVG_STEP_LENGTH),
            WidgetItem("odl_kroki", TextsMobilePL.SENSOR_DISTANCE_STEPS),
            WidgetItem("predkosc_kroki", TextsMobilePL.SENSOR_SPEED_STEPS),
            WidgetItem("gps_dystans", TextsMobilePL.SENSOR_DISTANCE_GPS),
            WidgetItem("predkosc", TextsMobilePL.SENSOR_SPEED_GPS),
            WidgetItem("wysokosc", TextsMobilePL.SENSOR_ALTITUDE),
            WidgetItem("przewyzszenia_gora", TextsMobilePL.SENSOR_TOTAL_ASCENT),
            WidgetItem("przewyzszenia_dol", TextsMobilePL.SENSOR_TOTAL_DESCENT),
            WidgetItem("pressure", TextsMobilePL.SENSOR_PRESSURE)
        )

        val DEFAULT_WIDGETS = listOf(
            WidgetItem("duration", TextsMobilePL.WIDGET_DURATION),
            WidgetItem("max_bpm", TextsMobilePL.WIDGET_MAX_BPM),
            WidgetItem("avg_bpm", TextsMobilePL.WIDGET_AVG_BPM),
            WidgetItem("total_calories", TextsMobilePL.WIDGET_TOTAL_CALORIES),
            WidgetItem("max_calories_min", TextsMobilePL.WIDGET_MAX_CALORIES_MIN),
            WidgetItem("avg_pace", TextsMobilePL.WIDGET_AVG_PACE),
            WidgetItem("avg_speed_gps", TextsMobilePL.WIDGET_AVG_SPEED_GPS),
            WidgetItem("avg_speed_steps", TextsMobilePL.WIDGET_AVG_SPEED_STEPS),
            WidgetItem("max_speed", TextsMobilePL.WIDGET_MAX_SPEED),
            WidgetItem("max_altitude", TextsMobilePL.WIDGET_MAX_ALTITUDE),
            WidgetItem("total_ascent", TextsMobilePL.WIDGET_TOTAL_ASCENT),
            WidgetItem("total_descent", TextsMobilePL.WIDGET_TOTAL_DESCENT),
            WidgetItem("avg_step_length", TextsMobilePL.WIDGET_AVG_STEP_LENGTH),
            WidgetItem("avg_cadence", TextsMobilePL.WIDGET_AVG_CADENCE_DESC),
            WidgetItem("max_cadence", TextsMobilePL.WIDGET_MAX_CADENCE),
            WidgetItem("total_steps", TextsMobilePL.WIDGET_TOTAL_STEPS),
            WidgetItem("total_distance_gps", TextsMobilePL.WIDGET_DISTANCE_GPS),
            WidgetItem("total_distance_steps", TextsMobilePL.WIDGET_DISTANCE_STEPS),
            WidgetItem("pressure_start", TextsMobilePL.WIDGET_PRESSURE_START),
            WidgetItem("pressure_end", TextsMobilePL.WIDGET_PRESSURE_END),
            WidgetItem("max_pressure", TextsMobilePL.WIDGET_MAX_PRESSURE),
            WidgetItem("min_pressure", TextsMobilePL.WIDGET_MIN_PRESSURE),
            WidgetItem("best_pace_1km", TextsMobilePL.WIDGET_BEST_PACE_1KM)
        )
        
        val DEFAULT_COLOR = 0xFFFF9800.toInt()
    }

    fun getSettingsFlow(typeName: String): Flow<ActivityDetailSettings> = context.activityDetailDataStore.data.map { preferences ->
        val chartsJson = preferences[getVisibleChartsKey(typeName)]
        val charts = decodeList(chartsJson, DEFAULT_CHARTS)
        
        val widgetsJson = preferences[getVisibleWidgetsKey(typeName)]
        val widgets = decodeList(widgetsJson, DEFAULT_WIDGETS)
        
        val colorHex = preferences[getTrackColorKey(typeName)]
        val color = if (colorHex != null) {
            try {
                colorHex.toLong(16).toInt()
            } catch (e: Exception) {
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
            val decoded: List<WidgetItem> = gson.fromJson(json, type)
            if (decoded.isEmpty()) return default
            
            // Filter out old/removed items and add new ones from default
            val currentIds = default.map { it.id }.toSet()
            val filtered = decoded.filter { it.id in currentIds }.toMutableList()
            
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
