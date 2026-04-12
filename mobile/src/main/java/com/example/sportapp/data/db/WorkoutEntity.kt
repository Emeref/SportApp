package com.example.sportapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activityName: String,
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
    // Nowe metryki statystyczne
    val avgPace: Double? = null, // min/km
    val maxSpeed: Double? = null, // km/h
    val maxAltitude: Double? = null, // m
    val minAltitude: Double? = null, // m
    val avgStepLength: Double? = null, // m
    val avgCadence: Double? = null, // kroki/min
    val maxCadence: Double? = null, // kroki/min
    val maxPressure: Double? = null, // hPa
    val minPressure: Double? = null, // hPa
    val bestPace1km: Double? = null, // min/km
    val autoLapDistance: Double? = null, // m - dystans autolapa w momencie startu
    val isSynced: Boolean = false,
    @ColumnInfo(name = "hc_session_id")
    val hcSessionId: String? = null,
    val isFinished: Boolean = true // Nowe pole dla kontroli synchronizacji
)

@Entity(tableName = "workout_points")
data class WorkoutPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val time: String,
    val latitude: Double?,
    val longitude: Double?,
    val bpm: Int?,
    val steps: Int?,
    val stepsMin: Double?,
    val distanceSteps: Int?,
    val distanceGps: Int?,
    val speedGps: Double?,
    val speedSteps: Double?,
    val altitude: Double?,
    val totalAscent: Double?,
    val totalDescent: Double?,
    val calorieMin: Double?,
    val calorieSum: Double?,
    val pressure: Double? = null
)
