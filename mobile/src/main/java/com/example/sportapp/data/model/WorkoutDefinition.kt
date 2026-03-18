package com.example.sportapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class WorkoutSensor(val id: String, val label: String) {
    HEART_RATE("bpm", "Tętno"),
    CALORIES_SUM("calorieSum", "Spalone kalorie"),
    CALORIES_PER_MINUTE("calorieMin", "Kalorie na minutę"),
    STEPS("steps", "Kroki"),
    STEPS_PER_MINUTE("stepsMin", "Kroki na minutę"),
    DISTANCE_STEPS("distanceSteps", "Dystans (kroki)"),
    SPEED_GPS("speedGps", "Prędkość"),
    SPEED_STEPS("speedSteps", "Prędkość (kroki)"),
    DISTANCE_GPS("distanceGps", "Dystans"),
    ALTITUDE("altitude", "Wysokość"),
    TOTAL_ASCENT("totalAscent", "W sumie w górę"),
    TOTAL_DESCENT("totalDescent", "W sumie do dołu"),
    PRESSURE("pressure", "Ciśnienie"),
    MAP("map", "Mapa")
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
    val isDefault: Boolean = false,
    val sortOrder: Int = 0
    val displayOrder: Int = 0
)
