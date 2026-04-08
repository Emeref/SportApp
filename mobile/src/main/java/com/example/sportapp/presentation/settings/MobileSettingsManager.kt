package com.example.sportapp.presentation.settings

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mobile_settings")

@Singleton
class MobileSettingsManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val gson = Gson()
    private val dataClient by lazy { Wearable.getDataClient(context) }
    private val messageClient by lazy { Wearable.getMessageClient(context) }
    private val nodeClient by lazy { Wearable.getNodeClient(context) }

    companion object {
        private val WIDGETS_JSON = stringPreferencesKey("widgets_json")
        private val PERIOD = stringPreferencesKey("period")
        private val CUSTOM_DAYS = intPreferencesKey("custom_days")
        
        private val WATCH_WIDGETS_JSON = stringPreferencesKey("watch_widgets_json")
        private val WATCH_PERIOD = stringPreferencesKey("watch_period")
        private val WATCH_CUSTOM_DAYS = intPreferencesKey("watch_custom_days")
        
        private val HEALTH_DATA_JSON = stringPreferencesKey("health_data_json")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val LANGUAGE = stringPreferencesKey("language")
    }

    val settingsFlow: Flow<MobileSettingsState> = context.dataStore.data.map { preferences ->
        val defaultState = MobileSettingsState()
        
        val widgetsJson = preferences[WIDGETS_JSON]
        val widgets = if (widgetsJson != null) {
            try {
                val type = object : TypeToken<List<WidgetItem>>() {}.type
                gson.fromJson<List<WidgetItem>>(widgetsJson, type) ?: defaultState.widgets
            } catch (e: Exception) {
                defaultState.widgets
            }
        } else {
            defaultState.widgets
        }

        val watchWidgetsJson = preferences[WATCH_WIDGETS_JSON]
        val watchWidgets = if (watchWidgetsJson != null) {
            try {
                val type = object : TypeToken<List<WidgetItem>>() {}.type
                gson.fromJson<List<WidgetItem>>(watchWidgetsJson, type) ?: defaultState.watchStatsWidgets
            } catch (e: Exception) {
                defaultState.watchStatsWidgets
            }
        } else {
            defaultState.watchStatsWidgets
        }

        val healthDataJson = preferences[HEALTH_DATA_JSON]
        val healthData = if (healthDataJson != null) {
            try {
                gson.fromJson(healthDataJson, HealthData::class.java) ?: defaultState.healthData
            } catch (e: Exception) {
                defaultState.healthData
            }
        } else {
            defaultState.healthData
        }

        val langCode = preferences[LANGUAGE]
        val language = AppLanguage.values().find { it.code == langCode } ?: defaultState.language

        MobileSettingsState(
            widgets = widgets,
            period = ReportingPeriod.valueOf(preferences[PERIOD] ?: defaultState.period.name),
            customDays = preferences[CUSTOM_DAYS] ?: defaultState.customDays,
            watchStatsWidgets = watchWidgets,
            watchStatsPeriod = ReportingPeriod.valueOf(preferences[WATCH_PERIOD] ?: defaultState.watchStatsPeriod.name),
            watchStatsCustomDays = preferences[WATCH_CUSTOM_DAYS] ?: defaultState.watchStatsCustomDays,
            healthData = healthData,
            themeMode = ThemeMode.valueOf(preferences[THEME_MODE] ?: defaultState.themeMode.name),
            language = language
        )
    }

    suspend fun saveSettings(state: MobileSettingsState) {
        context.dataStore.edit { preferences ->
            preferences[WIDGETS_JSON] = gson.toJson(state.widgets)
            preferences[PERIOD] = state.period.name
            preferences[CUSTOM_DAYS] = state.customDays
            
            preferences[WATCH_WIDGETS_JSON] = gson.toJson(state.watchStatsWidgets)
            preferences[WATCH_PERIOD] = state.watchStatsPeriod.name
            preferences[WATCH_CUSTOM_DAYS] = state.watchStatsCustomDays
            
            preferences[HEALTH_DATA_JSON] = gson.toJson(state.healthData)
            preferences[THEME_MODE] = state.themeMode.name
            preferences[LANGUAGE] = state.language.code
        }
        syncWatchStatsSettings(state)
        syncHealthData(state.healthData)
        requestFullSyncFromWatch()
    }

    private suspend fun requestFullSyncFromWatch() {
        try {
            val nodes = nodeClient.connectedNodes.await()
            nodes.forEach { node ->
                messageClient.sendMessage(node.id, "/request_sync", byteArrayOf()).await()
            }
            Log.d("SettingsSync", "Requested full sync from watch after settings change")
        } catch (e: Exception) {
            Log.e("SettingsSync", "Failed to request sync from watch", e)
        }
    }

    private suspend fun syncWatchStatsSettings(state: MobileSettingsState) {
        try {
            val request = PutDataMapRequest.create("/watch_stats_settings").apply {
                dataMap.putString("widgets_json", gson.toJson(state.watchStatsWidgets))
                dataMap.putString("period", state.watchStatsPeriod.name)
                dataMap.putInt("custom_days", state.watchStatsCustomDays)
                dataMap.putLong("timestamp", System.currentTimeMillis())
            }
            dataClient.putDataItem(request.asPutDataRequest().setUrgent()).await()
            Log.d("SettingsSync", "Synced watch stats settings")
        } catch (e: Exception) {
            Log.e("SettingsSync", "Failed to sync watch stats settings", e)
        }
    }

    private suspend fun syncHealthData(healthData: HealthData) {
        try {
            val request = PutDataMapRequest.create("/health_data").apply {
                dataMap.putString("health_data_json", gson.toJson(healthData))
                dataMap.putLong("timestamp", System.currentTimeMillis())
            }
            dataClient.putDataItem(request.asPutDataRequest().setUrgent()).await()
            Log.d("SettingsSync", "Synced health data to wear")
        } catch (e: Exception) {
            Log.e("SettingsSync", "Failed to sync health data", e)
        }
    }
}
