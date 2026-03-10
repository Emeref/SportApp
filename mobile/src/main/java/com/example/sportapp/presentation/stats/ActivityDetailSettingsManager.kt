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
        private val VISIBLE_ELEMENTS_JSON = stringPreferencesKey("visible_elements_json")
        private val TRACK_COLOR = stringPreferencesKey("track_color")
    }

    private val defaultElements = listOf(
        WidgetItem("map", "Mapa"),
        WidgetItem("bpm", "Tętno (bpm)"),
        WidgetItem("srednie_bpm", "Średnie tętno"),
        WidgetItem("kroki", "Kroki"),
        WidgetItem("kroki_min", "Kroki/min"),
        WidgetItem("odl_kroki", "Dystans (kroki)"),
        WidgetItem("gps_dystans", "Dystans (GPS)"),
        WidgetItem("predkosc", "Prędkość"),
        WidgetItem("wysokosc", "Wysokość"),
        WidgetItem("przewyzszenia_gora", "Przewyższenia +"),
        WidgetItem("przewyzszenia_dol", "Przewyższenia -")
    )

    val settingsFlow: Flow<ActivityDetailSettings> = context.activityDetailDataStore.data.map { preferences ->
        val elementsJson = preferences[VISIBLE_ELEMENTS_JSON]
        val elements = if (elementsJson != null) {
            val type = object : TypeToken<List<WidgetItem>>() {}.type
            gson.fromJson(elementsJson, type)
        } else {
            defaultElements
        }
        
        // Domyślny kolor: Szary (zgodnie z agents.md) lub pomarańczowy (zgodnie z Twoim wyborem dla zegarka)
        // Tutaj ustawimy domyślnie pomarańczowy 0xFFFF9800
        val color = preferences[TRACK_COLOR]?.toLong(16)?.toInt() ?: 0xFFFF9800.toInt()

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
