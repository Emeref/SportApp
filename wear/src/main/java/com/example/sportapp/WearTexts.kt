package com.example.sportapp

import androidx.compose.runtime.staticCompositionLocalOf

interface WearTexts {
    // Main Menu
    val MENU_SPORT: String
    val MENU_STATISTICS: String
    val MENU_SETTINGS: String
    val APP_LOGO_DESC: String

    // Choose Sport
    val CHOOSE_SPORT_TITLE: String
    val CHOOSE_SPORT_NO_DEFINITIONS: String
    val CHOOSE_SPORT_DEFAULT_NAME: String

    // Statistics
    val STATS_NO_WIDGETS: String
    val STATS_PERIOD_TODAY: String
    val STATS_PERIOD_7_DAYS: String
    val STATS_PERIOD_30_DAYS: String
    val STATS_PERIOD_YEAR: String
    fun statsPeriodCustom(days: Int): String

    val STATS_WIDGET_COUNT: String
    val STATS_WIDGET_CALORIES: String
    val STATS_WIDGET_DISTANCE_GPS: String
    val STATS_WIDGET_DISTANCE_STEPS: String
    val STATS_WIDGET_ASCENT: String
    val STATS_WIDGET_DESCENT: String
    val STATS_WIDGET_STEPS_ALL: String

    val STATS_WIDGET_MAX_SPEED: String
    val STATS_WIDGET_MAX_ALTITUDE: String
    val STATS_WIDGET_MAX_ELEVATION_GAIN: String
    val STATS_WIDGET_MAX_DISTANCE: String
    val STATS_WIDGET_MAX_DURATION: String
    val STATS_WIDGET_MAX_CALORIES: String
    val STATS_WIDGET_MAX_AVG_CADENCE: String
    val STATS_WIDGET_MAX_AVG_SPEED: String

    // Workout Ready
    val WORKOUT_READY_START: String
    val WORKOUT_READY_BACK: String

    // Settings
    val SETTINGS_TITLE: String
    val SETTINGS_HEALTH_DATA: String
    val SETTINGS_SCREEN: String
    val SETTINGS_SCREEN_ALWAYS_ON: String
    val SETTINGS_SCREEN_AMBIENT: String
    val SETTINGS_SCREEN_AUTO: String
    val SETTINGS_CLOCK_COLOR: String
    val SETTINGS_SCREEN_BEHAVIOR_TITLE: String
    val SETTINGS_LANGUAGE: String
    val SETTINGS_LANGUAGE_SELECTION_TITLE: String
    
    // Colors
    val COLOR_RED: String
    val COLOR_WHITE: String
    val COLOR_GREEN: String
    val COLOR_YELLOW: String
    val COLOR_BLUE: String
    val COLOR_BLACK: String
    val COLOR_NONE: String
    val COLOR_CUSTOM: String

    // Health Data
    val HEALTH_GENDER: String
    val HEALTH_AGE: String
    val HEALTH_WEIGHT: String
    val HEALTH_HEIGHT: String
    val HEALTH_STEP_LENGTH: String
    val HEALTH_RESTING_HR: String
    val HEALTH_MAX_HR: String
    val HEALTH_SAVE: String
    val HEALTH_CHOOSE_GENDER: String
    val GENDER_MALE: String
    val GENDER_FEMALE: String
    
    fun healthAgeValue(age: Int): String
    fun healthWeightValue(weight: Int): String
    fun healthHeightValue(height: Int): String
    fun healthStepLengthValue(length: Int): String
    fun healthHRValue(hr: Int): String

    // Units
    val UNIT_YEARS: String
    val UNIT_KG: String
    val UNIT_CM: String
    val UNIT_BPM: String
    val UNIT_M: String
    val UNIT_KM: String
    val UNIT_KMH: String
    val UNIT_KCAL: String
    val UNIT_HPA: String

    // Workout Data / Labels
    val WORKOUT_ERROR_CONFIG: String
    val WORKOUT_LABEL_TIMER: String
    val WORKOUT_LABEL_STEPS: String
    val WORKOUT_LABEL_DISTANCE: String
    val WORKOUT_LABEL_SPEED: String
    val WORKOUT_LABEL_HR: String
    val WORKOUT_LABEL_PRESSURE: String
    val WORKOUT_LABEL_ALTITUDE: String

    // Sensors Names (for WorkoutDefinition)
    val SENSOR_HEART_RATE: String
    val SENSOR_CALORIES_SUM: String
    val SENSOR_CALORIES_MIN: String
    val SENSOR_STEPS: String
    val SENSOR_STEPS_MIN: String
    val SENSOR_DISTANCE_STEPS: String
    val SENSOR_SPEED_GPS: String
    val SENSOR_SPEED_STEPS: String
    val SENSOR_DISTANCE_GPS: String
    val SENSOR_ALTITUDE: String
    val SENSOR_TOTAL_ASCENT: String
    val SENSOR_TOTAL_DESCENT: String
    val SENSOR_PRESSURE: String
    val SENSOR_MAP: String

    // Workout Controls
    val WORKOUT_RESUME: String
    val WORKOUT_PAUSE: String
    val WORKOUT_FINISH: String
    val WORKOUT_START: String
    val WORKOUT_STOP: String
    val WORKOUT_READY_MSG: String

    // Summary
    val SUMMARY_TITLE: String
    val SUMMARY_CONFIRM_DESC: String
    val SUMMARY_DURATION: String
    val SUMMARY_AVG_HR: String
    val SUMMARY_MAX_HR: String
    val SUMMARY_AVG_SPEED: String
    val SUMMARY_MAX_SPEED: String
    val SUMMARY_AVG_SPEED_STEPS: String
    val SUMMARY_MAX_SPEED_STEPS: String
    val SUMMARY_DISTANCE: String
    val SUMMARY_DISTANCE_STEPS: String
    val SUMMARY_STEPS: String
    val SUMMARY_TOTAL_ASCENT: String
    val SUMMARY_TOTAL_DESCENT: String
    val SUMMARY_CALORIES: String

    // General
    val GEN_ACTIVITY: String
    val VAL_EMPTY: String

    // Complication / Tile
    val COMP_MON: String
    val COMP_TUE: String
    val COMP_WED: String
    val COMP_THU: String
    val COMP_FRI: String
    val COMP_SAT: String
    val COMP_SUN: String
    
    val COMP_MONDAY: String
    val COMP_TUESDAY: String
    val COMP_WEDNESDAY: String
    val COMP_THURSDAY: String
    val COMP_FRIDAY: String
    val COMP_SATURDAY: String
    val COMP_SUNDAY: String

    val TILE_HELLO: String
}

val LocalWearTexts = staticCompositionLocalOf<WearTexts> {
    TextsWearPL
}
