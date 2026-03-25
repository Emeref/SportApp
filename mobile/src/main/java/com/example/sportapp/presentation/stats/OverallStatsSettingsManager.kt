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

// Używamy innej nazwy pliku, aby ustawienia były odizolowane
private val Context.overallStatsDataStore: DataStore<Preferences> by preferencesDataStore(name = "overall_stats_settings")

class OverallStatsSettingsManager(private val context: Context) {
    private val gson = Gson()

    companion object {
        private val WIDGETS_JSON = stringPreferencesKey("widgets_json")
        private val CHARTS_JSON = stringPreferencesKey("charts_json")
        
        val DEFAULT_WIDGETS = listOf(
            WidgetItem("count", "Liczba aktywności"),
            WidgetItem("calories", "Spalone kalorie"),
            WidgetItem("distanceGps", "Dystans (GPS)"),
            WidgetItem("distanceSteps", "Dystans (kroki)"),
            WidgetItem("ascent", "W sumie w górę"),
            WidgetItem("descent", "W sumie do dołu"),
            WidgetItem("steps", "Wszystkie kroki"),
            WidgetItem("max_speed", "Maks prędkość"),
            WidgetItem("max_altitude", "Maks wysokość"),
            WidgetItem("max_elevation_gain", "Najwięcej przewyższeń"),
            WidgetItem("max_distance", "Największy dystans"),
            WidgetItem("max_duration", "Najdłuższy czas"),
            WidgetItem("max_calories", "Najwięcej kalorii"),
            WidgetItem("max_avg_cadence", "Najwyższa śr. kadencja"),
            WidgetItem("max_avg_speed", "Najwyższa śr. prędkość")
        )

        val DEFAULT_CHARTS = listOf(
            WidgetItem("calories", "Spalone kalorie"),
            WidgetItem("distanceGps", "Dystans (GPS)"),
            WidgetItem("distanceSteps", "Dystans (kroki)"),
            WidgetItem("ascent", "Suma podejść"),
            WidgetItem("descent", "Suma zejść"),
            WidgetItem("steps", "Kroki"),
            WidgetItem("maxPressure", "Maks. ciśnienie"),
            WidgetItem("minPressure", "Min. ciśnienie"),
            WidgetItem("bestPace1km", "Najlepsze tempo (1km)")
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
