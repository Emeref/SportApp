package com.example.sportapp.presentation.workout

import android.content.Context
import android.util.Log
import com.example.sportapp.data.db.WorkoutDao
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataLayerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workoutDao: WorkoutDao
) {
    private val gson = Gson()

    suspend fun syncActivities() {
        Log.d("DataLayerManager", "syncActivities: Starting database sync...")
        val dataClient = Wearable.getDataClient(context)
        
        // Pobieramy wszystkie treningi z bazy
        val workouts = workoutDao.getWorkoutsSince(0) // Pobierz wszystko
        
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
                Log.d("DataLayerManager", "Successfully synced workout ID: ${workout.id}")
            } catch (e: Exception) {
                Log.e("DataLayerManager", "Error syncing workout ID: ${workout.id}", e)
            }
        }
    }
}
