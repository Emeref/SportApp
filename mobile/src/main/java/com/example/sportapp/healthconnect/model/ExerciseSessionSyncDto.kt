package com.example.sportapp.healthconnect.model

import java.time.Instant

data class ExerciseSessionSyncDto(
    val hcSessionId: String,
    val title: String,
    val exerciseType: Int, // Health Connect Exercise Type
    val startTime: Instant,
    val endTime: Instant,
    val distanceMeters: Double? = null,
    val activeCalories: Double? = null,
    val avgHeartRate: Int? = null,
    val maxHeartRate: Int? = null,
    val avgSpeedMps: Double? = null,
    val maxSpeedMps: Double? = null,
    val alreadyImported: Boolean = false,
    val isSelected: Boolean = !alreadyImported
)
