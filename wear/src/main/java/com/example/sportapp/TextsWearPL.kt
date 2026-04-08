package com.example.sportapp

object TextsWearPL : WearTexts {
    // Main Menu
    override val MENU_SPORT = "Sport"
    override val MENU_STATISTICS = "Statystyki"
    override val MENU_SETTINGS = "Ustawienia"
    override val APP_LOGO_DESC = "SportApp Logo"

    // Choose Sport
    override val CHOOSE_SPORT_TITLE = "Wybierz sport"
    override val CHOOSE_SPORT_NO_DEFINITIONS = "Brak definicji aktywności. Zdefiniuj je w aplikacji na telefonie."
    override val CHOOSE_SPORT_DEFAULT_NAME = "Standardowa aktywność"

    // Statistics
    override val STATS_NO_WIDGETS = "Brak wybranych pól"
    override val STATS_PERIOD_TODAY = "Dziś"
    override val STATS_PERIOD_7_DAYS = "Ostatnie 7 dni"
    override val STATS_PERIOD_30_DAYS = "Ostatnie 30 dni"
    override val STATS_PERIOD_YEAR = "Ostatni rok"
    override fun statsPeriodCustom(days: Int) = "Ostatnie $days dni"

    override val STATS_WIDGET_COUNT = "Liczba aktywności"
    override val STATS_WIDGET_DISTANCE_GPS = "Dystans (GPS)"
    override val STATS_WIDGET_STEPS_ALL = "Wszystkie kroki"
    override val STATS_WIDGET_CALORIES = "Spalone kalorie"
    override val STATS_WIDGET_DISTANCE_STEPS = "Dystans (kroki)"
    override val STATS_WIDGET_ASCENT = "Przewyższenia w górę"
    override val STATS_WIDGET_DESCENT = "Przewyższenia w dół"

    // Workout Ready
    override val WORKOUT_READY_START = "Start"
    override val WORKOUT_READY_BACK = "Powrót"

    // Settings
    override val SETTINGS_TITLE = "Ustawienia"
    override val SETTINGS_HEALTH_DATA = "Dane zdrowotne"
    override val SETTINGS_SCREEN = "Ekran"
    override val SETTINGS_SCREEN_ALWAYS_ON = "Zawsze włączony"
    override val SETTINGS_SCREEN_AMBIENT = "Tryb Ambient"
    override val SETTINGS_SCREEN_AUTO = "Tryb automatyczny"
    override val SETTINGS_CLOCK_COLOR = "Kolor zegara"
    override val SETTINGS_SCREEN_BEHAVIOR_TITLE = "Zachowanie ekranu"
    override val SETTINGS_LANGUAGE = "Język"
    override val SETTINGS_LANGUAGE_SELECTION_TITLE = "Wybierz język"
    
    // Colors
    override val COLOR_RED = "Czerwony"
    override val COLOR_WHITE = "Biały"
    override val COLOR_GREEN = "Zielony"
    override val COLOR_YELLOW = "Żółty"
    override val COLOR_BLUE = "Niebieski"
    override val COLOR_BLACK = "Czarny"
    override val COLOR_NONE = "Brak"
    override val COLOR_CUSTOM = "Niestandardowy"

    // Health Data
    override val HEALTH_GENDER = "Płeć"
    override val HEALTH_AGE = "Wiek"
    override val HEALTH_WEIGHT = "Waga"
    override val HEALTH_HEIGHT = "Wzrost"
    override val HEALTH_STEP_LENGTH = "Długość kroku"
    override val HEALTH_RESTING_HR = "Tętno spoczynkowe"
    override val HEALTH_MAX_HR = "Tętno maksymalne"
    override val HEALTH_SAVE = "Zapisz"
    override val HEALTH_CHOOSE_GENDER = "Wybierz płeć"
    override val GENDER_MALE = "Mężczyzna"
    override val GENDER_FEMALE = "Kobieta"
    
    override fun healthAgeValue(age: Int) = "$age lat"
    override fun healthWeightValue(weight: Int) = "$weight kg"
    override fun healthHeightValue(height: Int) = "$height cm"
    override fun healthStepLengthValue(length: Int) = "$length cm"
    override fun healthHRValue(hr: Int) = "$hr BPM"

    // Units
    override val UNIT_YEARS = "lat"
    override val UNIT_KG = "kg"
    override val UNIT_CM = "cm"
    override val UNIT_BPM = "BPM"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_KMH = "km/h"
    override val UNIT_KCAL = "kcal"
    override val UNIT_HPA = "hPa"

