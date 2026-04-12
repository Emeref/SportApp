package com.example.sportapp.healthconnect

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.sportapp.data.model.BaseType
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

    fun openHealthConnectSettings() {
        val intent = Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

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

    val writePermissions = setOf(
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),
        HealthPermission.PERMISSION_WRITE_EXERCISE_ROUTE,
        HealthPermission.getWritePermission(DistanceRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
        HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(SpeedRecord::class),
        HealthPermission.getWritePermission(ElevationGainedRecord::class)
    )

    suspend fun hasPermissions(permissions: Set<String>): Boolean {
        return if (isAvailable()) {
            healthConnectClient.permissionController.getGrantedPermissions().containsAll(permissions)
        } else {
            false
        }
    }

    fun mapBaseTypeToHealthConnect(baseType: String): Int {
        return when (baseType) {
            BaseType.WALKING -> ExerciseSessionRecord.EXERCISE_TYPE_WALKING
            BaseType.SPEED_WALKING -> ExerciseSessionRecord.EXERCISE_TYPE_WALKING
            BaseType.RUNNING -> ExerciseSessionRecord.EXERCISE_TYPE_RUNNING
            BaseType.TREADMILL_RUNNING -> ExerciseSessionRecord.EXERCISE_TYPE_RUNNING_TREADMILL
            BaseType.STAIR_CLIMBING -> ExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING
            BaseType.STAIR_CLIMBING_MACHINE -> ExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING_MACHINE
            BaseType.CYCLING -> ExerciseSessionRecord.EXERCISE_TYPE_BIKING
            BaseType.CYCLING_STATIONARY -> ExerciseSessionRecord.EXERCISE_TYPE_BIKING_STATIONARY
            BaseType.MOUNTAIN_BIKING -> ExerciseSessionRecord.EXERCISE_TYPE_BIKING
            BaseType.ROAD_BIKING -> ExerciseSessionRecord.EXERCISE_TYPE_BIKING
            BaseType.HIKING -> ExerciseSessionRecord.EXERCISE_TYPE_HIKING
            BaseType.ROCK_CLIMBING -> ExerciseSessionRecord.EXERCISE_TYPE_ROCK_CLIMBING
            BaseType.BOULDERING -> ExerciseSessionRecord.EXERCISE_TYPE_ROCK_CLIMBING
            BaseType.HIIT -> ExerciseSessionRecord.EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING
            BaseType.ELLIPTICAL -> ExerciseSessionRecord.EXERCISE_TYPE_ELLIPTICAL
            BaseType.ROWING_MACHINE -> ExerciseSessionRecord.EXERCISE_TYPE_ROWING_MACHINE
            BaseType.STRENGTH_TRAINING -> ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING
            BaseType.CALISTHENICS -> ExerciseSessionRecord.EXERCISE_TYPE_CALISTHENICS
            BaseType.YOGA -> ExerciseSessionRecord.EXERCISE_TYPE_YOGA
            BaseType.PILATES -> ExerciseSessionRecord.EXERCISE_TYPE_PILATES
            BaseType.AEROBICS -> ExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS
            BaseType.DANCING -> ExerciseSessionRecord.EXERCISE_TYPE_DANCING
            BaseType.SWIMMING_POOL -> ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL
            BaseType.SWIMMING_OPEN_WATER -> ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER
            BaseType.KAYAKING -> ExerciseSessionRecord.EXERCISE_TYPE_PADDLING
            BaseType.PADDLE_BOARDING -> ExerciseSessionRecord.EXERCISE_TYPE_PADDLING
            BaseType.SURFING -> ExerciseSessionRecord.EXERCISE_TYPE_SURFING
            BaseType.SAILING -> ExerciseSessionRecord.EXERCISE_TYPE_SAILING
            BaseType.FOOTBALL -> ExerciseSessionRecord.EXERCISE_TYPE_FOOTBALL_AMERICAN
            BaseType.BASKETBALL -> ExerciseSessionRecord.EXERCISE_TYPE_BASKETBALL
            BaseType.TENNIS -> ExerciseSessionRecord.EXERCISE_TYPE_TENNIS
            BaseType.SQUASH -> ExerciseSessionRecord.EXERCISE_TYPE_SQUASH
            BaseType.VOLLEYBALL -> ExerciseSessionRecord.EXERCISE_TYPE_VOLLEYBALL
            BaseType.GOLF -> ExerciseSessionRecord.EXERCISE_TYPE_GOLF
            BaseType.MARTIAL_ARTS -> ExerciseSessionRecord.EXERCISE_TYPE_MARTIAL_ARTS
            BaseType.SKIING -> ExerciseSessionRecord.EXERCISE_TYPE_SKIING
            BaseType.SNOWBOARDING -> ExerciseSessionRecord.EXERCISE_TYPE_SNOWBOARDING
            BaseType.SKATING -> ExerciseSessionRecord.EXERCISE_TYPE_SKATING
            BaseType.ICE_SKATING -> ExerciseSessionRecord.EXERCISE_TYPE_ICE_SKATING
            else -> ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT
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
