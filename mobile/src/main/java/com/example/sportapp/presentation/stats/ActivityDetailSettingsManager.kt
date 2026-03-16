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
    val visibleElements: List<WidgetItem>,
    val trackColor: Int
)

class ActivityDetailSettingsManager(private val context: Context) {
    private val gson = Gson()

    companion object {
        private fun getVisibleElementsKey(typeName: String) = stringPreferencesKey("visible_elements_$typeName")
        private fun getTrackColorKey(typeName: String) = stringPreferencesKey("track_color_$typeName")

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

    fun getSettingsFlow(typeName: String): Flow<ActivityDetailSettings> = context.activityDetailDataStore.data.map { preferences ->
        val elementsJson = preferences[getVisibleElementsKey(typeName)]
        val elements = if (elementsJson != null) {
            try {
                val type = object : TypeToken<List<WidgetItem>>() {}.type
                val decoded: List<WidgetItem>? = gson.fromJson(elementsJson, type)
                
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

        ActivityDetailSettings(elements, color)
    }

    suspend fun saveVisibleElements(typeName: String, elements: List<WidgetItem>) {
        context.activityDetailDataStore.edit { preferences ->
            preferences[getVisibleElementsKey(typeName)] = gson.toJson(elements)
        }
    }

    suspend fun saveTrackColor(typeName: String, color: Int) {
        context.activityDetailDataStore.edit { preferences ->
            preferences[getTrackColorKey(typeName)] = Integer.toHexString(color)
        }
    }

    suspend fun deleteSettings(typeName: String) {
        context.activityDetailDataStore.edit { preferences ->
            preferences.remove(getVisibleElementsKey(typeName))
            preferences.remove(getTrackColorKey(typeName))
        }
    }
}
