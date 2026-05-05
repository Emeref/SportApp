package com.example.sportapp.presentation.workout

import android.content.Intent
import android.util.Log
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.presentation.MainActivity
import com.example.sportapp.presentation.settings.HealthData
import com.example.sportapp.presentation.settings.ReportingPeriod
import com.example.sportapp.presentation.settings.SettingsManager
import com.example.sportapp.presentation.settings.WidgetItem
import com.example.sportapp.presentation.settings.IconManager
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class WearSyncService : WearableListenerService() {

    @Inject
    lateinit var dataLayerManager: DataLayerManager

    @Inject
    lateinit var workoutDefinitionDao: WorkoutDefinitionDao
    
    @Inject
    lateinit var workoutDao: WorkoutDao
    
    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var iconManager: IconManager

    private val gson = Gson()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d("WearSyncService", "onMessageReceived: path=${messageEvent.path}")
        when (messageEvent.path) {
            "/request_sync" -> {
                scope.launch {
                    dataLayerManager.syncAll()
                }
            }
            "/update_workout_id" -> {
                try {
                    val data = String(messageEvent.data).split(":")
                    if (data.size == 2) {
                        val oldId = data[0].toLong()
                        val newId = data[1].toLong()
                        scope.launch {
                            workoutDao.updateWorkoutId(oldId, newId)
                            Log.d("WearSyncService", "Updated workout ID (msg): $oldId -> $newId")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WearSyncService", "Failed to parse update_workout_id message", e)
                }
            }
            "/delete_workout" -> {
                try {
                    val payload = String(messageEvent.data)
                    Log.d("WearSyncService", "Received delete request with payload: $payload")
                    
                    // Obsługujemy oba formaty: sam ID lub id:startTime
                    val parts = payload.split(":")
                    val workoutId = parts[0].toLong()
                    
                    scope.launch {
                        workoutDao.deleteWorkoutWithPoints(workoutId)
                        Log.d("WearSyncService", "Deleted workout ID (msg): $workoutId")
                    }
                } catch (e: Exception) {
                    Log.e("WearSyncService", "Failed to parse delete_workout message", e)
                }
            }
            "/start_activity" -> {
                val definitionId = String(messageEvent.data).toLongOrNull() ?: return
                Log.d("WearSyncService", "Start activity request received: $definitionId")
                
                scope.launch {
                    try {
                        val settings = settingsManager.settingsFlow.first()
                        val hData = settings.healthData
                        val intent = Intent(this@WearSyncService, WorkoutService::class.java).apply {
                            action = WorkoutService.ACTION_START
                            putExtra(WorkoutService.EXTRA_DEFINITION_ID, definitionId)
                            putExtra(WorkoutService.EXTRA_HEALTH_DATA_JSON, gson.toJson(hData))
                        }
                        startForegroundService(intent)
                        
                        val activityIntent = Intent(this@WearSyncService, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            putExtra("EXTRA_DEFINITION_ID", definitionId)
                        }
                        startActivity(activityIntent)
                        
                        val nodeClient = Wearable.getNodeClient(this@WearSyncService)
                        val messageClient = Wearable.getMessageClient(this@WearSyncService)
                        val nodes = nodeClient.connectedNodes.await()
                        nodes.forEach { node ->
                            messageClient.sendMessage(node.id, "/activity_started", null).await()
                        }
                    } catch (e: Exception) {
                        Log.e("WearSyncService", "Failed to handle start_activity request", e)
                    }
                }
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            val path = event.dataItem.uri.path ?: return@forEach
            Log.d("WearSyncService", "onDataChanged: path=$path, type=${event.type}")
            
            if (event.type == DataEvent.TYPE_CHANGED) {
                when {
                    path == "/workout_definitions" -> {
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
                    path == "/mobile_settings" -> {
                        val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                        val widgetsJson = dataMapItem.dataMap.getString("watch_widgets_json")
                        val periodName = dataMapItem.dataMap.getString("watch_period")
                        val customDays = dataMapItem.dataMap.getInt("watch_custom_days")
                        val healthDataJson = dataMapItem.dataMap.getString("health_data_json")
                        val activeIconTier = dataMapItem.dataMap.getInt("active_icon_tier", 0)
                        
                        scope.launch {
                            try {
                                if (widgetsJson != null && periodName != null) {
                                    val type = object : TypeToken<List<WidgetItem>>() {}.type
                                    val widgets: List<WidgetItem> = gson.fromJson(widgetsJson, type)
                                    val period = ReportingPeriod.valueOf(periodName)
                                    settingsManager.saveWatchStatsSettings(widgets, period, customDays)
                                }
                                
                                if (healthDataJson != null) {
                                    val healthData = gson.fromJson(healthDataJson, HealthData::class.java)
                                    settingsManager.saveHealthData(healthData)
                                }

                                settingsManager.saveActiveIconTier(activeIconTier)
                                iconManager.setActiveTier(activeIconTier)
                                Log.d("WearSyncService", "Successfully synced all mobile settings (tier: $activeIconTier)")
                            } catch (e: Exception) {
                                Log.e("WearSyncService", "Failed to process mobile settings sync", e)
                            }
                        }
                    }
                    path == "/watch_stats_settings" -> {
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
                                Log.d("WearSyncService", "Successfully synced watch stats settings (legacy path)")
                            } catch (e: Exception) {
                                Log.e("WearSyncService", "Failed to process watch stats settings", e)
                            }
                        }
                    }
                    path == "/health_data" -> {
                        val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                        val json = dataMapItem.dataMap.getString("health_data_json") ?: return@forEach
                        scope.launch {
                            try {
                                val healthData = gson.fromJson(json, HealthData::class.java)
                                settingsManager.saveHealthData(healthData)
                                Log.d("WearSyncService", "Successfully synced health data (legacy path)")
                            } catch (e: Exception) {
                                Log.e("WearSyncService", "Failed to process health data sync", e)
                            }
                        }
                    }
                    path.startsWith("/deleted_workouts/") -> {
                        val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                        val workoutId = dataMapItem.dataMap.getLong("workoutId")
                        scope.launch {
                            workoutDao.deleteWorkoutWithPoints(workoutId)
                            Log.d("WearSyncService", "Deleted workout ID (data): $workoutId")
                        }
                    }
                    path.startsWith("/id_updates/") -> {
                        val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                        val oldId = dataMapItem.dataMap.getLong("oldId")
                        val newId = dataMapItem.dataMap.getLong("newId")
                        scope.launch {
                            workoutDao.updateWorkoutId(oldId, newId)
                            Log.d("WearSyncService", "Updated workout ID (data): $oldId -> $newId")
                        }
                    }
                }
            }
        }
    }
}
