package com.example.sportapp.presentation.workout

data class WorkoutConfig(
    val name: String,
    val rows: List<WorkoutRow>
)

data class WorkoutRow(
    val rowIndex: Int,
    val sensors: List<SensorConfig>
)

data class SensorConfig(
    val id: String,
    val positionInRow: Int
)

data class SensorInfo(
    val id: String,
    val label: String,
    val isEnabled: Boolean = true
)

data class SportConfig(
    val id: String,
    val name: String,
    val sensors: List<SensorInfo> = listOf(
        SensorInfo("hr", "Tętno"),
        SensorInfo("steps", "Kroki"),
        SensorInfo("dist_steps", "Dystans (kroki)"),
        SensorInfo("dist_gps", "Dystans (GPS)"),
        SensorInfo("calories", "Kalorie"),
        SensorInfo("alt", "Wysokość"),
        SensorInfo("ascent", "Wzniosy"),
        SensorInfo("descent", "Spadki"),
        SensorInfo("kcal_min", "kcal/min"),
        SensorInfo("steps_min", "kroki/min"),
        SensorInfo("speed_gps", "Prędkość (GPS)"),
        SensorInfo("speed_steps", "Prędkość (kroki)"),
        SensorInfo("map", "Mapa")
    )
)
