package com.example.sportapp

object TextsWearDE : WearTexts {
    // Main Menu
    override val MENU_SPORT = "Sport"
    override val MENU_STATISTICS = "Statistiken"
    override val MENU_SETTINGS = "Einstellungen"
    override val APP_LOGO_DESC = "SportApp Logo"

    // Choose Sport
    override val CHOOSE_SPORT_TITLE = "Sportart wählen"
    override val CHOOSE_SPORT_NO_DEFINITIONS = "Keine Aktivitätsdefinitionen. Definieren Sie diese in der Handy-App."
    override val CHOOSE_SPORT_DEFAULT_NAME = "Standardaktivität"

    // Statistics
    override val STATS_NO_WIDGETS = "Keine Felder ausgewählt"
    override val STATS_PERIOD_TODAY = "Heute"
    override val STATS_PERIOD_7_DAYS = "Letzte 7 Tage"
    override val STATS_PERIOD_30_DAYS = "Letzte 30 Tage"
    override val STATS_PERIOD_YEAR = "Letztes Jahr"
    override fun statsPeriodCustom(days: Int) = "Letzte $days Tage"

    override val STATS_WIDGET_COUNT = "Anzahl der Aktivitäten"
    override val STATS_WIDGET_CALORIES = "Verbrannte Kalorien"
    override val STATS_WIDGET_DISTANCE_GPS = "Distanz (GPS)"
    override val STATS_WIDGET_DISTANCE_STEPS = "Distanz (Schritte)"
    override val STATS_WIDGET_ASCENT = "Gesamtaufstieg"
    override val STATS_WIDGET_DESCENT = "Gesamtabstieg"
    override val STATS_WIDGET_STEPS_ALL = "Alle Schritte"

    override val STATS_WIDGET_MAX_SPEED = "Höchstgeschwindigkeit"
    override val STATS_WIDGET_MAX_ALTITUDE = "Maximale Höhe"
    override val STATS_WIDGET_MAX_ELEVATION_GAIN = "Maximaler Höhengewinn"
    override val STATS_WIDGET_MAX_DISTANCE = "Maximale Distanz"
    override val STATS_WIDGET_MAX_DURATION = "Maximale Dauer"
    override val STATS_WIDGET_MAX_CALORIES = "Maximaler Kalorienverbrauch"
    override val STATS_WIDGET_MAX_AVG_CADENCE = "Maximale ø Trittfrequenz"
    override val STATS_WIDGET_MAX_AVG_SPEED = "Maximale ø Geschwindigkeit"

    // Workout Ready
    override val WORKOUT_READY_START = "Start"
    override val WORKOUT_READY_BACK = "Zurück"

    // Settings
    override val SETTINGS_TITLE = "Einstellungen"
    override val SETTINGS_HEALTH_DATA = "Gesundheitsdaten"
    override val SETTINGS_SCREEN = "Bildschirm"
    override val SETTINGS_SCREEN_ALWAYS_ON = "Immer an"
    override val SETTINGS_SCREEN_AMBIENT = "Ambient-Modus"
    override val SETTINGS_SCREEN_AUTO = "Automatischer Modus"
    override val SETTINGS_CLOCK_COLOR = "Uhrenfarbe"
    override val SETTINGS_SCREEN_BEHAVIOR_TITLE = "Bildschirmverhalten"
    override val SETTINGS_LANGUAGE = "Sprache"
    override val SETTINGS_LANGUAGE_SELECTION_TITLE = "Sprache wählen"
    
    // Colors
    override val COLOR_RED = "Rot"
    override val COLOR_WHITE = "Weiß"
    override val COLOR_GREEN = "Grün"
    override val COLOR_YELLOW = "Gelb"
    override val COLOR_BLUE = "Blau"
    override val COLOR_BLACK = "Schwarz"
    override val COLOR_NONE = "Keine"
    override val COLOR_CUSTOM = "Benutzerdefiniert"

    // Health Data
    override val HEALTH_GENDER = "Geschlecht"
    override val HEALTH_AGE = "Alter"
    override val HEALTH_WEIGHT = "Gewicht"
    override val HEALTH_HEIGHT = "Größe"
    override val HEALTH_STEP_LENGTH = "Schrittlänge"
    override val HEALTH_RESTING_HR = "Ruhepuls"
    override val HEALTH_MAX_HR = "Maximalpuls"
    override val HEALTH_SAVE = "Speichern"
    override val HEALTH_CHOOSE_GENDER = "Geschlecht wählen"
    override val GENDER_MALE = "Männlich"
    override val GENDER_FEMALE = "Weiblich"
    
