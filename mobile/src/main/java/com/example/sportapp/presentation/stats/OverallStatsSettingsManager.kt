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
    }

    val widgetsFlow: Flow<List<WidgetItem>> = context.overallStatsDataStore.data.map { preferences ->
        val widgetsJson = preferences[WIDGETS_JSON]
        if (widgetsJson != null) {
            try {
                val type = object : TypeToken<List<WidgetItem>>() {}.type
                val decoded: List<WidgetItem>? = gson.fromJson(widgetsJson, type)
                
                if (decoded.isNullOrEmpty()) {
                    DEFAULT_WIDGETS
                } else {
                    // Merging logic to add new default widgets to existing user settings
                    val currentIds = DEFAULT_WIDGETS.map { it.id }.toSet()
                    val filtered = decoded.filter { it.id in currentIds }.toMutableList()
                    val missing = DEFAULT_WIDGETS.filter { def -> filtered.none { it.id == def.id } }
                    if (missing.isNotEmpty()) {
                        filtered.addAll(missing)
                    }
                    filtered
                }
            } catch (e: Exception) {
                DEFAULT_WIDGETS
            }
        } else {
            DEFAULT_WIDGETS
        }
    }

    suspend fun saveWidgets(widgets: List<WidgetItem>) {
        context.overallStatsDataStore.edit { preferences ->
            preferences[WIDGETS_JSON] = gson.toJson(widgets)
        }
    }
}
