package com.example.sportapp

object TextsWearPL {
    // Main Menu
    const val MENU_SPORT = "Sport"
    const val MENU_STATISTICS = "Statystyki"
    const val MENU_SETTINGS = "Ustawienia"
    const val APP_LOGO_DESC = "SportApp Logo"

    // Choose Sport
    const val CHOOSE_SPORT_TITLE = "Wybierz sport"
    const val CHOOSE_SPORT_NO_DEFINITIONS = "Brak definicji aktywności. Zdefiniuj je w aplikacji na telefonie."
    const val CHOOSE_SPORT_DEFAULT_NAME = "Standardowa aktywność"

    // Statistics
    const val STATS_NO_WIDGETS = "Brak wybranych pól"
    const val STATS_PERIOD_TODAY = "Dziś"
    const val STATS_PERIOD_7_DAYS = "Ostatnie 7 dni"
    const val STATS_PERIOD_30_DAYS = "Ostatnie 30 dni"
    const val STATS_PERIOD_YEAR = "Ostatni rok"
    fun statsPeriodCustom(days: Int) = "Ostatnie $days dni"

    const val STATS_WIDGET_COUNT = "Liczba aktywności"
    const val STATS_WIDGET_DISTANCE_GPS = "Dystans (GPS)"
    const val STATS_WIDGET_STEPS_ALL = "Wszystkie kroki"

    // Workout Ready
    const val WORKOUT_READY_START = "Start"
    const val WORKOUT_READY_BACK = "Powrót"

    // Settings
    const val SETTINGS_TITLE = "Ustawienia"
    const val SETTINGS_HEALTH_DATA = "Dane zdrowotne"
    const val SETTINGS_SCREEN = "Ekran"
    const val SETTINGS_SCREEN_ALWAYS_ON = "Zawsze włączony"
    const val SETTINGS_SCREEN_AMBIENT = "Tryb Ambient"
    const val SETTINGS_SCREEN_AUTO = "Tryb automatyczny"
    const val SETTINGS_CLOCK_COLOR = "Kolor zegara"
    const val SETTINGS_SCREEN_BEHAVIOR_TITLE = "Zachowanie ekranu"
    
    // Colors
    const val COLOR_RED = "Czerwony"
    const val COLOR_WHITE = "Biały"
    const val COLOR_GREEN = "Zielony"
    const val COLOR_YELLOW = "Żółty"
    const val COLOR_BLUE = "Niebieski"
    const val COLOR_BLACK = "Czarny"
    const val COLOR_NONE = "Brak"
    const val COLOR_CUSTOM = "Niestandardowy"

    // Health Data
    const val HEALTH_GENDER = "Płeć"
    const val HEALTH_AGE = "Wiek"
    const val HEALTH_WEIGHT = "Waga"
    const val HEALTH_HEIGHT = "Wzrost"
    const val HEALTH_STEP_LENGTH = "Długość kroku"
    const val HEALTH_RESTING_HR = "Tętno spoczynkowe"
    const val HEALTH_MAX_HR = "Tętno maksymalne"
    const val HEALTH_SAVE = "Zapisz"
    const val HEALTH_CHOOSE_GENDER = "Wybierz płeć"
    const val GENDER_MALE = "Mężczyzna"
    const val GENDER_FEMALE = "Kobieta"
    
    fun healthAgeValue(age: Int) = "$age lat"
    fun healthWeightValue(weight: Int) = "$weight kg"
    fun healthHeightValue(height: Int) = "$height cm"
    fun healthStepLengthValue(length: Int) = "$length cm"
    fun healthHRValue(hr: Int) = "$hr BPM"

    // Units
    const val UNIT_YEARS = "lat"
    const val UNIT_KG = "kg"
    const val UNIT_CM = "cm"
    const val UNIT_BPM = "BPM"
    const val UNIT_M = "m"
    const val UNIT_KM = "km"
    const val UNIT_KMH = "km/h"
    const val UNIT_KCAL = "kcal"
    const val UNIT_HPA = "hPa"

