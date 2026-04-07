package com.example.sportapp

object TextsWearEN : WearTexts {
    // Main Menu
    override val MENU_SPORT = "Sport"
    override val MENU_STATISTICS = "Statistics"
    override val MENU_SETTINGS = "Settings"
    override val APP_LOGO_DESC = "SportApp Logo"

    // Choose Sport
    override val CHOOSE_SPORT_TITLE = "Choose Sport"
    override val CHOOSE_SPORT_NO_DEFINITIONS = "No activity definitions. Define them in the phone app."
    override val CHOOSE_SPORT_DEFAULT_NAME = "Standard Activity"

    // Statistics
    override val STATS_NO_WIDGETS = "No fields selected"
    override val STATS_PERIOD_TODAY = "Today"
    override val STATS_PERIOD_7_DAYS = "Last 7 days"
    override val STATS_PERIOD_30_DAYS = "Last 30 days"
    override val STATS_PERIOD_YEAR = "Last year"
    override fun statsPeriodCustom(days: Int) = "Last $days days"

    override val STATS_WIDGET_COUNT = "Activity Count"
    override val STATS_WIDGET_CALORIES = "Calories Burned"
    override val STATS_WIDGET_DISTANCE_GPS = "Distance (GPS)"
    override val STATS_WIDGET_DISTANCE_STEPS = "Distance (steps)"
    override val STATS_WIDGET_ASCENT = "Total Ascent"
    override val STATS_WIDGET_DESCENT = "Total Descent"
    override val STATS_WIDGET_STEPS_ALL = "Total Steps"

    override val STATS_WIDGET_MAX_SPEED = "Max Speed"
    override val STATS_WIDGET_MAX_ALTITUDE = "Max Altitude"
    override val STATS_WIDGET_MAX_ELEVATION_GAIN = "Max Elevation Gain"
    override val STATS_WIDGET_MAX_DISTANCE = "Max Distance"
    override val STATS_WIDGET_MAX_DURATION = "Max Duration"
    override val STATS_WIDGET_MAX_CALORIES = "Max Calories Burned"
    override val STATS_WIDGET_MAX_AVG_CADENCE = "Max Avg Cadence"
    override val STATS_WIDGET_MAX_AVG_SPEED = "Max Avg Speed"

    // Workout Ready
    override val WORKOUT_READY_START = "Start"
    override val WORKOUT_READY_BACK = "Back"

    // Settings
    override val SETTINGS_TITLE = "Settings"
    override val SETTINGS_HEALTH_DATA = "Health Data"
    override val SETTINGS_SCREEN = "Screen"
    override val SETTINGS_SCREEN_ALWAYS_ON = "Always On"
    override val SETTINGS_SCREEN_AMBIENT = "Ambient Mode"
    override val SETTINGS_SCREEN_AUTO = "Automatic Mode"
    override val SETTINGS_CLOCK_COLOR = "Clock Color"
    override val SETTINGS_SCREEN_BEHAVIOR_TITLE = "Screen Behavior"
    override val SETTINGS_LANGUAGE = "Language"
    override val SETTINGS_LANGUAGE_SELECTION_TITLE = "Select Language"
    
    // Colors
    override val COLOR_RED = "Red"
    override val COLOR_WHITE = "White"
    override val COLOR_GREEN = "Green"
    override val COLOR_YELLOW = "Yellow"
    override val COLOR_BLUE = "Blue"
    override val COLOR_BLACK = "Black"
    override val COLOR_NONE = "None"
    override val COLOR_CUSTOM = "Custom"

    // Health Data
    override val HEALTH_GENDER = "Gender"
    override val HEALTH_AGE = "Age"
    override val HEALTH_WEIGHT = "Weight"
    override val HEALTH_HEIGHT = "Height"
    override val HEALTH_STEP_LENGTH = "Step Length"
    override val HEALTH_RESTING_HR = "Resting HR"
    override val HEALTH_MAX_HR = "Max HR"
    override val HEALTH_SAVE = "Save"
    override val HEALTH_CHOOSE_GENDER = "Select Gender"
    override val GENDER_MALE = "Male"
    override val GENDER_FEMALE = "Female"
    
    override fun healthAgeValue(age: Int) = "$age years"
    override fun healthWeightValue(weight: Int) = "$weight kg"
    override fun healthHeightValue(height: Int) = "$height cm"
    override fun healthStepLengthValue(length: Int) = "$length cm"
    override fun healthHRValue(hr: Int) = "$hr BPM"

