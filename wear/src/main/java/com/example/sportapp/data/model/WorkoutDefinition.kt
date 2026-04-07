package com.example.sportapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sportapp.TextsWearPL
import com.example.sportapp.WearTexts

enum class WorkoutSensor(val id: String, val getLabel: (WearTexts) -> String) {
    HEART_RATE("bpm", { it.SENSOR_HEART_RATE }),
    CALORIES_SUM("calorieSum", { it.SENSOR_CALORIES_SUM }),
    CALORIES_PER_MINUTE("calorieMin", { it.SENSOR_CALORIES_MIN }),
    STEPS("steps", { it.SENSOR_STEPS }),
    STEPS_PER_MINUTE("stepsMin", { it.SENSOR_STEPS_MIN }),
    DISTANCE_STEPS("distanceSteps", { it.SENSOR_DISTANCE_STEPS }),
    SPEED_GPS("speedGps", { it.SENSOR_SPEED_GPS }),
    SPEED_STEPS("speedSteps", { it.SENSOR_SPEED_STEPS }),
    DISTANCE_GPS("distanceGps", { it.SENSOR_DISTANCE_GPS }),
    ALTITUDE("altitude", { it.SENSOR_ALTITUDE }),
    TOTAL_ASCENT("totalAscent", { it.SENSOR_TOTAL_ASCENT }),
    TOTAL_DESCENT("totalDescent", { it.SENSOR_TOTAL_DESCENT }),
    PRESSURE("pressure", { it.SENSOR_PRESSURE }),
    MAP("map", { it.SENSOR_MAP });

    @Deprecated("Use getLabel(texts)", ReplaceWith("getLabel(texts)"))
    val label: String
        get() = getLabel(TextsWearPL)
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
