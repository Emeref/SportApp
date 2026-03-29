package com.example.sportapp.core.i18n

import androidx.compose.runtime.staticCompositionLocalOf

interface AppStrings {
    // Locale
    val localeCode: String

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
    val cadenceUnit: String
    val paceUnit: String
    val kcalPerMinUnit: String

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
    val totalAscentLabel: String
    val totalDescentLabel: String
    val caloriesMin: String
    val locationData: String
    val noDefinitions: String
    val distanceGps: String
    val allSteps: String
    val helloWorld: String
    val workoutMonitoring: String
    val standardActivity: String

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

    // Additional Mobile Strings
    val editActivity: String
    val trimAndSave: String
    val confirmTrimTitle: String
    val confirmTrimMessage: String
    val previewNewStats: String
    val applyChanges: String
    val activityNameLabel: String
    val autoLapLabel: String
    val chooseIcon: String
    val finish: String
    val baseType: String
    val laps: String
    val predominantEffect: String
    val trendCharts: String
    val numberOfDays: String
    val widgetsSelectionSupporting: String
    val sensorsManagementSupporting: String
    val healthDataSupporting: String
    val selectAndOrderFields: String
    val watchStatsFields: String
    val googleDriveHeadline: String
    val googleDriveSupporting: String
    val welcomeMessage: String
    val deleteActivityTitle: String
    fun deleteActivityMessage(name: String): String
    val chooseActivityToModify: String
    val errorDataTitle: String
    val periodSelection: String
    val statsPeriodSelection: String
    val maleLabel: String
    val femaleLabel: String
    val heartRateChart: String
    val chooseWorkoutRange: String
    val newDuration: String
    val newDistanceGps: String
    val newDistanceSteps: String
    val newCalories: String
    val newAvgHr: String
    val moveUp: String
    val moveDown: String
    val close: String
    val ok: String
    val dataError: String
    val activityDetails: String
    val activitySettings: String

    // Comparison & Details
    val overallStatsSettingsTitle: String
    val widgetsSectionLabel: String
    val trendChartsSectionLabel: String
    val chartsSectionLabel: String
    fun comparisonTitle(activityName: String): String
    val activityComparisonLabel: String
    val durationLabel: String
    val avgHeartRateLabel: String
    val maxHeartRateLabel: String
    val totalCaloriesLabel: String
    val maxCaloriesBurnLabel: String
    val avgPaceLabel: String
    val maxSpeedLabel: String
    val maxAltitudeLabel: String
    val avgStepLengthLabel: String
    val avgCadenceLabel: String
    val maxCadenceLabel: String
    val maxAvgAvgCadence: String
    val totalStepsLabel: String
    val pressureStartLabel: String
    val pressureEndLabel: String
    val maxPressureLabel: String
    val minPressureLabel: String
    val bestPace1kmLabel: String
    val numberShortLabel: String
    val upDownShortLabel: String
    val settingsLabel: String
    val exportInit: String
    fun exportingActivity(name: String, current: Int, total: Int): String
    val noFilesGenerated: String
    val packingZip: String
    val exportError: String
    val potentialDuplicate: String
    val importError: String
    val cannotOpenFile: String
    val chooseWidgetsPeriod: String
    val chooseStatsPeriod: String
    val importSuccess: String
    
    val addNewActivity: String
    val newActivity: String
    val widgetInActivity: String
    val visibilityLabel: String
    val saveLabel: String
    val widgetSelectionTitle: String

    fun getWidgetLabel(id: String): String

    // HR Zones
    val hrZone0Name: String
    val hrZone1Name: String
    val hrZone2Name: String
    val hrZone3Name: String
    val hrZone4Name: String
    val hrZone5Name: String
    
    val hrZone0Desc: String
    val hrZone1Desc: String
    val hrZone2Desc: String
    val hrZone3Desc: String
    val hrZone4Desc: String
    val hrZone5Desc: String

    // Training Effects
    val effectNone: String
    val effectWarmup: String
    val effectFatBurn: String
    val effectAerobic: String
    val effectAnaerobic: String
    val effectVO2Max: String
    val effectRecovery: String
    val effectLactate: String

    // Data Status
    val tooLittleData: String
    val noHrData: String
    val hrBelowZones: String

    // Chart / Widget IDs labels
    val mapLabel: String
    val distanceGpsLabel: String
    val distanceStepsLabel: String
    val speedGpsLabel: String
    val speedStepsLabel: String
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> {
    error("No AppStrings provided")
}
