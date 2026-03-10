package com.example.sportapp.presentation.activities

data class ActivityItem(
    val id: String, // To będzie nazwa pliku CSV
    val type: String,
    val date: String,
    val duration: String,
    val calories: String,
    val distanceGps: String,
    val distanceSteps: String
)
