package com.example.sportapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class WorkoutSensor(val id: String, val label: String) {
    MAP("map", "Mapa"),
    HEART_RATE("bpm", "Tętno"),
    AVG_HEART_RATE("avgBpm", "Średnie tętno"),
    STEPS("steps", "Kroki"),
    STEPS_PER_MINUTE("stepsMin", "Kroki na minutę"),
    DISTANCE_STEPS("distanceSteps", "Dystans (kroki)"),
    DISTANCE_GPS("distanceGps", "Dystans"),
    SPEED_GPS("speedGps", "Prędkość"),
    SPEED_STEPS("speedSteps", "Prędkość (kroki)"),
    ALTITUDE("altitude", "Wysokość"),
    TOTAL_ASCENT("totalAscent", "Przewyższenie +"),
    TOTAL_DESCENT("totalDescent", "Przewyższenie -"),
    CALORIES_PER_MINUTE("calorieMin", "Kalorie na minutę"),
    CALORIES_SUM("calorieSum", "Spalone kalorie")
}

data class SensorConfig(
    val sensorId: String,
    val isVisible: Boolean,
    val isRecording: Boolean
)

@Entity(tableName = "workout_definitions")
data class WorkoutDefinition(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconName: String,
    val sensors: List<SensorConfig>,
    val baseType: String,
    val isDefault: Boolean = false
)
