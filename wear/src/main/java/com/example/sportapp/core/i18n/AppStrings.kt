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
    val activity: String
    val time: String

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
    val screen: String
    val screenBehavior: String
    val alwaysOn: String
    val ambientMode: String
    val autoMode: String
    val clockColor: String
    val colorRed: String
    val colorWhite: String
    val colorGreen: String
    val colorYellow: String
    val colorBlue: String
    val colorBlack: String
    val colorNone: String
    val colorCustom: String
    val age: String
    val weight: String
    val height: String
    val stepLength: String
    val restingHeartRate: String
    val maxHeartRate: String
    val gender: String
    val male: String
    val female: String
    val selectGender: String

    // Units
    val yearsUnit: String
    val kgUnit: String
    val cmUnit: String
    val bpmUnit: String
    val kcalUnit: String
    val kmhUnit: String
    val hpaUnit: String
    val metersUnit: String
    val kmUnit: String

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
    val last7Days: String
    val last30Days: String
    val lastYear: String
    fun lastXDays(days: Int): String

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
    val chooseSport: String
    val configurationError: String
    val noFieldsSelected: String
    val summary: String
    val confirm: String
    val pressure: String
    val cadenceSteps: String
    val distanceSteps: String
    val speedSteps: String
    val totalAscent: String
    val totalDescent: String
    val caloriesMin: String
    val locationData: String
    val noDefinitions: String
    val distanceGps: String
    val allSteps: String
    val helloWorld: String
    val workoutMonitoring: String

    // Days
    val mon: String
    val tue: String
    val wed: String
    val thu: String
    val fri: String
    val sat: String
    val sun: String
    val monday: String
    val tuesday: String
    val wednesday: String
    val thursday: String
    val friday: String
    val saturday: String
    val sunday: String
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> {
    error("No AppStrings provided")
}