    // Workout Data / Labels
    override val WORKOUT_ERROR_CONFIG = "Błąd konfiguracji"
    override val WORKOUT_LABEL_TIMER = "CZAS AKTYWNOŚCI"
    override val WORKOUT_LABEL_STEPS = "Kroki"
    override val WORKOUT_LABEL_DISTANCE = "Dystans"
    override val WORKOUT_LABEL_SPEED = "Prędkość"
    override val WORKOUT_LABEL_HR = "Tętno"
    override val WORKOUT_LABEL_PRESSURE = "Ciśnienie"
    override val WORKOUT_LABEL_ALTITUDE = "Wysokość"

    // Sensors Names (for WorkoutDefinition)
    override val SENSOR_HEART_RATE = "Tętno"
    override val SENSOR_CALORIES_SUM = "Spalone kalorie"
    override val SENSOR_CALORIES_MIN = "Kalorie na minutę"
    override val SENSOR_STEPS = "Kroki"
    override val SENSOR_STEPS_MIN = "Kadencja (kroki/min)"
    override val SENSOR_DISTANCE_STEPS = "Dystans (kroki)"
    override val SENSOR_SPEED_GPS = "Prędkość"
    override val SENSOR_SPEED_STEPS = "Prędkość (kroki)"
    override val SENSOR_DISTANCE_GPS = "Dystans"
    override val SENSOR_ALTITUDE = "Wysokość"
    override val SENSOR_TOTAL_ASCENT = "W sumie w górę"
    override val SENSOR_TOTAL_DESCENT = "W sumie do dołu"
    override val SENSOR_PRESSURE = "Ciśnienie atm."
    override val SENSOR_MAP = "Dane lokalizacji"

    // Workout Controls
    override val WORKOUT_RESUME = "Wznów"
    override val WORKOUT_PAUSE = "Pauza"
    override val WORKOUT_FINISH = "Zakończ"
    override val WORKOUT_START = "Start"
    override val WORKOUT_STOP = "Stop"
    override val WORKOUT_READY_MSG = "Użyj DynamicWorkoutScreen"

    // Summary
    override val SUMMARY_TITLE = "PODSUMOWANIE"
    override val SUMMARY_CONFIRM_DESC = "Potwierdź"
    override val SUMMARY_DURATION = "Czas trwania"
    override val SUMMARY_AVG_HR = "Średnie tętno"
    override val SUMMARY_MAX_HR = "Maksymalne tętno"
    override val SUMMARY_AVG_SPEED = "Średnia prędkość"
    override val SUMMARY_MAX_SPEED = "Maksymalna prędkość"
    override val SUMMARY_AVG_SPEED_STEPS = "Średnia prędkość (kroki)"
    override val SUMMARY_MAX_SPEED_STEPS = "Maksymalna prędkość (kroki)"
    override val SUMMARY_DISTANCE = "Dystans"
    override val SUMMARY_DISTANCE_STEPS = "Dystans (kroki)"
    override val SUMMARY_STEPS = "Kroki"
    override val SUMMARY_TOTAL_ASCENT = "W sumie w górę"
    override val SUMMARY_TOTAL_DESCENT = "W sumie do dołu"
    override val SUMMARY_CALORIES = "Kalorie"

    // General
    override val GEN_ACTIVITY = "Aktywność"
    override val VAL_EMPTY = "--"

    // Complication / Tile
    override val COMP_MON = "Pon"
    override val COMP_TUE = "Wt"
    override val COMP_WED = "Śr"
    override val COMP_THU = "Czw"
    override val COMP_FRI = "Pt"
    override val COMP_SAT = "Sob"
    override val COMP_SUN = "Niedz"
    
    override val COMP_MONDAY = "Poniedziałek"
    override val COMP_TUESDAY = "Wtorek"
    override val COMP_WEDNESDAY = "Środa"
    override val COMP_THURSDAY = "Czwartek"
    override val COMP_FRIDAY = "Piątek"
    override val COMP_SATURDAY = "Sobota"
    override val COMP_SUNDAY = "Niedziela"

    override val TILE_HELLO = "Witaj!"

    //Stats page
    override val STATS_WIDGET_MAX_SPEED = "Największa prędkość"
    override val STATS_WIDGET_MAX_ALTITUDE = "Największa wysokość"
    override val STATS_WIDGET_MAX_ELEVATION_GAIN = "Największa suma przewyższeń"
    override val STATS_WIDGET_MAX_DISTANCE = "Największy dystans"
    override val STATS_WIDGET_MAX_DURATION = "Najdłuższy czas trwania"
    override val STATS_WIDGET_MAX_CALORIES = "Maksymalne spalanie kalorii"
    override val STATS_WIDGET_MAX_AVG_CADENCE = "Największa średnia kadencja"
    override val STATS_WIDGET_MAX_AVG_SPEED = "Największa średnia prędkość"
}
