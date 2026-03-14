package com.example.sportapp.data

import android.content.Context
import android.util.Log
import com.example.sportapp.data.model.WorkoutDefinition
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutDefinitionSyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()
    private val dataClient by lazy { Wearable.getDataClient(context) }

    suspend fun syncDefinitions(definitions: List<WorkoutDefinition>) {
        try {
            val json = gson.toJson(definitions)
            val putDataMapReq = PutDataMapRequest.create("/workout_definitions").apply {
                dataMap.putString("definitions_json", json)
                dataMap.putLong("timestamp", System.currentTimeMillis()) // Force update
            }
            val putDataReq = putDataMapReq.asPutDataRequest().setUrgent()
            dataClient.putDataItem(putDataReq).await()
            Log.d("SyncManager", "Successfully synced ${definitions.size} definitions to wear")
        } catch (e: Exception) {
            Log.e("SyncManager", "Failed to sync definitions", e)
        }
    }
}
