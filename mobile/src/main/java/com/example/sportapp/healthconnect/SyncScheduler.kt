package com.example.sportapp.healthconnect

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(false)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<HealthConnectSyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "HealthConnectSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    fun cancelSync() {
        WorkManager.getInstance(context).cancelUniqueWork("HealthConnectSync")
    }
}
