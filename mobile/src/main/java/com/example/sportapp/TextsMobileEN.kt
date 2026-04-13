package com.example.sportapp

import java.util.Calendar
import java.util.Locale

object TextsMobileEN : MobileTexts {
    // Navigation
    override val NAV_HOME = "Home"
    override val NAV_STATS = "Stats"
    override val NAV_ACTIVITIES = "Activities"
    override val NAV_SETTINGS = "Settings"

    // Home Screen
    override val HOME_TITLE = "SportApp"
    override val HOME_NO_WIDGETS = "No widgets selected"
    override val HOME_ADD_WIDGETS = "Add widgets"
    override val HOME_LAST_ACTIVITY = "Last activity"
    override val HOME_ACTIVITY_COUNT = "Activity count"
    override val HOME_SYNC = "Sync"
    override val HOME_OPTIONS = "Options"
    override val HOME_GENERAL_STATS = "General Stats"
    override val HOME_WORKOUT_DETAILS = "Workout Details"
    override val HOME_LOGO_DESC = "App Logo"
    override val HOME_SECRET_TITLE = "Great that you're clicking, but there's nothing here"
    override val HOME_CLOSE = "Close"

    override fun homeResultsToday() = "Today's results:"
    override fun homeResultsWeek() = "Weekly results:"
    override fun homeResultsMonth(): String {
        val monthName = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)
        return "$monthName results:"
    }
    override fun homeResultsYear() = "This year's results:"
    override fun homeResultsCustom(days: Int) = if (days == 1) "Results from last day:" else "Results from last $days days:"

    // Settings Screen
    override val SETTINGS_TITLE = "Settings"
    override val SETTINGS_GENERAL = "General"
    override val SETTINGS_THEME = "App Theme"
    override val SETTINGS_THEME_SYSTEM = "System"
    override val SETTINGS_THEME_LIGHT = "Light"
    override val SETTINGS_THEME_DARK = "Dark"
    override val SETTINGS_LANGUAGE = "Language"
    override val SETTINGS_LANGUAGE_TITLE = "Select Language"
    override val SETTINGS_HEALTH_DATA = "Health Data & HR"
    override val SETTINGS_HEALTH_DATA_DESC = "Age, weight, HR Max & zones"
    override val SETTINGS_DEFINITIONS = "Activity Definitions"
    override val SETTINGS_DEFINITIONS_DESC = "Manage sports list and sensors"
    override val SETTINGS_WIDGETS_HOME = "Home Screen Widgets"
    override val SETTINGS_WIDGETS_HOME_TITLE = "Home View"
    override val SETTINGS_WIDGETS_HOME_DESC = "Select and set order"
    override val SETTINGS_WIDGETS_WATCH = "Watch Stats"
    override val SETTINGS_WIDGETS_WATCH_TITLE = "Stats Fields"
    override val SETTINGS_WIDGETS_WATCH_DESC = "Select and set order on watch"
    override val SETTINGS_SAVE = "Save"
    override val SETTINGS_CANCEL = "Cancel"
    override val SETTINGS_CLOSE = "Close"
    override val SETTINGS_PERIOD = "Default Period"
    override val SETTINGS_PERIOD_HOME_DESC = "For which period to show widgets?"
    override val SETTINGS_PERIOD_WATCH_DESC = "Stats from which period?"
    override val SETTINGS_CUSTOM_DAYS_LABEL = "Number of days"
    override val SETTINGS_WATCH_STATS_DAYS_LABEL = "Show stats from how many days?"
    override val SETTINGS_CUSTOM_DAYS_DESC = "Number of days for 'Custom' period"
    override val SETTINGS_WATCH_STATS_DAYS_DESC = "Number of days for watch stats"
    override val SETTINGS_INTEGRATION = "Integration"
    override val SETTINGS_SYNC = "Sync"
    override val SETTINGS_STRAVA = "Strava"
    override val SETTINGS_STRAVA_DESC = "Sync your workouts with Strava"
    override val SETTINGS_GOOGLE_DRIVE = "Google Drive"
    override val SETTINGS_GOOGLE_DRIVE_DESC = "Save history and summaries (Soon)"
    override val SETTINGS_APPEARANCE = "Appearance"
    override val SETTINGS_MY_PROFILE = "My Profile"
    override val LANG_PL = "Polish"
    override val LANG_EN = "English"

    // Health Connect Strings
    override val SETTINGS_HC_TITLE = "Health Connect"
    override val SETTINGS_HC_MANAGE_PERMISSIONS = "Manage Health Connect permissions"
    override val SETTINGS_HC_STATUS = "Health Connect Status"
    override val HC_STATUS_AVAILABLE = "Available"
    override val HC_STATUS_UNAVAILABLE = "Unavailable"
    override val HC_STATUS_NOT_INSTALLED = "Not installed"
    override val HC_INSTALL = "Install"
    override val HC_SYNC_HEALTH_DATA = "Sync with Health Connect"
    override val HC_SYNC_WORKOUTS = "Import workouts from Health Connect"
    override val SETTINGS_HC_AUTO_EXPORT = "Auto export"
    override val SETTINGS_HC_AUTO_EXPORT_DESC = "Automatically export new workouts to Health Connect"
    override val HC_SYNC_CONFIRM_TITLE = "Data Synchronization"
    override val HC_SYNC_CONFIRM_DESC = "Do you want to update your profile with data found in Health Connect?"
    override val HC_SYNC_SUCCESS = "Synchronization successful"
    override val HC_SYNC_ERROR = "Synchronization error"
    override val HC_SYNC_NO_DATA = "No new data found"
    override fun hcSyncPreview(weight: String?, height: String?, vo2max: String?): String {
        val parts = mutableListOf<String>()
        weight?.let { parts.add("weight $it kg") }
        height?.let { parts.add("height $it cm") }
        vo2max?.let { parts.add("VO2Max $it ml/kg/min") }
        return "Found in Health Connect: ${parts.joinToString(", ")} – update?"
    }
    
    // Stage 3 - Import Workouts
    override val HC_IMPORT_TITLE = "Import Workouts"
    override val HC_IMPORT_ALREADY_IMPORTED = "Already imported"
    override val HC_IMPORT_EMPTY = "No workouts found in Health Connect from last 30 days."
    override val HC_IMPORT_CONFIRM_DESC_PLURAL = "Are you sure you want to import selected workouts?"
    override val HC_IMPORT_SELECT_ALL = "Select all"
    override fun hcImportSelected(count: Int) = "Import selected ($count)"
    override fun hcImportConfirmDesc(count: Int) = "Do you want to import $count workouts?"
    override fun hcImportProgress(current: Int, total: Int) = "Importing $current/$total workouts..."

    // Stage 5 - Export
    override val HC_EXPORT_TO = "Export to Health Connect"
    override val HC_EXPORTED_ON = "✓ Synced with Health Connect"
    override val HC_EXPORT_SUCCESS = "Export successful"
    override val HC_EXPORT_ERROR = "Export error: "
    override val HC_EXPORT_PERMISSION_DENIED = "No Health Connect write permission"

    // Stage 6 - Sync Status
    override val SYNC_STATUS_TITLE = "HC Sync Status"
    override val SYNC_LAST_HEALTH = "Last health data sync"
    override val SYNC_LAST_WORKOUT = "Last workout sync"
    override val SYNC_UNSYNCED_COUNT = "Unsynced records"
    override val SYNC_NOW = "Sync now"
    override val SYNC_HISTORY_TITLE = "Sync History"
    override val SYNC_TYPE_IMPORT = "Import"
    override val SYNC_TYPE_EXPORT = "Export"
    override val SYNC_NEVER = "Never"
    override val SYNC_CONFLICT_POLICY = "Conflict Policy"
    override val SYNC_CONFLICT_NEWER = "Newer wins"
    override val SYNC_CONFLICT_LOCAL = "Local wins"
    override val SYNC_CONFLICT_HC = "Health Connect wins"

    // Health Connect Permissions Dialog
    override val HC_PERMISSIONS_DIALOG_TITLE = "Required Permissions"
    override val HC_PERMISSIONS_DIALOG_DESC = "Write permissions are necessary to export workouts to Health Connect. You can grant them in system settings."
    override val HC_OPEN_SETTINGS = "Open settings"

    // Health Data Screen
    override val HEALTH_TITLE = "Health Data"
    override val HEALTH_GENDER = "Gender"
    override val HEALTH_GENDER_MALE = "Male"
    override val HEALTH_GENDER_FEMALE = "Female"
    override val HEALTH_AGE = "Age"
    override val HEALTH_WEIGHT = "Weight"
    override val HEALTH_WEIGHT_KG = "Weight (kg)"
    override val HEALTH_HEIGHT = "Height"
    override val HEALTH_HEIGHT_CM = "Height (cm)"
    override val HEALTH_RESTING_HR = "Resting HR"
    override val HEALTH_MAX_HR = "Max HR"
    override val HEALTH_MAX_HR_DESC = "Max HR"
    override val HEALTH_STEP_LENGTH = "Step Length"
    override val HEALTH_STEP_LENGTH_CM = "Step Length (cm)"
    override val HEALTH_VO2_MAX = "VO2 Max"

    // Activity List
    override val ACTIVITY_LIST_TITLE = "Activity List"
    override val ACTIVITY_EMPTY = "No activities"
    override val ACTIVITY_DELETE_CONFIRM = "Are you sure you want to permanently delete selected activities from the database?"
    override val ACTIVITY_COMPARE = "Compare"
    override val ACTIVITY_TRIM = "Trim"
    override val ACTIVITY_DETAIL = "Details"
    override val ACTIVITY_EDIT = "Edit"
    override val ACTIVITY_IMPORT_GPX = "Import GPX"
    override val ACTIVITY_EXPORT_GPX = "Export GPX"
    override val ACTIVITY_CHART_SETTINGS = "Chart Settings"
    override val ACTIVITY_FILTERS = "Filters"
    override val ACTIVITY_ALL_TYPES = "All types"
    override val ACTIVITY_FROM = "From"
    override val ACTIVITY_TO = "To"
    override val ACTIVITY_TYPE = "Type"
    override val ACTIVITY_DATE = "Date"
    override val ACTIVITY_DURATION = "Time"
    override val ACTIVITY_CALORIES = "Calories"
    override val ACTIVITY_DISTANCE_GPS = "Distance (GPS)"
    override val ACTIVITY_DISTANCE_STEPS = "Distance (Steps)"
    override val ACTIVITY_DELETE = "Delete"
    override val ACTIVITY_IMPORT_SELECT_TYPE = "Select activity type"
    override val ACTIVITY_IMPORT_SELECT_DESC = "Select workout type for imported GPX file:"
    override val ACTIVITY_IMPORT_WARNING = "Warning"
    override val ACTIVITY_IMPORT_CONTINUE = "Continue"
    override val ACTIVITY_IMPORT_PROGRESS = "Importing data..."
    override val ACTIVITY_EXPORT_ERROR = "Export error"
    override val ACTIVITY_SHARE_TITLE = "Share workout(s)"
    override val ACTIVITY_OK = "OK"
    override val ACTIVITY_CONFIRM_DELETE_TITLE = "Delete activities"
    override val ACTIVITY_ALL = "All"
    override val ACTIVITY_NONE = "None"

    // Activity Detail
    override val DETAIL_TITLE = "Activity Details"
    override val DETAIL_MAP = "Map"
    override val DETAIL_CHARTS = "Charts"
    override val DETAIL_LAPS = "Laps"
    override val DETAIL_STATISTICS = "Statistics"
    override val DETAIL_DATA_ERROR_TITLE = "Data Error"
    override val DETAIL_ERROR_OK = "OK"
    override val DETAIL_INTERVALS = "Intervals"
    override fun detailLapsWithDistance(distance: String) = "Intervals ($distance)"
    override fun detailLapsCount(count: Int) = "Laps count: $count"
    override val DETAIL_HEART_RATE = "Heart Rate (bpm)"
    override val DETAIL_HR_ZONES = "HR Zones"
    override val DETAIL_TRAINING_EFFECT = "Training Effect"
    override val DETAIL_TRAINING_EFFECT_DESC = "Training effect description"
    override val DETAIL_LAP_NR = "No."
    override val DETAIL_LAP_TIME = "Time"
    override val DETAIL_LAP_AVG_PACE = "Avg Pace"
    override val DETAIL_LAP_AVG_SPEED = "Avg Speed"
    override val DETAIL_LAP_MAX_SPEED = "Max Speed"
    override val DETAIL_LAP_AVG_HR = "Avg HR"
    override val DETAIL_LAP_MAX_HR = "Max HR"
    override val DETAIL_LAP_ASCENT_DESCENT = "Up/Down"
    override val DETAIL_MAP_START = "Start"
    override val DETAIL_MAP_FINISH = "Finish"
    override val DETAIL_MAP_EXPAND = "Expand map"
    override val DETAIL_MAP_COLLAPSE = "Collapse map"
    override val DETAIL_EXPAND = "Expand"
    override val DETAIL_COLLAPSE = "Collapse"
    override val DETAIL_PREDOMINANT_EFFECT = "Predominant training effect"

    // Stats
    override val STATS_TITLE = "General Stats"
    override val STATS_CHARTS = "Charts"
    override val STATS_WIDGETS = "Widgets"
    override val STATS_NO_DATA = "No data to display charts."
    override val STATS_TREND_CHARTS = "Trend Charts"
    override val STATS_FILTERS = "Filters"
    override val STATS_ALL_TYPES = "All types"
    override val STATS_FROM = "From"
    override val STATS_TO = "To"
    override val STATS_NO_WIDGETS = "No active widgets. Enable them in options."
    override val STATS_SETTINGS_TITLE = "General Stats Settings"
    override val STATS_SECTION_WIDGETS = "Section: Widgets"
    override val STATS_SECTION_CHARTS = "Section: Charts"
    override val STATS_MOVE_UP = "Move up"
    override val STATS_MOVE_DOWN = "Move down"
    override fun chartDistanceGps(km: Boolean) = if (km) "Distance (GPS) in km" else "Distance (GPS) in m"
    override fun chartDistanceSteps(km: Boolean) = if (km) "Distance (steps) in km" else "Distance (steps) in m"
    override val CHART_STEPS = "Steps"

    // Definitions
    override val DEF_TITLE = "Workout Definitions"
    override val DEF_ADD = "Add Definition"
    override val DEF_EDIT = "Edit Definition"
    override val DEF_DELETE = "Delete Definition"
    override val DEF_NAME = "Name"
    override val DEF_ICON = "Icon"
    override val DEF_SENSORS = "Sensors"
    override val DEF_LIST_TITLE = "Activity Definition"
    override val DEF_SENSORS_DESC = "Manage sports list and sensors"
    override val DEF_RECORDING = "Recording"
    override val DEF_SELECT_ICON = "Select Icon"
    override val DEF_SAVE = "Save"
    override val DEF_MOVE_UP = "Move up"
    override val DEF_MOVE_DOWN = "Move down"
    override val DEF_DELETE_TITLE = "Delete activity"
    override fun defDeleteConfirm(name: String) = "Are you sure you want to delete activity '$name'?"
    override val DEF_NEW_ACTIVITY = "New activity"
    override val DEF_EDIT_ACTIVITY = "Edit activity"
    override val DEF_NAME_LABEL = "Activity name"
    override val DEF_AUTO_LAP_LABEL = "Auto lap (meters, optional)"
    override val DEF_WIDGET_IN_ACTIVITY = "Widget in activity"
    override val DEF_VISIBILITY = "Visibility"
    override val DEF_RECORD = "Record"
    override val DEF_BASE_TYPE = "Base type"
    override val DEF_FINISH = "Finish"
    override val DEF_SELECT_ICON_TITLE = "Select icon"
    
    // Base Types
    override val DEF_WALKING = "Walking"
    override val DEF_SPEED_WALKING = "Speed walking"
    override val DEF_RUNNING = "Running"
    override val DEF_TREADMILL_RUNNING = "Treadmill running"
    override val DEF_STAIR_CLIMBING = "Stair climbing"
    override val DEF_STAIR_CLIMBING_MACHINE = "Stair climbing machine"
    override val DEF_CYCLING = "Cycling"
    override val DEF_CYCLING_STATIONARY = "Stationary cycling"
    override val DEF_MOUNTAIN_BIKING = "Mountain biking"
    override val DEF_ROAD_BIKING = "Road biking"
    override val DEF_HIKING = "Hiking"
    override val DEF_ROCK_CLIMBING = "Rock climbing"
    override val DEF_BOULDERING = "Bouldering"
    override val DEF_HIIT = "HIIT"
    override val DEF_ELLIPTICAL = "Elliptical"
    override val DEF_ROWING_MACHINE = "Rowing machine"
    override val DEF_STRENGTH_TRAINING = "Strength training"
    override val DEF_CALISTHENICS = "Calisthenics"
    override val DEF_YOGA = "Yoga"
    override val DEF_PILATES = "Pilates"
    override val DEF_AEROBICS = "Aerobics"
    override val DEF_DANCING = "Dancing"
    override val DEF_SWIMMING = "Swimming"
    override val DEF_SWIMMING_POOL = "Swimming (pool)"
    override val DEF_SWIMMING_OPEN_WATER = "Swimming (open water)"
    override val DEF_KAYAKING = "Kayaking"
    override val DEF_PADDLE_BOARDING = "Paddle boarding"
    override val DEF_SURFING = "Surfing"
    override val DEF_SAILING = "Sailing"
    override val DEF_FOOTBALL = "Football"
    override val DEF_BASKETBALL = "Basketball"
    override val DEF_TENNIS = "Tennis"
    override val DEF_SQUASH = "Squash"
    override val DEF_VOLLEYBALL = "Volleyball"
    override val DEF_GOLF = "Golf"
    override val DEF_MARTIAL_ARTS = "Martial arts"
    override val DEF_SKIING = "Skiing"
    override val DEF_SNOWBOARDING = "Snowboarding"
    override val DEF_SKATING = "Skating"
    override val DEF_ICE_SKATING = "Ice skating"
    
    override val DEF_GYM = "Gym"
    override val DEF_BASEBALL = "Baseball"
    override val DEF_SKATEBOARDING = "Skateboarding"
    override val DEF_COMPETITION = "Competition"
    override val DEF_STOPWATCH = "Stopwatch"
    override val DEF_OTHER = "Other"
    override val DEF_STANDARD_ACTIVITY = "Standard activity"

    // Heart Rate Math
    override val HR_NO_DATA = "No HR data"
    override val HR_TOO_LITTLE_DATA = "Too little data"
    override val HR_BELOW_ZONES = "HR below zones"
    override val HR_EFFECT_Z0 = "Low intensity / Warm up"
    override val HR_EFFECT_Z1 = "Aerobic base and recovery"
    override val HR_EFFECT_Z2 = "Effective fat burn"
    override val HR_EFFECT_Z3 = "Aerobic capacity improvement"
    override val HR_EFFECT_Z4 = "Lactate threshold increase"
    override val HR_EFFECT_Z5 = "Analobic training and VO2 Max"
    override val HR_EFFECT_NONE = "No dominant zone"

    // HR Zones Names
    override val ZONE_Z0 = "Warm up"
    override val ZONE_Z1 = "Very light"
    override val ZONE_Z2 = "Light"
    override val ZONE_Z3 = "Moderate"
    override val ZONE_Z4 = "Hard"
    override val ZONE_Z5 = "Maximum"

    // Compare Screen
    override val COMPARE_TITLE = "Activity Comparison"
    override val COMPARE_VS = "Comparison:"
    override val COMPARE_HIGHER_IS_BETTER = "Higher score is better"
    override val COMPARE_LOWER_IS_BETTER = "Lower score is better"

    // ViewModels Messages
    override val VM_EXPORT_INITIALIZING = "Initializing export..."
    override fun vmExportGenerating(name: String, current: Int, total: Int) = "Generating: $name ($current/$total)"
    override val VM_EXPORT_NO_FILES = "No files generated."
    override val VM_EXPORT_ZIPPING = "Zipping to ZIP..."
    override fun vmExportError(msg: String) = "Export error: $msg"
    override val VM_IMPORT_OPEN_ERROR = "Cannot open file"
    override val VM_IMPORT_DUPLICATE_WARNING = "Potential duplicate detected (same start date and duration)."
    override val VM_IMPORT_DUPLICATE_WARNING_DESC = "Do you want to continue anyway?"
    override val VM_IMPORT_SUCCESS = "Workout imported successfully."
    override fun vmImportError(msg: String) = "Import error: $msg"

    // Gpx Importer
    override val GPX_NO_POINTS = "GPX file contains no track points."
    override val GPX_WARN_HR = "File contains HR data, but selected activity does not support it."
    override val GPX_WARN_ELE = "File contains elevation data, but selected activity does not support it."
    override val GPX_WARN_CADENCE = "File contains cadence data, but selected activity does not support it."

    // Periods
    override val PERIOD_TODAY = "Today"
    override val PERIOD_WEEK = "Week"
    override val PERIOD_MONTH = "Month"
    override val PERIOD_YEAR = "Year"
    override val PERIOD_CUSTOM = "Other"
    override fun periodCustomDays(days: Int) = if (days == 1) "$days day" else "$days days"

    // Widgets
    override val WIDGET_COUNT = "Activity count"
    override val WIDGET_CALORIES = "Calories burned"
    override val WIDGET_DISTANCE_GPS = "Distance (GPS)"
    override val WIDGET_DISTANCE_STEPS = "Distance (steps)"
    override val WIDGET_ASCENT = "Total Ascent"
    override val WIDGET_DESCENT = "Total Descent"
    override val WIDGET_STEPS = "Steps"
    override val WIDGET_AVG_BPM = "Average HR"
    override val WIDGET_AVG_CADENCE = "Average cadence"
    override val WIDGET_MAX_SPEED = "Max speed"
    override val WIDGET_MAX_ALTITUDE = "Max altitude"
    override val WIDGET_MAX_ELEVATION_GAIN = "Most elevation gain"
    override val WIDGET_MAX_DISTANCE = "Longest distance"
    override val WIDGET_MAX_DURATION = "Longest duration"
    override val WIDGET_MAX_CALORIES = "Most calories"
    override val WIDGET_MAX_AVG_CADENCE = "Highest avg cadence"
    override val WIDGET_MAX_AVG_SPEED = "Highest avg speed"
    override val WIDGET_DURATION = "Duration"
    override val WIDGET_MAX_BPM = "Maximum HR"
    override val WIDGET_TOTAL_CALORIES = "Calories burned"
    override val WIDGET_MAX_CALORIES_MIN = "Max calorie burn"
    override val WIDGET_AVG_PACE = "Average pace"
    override val WIDGET_AVG_SPEED_GPS = "Average speed (GPS)"
    override val WIDGET_AVG_SPEED_STEPS = "Average speed (steps)"
    override val WIDGET_MAX_ALTITUDE_DESC = "Max altitude"
    override val WIDGET_TOTAL_ASCENT = "Total ascent"
    override val WIDGET_TOTAL_DESCENT = "Total descent"
    override val WIDGET_AVG_STEP_LENGTH = "Calculated step length"
    override val WIDGET_AVG_CADENCE_DESC = "Avg cadence"
    override val WIDGET_MAX_CADENCE = "Max cadence"
    override val WIDGET_TOTAL_STEPS = "Step count"
    override val WIDGET_PRESSURE_START = "Atm. pressure (start)"
    override val WIDGET_PRESSURE_END = "Atm. pressure (end)"
    override val WIDGET_MAX_PRESSURE = "Max pressure"
    override val WIDGET_MIN_PRESSURE = "Min pressure"
    override val WIDGET_BEST_PACE_1KM = "Best pace (1km)"
    override val WIDGET_WATCH_ASCENT = "Elevation gain up"
    override val WIDGET_WATCH_DESCENT = "Elevation gain down"

    // Sensors
    override val SENSOR_HEART_RATE = "Heart Rate"
    override val SENSOR_CALORIES_SUM = "Calories burned"
    override val SENSOR_CALORIES_MIN = "Calories per minute"
    override val SENSOR_STEPS = "Steps"
    override val SENSOR_STEPS_MIN = "Cadence (steps/min)"
    override val SENSOR_DISTANCE_STEPS = "Distance (steps)"
    override val SENSOR_SPEED_GPS = "Speed"
    override val SENSOR_SPEED_STEPS = "Speed (steps)"
    override val SENSOR_DISTANCE_GPS = "Distance"
    override val SENSOR_ALTITUDE = "Altitude"
    override val SENSOR_TOTAL_ASCENT = "Total ascent"
    override val SENSOR_TOTAL_DESCENT = "Total descent"
    override val SENSOR_PRESSURE = "Atm. pressure"
    override val SENSOR_MAP = "Location data"
    override val SENSOR_AVG_STEP_LENGTH = "Average step length"

    // Trim Screen
    override val TRIM_TITLE = "Edit Workout (Trimming)"
    override val TRIM_CONFIRM_TITLE = "Confirm trimming"
    override val TRIM_CONFIRM_DESC = "Are you sure you want to delete data outside selected range? This data will be permanently removed."
    override val TRIM_SAVE_BTN = "Trim and save"
    override val TRIM_CHART_HR = "Heart rate chart"
    override val TRIM_RANGE_TITLE = "Select workout range"
    override val TRIM_PREVIEW_TITLE = "New statistics preview"
    override val TRIM_NEW_DURATION = "New duration:"
    override val TRIM_DISTANCE_GPS = "Distance (GPS):"
    override val TRIM_DISTANCE_STEPS = "Distance (Steps):"
    override val TRIM_CALORIES = "Calories burned:"
    override val TRIM_AVG_BPM = "Average HR:"
    override val TRIM_START = "Start"
    override val TRIM_END = "Finish"

    // Units
    override val UNIT_KCAL = "kcal"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_STEP_MIN = "st/min"
    override val UNIT_KM_H = "km/h"
    override val UNIT_STEPS = "steps"
    override val UNIT_HPA = "hPa"
    override val UNIT_MIN_KM = "min/km"
    override val UNIT_M_ASL = "m a.s.l."
    override val UNIT_MIN_KM_LABEL = "min/km"
    override val UNIT_BPM = "bpm"
    override val UNIT_KCAL_MIN = "kcal/min"
    override val UNIT_VO2_MAX = "ml/kg/min"

    // Activity Detail Settings
    override val AD_SETTINGS_LIST_TITLE = "Select activity to modify"
    override val AD_SETTINGS_EDIT_TITLE = "Settings"
    override val AD_SETTINGS_SECTION_WIDGETS = "Section: Widgets"
    override val AD_SETTINGS_SECTION_CHARTS = "Section: Charts"

    override fun getWidgetLabel(id: String): String {
        return when (id) {
            "count" -> WIDGET_COUNT
            "calories" -> WIDGET_CALORIES
            "distanceGps" -> WIDGET_DISTANCE_GPS
            "distanceSteps" -> WIDGET_DISTANCE_STEPS
            "ascent" -> WIDGET_ASCENT
            "descent" -> WIDGET_DESCENT
            "steps" -> WIDGET_STEPS
            "avg_cadence" -> WIDGET_AVG_CADENCE
            "max_speed" -> WIDGET_MAX_SPEED
            "max_altitude" -> WIDGET_MAX_ALTITUDE
            "max_elevation_gain" -> WIDGET_MAX_ELEVATION_GAIN
            "max_distance" -> WIDGET_MAX_DISTANCE
            "max_duration" -> WIDGET_MAX_DURATION
            "max_calories" -> WIDGET_MAX_CALORIES
            "max_avg_cadence" -> WIDGET_MAX_AVG_CADENCE
            "max_avg_speed" -> WIDGET_MAX_AVG_SPEED
            "duration" -> WIDGET_DURATION
            "max_bpm" -> WIDGET_MAX_BPM
            "avg_bpm" -> WIDGET_AVG_BPM
            "total_calories" -> WIDGET_TOTAL_CALORIES
            "max_calories_min" -> WIDGET_MAX_CALORIES_MIN
            "avg_pace" -> WIDGET_AVG_PACE
            "avg_speed_gps" -> WIDGET_AVG_SPEED_GPS
            "avg_speed_steps" -> WIDGET_AVG_SPEED_STEPS
            "max_altitude_desc" -> WIDGET_MAX_ALTITUDE_DESC
            "total_ascent" -> WIDGET_TOTAL_ASCENT
            "total_descent" -> WIDGET_TOTAL_DESCENT
            "avg_step_length" -> WIDGET_AVG_STEP_LENGTH
            "avg_cadence_desc" -> WIDGET_AVG_CADENCE_DESC
            "max_cadence" -> WIDGET_MAX_CADENCE
            "total_steps" -> WIDGET_TOTAL_STEPS
            "pressure_start" -> WIDGET_PRESSURE_START
            "pressure_end" -> WIDGET_PRESSURE_END
            "max_pressure" -> WIDGET_MAX_PRESSURE
            "min_pressure" -> WIDGET_MIN_PRESSURE
            "best_pace_1km" -> WIDGET_BEST_PACE_1KM
            "total_distance_gps" -> WIDGET_DISTANCE_GPS
            "total_distance_steps" -> WIDGET_DISTANCE_STEPS
            "maxPressure" -> WIDGET_MAX_PRESSURE
            "minPressure" -> WIDGET_MIN_PRESSURE
            "bestPace1km" -> WIDGET_BEST_PACE_1KM
            else -> getSensorLabel(id)
        }
    }
    override fun getSensorLabel(id: String): String {
        return when (id) {
            "bpm" -> SENSOR_HEART_RATE
            "calorieSum" -> SENSOR_CALORIES_SUM
            "calorieMin" -> SENSOR_CALORIES_MIN
            "kalorie_min" -> SENSOR_CALORIES_MIN
            "calories" -> WIDGET_CALORIES
            "steps" -> SENSOR_STEPS
            "stepsMin" -> SENSOR_STEPS_MIN
            "kroki_min" -> SENSOR_STEPS_MIN
            "distanceSteps" -> SENSOR_DISTANCE_STEPS
            "odl_kroki" -> SENSOR_DISTANCE_STEPS
            "speedGps" -> SENSOR_SPEED_GPS
            "predkosc" -> SENSOR_SPEED_GPS
            "speedSteps" -> SENSOR_SPEED_STEPS
            "predkosc_kroki" -> SENSOR_SPEED_STEPS
            "distanceGps" -> SENSOR_DISTANCE_GPS
            "gps_dystans" -> SENSOR_DISTANCE_GPS
            "altitude" -> SENSOR_ALTITUDE
            "wysokosc" -> SENSOR_ALTITUDE
            "totalAscent" -> SENSOR_TOTAL_ASCENT
            "przewyzszenia_gora" -> SENSOR_TOTAL_ASCENT
            "ascent" -> WIDGET_ASCENT
            "totalDescent" -> SENSOR_TOTAL_DESCENT
            "przewyzszenia_dol" -> SENSOR_TOTAL_DESCENT
            "descent" -> WIDGET_DESCENT
            "pressure" -> SENSOR_PRESSURE
            "map" -> SENSOR_MAP
            "maxPressure" -> WIDGET_MAX_PRESSURE
            "minPressure" -> WIDGET_MIN_PRESSURE
            "bestPace1km" -> WIDGET_BEST_PACE_1KM
            "avg_cadence" -> WIDGET_AVG_CADENCE
            "avg_step_length_over_time" -> SENSOR_AVG_STEP_LENGTH
            else -> id
        }
    }

    // Strava Strings
    override val STRAVA_TITLE = "Strava Synchronization"
    override val STRAVA_CONNECT = "Connect to Strava"
    override val STRAVA_DISCONNECT = "Disconnect Strava"
    override val STRAVA_CONNECTED = "Connected to Strava"
    override val STRAVA_NOT_CONNECTED = "Not connected"
    override val STRAVA_SYNC_NOW = "Sync now"
    override val STRAVA_SYNC_SUCCESS = "Workout uploaded!"
    override val STRAVA_SYNC_FAILED = "Upload failed"
    override val STRAVA_SYNCING = "Uploading..."
    override val STRAVA_AUTH_ERROR = "Authorization error"
}
