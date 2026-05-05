package com.example.sportapp.data

import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.strava.StravaSyncWorker
import com.example.sportapp.healthconnect.ExerciseExportUseCase
import com.example.sportapp.presentation.settings.MobileSettingsManager
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.PutDataMapRequest
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : WearableListenerService() {

    @Inject lateinit var workoutDao: WorkoutDao
    @Inject lateinit var workoutDefinitionDao: WorkoutDefinitionDao
    @Inject lateinit var syncManager: WorkoutDefinitionSyncManager
    @Inject lateinit var syncStatusManager: SyncStatusManager
    @Inject lateinit var mobileSettingsManager: MobileSettingsManager
    @Inject lateinit var exerciseExportUseCase: ExerciseExportUseCase
    
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
                     scope.launch {
                         try {
                             // Wearable sends UserSettings which contains healthData
                             val type = object : TypeToken<Map<String, Any>>() {}.type
                             val wearSettings: Map<String, Any> = gson.fromJson(json, type)
                             val healthDataMap = wearSettings["healthData"]

                             if (healthDataMap != null) {
                                 val healthDataJson = gson.toJson(healthDataMap)
                                 val incomingHealthData = gson.fromJson(healthDataJson, com.example.sportapp.presentation.settings.HealthData::class.java)
                                 
                                 val currentSettings = mobileSettingsManager.settingsFlow.first()
                                 if (currentSettings.healthData != incomingHealthData) {
                                     mobileSettingsManager.saveSettings(currentSettings.copy(healthData = incomingHealthData))
                                     Log.d("SyncService", "Updated mobile health data from wear sync")
                                 }
                             }
                         } catch (e: Exception) {
                             Log.e("SyncService", "Failed to process wear settings sync", e)
                         }
                     }
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
            
            val workoutMap = rawData["workout"] as Map<String, Any>
            val pointsJson = gson.toJson(rawData["points"])

            val workout: WorkoutEntity = gson.fromJson(gson.toJson(workoutMap), WorkoutEntity::class.java)
            val points: List<WorkoutPointEntity> = gson.fromJson(pointsJson, object : TypeToken<List<WorkoutPointEntity>>() {}.type)

            val existingWorkouts = workoutDao.getWorkoutsSince(workout.startTime - 1000)
            val alreadyExistsByContent = existingWorkouts.find { it.startTime == workout.startTime && it.activityName == workout.activityName }

            val localId: Long
            val wearId = workout.id
            
            val finalWorkout: WorkoutEntity
            
            if (alreadyExistsByContent != null) {
                localId = alreadyExistsByContent.id
                finalWorkout = workout.copy(id = localId, isSynced = true)
                
                if (wearId != localId) {
                    notifyWatchIdUpdate(wearId, localId)
                }
            } else {
                val isIdTaken = workoutDao.existsById(wearId)
                
                if (!isIdTaken && wearId > 0) {
                    localId = wearId
                    finalWorkout = workout.copy(isSynced = true)
                } else {
                    val maxId = workoutDao.getMaxId() ?: 0L
                    localId = maxId + 1
                    finalWorkout = workout.copy(id = localId, isSynced = true)
                    notifyWatchIdUpdate(wearId, localId)
                }
            }
            
            // Używamy transakcji do jednoczesnej aktualizacji treningu i punktów
            val updatedPoints = points.map { it.copy(id = 0, workoutId = localId) }
            workoutDao.updateWorkoutWithPoints(finalWorkout, updatedPoints)

            Log.d("SyncService", "Successfully synced workout: ${workout.activityName} (ID: $localId, wearId: $wearId)")

            if (workout.isFinished) {
                val durationOk = workout.durationSeconds >= 60
                val distanceGpsOk = (workout.distanceGps ?: 0.0) >= 10.0
                
                if (durationOk && distanceGpsOk) {
                    val settings = mobileSettingsManager.settingsFlow.first()
                    
                    if (settings.autoExportToHC) {
                        exerciseExportUseCase.exportActivityToHC(localId)
                    }
                    
                    if (settings.autoExportToStrava) {
                        enqueueStravaSync(localId)
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("SyncService", "Error processing workout asset", e)
        }
    }

    private suspend fun notifyWatchIdUpdate(oldId: Long, newId: Long) {
        try {
            val messageClient = Wearable.getMessageClient(this)
            val nodes = Wearable.getNodeClient(this).connectedNodes.await()
            val payload = "$oldId:$newId".toByteArray()
            nodes.forEach { node ->
                messageClient.sendMessage(node.id, "/update_workout_id", payload).await()
            }
            
            val dataClient = Wearable.getDataClient(this)
            val request = PutDataMapRequest.create("/id_updates/$oldId").apply {
                dataMap.putLong("oldId", oldId)
                dataMap.putLong("newId", newId)
                dataMap.putLong("timestamp", System.currentTimeMillis())
            }.asPutDataRequest().setUrgent()
            dataClient.putDataItem(request).await()
            
            Log.d("SyncService", "Sent ID update to watch: $oldId -> $newId")
        } catch (e: Exception) {
            Log.e("SyncService", "Failed to send ID update to watch", e)
        }
    }

    private fun enqueueStravaSync(workoutId: Long) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<StravaSyncWorker>()
            .setConstraints(constraints)
            .setInputData(workDataOf(StravaSyncWorker.EXTRA_WORKOUT_ID to workoutId))
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag("StravaSync_$workoutId")
            .build()

        WorkManager.getInstance(this).enqueue(syncRequest)
    }
}
