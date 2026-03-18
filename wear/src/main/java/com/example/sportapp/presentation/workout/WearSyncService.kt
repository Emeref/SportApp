package com.example.sportapp.presentation.workout

import android.util.Log
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.presentation.settings.ReportingPeriod
import com.example.sportapp.presentation.settings.SettingsManager
import com.example.sportapp.presentation.settings.WidgetItem
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WearSyncService : WearableListenerService() {

    @Inject
    lateinit var dataLayerManager: DataLayerManager

    @Inject
    lateinit var workoutDefinitionDao: WorkoutDefinitionDao
    
    @Inject
    lateinit var settingsManager: SettingsManager

    private val gson = Gson()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/request_sync") {
            Log.d("WearSyncService", "Sync request received")
            scope.launch {
                dataLayerManager.syncActivities()
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path ?: return@forEach
                when (path) {
                    "/workout_definitions" -> {
                        val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                        val json = dataMapItem.dataMap.getString("definitions_json") ?: return@forEach
                        
                        scope.launch {
                            try {
                                val type = object : TypeToken<List<WorkoutDefinition>>() {}.type
                                val definitions: List<WorkoutDefinition> = gson.fromJson(json, type)
                                workoutDefinitionDao.syncDefinitions(definitions)
                                Log.d("WearSyncService", "Successfully synced ${definitions.size} definitions from mobile")
                            } catch (e: Exception) {
                                Log.e("WearSyncService", "Failed to process definitions JSON", e)
                            }
                        }
                    }
                    "/watch_stats_settings" -> {
                        val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                        val widgetsJson = dataMapItem.dataMap.getString("widgets_json") ?: return@forEach
                        val periodName = dataMapItem.dataMap.getString("period") ?: return@forEach
                        val customDays = dataMapItem.dataMap.getInt("custom_days")
                        
                        scope.launch {
                            try {
                                val type = object : TypeToken<List<WidgetItem>>() {}.type
                                val widgets: List<WidgetItem> = gson.fromJson(widgetsJson, type)
                                val period = ReportingPeriod.valueOf(periodName)
                                settingsManager.saveWatchStatsSettings(widgets, period, customDays)
                                Log.d("WearSyncService", "Successfully synced watch stats settings from mobile")
                            } catch (e: Exception) {
                                Log.e("WearSyncService", "Failed to process watch stats settings", e)
                            }
                        }
                    }
                }
            }
        }
    }
}
