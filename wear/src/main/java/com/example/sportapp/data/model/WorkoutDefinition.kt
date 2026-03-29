package com.example.sportapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sportapp.core.i18n.AppStrings

enum class WorkoutSensor(val id: String) {
    HEART_RATE("bpm"),
    CALORIES_SUM("calorieSum"),
    CALORIES_PER_MINUTE("calorieMin"),
    STEPS("steps"),
    STEPS_PER_MINUTE("stepsMin"),
    DISTANCE_STEPS("distanceSteps"),
    SPEED_GPS("speedGps"),
    SPEED_STEPS("speedSteps"),
    DISTANCE_GPS("distanceGps"),
    ALTITUDE("altitude"),
    TOTAL_ASCENT("totalAscent"),
    TOTAL_DESCENT("totalDescent"),
    PRESSURE("pressure"),
    MAP("map");
    
    fun getLabel(strings: AppStrings): String = when (this) {
        HEART_RATE -> strings.heartRate
        CALORIES_SUM -> strings.totalCalories
        CALORIES_PER_MINUTE -> strings.caloriesMin
        STEPS -> strings.steps
        STEPS_PER_MINUTE -> strings.cadenceSteps
        DISTANCE_STEPS -> strings.distanceSteps
        SPEED_GPS -> strings.speed
        SPEED_STEPS -> strings.speedSteps
        DISTANCE_GPS -> strings.distance
        ALTITUDE -> strings.altitude
        TOTAL_ASCENT -> strings.totalAscent
        TOTAL_DESCENT -> strings.totalDescent
        PRESSURE -> strings.pressure
        MAP -> strings.locationData
    }
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
    val sortOrder: Int = 0,
    val displayOrder: Int = 0,
    val autoLapDistance: Double? = null
)
