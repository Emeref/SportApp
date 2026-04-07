package com.example.sportapp

import androidx.compose.runtime.staticCompositionLocalOf

interface MobileTexts {
    // Navigation
    val NAV_HOME: String
    val NAV_STATS: String
    val NAV_ACTIVITIES: String
    val NAV_SETTINGS: String

    // Home Screen
    val HOME_TITLE: String
    val HOME_NO_WIDGETS: String
    val HOME_ADD_WIDGETS: String
    val HOME_LAST_ACTIVITY: String
    val HOME_ACTIVITY_COUNT: String
    val HOME_SYNC: String
    val HOME_OPTIONS: String
    val HOME_GENERAL_STATS: String
    val HOME_WORKOUT_DETAILS: String
    val HOME_LOGO_DESC: String
    val HOME_SECRET_TITLE: String
    val HOME_CLOSE: String
    fun homeResultsToday(): String
    fun homeResultsWeek(): String
    fun homeResultsMonth(): String
    fun homeResultsYear(): String
    fun homeResultsCustom(days: Int): String

    // Settings Screen
    val SETTINGS_TITLE: String
    val SETTINGS_GENERAL: String
    val SETTINGS_THEME: String
    val SETTINGS_THEME_SYSTEM: String
    val SETTINGS_THEME_LIGHT: String
    val SETTINGS_THEME_DARK: String
    val SETTINGS_LANGUAGE: String
    val SETTINGS_LANGUAGE_TITLE: String
    val SETTINGS_HEALTH_DATA: String
    val SETTINGS_HEALTH_DATA_DESC: String
    val SETTINGS_DEFINITIONS: String
    val SETTINGS_DEFINITIONS_DESC: String
    val SETTINGS_WIDGETS_HOME: String
    val SETTINGS_WIDGETS_HOME_TITLE: String
    val SETTINGS_WIDGETS_HOME_DESC: String
    val SETTINGS_WIDGETS_WATCH: String
    val SETTINGS_WIDGETS_WATCH_TITLE: String
    val SETTINGS_WIDGETS_WATCH_DESC: String
    val SETTINGS_SAVE: String
    val SETTINGS_CANCEL: String
    val SETTINGS_CLOSE: String
    val SETTINGS_PERIOD: String
    val SETTINGS_PERIOD_HOME_DESC: String
    val SETTINGS_PERIOD_WATCH_DESC: String
    val SETTINGS_CUSTOM_DAYS_LABEL: String
    val SETTINGS_INTEGRATION: String
    val SETTINGS_GOOGLE_DRIVE: String
    val SETTINGS_GOOGLE_DRIVE_DESC: String
    val SETTINGS_APPEARANCE: String
    val SETTINGS_MY_PROFILE: String
    val LANG_PL: String
    val LANG_EN: String

    // Health Data Screen
    val HEALTH_TITLE: String
    val HEALTH_GENDER: String
    val HEALTH_GENDER_MALE: String
    val HEALTH_GENDER_FEMALE: String
    val HEALTH_AGE: String
    val HEALTH_WEIGHT: String
    val HEALTH_WEIGHT_KG: String
    val HEALTH_HEIGHT: String
    val HEALTH_HEIGHT_CM: String
    val HEALTH_RESTING_HR: String
    val HEALTH_MAX_HR: String
    val HEALTH_MAX_HR_DESC: String
    val HEALTH_STEP_LENGTH: String
    val HEALTH_STEP_LENGTH_CM: String

    // Activity List
    val ACTIVITY_LIST_TITLE: String
    val ACTIVITY_EMPTY: String
    val ACTIVITY_DELETE_CONFIRM: String
    val ACTIVITY_COMPARE: String
    val ACTIVITY_TRIM: String
    val ACTIVITY_DETAIL: String
    val ACTIVITY_EDIT: String
    val ACTIVITY_IMPORT_GPX: String
    val ACTIVITY_EXPORT_GPX: String
    val ACTIVITY_CHART_SETTINGS: String
    val ACTIVITY_FILTERS: String
    val ACTIVITY_ALL_TYPES: String
    val ACTIVITY_FROM: String
    val ACTIVITY_TO: String
    val ACTIVITY_TYPE: String
    val ACTIVITY_DATE: String
    val ACTIVITY_DURATION: String
    val ACTIVITY_CALORIES: String
    val ACTIVITY_DISTANCE_GPS: String
    val ACTIVITY_DISTANCE_STEPS: String
    val ACTIVITY_DELETE: String
    val ACTIVITY_IMPORT_SELECT_TYPE: String
    val ACTIVITY_IMPORT_SELECT_DESC: String
    val ACTIVITY_IMPORT_WARNING: String
    val ACTIVITY_IMPORT_CONTINUE: String
    val ACTIVITY_IMPORT_PROGRESS: String
    val ACTIVITY_EXPORT_ERROR: String
    val ACTIVITY_SHARE_TITLE: String
    val ACTIVITY_OK: String
    val ACTIVITY_CONFIRM_DELETE_TITLE: String
    val ACTIVITY_ALL: String

