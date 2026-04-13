package com.example.sportapp.healthconnect

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class HealthConnectSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val healthDataSyncUseCase: HealthDataSyncUseCase,
    private val exerciseSyncUseCase: ExerciseSyncUseCase,
    private val exerciseExportUseCase: ExerciseExportUseCase,
    private val healthConnectManager: HealthConnectManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!healthConnectManager.hasPermissions(HealthConnectPermissions.ALL_PERMISSIONS)) {
            return Result.failure()
        }

        return try {
            // Etap 2 & 4: Synchronizacja danych zdrowotnych (odczyt + zapis)
            healthDataSyncUseCase.sync()
            
            // Etap 3: Import treningów z Health Connect
            exerciseSyncUseCase.sync()
            
            // Etap 5: Eksport treningów do Health Connect
            exerciseExportUseCase.exportAllUnsynced()

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
