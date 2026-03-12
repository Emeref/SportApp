package com.example.sportapp.presentation.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mobile_settings")

@Singleton
class MobileSettingsManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val gson = Gson()

    companion object {
        private val WIDGETS_JSON = stringPreferencesKey("widgets_json")
        private val PERIOD = stringPreferencesKey("period")
        private val CUSTOM_DAYS = intPreferencesKey("custom_days")
        private val USE_TEST_DATA = booleanPreferencesKey("use_test_data")
    }

    val settingsFlow: Flow<MobileSettingsState> = context.dataStore.data.map { preferences ->
        val widgetsJson = preferences[WIDGETS_JSON]
        val defaultWidgets = MobileSettingsState().widgets
        val widgets = if (widgetsJson != null) {
            try {
                val type = object : TypeToken<List<WidgetItem>>() {}.type
                val decoded: List<WidgetItem>? = gson.fromJson(widgetsJson, type)
                if (decoded.isNullOrEmpty()) defaultWidgets else decoded
            } catch (e: Exception) {
                defaultWidgets
            }
        } else {
            defaultWidgets
        }

        MobileSettingsState(
            widgets = widgets,
            period = ReportingPeriod.valueOf(preferences[PERIOD] ?: ReportingPeriod.WEEK.name),
            customDays = preferences[CUSTOM_DAYS] ?: 7,
            useTestData = preferences[USE_TEST_DATA] ?: false
        )
    }

    suspend fun saveSettings(state: MobileSettingsState) {
        context.dataStore.edit { preferences ->
            preferences[WIDGETS_JSON] = gson.toJson(state.widgets)
            preferences[PERIOD] = state.period.name
            preferences[CUSTOM_DAYS] = state.customDays
            preferences[USE_TEST_DATA] = state.useTestData
        }
    }
}
