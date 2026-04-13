package com.example.sportapp.data.strava

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sportapp.data.GpxGenerator
import com.example.sportapp.data.db.SyncMetadataDao
import com.example.sportapp.data.db.SyncMetadataEntity
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.strava.api.StravaUploadApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@HiltWorker
class StravaSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val workoutDao: WorkoutDao,
    private val syncMetadataDao: SyncMetadataDao,
    private val stravaUploadApi: StravaUploadApi,
    private val stravaStorage: StravaStorage
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val workoutId = inputData.getLong(EXTRA_WORKOUT_ID, -1L)
        if (workoutId == -1L) return Result.failure()

        Log.d("StravaSyncWorker", "Starting sync for workout $workoutId")

        try {
            val workout = workoutDao.getWorkoutById(workoutId) ?: return Result.failure()
            val points = workoutDao.getPointsForWorkout(workoutId)
            
            val gpxGenerator = GpxGenerator()
            val gpxString = gpxGenerator.generateGpx(workout, points)
            
            val tempFile = File(context.cacheDir, "workout_${workoutId}.gpx")
            tempFile.writeText(gpxString)

            val requestFile = tempFile.asRequestBody("application/gpx+xml".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)
            val dataType = "gpx".toRequestBody("text/plain".toMediaTypeOrNull())
            val activityType = mapToBaseStravaType(workout.baseType).toRequestBody("text/plain".toMediaTypeOrNull())
            val name = workout.activityName.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = stravaUploadApi.uploadActivity(
                file = body,
                dataType = dataType,
                activityType = activityType,
                name = name
            )

            if (response.isSuccessful && response.body() != null) {
                val uploadResponse = response.body()!!
                Log.d("StravaSyncWorker", "Upload successful, uploadId: ${uploadResponse.id}")
                
                val existingMetadata = syncMetadataDao.getByLocalId(workoutId, "EXERCISE")
                val metadata = if (existingMetadata != null) {
                    existingMetadata.copy(
                        stravaUploadId = uploadResponse.id,
                        stravaSyncStatus = "SUCCESS",
                        lastSyncTime = System.currentTimeMillis()
                    )
                } else {
                    SyncMetadataEntity(
                        hcRecordId = "strava_${uploadResponse.id}",
                        localRecordId = workoutId,
                        recordType = "EXERCISE",
                        lastSyncTime = System.currentTimeMillis(),
                        syncDirection = "TO_STRAVA",
                        localModifiedTime = System.currentTimeMillis(),
                        hcModifiedTime = 0L,
                        activityName = workout.activityName,
                        startTime = workout.startTime,
                        stravaUploadId = uploadResponse.id,
                        stravaSyncStatus = "SUCCESS"
                    )
                }
                syncMetadataDao.insert(metadata)
                
                tempFile.delete()
                return Result.success()
            } else {
                Log.e("StravaSyncWorker", "Upload failed: ${response.errorBody()?.string()}")
                return Result.retry()
            }
        } catch (e: Exception) {
            Log.e("StravaSyncWorker", "Error during sync", e)
            return Result.retry()
        }
    }

    private fun mapToBaseStravaType(baseType: String): String {
        return when (baseType.lowercase()) {
            "run" -> "run"
            "bike", "cycling" -> "ride"
            "walk" -> "walk"
            "hike" -> "hike"
            "swim" -> "swim"
            else -> "workout"
        }
    }

    companion object {
        const val EXTRA_WORKOUT_ID = "EXTRA_WORKOUT_ID"
    }
}