    // Workout Data / Labels
    const val WORKOUT_ERROR_CONFIG = "Błąd konfiguracji"
    const val WORKOUT_LABEL_TIMER = "CZAS AKTYWNOŚCI"
    const val WORKOUT_LABEL_STEPS = "Kroki"
    const val WORKOUT_LABEL_DISTANCE = "Dystans"
    const val WORKOUT_LABEL_SPEED = "Prędkość"
    const val WORKOUT_LABEL_HR = "Tętno"
    const val WORKOUT_LABEL_PRESSURE = "Ciśnienie"
    const val WORKOUT_LABEL_ALTITUDE = "Wysokość"

    // Sensors Names (for WorkoutDefinition)
    const val SENSOR_HEART_RATE = "Tętno"
    const val SENSOR_CALORIES_SUM = "Spalone kalorie"
    const val SENSOR_CALORIES_MIN = "Kalorie na minutę"
    const val SENSOR_STEPS = "Kroki"
    const val SENSOR_STEPS_MIN = "Kadencja (kroki/min)"
    const val SENSOR_DISTANCE_STEPS = "Dystans (kroki)"
    const val SENSOR_SPEED_GPS = "Prędkość"
    const val SENSOR_SPEED_STEPS = "Prędkość (kroki)"
    const val SENSOR_DISTANCE_GPS = "Dystans"
    const val SENSOR_ALTITUDE = "Wysokość"
    const val SENSOR_TOTAL_ASCENT = "W sumie w górę"
    const val SENSOR_TOTAL_DESCENT = "W sumie do dołu"
    const val SENSOR_PRESSURE = "Ciśnienie atm."
    const val SENSOR_MAP = "Dane lokalizacji"

    // Workout Controls
    const val WORKOUT_RESUME = "Wznów"
    const val WORKOUT_PAUSE = "Pauza"
    const val WORKOUT_FINISH = "Zakończ"
    const val WORKOUT_START = "Start"
    const val WORKOUT_STOP = "Stop"
    const val WORKOUT_READY_MSG = "Użyj DynamicWorkoutScreen"

    // Summary
    const val SUMMARY_TITLE = "PODSUMOWANIE"
    const val SUMMARY_CONFIRM_DESC = "Potwierdź"
    const val SUMMARY_DURATION = "Czas trwania"
    const val SUMMARY_AVG_HR = "Średnie tętno"
    const val SUMMARY_MAX_HR = "Maksymalne tętno"
    const val SUMMARY_AVG_SPEED = "Średnia prędkość"
    const val SUMMARY_MAX_SPEED = "Maksymalna prędkość"
    const val SUMMARY_AVG_SPEED_STEPS = "Średnia prędkość (kroki)"
    const val SUMMARY_MAX_SPEED_STEPS = "Maksymalna prędkość (kroki)"
    const val SUMMARY_DISTANCE = "Dystans"
    const val SUMMARY_DISTANCE_STEPS = "Dystans (kroki)"
    const val SUMMARY_STEPS = "Kroki"
    const val SUMMARY_TOTAL_ASCENT = "W sumie w górę"
    const val SUMMARY_TOTAL_DESCENT = "W sumie do dołu"
    const val SUMMARY_CALORIES = "Kalorie"

    // General
    const val GEN_ACTIVITY = "Aktywność"
    const val VAL_EMPTY = "--"

    // Complication / Tile
    const val COMP_MON = "Pon"
    const val COMP_TUE = "Wt"
    const val COMP_WED = "Śr"
    const val COMP_THU = "Czw"
    const val COMP_FRI = "Pt"
    const val COMP_SAT = "Sob"
    const val COMP_SUN = "Niedz"
    
    const val COMP_MONDAY = "Poniedziałek"
    const val COMP_TUESDAY = "Wtorek"
    const val COMP_WEDNESDAY = "Środa"
    const val COMP_THURSDAY = "Czwartek"
    const val COMP_FRIDAY = "Piątek"
    const val COMP_SATURDAY = "Sobota"
    const val COMP_SUNDAY = "Niedziela"

    const val TILE_HELLO = "Witaj!"
}