    // Activity Detail
    val DETAIL_TITLE: String
    val DETAIL_MAP: String
    val DETAIL_CHARTS: String
    val DETAIL_LAPS: String
    val DETAIL_STATISTICS: String
    val DETAIL_DATA_ERROR_TITLE: String
    val DETAIL_ERROR_OK: String
    val DETAIL_INTERVALS: String
    fun detailLapsWithDistance(distance: String): String
    fun detailLapsCount(count: Int): String
    val DETAIL_HEART_RATE: String
    val DETAIL_HR_ZONES: String
    val DETAIL_TRAINING_EFFECT: String
    val DETAIL_LAP_NR: String
    val DETAIL_LAP_TIME: String
    val DETAIL_LAP_AVG_PACE: String
    val DETAIL_LAP_AVG_SPEED: String
    val DETAIL_LAP_MAX_SPEED: String
    val DETAIL_LAP_AVG_HR: String
    val DETAIL_LAP_MAX_HR: String
    val DETAIL_LAP_ASCENT_DESCENT: String
    val DETAIL_MAP_START: String
    val DETAIL_MAP_FINISH: String
    val DETAIL_MAP_EXPAND: String
    val DETAIL_MAP_COLLAPSE: String
    val DETAIL_EXPAND: String
    val DETAIL_COLLAPSE: String
    val DETAIL_PREDOMINANT_EFFECT: String

    // Stats
    val STATS_TITLE: String
    val STATS_CHARTS: String
    val STATS_WIDGETS: String
    val STATS_NO_DATA: String
    val STATS_TREND_CHARTS: String
    val STATS_FILTERS: String
    val STATS_ALL_TYPES: String
    val STATS_FROM: String
    val STATS_TO: String
    val STATS_NO_WIDGETS: String
    val STATS_SETTINGS_TITLE: String
    val STATS_SECTION_WIDGETS: String
    val STATS_SECTION_CHARTS: String
    val STATS_MOVE_UP: String
    val STATS_MOVE_DOWN: String
    fun chartDistanceGps(km: Boolean): String
    fun chartDistanceSteps(km: Boolean): String
    val CHART_STEPS: String

    // Definitions
    val DEF_TITLE: String
    val DEF_ADD: String
    val DEF_EDIT: String
    val DEF_DELETE: String
    val DEF_NAME: String
    val DEF_ICON: String
    val DEF_SENSORS: String
    val DEF_LIST_TITLE: String
    val DEF_SENSORS_DESC: String
    val DEF_RECORDING: String
    val DEF_SELECT_ICON: String
    val DEF_SAVE: String
    val DEF_MOVE_UP: String
    val DEF_MOVE_DOWN: String
    val DEF_DELETE_TITLE: String
    fun defDeleteConfirm(name: String): String
    val DEF_NEW_ACTIVITY: String
    val DEF_EDIT_ACTIVITY: String
    val DEF_NAME_LABEL: String
    val DEF_AUTO_LAP_LABEL: String
    val DEF_WIDGET_IN_ACTIVITY: String
    val DEF_VISIBILITY: String
    val DEF_RECORD: String
    val DEF_BASE_TYPE: String
    val DEF_FINISH: String
    val DEF_SELECT_ICON_TITLE: String
    val DEF_WALKING: String
    val DEF_RUNNING: String
    val DEF_CYCLING: String
    val DEF_HIKING: String
    val DEF_OTHER: String
    val DEF_STANDARD_ACTIVITY: String

    // Activity Detail Settings
    val AD_SETTINGS_LIST_TITLE: String
    val AD_SETTINGS_EDIT_TITLE: String
    val AD_SETTINGS_SECTION_WIDGETS: String
    val AD_SETTINGS_SECTION_CHARTS: String

    // Heart Rate Math
    val HR_NO_DATA: String
    val HR_TOO_LITTLE_DATA: String
    val HR_BELOW_ZONES: String
    val HR_EFFECT_Z0: String
    val HR_EFFECT_Z1: String
    val HR_EFFECT_Z2: String
    val HR_EFFECT_Z3: String
    val HR_EFFECT_Z4: String
    val HR_EFFECT_Z5: String
    val HR_EFFECT_NONE: String

