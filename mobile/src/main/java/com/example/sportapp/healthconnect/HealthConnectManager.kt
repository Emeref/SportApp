package com.example.sportapp.healthconnect

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthConnectManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val healthConnectClient: HealthConnectClient
) {

    fun isAvailable(): Boolean =
        HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE

    fun openHealthConnectInstallPage() {
        val uri = Uri.parse("market://details?id=com.google.android.apps.healthdata")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.android.vending")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("overlay", true)
            putExtra("callerId", context.packageName)
        }
        context.startActivity(intent)
    }

    // Dodajemy uprawnienie do historii, aby móc czytać dane starsze niż 30 dni
    val profilePermissions = setOf(
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getReadPermission(HeightRecord::class),
        HealthPermission.getReadPermission(RestingHeartRateRecord::class),
        HealthPermission.getReadPermission(Vo2MaxRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        "android.permission.health.READ_HEALTH_DATA_HISTORY"
    )

    val workoutPermissions = setOf(
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(SpeedRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(ElevationGainedRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class)
    )

    suspend fun hasPermissions(permissions: Set<String>): Boolean {
        return if (isAvailable()) {
            healthConnectClient.permissionController.getGrantedPermissions().containsAll(permissions)
        } else {
            false
        }
    }

    suspend fun getLatestWeightKg(): Double? {
        if (!isAvailable()) return null
        return try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = WeightRecord::class,
                    timeRangeFilter = TimeRangeFilter.before(Instant.now()),
                    ascendingOrder = false,
                    pageSize = 1
                )
            )
            response.records.firstOrNull()?.weight?.inKilograms
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getLatestHeightCm(): Double? {
        if (!isAvailable()) return null
        return try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = HeightRecord::class,
                    timeRangeFilter = TimeRangeFilter.before(Instant.now()),
                    ascendingOrder = false,
                    pageSize = 1
                )
            )
            response.records.firstOrNull()?.height?.inMeters?.times(100.0)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getLatestRestingHeartRate(): Long? {
        if (!isAvailable()) return null
        return try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = RestingHeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.before(Instant.now()),
                    ascendingOrder = false,
                    pageSize = 1
                )
            )
            response.records.firstOrNull()?.beatsPerMinute
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getLatestVo2Max(): Double? {
        if (!isAvailable()) return null
        return try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = Vo2MaxRecord::class,
                    timeRangeFilter = TimeRangeFilter.before(Instant.now()),
                    ascendingOrder = false,
                    pageSize = 1
                )
            )
            response.records.firstOrNull()?.vo2MillilitersPerMinuteKilogram
        } catch (e: Exception) {
            null
        }
    }
}
