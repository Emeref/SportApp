package com.example.sportapp.healthconnect

import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*

object HealthConnectPermissions {

    // Note: Some Record classes like BiologicalSexRecord and DateOfBirthRecord 
    // are not available in all versions of the Jetpack SDK or require higher API levels.
    // We use strings for permissions that don't have a corresponding Record class in this SDK version.
    
    val HEALTH_DATA_PERMISSIONS = setOf(
        HealthPermission.getReadPermission(HeightRecord::class),
        HealthPermission.getWritePermission(HeightRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getWritePermission(WeightRecord::class),
        HealthPermission.getReadPermission(RestingHeartRateRecord::class),
        HealthPermission.getWritePermission(RestingHeartRateRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(Vo2MaxRecord::class),
        HealthPermission.getWritePermission(Vo2MaxRecord::class),
        "android.permission.health.READ_BIOLOGICAL_SEX",
        "android.permission.health.READ_DATE_OF_BIRTH"
    )

    val EXERCISE_PERMISSIONS = setOf(
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getWritePermission(DistanceRecord::class),
        HealthPermission.getReadPermission(SpeedRecord::class),
        HealthPermission.getWritePermission(SpeedRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(ElevationGainedRecord::class),
        // Routes are often handled as part of ExerciseSessionRecord or via special permissions
        "android.permission.health.READ_EXERCISE_ROUTES",
        "android.permission.health.WRITE_EXERCISE_ROUTE"
    )

    val ALL_PERMISSIONS = HEALTH_DATA_PERMISSIONS + EXERCISE_PERMISSIONS
}