    // ViewModels Messages
    val VM_EXPORT_INITIALIZING: String
    fun vmExportGenerating(name: String, current: Int, total: Int): String
    val VM_EXPORT_NO_FILES: String
    val VM_EXPORT_ZIPPING: String
    fun vmExportError(msg: String): String
    val VM_IMPORT_OPEN_ERROR: String
    val VM_IMPORT_DUPLICATE_WARNING: String
    val VM_IMPORT_SUCCESS: String
    fun vmImportError(msg: String): String

    // Gpx Importer
    val GPX_NO_POINTS: String
    val GPX_WARN_HR: String
    val GPX_WARN_ELE: String
    val GPX_WARN_CADENCE: String

    // Periods
    val PERIOD_TODAY: String
    val PERIOD_WEEK: String
    val PERIOD_MONTH: String
    val PERIOD_YEAR: String
    val PERIOD_CUSTOM: String
    fun periodCustomDays(days: Int): String

    // Widgets
    val WIDGET_COUNT: String
    val WIDGET_CALORIES: String
    val WIDGET_DISTANCE_GPS: String
    val WIDGET_DISTANCE_STEPS: String
    val WIDGET_ASCENT: String
    val WIDGET_DESCENT: String
    val WIDGET_STEPS: String
    val WIDGET_AVG_CADENCE: String
    val WIDGET_MAX_SPEED: String
    val WIDGET_MAX_ALTITUDE: String
    val WIDGET_MAX_ELEVATION_GAIN: String
    val WIDGET_MAX_DISTANCE: String
    val WIDGET_MAX_DURATION: String
    val WIDGET_MAX_CALORIES: String
    val WIDGET_MAX_AVG_CADENCE: String
    val WIDGET_MAX_AVG_SPEED: String
    val WIDGET_DURATION: String
    val WIDGET_MAX_BPM: String
    val WIDGET_AVG_BPM: String
    val WIDGET_TOTAL_CALORIES: String
    val WIDGET_MAX_CALORIES_MIN: String
    val WIDGET_AVG_PACE: String
    val WIDGET_AVG_SPEED_GPS: String
    val WIDGET_AVG_SPEED_STEPS: String
    val WIDGET_MAX_ALTITUDE_DESC: String
    val WIDGET_TOTAL_ASCENT: String
    val WIDGET_TOTAL_DESCENT: String
    val WIDGET_AVG_STEP_LENGTH: String
    val WIDGET_AVG_CADENCE_DESC: String
    val WIDGET_MAX_CADENCE: String
    val WIDGET_TOTAL_STEPS: String
    val WIDGET_PRESSURE_START: String
    val WIDGET_PRESSURE_END: String
    val WIDGET_MAX_PRESSURE: String
    val WIDGET_MIN_PRESSURE: String
    val WIDGET_BEST_PACE_1KM: String

    // Trim Screen
    val TRIM_TITLE: String
    val TRIM_CONFIRM_TITLE: String
    val TRIM_CONFIRM_DESC: String
    val TRIM_SAVE_BTN: String
    val TRIM_CHART_HR: String
    val TRIM_RANGE_TITLE: String
    val TRIM_PREVIEW_TITLE: String
    val TRIM_NEW_DURATION: String
    val TRIM_DISTANCE_GPS: String
    val TRIM_DISTANCE_STEPS: String
    val TRIM_CALORIES: String
    val TRIM_AVG_BPM: String
    val TRIM_START: String
    val TRIM_END: String

    // Compare Screen
    val COMPARE_TITLE: String
    val COMPARE_VS: String
    val COMPARE_HIGHER_IS_BETTER: String
    val COMPARE_LOWER_IS_BETTER: String

    // Units
    val UNIT_KCAL: String
    val UNIT_M: String
    val UNIT_KM: String
    val UNIT_STEP_MIN: String
    val UNIT_KM_H: String
    val UNIT_STEPS: String
    val UNIT_HPA: String
    val UNIT_MIN_KM: String
    val UNIT_M_ASL: String
    val UNIT_MIN_KM_LABEL: String
    val UNIT_BPM: String
    val UNIT_KCAL_MIN: String
    
    fun getWidgetLabel(id: String): String
}

val LocalMobileTexts = staticCompositionLocalOf<MobileTexts> {
    error("No MobileTexts provided")
}

