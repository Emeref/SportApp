package com.example.sportapp.presentation.workout

data class SportConfig(
    val id: String,
    val name: String,
    val rows: List<WorkoutRow> = emptyList()
) {
    val sensors: List<SensorConfig> get() = rows.flatMap { it.sensors }
}

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
    val positionInRow: Int,
    val isEnabled: Boolean = true
)
