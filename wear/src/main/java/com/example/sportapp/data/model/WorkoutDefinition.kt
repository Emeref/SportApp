package com.example.sportapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sportapp.TextsWearPL

enum class WorkoutSensor(val id: String, val label: String) {
    HEART_RATE("bpm", TextsWearPL.SENSOR_HEART_RATE),
    CALORIES_SUM("calorieSum", TextsWearPL.SENSOR_CALORIES_SUM),
    CALORIES_PER_MINUTE("calorieMin", TextsWearPL.SENSOR_CALORIES_MIN),
    STEPS("steps", TextsWearPL.SENSOR_STEPS),
    STEPS_PER_MINUTE("stepsMin", TextsWearPL.SENSOR_STEPS_MIN),
    DISTANCE_STEPS("distanceSteps", TextsWearPL.SENSOR_DISTANCE_STEPS),
    SPEED_GPS("speedGps", TextsWearPL.SENSOR_SPEED_GPS),
    SPEED_STEPS("speedSteps", TextsWearPL.SENSOR_SPEED_STEPS),
    DISTANCE_GPS("distanceGps", TextsWearPL.SENSOR_DISTANCE_GPS),
    ALTITUDE("altitude", TextsWearPL.SENSOR_ALTITUDE),
    TOTAL_ASCENT("totalAscent", TextsWearPL.SENSOR_TOTAL_ASCENT),
    TOTAL_DESCENT("totalDescent", TextsWearPL.SENSOR_TOTAL_DESCENT),
    PRESSURE("pressure", TextsWearPL.SENSOR_PRESSURE),
    MAP("map", TextsWearPL.SENSOR_MAP)
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
