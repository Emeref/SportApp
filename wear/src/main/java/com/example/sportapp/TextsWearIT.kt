package com.example.sportapp

object TextsWearIT : WearTexts {
    // Main Menu
    override val MENU_SPORT = "Sport"
    override val MENU_STATISTICS = "Statistiche"
    override val MENU_SETTINGS = "Impostazioni"
    override val APP_LOGO_DESC = "SportApp Logo"

    // Choose Sport
    override val CHOOSE_SPORT_TITLE = "Scegli sport"
    override val CHOOSE_SPORT_NO_DEFINITIONS = "Nessuna definizione di attività. Definiscile nell'app del telefono."
    override val CHOOSE_SPORT_DEFAULT_NAME = "Attività standard"

    // Statistics
    override val STATS_NO_WIDGETS = "Nessun campo selezionato"
    override val STATS_PERIOD_TODAY = "Oggi"
    override val STATS_PERIOD_7_DAYS = "Ultimi 7 giorni"
    override val STATS_PERIOD_30_DAYS = "Ultimi 30 giorni"
    override val STATS_PERIOD_YEAR = "Ultimo anno"
    override fun statsPeriodCustom(days: Int) = "Ultimi $days giorni"

    override val STATS_WIDGET_COUNT = "Numero attività"
    override val STATS_WIDGET_CALORIES = "Calorie bruciate"
    override val STATS_WIDGET_DISTANCE_GPS = "Distanza (GPS)"
    override val STATS_WIDGET_DISTANCE_STEPS = "Distanza (passi)"
    override val STATS_WIDGET_ASCENT = "Dislivello positivo"
    override val STATS_WIDGET_DESCENT = "Dislivello negativo"
    override val STATS_WIDGET_STEPS_ALL = "Tutti i passi"

    override val STATS_WIDGET_MAX_SPEED = "Velocità massima"
    override val STATS_WIDGET_MAX_ALTITUDE = "Altitudine massima"
    override val STATS_WIDGET_MAX_ELEVATION_GAIN = "Massimo dislivello"
    override val STATS_WIDGET_MAX_DISTANCE = "Distanza massima"
    override val STATS_WIDGET_MAX_DURATION = "Durata massima"
    override val STATS_WIDGET_MAX_CALORIES = "Massimo consumo calorie"
    override val STATS_WIDGET_MAX_AVG_CADENCE = "Cadenza media massima"
    override val STATS_WIDGET_MAX_AVG_SPEED = "Velocità media massima"

    // Workout Ready
    override val WORKOUT_READY_START = "Inizia"
    override val WORKOUT_READY_BACK = "Indietro"

    // Settings
    override val SETTINGS_TITLE = "Impostazioni"
    override val SETTINGS_HEALTH_DATA = "Dati salute"
    override val SETTINGS_SCREEN = "Schermo"
    override val SETTINGS_SCREEN_ALWAYS_ON = "Sempre acceso"
    override val SETTINGS_SCREEN_AMBIENT = "Modalità Ambient"
    override val SETTINGS_SCREEN_AUTO = "Modalità automatica"
    override val SETTINGS_CLOCK_COLOR = "Colore orologio"
    override val SETTINGS_SCREEN_BEHAVIOR_TITLE = "Comportamento schermo"
    override val SETTINGS_LANGUAGE = "Lingua"
    override val SETTINGS_LANGUAGE_SELECTION_TITLE = "Scegli lingua"
    
    // Colors
    override val COLOR_RED = "Rosso"
    override val COLOR_WHITE = "Bianco"
    override val COLOR_GREEN = "Verde"
    override val COLOR_YELLOW = "Giallo"
    override val COLOR_BLUE = "Blu"
    override val COLOR_BLACK = "Nero"
    override val COLOR_NONE = "Nessuno"
    override val COLOR_CUSTOM = "Personalizzato"

    // Health Data
    override val HEALTH_GENDER = "Genere"
    override val HEALTH_AGE = "Età"
    override val HEALTH_WEIGHT = "Peso"
    override val HEALTH_HEIGHT = "Altezza"
    override val HEALTH_STEP_LENGTH = "Lunghezza passo"
    override val HEALTH_RESTING_HR = "FC a riposo"
    override val HEALTH_MAX_HR = "FC massima"
    override val HEALTH_SAVE = "Salva"
    override val HEALTH_CHOOSE_GENDER = "Scegli genere"
    override val GENDER_MALE = "Uomo"
    override val GENDER_FEMALE = "Donna"
    
