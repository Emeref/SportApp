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
