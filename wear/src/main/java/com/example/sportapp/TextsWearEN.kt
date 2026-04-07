package com.example.sportapp

object TextsWearEN {
    // Main Menu
    const val MENU_SPORT = "Sport"
    const val MENU_STATISTICS = "Statistics"
    const val MENU_SETTINGS = "Settings"
    const val APP_LOGO_DESC = "SportApp Logo"

    // Choose Sport
    const val CHOOSE_SPORT_TITLE = "Choose Sport"
    const val CHOOSE_SPORT_NO_DEFINITIONS = "No activity definitions. Define them in the phone app."
    const val CHOOSE_SPORT_DEFAULT_NAME = "Standard Activity"

    // Statistics
    const val STATS_NO_WIDGETS = "No fields selected"
    const val STATS_PERIOD_TODAY = "Today"
    const val STATS_PERIOD_7_DAYS = "Last 7 days"
    const val STATS_PERIOD_30_DAYS = "Last 30 days"
    const val STATS_PERIOD_YEAR = "Last year"
    fun statsPeriodCustom(days: Int) = "Last $days days"

    const val STATS_WIDGET_COUNT = "Activity Count"
    const val STATS_WIDGET_DISTANCE_GPS = "Distance (GPS)"
    const val STATS_WIDGET_STEPS_ALL = "All Steps"

    // Workout Ready
    const val WORKOUT_READY_START = "Start"
    const val WORKOUT_READY_BACK = "Back"

    // Settings
    const val SETTINGS_TITLE = "Settings"
    const val SETTINGS_HEALTH_DATA = "Health Data"
    const val SETTINGS_SCREEN = "Screen"
    const val SETTINGS_SCREEN_ALWAYS_ON = "Always On"
    const val SETTINGS_SCREEN_AMBIENT = "Ambient Mode"
    const val SETTINGS_SCREEN_AUTO = "Automatic Mode"
    const val SETTINGS_CLOCK_COLOR = "Clock Color"
    const val SETTINGS_SCREEN_BEHAVIOR_TITLE = "Screen Behavior"
    
    // Colors
    const val COLOR_RED = "Red"
    const val COLOR_WHITE = "White"
    const val COLOR_GREEN = "Green"
    const val COLOR_YELLOW = "Yellow"
    const val COLOR_BLUE = "Blue"
    const val COLOR_BLACK = "Black"
    const val COLOR_NONE = "None"
    const val COLOR_CUSTOM = "Custom"

    // Health Data
    const val HEALTH_GENDER = "Gender"
    const val HEALTH_AGE = "Age"
    const val HEALTH_WEIGHT = "Weight"
    const val HEALTH_HEIGHT = "Height"
    const val HEALTH_STEP_LENGTH = "Step Length"
    const val HEALTH_RESTING_HR = "Resting HR"
    const val HEALTH_MAX_HR = "Max HR"
    const val HEALTH_SAVE = "Save"
    const val HEALTH_CHOOSE_GENDER = "Select Gender"
    const val GENDER_MALE = "Male"
    const val GENDER_FEMALE = "Female"
    
    fun healthAgeValue(age: Int) = "$age years"
    fun healthWeightValue(weight: Int) = "$weight kg"
    fun healthHeightValue(height: Int) = "$height cm"
    fun healthStepLengthValue(length: Int) = "$length cm"
    fun healthHRValue(hr: Int) = "$hr BPM"

    // Units
    const val UNIT_YEARS = "years"
    const val UNIT_KG = "kg"
    const val UNIT_CM = "cm"
    const val UNIT_BPM = "BPM"
    const val UNIT_M = "m"
    const val UNIT_KM = "km"
    const val UNIT_KMH = "km/h"
    const val UNIT_KCAL = "kcal"
    const val UNIT_HPA = "hPa"

    // Workout Data / Labels
    const val WORKOUT_ERROR_CONFIG = "Configuration Error"
    const val WORKOUT_LABEL_TIMER = "ACTIVITY TIME"
    const val WORKOUT_LABEL_STEPS = "Steps"
    const val WORKOUT_LABEL_DISTANCE = "Distance"
    const val WORKOUT_LABEL_SPEED = "Speed"
    const val WORKOUT_LABEL_HR = "Heart Rate"
    const val WORKOUT_LABEL_PRESSURE = "Pressure"
    const val WORKOUT_LABEL_ALTITUDE = "Altitude"

    // Sensors Names (for WorkoutDefinition)
    const val SENSOR_HEART_RATE = "Heart Rate"
    const val SENSOR_CALORIES_SUM = "Calories Burned"
    const val SENSOR_CALORIES_MIN = "Calories per Minute"
    const val SENSOR_STEPS = "Steps"
    const val SENSOR_STEPS_MIN = "Cadence (steps/min)"
    const val SENSOR_DISTANCE_STEPS = "Distance (steps)"
    const val SENSOR_SPEED_GPS = "Speed"
    const val SENSOR_SPEED_STEPS = "Speed (steps)"
    const val SENSOR_DISTANCE_GPS = "Distance"
    const val SENSOR_ALTITUDE = "Altitude"
    const val SENSOR_TOTAL_ASCENT = "Total Ascent"
    const val SENSOR_TOTAL_DESCENT = "Total Descent"
    const val SENSOR_PRESSURE = "Atm. Pressure"
    const val SENSOR_MAP = "Location Data"

    // Workout Controls
    const val WORKOUT_RESUME = "Resume"
    const val WORKOUT_PAUSE = "Pause"
    const val WORKOUT_FINISH = "Finish"
    const val WORKOUT_START = "Start"
    const val WORKOUT_STOP = "Stop"
    const val WORKOUT_READY_MSG = "Use DynamicWorkoutScreen"

    // Summary
    const val SUMMARY_TITLE = "SUMMARY"
    const val SUMMARY_CONFIRM_DESC = "Confirm"
    const val SUMMARY_DURATION = "Duration"
    const val SUMMARY_AVG_HR = "Avg Heart Rate"
    const val SUMMARY_MAX_HR = "Max Heart Rate"
    const val SUMMARY_AVG_SPEED = "Avg Speed"
    const val SUMMARY_MAX_SPEED = "Max Speed"
    const val SUMMARY_AVG_SPEED_STEPS = "Avg Speed (steps)"
    const val SUMMARY_MAX_SPEED_STEPS = "Max Speed (steps)"
    const val SUMMARY_DISTANCE = "Distance"
    const val SUMMARY_DISTANCE_STEPS = "Distance (steps)"
    const val SUMMARY_STEPS = "Steps"
    const val SUMMARY_TOTAL_ASCENT = "Total Ascent"
    const val SUMMARY_TOTAL_DESCENT = "Total Descent"
    const val SUMMARY_CALORIES = "Calories"

    // General
    const val GEN_ACTIVITY = "Activity"
    const val VAL_EMPTY = "--"

    // Complication / Tile
    const val COMP_MON = "Mon"
    const val COMP_TUE = "Tue"
    const val COMP_WED = "Wed"
    const val COMP_THU = "Thu"
    const val COMP_FRI = "Fri"
    const val COMP_SAT = "Sat"
    const val COMP_SUN = "Sun"
    
    const val COMP_MONDAY = "Monday"
    const val COMP_TUESDAY = "Tuesday"
    const val COMP_WEDNESDAY = "Wednesday"
    const val COMP_THURSDAY = "Thursday"
    const val COMP_FRIDAY = "Friday"
    const val COMP_SATURDAY = "Saturday"
    const val COMP_SUNDAY = "Sunday"

    const val TILE_HELLO = "Hello!"
}