    override fun healthAgeValue(age: Int) = "$age Jahre"
    override fun healthWeightValue(weight: Int) = "$weight kg"
    override fun healthHeightValue(height: Int) = "$height cm"
    override fun healthStepLengthValue(length: Int) = "$length cm"
    override fun healthHRValue(hr: Int) = "$hr BPM"

    // Units
    override val UNIT_YEARS = "Jahre"
    override val UNIT_KG = "kg"
    override val UNIT_CM = "cm"
    override val UNIT_BPM = "BPM"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_KMH = "km/h"
    override val UNIT_KCAL = "kcal"
    override val UNIT_HPA = "hPa"

    // Workout Data / Labels
    override val WORKOUT_ERROR_CONFIG = "Konfigurationsfehler"
    override val WORKOUT_LABEL_TIMER = "AKTIVITÄTSZEIT"
    override val WORKOUT_LABEL_STEPS = "Schritte"
    override val WORKOUT_LABEL_DISTANCE = "Distanz"
    override val WORKOUT_LABEL_SPEED = "Geschwindigkeit"
    override val WORKOUT_LABEL_HR = "Herzfrequenz"
    override val WORKOUT_LABEL_PRESSURE = "Druck"
    override val WORKOUT_LABEL_ALTITUDE = "Höhe"

    // Sensors Names (for WorkoutDefinition)
    override val SENSOR_HEART_RATE = "Herzfrequenz"
    override val SENSOR_CALORIES_SUM = "Verbrannte Kalorie"
    override val SENSOR_CALORIES_MIN = "Kalorien pro Minute"
    override val SENSOR_STEPS = "Schritte"
    override val SENSOR_STEPS_MIN = "Trittfrequenz (Schritte/Min)"
    override val SENSOR_DISTANCE_STEPS = "Distanz (Schritte)"
    override val SENSOR_SPEED_GPS = "Geschwindigkeit"
    override val SENSOR_SPEED_STEPS = "Geschwindigkeit (Schritte)"
    override val SENSOR_DISTANCE_GPS = "Distanz"
    override val SENSOR_ALTITUDE = "Höhe"
    override val SENSOR_TOTAL_ASCENT = "Gesamtaufstieg"
    override val SENSOR_TOTAL_DESCENT = "Gesamtabstieg"
    override val SENSOR_PRESSURE = "Luftdruck"
    override val SENSOR_MAP = "Standortdaten"

    // Workout Controls
    override val WORKOUT_RESUME = "Fortsetzen"
    override val WORKOUT_PAUSE = "Pause"
    override val WORKOUT_FINISH = "Beenden"
    override val WORKOUT_START = "Start"
    override val WORKOUT_STOP = "Stopp"
    override val WORKOUT_READY_MSG = "DynamicWorkoutScreen verwenden"

    // Summary
    override val SUMMARY_TITLE = "ZUSAMMENFASSUNG"
    override val SUMMARY_CONFIRM_DESC = "Bestätigen"
    override val SUMMARY_DURATION = "Dauer"
    override val SUMMARY_AVG_HR = "ø Herzfrequenz"
    override val SUMMARY_MAX_HR = "Maximale Herzfrequenz"
    override val SUMMARY_AVG_SPEED = "ø Geschwindigkeit"
    override val SUMMARY_MAX_SPEED = "Höchstgeschwindigkeit"
    override val SUMMARY_AVG_SPEED_STEPS = "ø Geschwindigkeit (Schritte)"
    override val SUMMARY_MAX_SPEED_STEPS = "Höchstgeschwindigkeit (Schritte)"
    override val SUMMARY_DISTANCE = "Distanz"
    override val SUMMARY_DISTANCE_STEPS = "Distanz (Schritte)"
    override val SUMMARY_STEPS = "Schritte"
    override val SUMMARY_TOTAL_ASCENT = "Gesamtaufstieg"
    override val SUMMARY_TOTAL_DESCENT = "Gesamtabstieg"
    override val SUMMARY_CALORIES = "Kalorien"

    // General
    override val GEN_ACTIVITY = "Aktivität"
    override val VAL_EMPTY = "--"

    // Complication / Tile
    override val COMP_MON = "Mo"
    override val COMP_TUE = "Di"
    override val COMP_WED = "Mi"
    override val COMP_THU = "Do"
    override val COMP_FRI = "Fr"
    override val COMP_SAT = "Sa"
    override val COMP_SUN = "So"
    
    override val COMP_MONDAY = "Montag"
    override val COMP_TUESDAY = "Dienstag"
    override val COMP_WEDNESDAY = "Mittwoch"
    override val COMP_THURSDAY = "Donnerstag"
    override val COMP_FRIDAY = "Freitag"
    override val COMP_SATURDAY = "Samstag"
    override val COMP_SUNDAY = "Sonntag"

    override val TILE_HELLO = "Hallo!"
}