    override fun healthAgeValue(age: Int) = "$age anni"
    override fun healthWeightValue(weight: Int) = "$weight kg"
    override fun healthHeightValue(height: Int) = "$height cm"
    override fun healthStepLengthValue(length: Int) = "$length cm"
    override fun healthHRValue(hr: Int) = "$hr BPM"

    // Units
    override val UNIT_YEARS = "anni"
    override val UNIT_KG = "kg"
    override val UNIT_CM = "cm"
    override val UNIT_BPM = "BPM"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_KMH = "km/h"
    override val UNIT_KCAL = "kcal"
    override val UNIT_HPA = "hPa"

    // Workout Data / Labels
    override val WORKOUT_ERROR_CONFIG = "Errore configurazione"
    override val WORKOUT_LABEL_TIMER = "TEMPO ATTIVITÀ"
    override val WORKOUT_LABEL_STEPS = "Passi"
    override val WORKOUT_LABEL_DISTANCE = "Distanza"
    override val WORKOUT_LABEL_SPEED = "Velocità"
    override val WORKOUT_LABEL_HR = "Freq. cardiaca"
    override val WORKOUT_LABEL_PRESSURE = "Pressione"
    override val WORKOUT_LABEL_ALTITUDE = "Altitudine"

    // Sensors Names (for WorkoutDefinition)
    override val SENSOR_HEART_RATE = "Freq. cardiaca"
    override val SENSOR_CALORIES_SUM = "Calorie bruciate"
    override val SENSOR_CALORIES_MIN = "Calorie al minuto"
    override val SENSOR_STEPS = "Passi"
    override val SENSOR_STEPS_MIN = "Cadenza (passi/min)"
    override val SENSOR_DISTANCE_STEPS = "Dystans (passi)"
    override val SENSOR_SPEED_GPS = "Velocità"
    override val SENSOR_SPEED_STEPS = "Velocità (passi)"
    override val SENSOR_DISTANCE_GPS = "Distanza"
    override val SENSOR_ALTITUDE = "Altitudine"
    override val SENSOR_TOTAL_ASCENT = "Dislivello positivo"
    override val SENSOR_TOTAL_DESCENT = "Dislivello negativo"
    override val SENSOR_PRESSURE = "Pressione atm."
    override val SENSOR_MAP = "Dati posizione"

    // Workout Controls
    override val WORKOUT_RESUME = "Riprendi"
    override val WORKOUT_PAUSE = "Pausa"
    override val WORKOUT_FINISH = "Termina"
    override val WORKOUT_START = "Inizia"
    override val WORKOUT_STOP = "Ferma"
    override val WORKOUT_READY_MSG = "Usa DynamicWorkoutScreen"

    // Summary
    override val SUMMARY_TITLE = "RIEPILOGO"
    override val SUMMARY_CONFIRM_DESC = "Conferma"
    override val SUMMARY_DURATION = "Durata"
    override val SUMMARY_AVG_HR = "FC media"
    override val SUMMARY_MAX_HR = "FC massima"
    override val SUMMARY_AVG_SPEED = "Velocità media"
    override val SUMMARY_MAX_SPEED = "Velocità massima"
    override val SUMMARY_AVG_SPEED_STEPS = "Velocità media (passi)"
    override val SUMMARY_MAX_SPEED_STEPS = "Velocità massima (passi)"
    override val SUMMARY_DISTANCE = "Distanza"
    override val SUMMARY_DISTANCE_STEPS = "Distanza (passi)"
    override val SUMMARY_STEPS = "Passi"
    override val SUMMARY_TOTAL_ASCENT = "Dislivello positivo"
    override val SUMMARY_TOTAL_DESCENT = "Dislivello negativo"
    override val SUMMARY_CALORIES = "Calorie"

    // General
    override val GEN_ACTIVITY = "Attività"
    override val VAL_EMPTY = "--"

    // Complication / Tile
    override val COMP_MON = "Lun"
    override val COMP_TUE = "Mar"
    override val COMP_WED = "Mer"
    override val COMP_THU = "Gio"
    override val COMP_FRI = "Ven"
    override val COMP_SAT = "Sab"
    override val COMP_SUN = "Dom"
    
    override val COMP_MONDAY = "Lunedì"
    override val COMP_TUESDAY = "Martedì"
    override val COMP_WEDNESDAY = "Mercoledì"
    override val COMP_THURSDAY = "Giovedì"
    override val COMP_FRIDAY = "Venerdì"
    override val COMP_SATURDAY = "Sabato"
    override val COMP_SUNDAY = "Domenica"

    override val TILE_HELLO = "Ciao!"
}
