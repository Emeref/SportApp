package com.example.sportapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ActivityExportModel(
    val workout: WorkoutExportDto,
    val points: List<WorkoutPointExportDto>,
    val laps: List<WorkoutLapExportDto> = emptyList()
)

@Serializable
data class WorkoutExportDto(
    val activityName: String,
    val baseType: String,
    val startTime: Long,
    val durationFormatted: String,
    val steps: Int? = null,
    val distanceSteps: Double? = null,
    val distanceGps: Double? = null,
    val avgSpeedSteps: Double? = null,
    val avgSpeedGps: Double? = null,
    val totalAscent: Double? = null,
    val totalDescent: Double? = null,
    val avgBpm: Double? = null,
    val maxBpm: Int? = null,
    val totalCalories: Double? = null,
    val maxCalorieMin: Double? = null,
    val durationSeconds: Long,
    val avgPace: Double? = null,
    val maxSpeed: Double? = null,
    val maxAltitude: Double? = null,
    val minAltitude: Double? = null,
    val avgStepLength: Double? = null,
    val avgCadence: Double? = null,
    val maxCadence: Double? = null,
    val maxPressure: Double? = null,
    val minPressure: Double? = null,
    val bestPace1km: Double? = null,
    val autoLapDistance: Double? = null,
    val destinationLatitude: Double? = null,
    val destinationLongitude: Double? = null
)

@Serializable
data class WorkoutPointExportDto(
    val time: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val bpm: Int? = null,
    val steps: Int? = null,
    val stepsMin: Double? = null,
    val distanceSteps: Int? = null,
    val distanceGps: Int? = null,
    val speedGps: Double? = null,
    val speedSteps: Double? = null,
    val altitude: Double? = null,
    val horizontalAccuracy: Double? = null,
    val totalAscent: Double? = null,
    val totalDescent: Double? = null,
    val calorieMin: Double? = null,
    val calorieSum: Double? = null,
    val pressure: Double? = null
)

@Serializable
data class WorkoutLapExportDto(
    val lapNumber: Int,
    val durationMillis: Long,
    val distanceMeters: Double,
    val avgPaceSecondsPerKm: Int,
    val avgSpeed: Double,
    val maxSpeed: Double,
    val avgHeartRate: Int,
    val maxHeartRate: Int,
    val totalAscent: Double,
    val totalDescent: Double,
    val startLocationIndex: Int,
    val endLocationIndex: Int
)
