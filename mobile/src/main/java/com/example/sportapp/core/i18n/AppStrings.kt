package com.example.sportapp.core.i18n

import androidx.compose.runtime.staticCompositionLocalOf

interface AppStrings {
    // Disciplines
    val running: String
    val walking: String
    val cycling: String
    val swimming: String
    val heartRate: String
    val steps: String
    val calories: String
    val distance: String
    val altitude: String
    val pace: String
    val speed: String
    val cadence: String
    val ascent: String
    val descent: String

    // Session UI
    val start: String
    val pause: String
    val stop: String
    val resume: String
    val save: String
    val discard: String
    val workoutStart: String
    val activeTime: String

    // Settings
    val darkMode: String
    val units: String
    val sensors: String
    val widgets: String
    val language: String
    val theme: String
    val systemDefault: String
    val light: String
    val dark: String
    val healthData: String
    val workouts: String
    val mainScreenView: String
    val watchStats: String
    val integration: String
    val myProfile: String

    // Stats
    val today: String
    val week: String
    val month: String
    val year: String
    val custom: String
    val charts: String
    val heartRateZones: String
    val activityCount: String
    val totalCalories: String
    val totalDistance: String
    val maxSpeed: String
    val maxAltitude: String
    val avgCadence: String
    val avgSpeed: String
    val maxElevationGain: String
    val maxDistance: String
    val maxDuration: String
    val maxCalories: String
    val maxAvgCadence: String
    val maxAvgSpeed: String

    // Messages
    val noGpsPermission: String
    val saveError: String
    val importFinished: String
    val noData: String
    val back: String
    val noWidgetsSelected: String
    val resultsFromToday: String
    val resultsFromWeek: String
    val resultsFromMonth: String
    val resultsFromYear: String
    fun resultsFromLastDays(days: Int): String
    val generalStats: String
    val workoutDetails: String
    val sync: String
    val options: String
    val confirmDelete: String
    val cancel: String
    val delete: String
    val edit: String
    val compare: String
    val details: String
    val filters: String
    val allTypes: String
    val from: String
    val to: String
    val activityList: String
    val importGpx: String
    val exportGpx: String
    val importingData: String
    val warning: String
    val continueLabel: String
    val chooseActivityType: String
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> {
    error("No AppStrings provided")
}
