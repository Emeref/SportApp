package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
            WidgetItem("map", "Mapa"),
            WidgetItem("bpm", "Tętno (bpm)"),
            WidgetItem("kalorie_min", "Kalorie/min"),
            WidgetItem("kroki_min", "Kadencja (kroki/min)"),
            WidgetItem("odl_kroki", "Dystans (kroki)"),
            WidgetItem("predkosc_kroki", "Prędkość (kroki)"),
            WidgetItem("gps_dystans", "Dystans (GPS)"),
            WidgetItem("predkosc", "Prędkość (GPS)"),
            WidgetItem("wysokosc", "Wysokość"),
            WidgetItem("przewyzszenia_gora", "Suma podejść"),
            WidgetItem("przewyzszenia_dol", "Suma zejść"),
            WidgetItem("pressure", "Ciśnienie atmosferyczne")
        )

        val DEFAULT_WIDGETS = listOf(
            WidgetItem("duration", "Czas trwania"),
            WidgetItem("max_bpm", "Maksymalne tętno"),
            WidgetItem("avg_bpm", "Średnie tętno"),
            WidgetItem("total_calories", "Spalone kalorie"),
            WidgetItem("max_calories_min", "Maks spalanie kalorii"),
            WidgetItem("avg_pace", "Średnie tempo"),
            WidgetItem("avg_speed_gps", "Średnia prędkość (GPS)"),
            WidgetItem("avg_speed_steps", "Średnia prędkość (kroki)"),
            WidgetItem("max_speed", "Maks prędkość"),
            WidgetItem("max_altitude", "Maks wysokość"),
            WidgetItem("total_ascent", "Suma podejść"),
            WidgetItem("total_descent", "Suma zejść"),
            WidgetItem("avg_step_length", "Wyliczona długość kroku"),
            WidgetItem("avg_cadence", "Śr. kadencja"),
            WidgetItem("max_cadence", "Maks. kadencja"),
            WidgetItem("total_steps", "Liczba kroków"),
            WidgetItem("total_distance_gps", "Dystans (GPS)"),
            WidgetItem("total_distance_steps", "Dystans (kroki)"),
            WidgetItem("pressure_start", "Ciśnienie atm. (start)"),
            WidgetItem("pressure_end", "Ciśnienie atm. (koniec)"),
            WidgetItem("max_pressure", "Maks. ciśnienie atm."),
            WidgetItem("min_pressure", "Min. ciśnienie atm."),
            WidgetItem("best_pace_1km", "Najlepsze tempo (1km)")
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
