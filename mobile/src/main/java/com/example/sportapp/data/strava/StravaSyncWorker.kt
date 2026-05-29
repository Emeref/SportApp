package com.example.sportapp.data.strava

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sportapp.data.db.SyncMetadataDao
import com.example.sportapp.data.db.SyncMetadataEntity
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.export.FitExporter
import com.example.sportapp.data.export.GpxExporter
import com.example.sportapp.data.strava.api.StravaUploadApi
import com.example.sportapp.presentation.settings.ExportFormat
import com.example.sportapp.presentation.settings.MobileSettingsManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@HiltWorker
class StravaSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workoutDao: WorkoutDao,
    private val syncMetadataDao: SyncMetadataDao,
    private val stravaUploadApi: StravaUploadApi,
    private val stravaStorage: StravaStorage,
    private val settingsManager: MobileSettingsManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val workoutId = inputData.getLong(EXTRA_WORKOUT_ID, -1L)
        if (workoutId == -1L) return Result.failure()

        Log.d("StravaSyncWorker", "Starting sync for workout $workoutId")

        val workout = workoutDao.getWorkoutById(workoutId) ?: return Result.failure()

        try {
            val points = workoutDao.getPointsForWorkout(workoutId)
            val laps = workoutDao.getLapsForWorkout(workoutId)
            
            val settings = settingsManager.settingsFlow.first()
            val exporter = if (settings.defaultExportFormat == ExportFormat.FIT) {
                FitExporter()
            } else {
                GpxExporter()
            }
            
            val exportData = exporter.generateExport(workout, points, laps)
            val extension = exporter.getExtension()
            
            val tempFile = File(applicationContext.cacheDir, "workout_${workoutId}.$extension")
            tempFile.writeBytes(exportData)

            val requestFile = exportData.toRequestBody(exporter.getMimeType().toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)
            val dataType = exporter.getExtension().toRequestBody("text/plain".toMediaTypeOrNull())
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
                
                val stravaMetadataId = "strava_${uploadResponse.id}"
                
                val metadata = SyncMetadataEntity(
                    hcRecordId = stravaMetadataId,
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
                syncMetadataDao.insert(metadata)
                workoutDao.updateStravaExportStatus(workoutId, true)
                
                tempFile.delete()
                return Result.success()
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Log.e("StravaSyncWorker", "Upload failed: $errorMsg")
                recordFailure(workoutId, workout.activityName, workout.startTime)
                return if (response.code() in 400..499) Result.failure() else Result.retry()
            }
        } catch (e: Exception) {
            Log.e("StravaSyncWorker", "Error during sync", e)
            recordFailure(workoutId, workout.activityName, workout.startTime)
            return Result.retry()
        }
    }

    private suspend fun recordFailure(workoutId: Long, activityName: String, startTime: Long) {
        val metadata = SyncMetadataEntity(
            hcRecordId = "strava_failed_${workoutId}_${System.currentTimeMillis()}",
            localRecordId = workoutId,
            recordType = "EXERCISE",
            lastSyncTime = System.currentTimeMillis(),
            syncDirection = "TO_STRAVA",
            localModifiedTime = System.currentTimeMillis(),
            hcModifiedTime = 0L,
            activityName = activityName,
            startTime = startTime,
            stravaSyncStatus = "FAILED"
        )
        syncMetadataDao.insert(metadata)
        workoutDao.updateStravaExportStatus(workoutId, false)
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
