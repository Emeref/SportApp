package com.example.sportapp.presentation.stats

import android.content.Context
import android.util.Log
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
    val visibleElements: List<WidgetItem>,
    val trackColor: Int
)

class ActivityDetailSettingsManager(private val context: Context) {
    private val gson = Gson()

    companion object {
        private val VISIBLE_ELEMENTS_JSON = stringPreferencesKey("visible_elements_json")
        private val TRACK_COLOR = stringPreferencesKey("track_color")

        val DEFAULT_ELEMENTS = listOf(
            WidgetItem("map", "Mapa"),
            WidgetItem("bpm", "Tętno (bpm)"),
            WidgetItem("kalorie_min", "Kalorie/min"),
            WidgetItem("kroki_min", "Kroki/min"),
            WidgetItem("odl_kroki", "Dystans (kroki)"),
            WidgetItem("predkosc_kroki", "Prędkość (kroki)"),
            WidgetItem("gps_dystans", "Dystans (GPS)"),
            WidgetItem("predkosc", "Prędkość (GPS)"),
            WidgetItem("wysokosc", "Wysokość"),
            WidgetItem("przewyzszenia_gora", "Przewyższenia +"),
            WidgetItem("przewyzszenia_dol", "Przewyższenia -")
        )
        
        val DEFAULT_COLOR = 0xFFFF9800.toInt()
    }

    val settingsFlow: Flow<ActivityDetailSettings> = context.activityDetailDataStore.data.map { preferences ->
        val elementsJson = preferences[VISIBLE_ELEMENTS_JSON]
        val elements = if (elementsJson != null) {
            try {
                val type = object : TypeToken<List<WidgetItem>>() {}.type
                val decoded: List<WidgetItem>? = gson.fromJson(elementsJson, type)
                
                // Filtrujemy niechciane elementy (srednie_bpm, kroki, kalorie_suma)
                val filtered = decoded?.filter { 
                    it.id != "srednie_bpm" && it.id != "kroki" && it.id != "kalorie_suma" 
                }
                
                if (filtered.isNullOrEmpty()) {
                    DEFAULT_ELEMENTS
                } else {
                    val missing = DEFAULT_ELEMENTS.filter { def -> filtered.none { it.id == def.id } }
                    if (missing.isNotEmpty()) {
                        filtered + missing
                    } else {
                        filtered
                    }
                }
            } catch (e: Exception) {
                DEFAULT_ELEMENTS
            }
        } else {
            DEFAULT_ELEMENTS
        }
        
        val colorHex = preferences[TRACK_COLOR]
        val color = if (colorHex != null) {
            try {
                colorHex.toLong(16).toInt()
            } catch (e: Exception) {
                DEFAULT_COLOR
            }
        } else {
            DEFAULT_COLOR
        }

        ActivityDetailSettings(elements, color)
    }

    suspend fun saveVisibleElements(elements: List<WidgetItem>) {
        context.activityDetailDataStore.edit { preferences ->
            preferences[VISIBLE_ELEMENTS_JSON] = gson.toJson(elements)
        }
    }

    suspend fun saveTrackColor(color: Int) {
        context.activityDetailDataStore.edit { preferences ->
            preferences[TRACK_COLOR] = Integer.toHexString(color)
        }
    }
}