    // Units
    override val UNIT_YEARS = "years"
    override val UNIT_KG = "kg"
    override val UNIT_CM = "cm"
    override val UNIT_BPM = "BPM"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_KMH = "km/h"
    override val UNIT_KCAL = "kcal"
    override val UNIT_HPA = "hPa"

    // Workout Data / Labels
    override val WORKOUT_ERROR_CONFIG = "Configuration Error"
    override val WORKOUT_LABEL_TIMER = "ACTIVITY TIME"
    override val WORKOUT_LABEL_STEPS = "Steps"
    override val WORKOUT_LABEL_DISTANCE = "Distance"
    override val WORKOUT_LABEL_SPEED = "Speed"
    override val WORKOUT_LABEL_HR = "Heart Rate"
    override val WORKOUT_LABEL_PRESSURE = "Pressure"
    override val WORKOUT_LABEL_ALTITUDE = "Altitude"

    // Sensors Names (for WorkoutDefinition)
    override val SENSOR_HEART_RATE = "Heart Rate"
    override val SENSOR_CALORIES_SUM = "Calories Burned"
    override val SENSOR_CALORIES_MIN = "Calories per Minute"
    override val SENSOR_STEPS = "Steps"
    override val SENSOR_STEPS_MIN = "Cadence (steps/min)"
    override val SENSOR_DISTANCE_STEPS = "Distance (steps)"
    override val SENSOR_SPEED_GPS = "Speed"
    override val SENSOR_SPEED_STEPS = "Speed (steps)"
    override val SENSOR_DISTANCE_GPS = "Distance"
    override val SENSOR_ALTITUDE = "Altitude"
    override val SENSOR_TOTAL_ASCENT = "Total Ascent"
    override val SENSOR_TOTAL_DESCENT = "Total Descent"
    override val SENSOR_PRESSURE = "Atm. Pressure"
    override val SENSOR_MAP = "Location Data"

    // Workout Controls
    override val WORKOUT_RESUME = "Resume"
    override val WORKOUT_PAUSE = "Pause"
    override val WORKOUT_FINISH = "Finish"
    override val WORKOUT_START = "Start"
    override val WORKOUT_STOP = "Stop"
    override val WORKOUT_READY_MSG = "Use DynamicWorkoutScreen"

    // Summary
    override val SUMMARY_TITLE = "SUMMARY"
    override val SUMMARY_CONFIRM_DESC = "Confirm"
    override val SUMMARY_DURATION = "Duration"
    override val SUMMARY_AVG_HR = "Avg Heart Rate"
    override val SUMMARY_MAX_HR = "Max Heart Rate"
    override val SUMMARY_AVG_SPEED = "Avg Speed"
    override val SUMMARY_MAX_SPEED = "Max Speed"
    override val SUMMARY_AVG_SPEED_STEPS = "Avg Speed (steps)"
    override val SUMMARY_MAX_SPEED_STEPS = "Max Speed (steps)"
    override val SUMMARY_DISTANCE = "Distance"
    override val SUMMARY_DISTANCE_STEPS = "Distance (steps)"
    override val SUMMARY_STEPS = "Steps"
    override val SUMMARY_TOTAL_ASCENT = "Total Ascent"
    override val SUMMARY_TOTAL_DESCENT = "Total Descent"
    override val SUMMARY_CALORIES = "Calories"

    // General
    override val GEN_ACTIVITY = "Activity"
    override val VAL_EMPTY = "--"

    // Complication / Tile
    override val COMP_MON = "Mon"
    override val COMP_TUE = "Tue"
    override val COMP_WED = "Wed"
    override val COMP_THU = "Thu"
    override val COMP_FRI = "Fri"
    override val COMP_SAT = "Sat"
    override val COMP_SUN = "Sun"
    
    override val COMP_MONDAY = "Monday"
    override val COMP_TUESDAY = "Tuesday"
    override val COMP_WEDNESDAY = "Wednesday"
    override val COMP_THURSDAY = "Thursday"
    override val COMP_FRIDAY = "Friday"
    override val COMP_SATURDAY = "Saturday"
    override val COMP_SUNDAY = "Sunday"

    override val TILE_HELLO = "Hello!"
}
