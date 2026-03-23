package com.example.sportapp.data

import android.util.Log
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : WearableListenerService() {

    @Inject lateinit var workoutDao: WorkoutDao
    @Inject lateinit var workoutDefinitionDao: WorkoutDefinitionDao
    @Inject lateinit var syncManager: WorkoutDefinitionSyncManager
    @Inject lateinit var syncStatusManager: SyncStatusManager
    
    private val gson = Gson()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            "/request_definitions" -> {
                Log.d("SyncService", "Request for definitions received from wear")
                scope.launch {
                    val definitions = workoutDefinitionDao.getAllDefinitionsOnce()
                    syncManager.syncDefinitions(definitions)
                }
            }
            "/sync_status" -> {
                val status = String(messageEvent.data)
                Log.d("SyncService", "Sync status received: $status")
                syncStatusManager.setSyncing(status == "STARTED")
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("SyncService", "onDataChanged: Received ${dataEvents.count} events")
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path ?: return@forEach
                
                if (path.startsWith("/db_workouts/")) {
                    val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                    val asset = dataMapItem.dataMap.getAsset("workout_asset") ?: return@forEach

                    scope.launch {
                        processWorkoutAsset(asset)
                    }
                } else if (path == "/wear_settings") {
                     val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                     val json = dataMapItem.dataMap.getString("settings_json") ?: return@forEach
                     Log.d("SyncService", "Received wear settings update")
                     // Tutaj można by zapisać te ustawienia lokalnie jeśli mobilka ma je wyświetlać
                }
            }
        }
    }

    private suspend fun processWorkoutAsset(asset: com.google.android.gms.wearable.Asset) {
        try {
            val dataClient = Wearable.getDataClient(this)
            val fd = dataClient.getFdForAsset(asset).await()
            
            val json = fd.inputStream.use { it.readBytes().toString(Charsets.UTF_8) }
            
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val rawData: Map<String, Any> = gson.fromJson(json, type)
            
            val workoutJson = gson.toJson(rawData["workout"])
            val pointsJson = gson.toJson(rawData["points"])

            val workout: WorkoutEntity = gson.fromJson(workoutJson, WorkoutEntity::class.java)
            val points: List<WorkoutPointEntity> = gson.fromJson(pointsJson, object : TypeToken<List<WorkoutPointEntity>>() {}.type)

            val existingWorkouts = workoutDao.getWorkoutsSince(workout.startTime - 1000)
            val alreadyExists = existingWorkouts.find { it.startTime == workout.startTime && it.activityName == workout.activityName }

            val localId = if (alreadyExists != null) {
                val updatedWorkout = workout.copy(id = alreadyExists.id)
                workoutDao.updateWorkout(updatedWorkout)
                alreadyExists.id
            } else {
                workoutDao.insertWorkout(workout.copy(id = 0))
            }
            
            val updatedPoints = points.map { it.copy(id = 0, workoutId = localId) }
            workoutDao.deletePointsForWorkout(localId)
            workoutDao.insertPoints(updatedPoints)

            Log.d("SyncService", "Successfully synced workout: ${workout.activityName} (ID: $localId) with ${points.size} points")
        } catch (e: Exception) {
            Log.e("SyncService", "Error processing workout asset", e)
        }
    }
}
