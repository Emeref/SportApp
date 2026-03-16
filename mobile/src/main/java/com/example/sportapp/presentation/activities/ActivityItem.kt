package com.example.sportapp.presentation.activities

data class ActivityItem(
    val id: String,
    val type: String,
    val date: String,
    val duration: String,
    val calories: String,
    val distanceGps: String,
    val distanceSteps: String,
    // Raw values for sorting
    val rawTimestamp: Long = 0L,
    val rawDurationSeconds: Long = 0L,
    val rawCalories: Double = 0.0,
    val rawDistanceGps: Double = 0.0,
    val rawDistanceSteps: Double = 0.0
)
