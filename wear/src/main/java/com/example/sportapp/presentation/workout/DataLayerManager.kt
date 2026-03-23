package com.example.sportapp.presentation.workout

import android.content.Context
import android.util.Log
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.presentation.settings.SettingsManager
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataLayerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workoutDao: WorkoutDao,
    private val settingsManager: SettingsManager
) {
    private val gson = Gson()
    private val messageClient by lazy { Wearable.getMessageClient(context) }
    private val nodeClient by lazy { Wearable.getNodeClient(context) }

    private suspend fun sendSyncStatus(status: String) {
        try {
            val nodes = nodeClient.connectedNodes.await()
            nodes.forEach { node ->
                messageClient.sendMessage(node.id, "/sync_status", status.toByteArray()).await()
            }
        } catch (e: Exception) {
            Log.e("DataLayerManager", "Failed to send sync status: $status", e)
        }
    }

    suspend fun syncAll() {
        Log.d("DataLayerManager", "syncAll: Starting full sync...")
        sendSyncStatus("STARTED")
        try {
            syncActivities()
            syncSettings()
            requestDefinitionsSync()
        } finally {
            sendSyncStatus("FINISHED")
            Log.d("DataLayerManager", "syncAll: Full sync finished")
        }
    }

    suspend fun syncActivities() {
        Log.d("DataLayerManager", "syncActivities: Starting database sync (only unsynced)...")
        val dataClient = Wearable.getDataClient(context)
        
        // Pobieramy tylko te treningi, które nie zostały jeszcze zsynchronizowane
        val workouts = workoutDao.getUnsyncedWorkouts()
        
        workouts.forEach { workout ->
            try {
                // Dla każdego treningu pobieramy punkty
                val points = workoutDao.getPointsForWorkout(workout.id)
                
                // Tworzymy obiekt mapy danych dla całego treningu
                val workoutData = mapOf(
                    "workout" to workout,
                    "points" to points
                )
                
                val json = gson.toJson(workoutData)
                val asset = Asset.createFromBytes(json.toByteArray())
                
                val request = PutDataMapRequest.create("/db_workouts/${workout.id}").apply {
                    dataMap.putAsset("workout_asset", asset)
                    dataMap.putLong("timestamp", System.currentTimeMillis())
                }.asPutDataRequest().setUrgent()

                dataClient.putDataItem(request).await()
                
                // Oznaczamy jako zsynchronizowany w lokalnej bazie zegarka
                workoutDao.markAsSynced(workout.id)

                Log.d("DataLayerManager", "Successfully synced workout ID: ${workout.id}")
            } catch (e: Exception) {
                Log.e("DataLayerManager", "Error syncing workout ID: ${workout.id}", e)
            }
        }
    }

    suspend fun syncSettings() {
        try {
            val settings = settingsManager.settingsFlow.first()
            val request = PutDataMapRequest.create("/wear_settings").apply {
                dataMap.putString("settings_json", gson.toJson(settings))
                dataMap.putLong("timestamp", System.currentTimeMillis())
            }.asPutDataRequest().setUrgent()
            
            Wearable.getDataClient(context).putDataItem(request).await()
            Log.d("DataLayerManager", "Successfully synced wear settings to mobile")
        } catch (e: Exception) {
            Log.e("DataLayerManager", "Failed to sync wear settings", e)
        }
    }

    suspend fun requestDefinitionsSync() {
        try {
            val nodes = nodeClient.connectedNodes.await()
            nodes.forEach { node ->
                messageClient.sendMessage(node.id, "/request_definitions", null).await()
            }
            Log.d("DataLayerManager", "Requested definitions sync from ${nodes.size} nodes")
        } catch (e: Exception) {
            Log.e("DataLayerManager", "Failed to request definitions sync", e)
        }
    }
}
