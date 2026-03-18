package com.example.sportapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.sportapp.data.db.WorkoutEntity

@Entity(
    tableName = "workout_laps",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutId")]
)
data class WorkoutLap(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
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
