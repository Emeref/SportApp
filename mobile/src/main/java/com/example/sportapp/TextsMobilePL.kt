package com.example.sportapp

import java.util.Calendar
import java.util.Locale

object TextsMobilePL : MobileTexts {
    // Navigation
    override val NAV_HOME = "Start"
    override val NAV_STATS = "Statystyki"
    override val NAV_ACTIVITIES = "Aktywności"
    override val NAV_SETTINGS = "Ustawienia"

    // Home Screen
    override val HOME_TITLE = "SportApp"
    override val HOME_NO_WIDGETS = "Brak wybranych widgetów"
    override val HOME_ADD_WIDGETS = "Dodaj widgety"
    override val HOME_LAST_ACTIVITY = "Ostatnia aktywność"
    override val HOME_ACTIVITY_COUNT = "Liczba aktywności"
    override val HOME_SYNC = "Synchronizuj"
    override val HOME_OPTIONS = "Opcje"
    override val HOME_GENERAL_STATS = "Statystyki ogólne"
    override val HOME_WORKOUT_DETAILS = "Szczegóły treningu"
    override val HOME_LOGO_DESC = "Logo aplikacji"
    override val HOME_SECRET_TITLE = "Fajnie, że klikasz, ale tutaj nic nie ma"
    override val HOME_CLOSE = "Zamknij"
    override val HOME_START_LIVE = "Rozpocznij Live Tracking"
    override val HOME_ACTIVE_WORKOUT = "Trwa aktywność"
    override val HOME_RESUME_TRACKING = "Wróć do śledzenia"

    override fun homeResultsToday() = "Dzisiejsze wyniki:"
    override fun homeResultsWeek() = "Wyniki z tygodnia:"
    override fun homeResultsMonth(): String {
        val monthName = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pl", "PL"))
        return "Wyniki z $monthName:"
    }
    override fun homeResultsYear() = "Wyniki z tego roku:"
    override fun homeResultsCustom(days: Int) = if (days == 1) "Wyniki z ostatniego dnia:" else "Wyniki z ostatnich $days dni:"

    // Settings Screen
    override val SETTINGS_TITLE = "Ustawienia"
    override val SETTINGS_GENERAL = "Ogólne"
    override val SETTINGS_THEME = "Motyw aplikacji"
    override val SETTINGS_THEME_SYSTEM = "Systemowy"
    override val SETTINGS_THEME_LIGHT = "Jasny"
    override val SETTINGS_THEME_DARK = "Ciemny"
    override val SETTINGS_LANGUAGE = "Język"
    override val SETTINGS_LANGUAGE_TITLE = "Wybierz język"
    override val SETTINGS_HEALTH_DATA = "Dane zdrowotne i HR"
    override val SETTINGS_HEALTH_DATA_DESC = "Wiek, waga, HR Max i strefy"
    override val SETTINGS_DEFINITIONS = "Definicje aktywności"
    override val SETTINGS_DEFINITIONS_DESC = "Zarządzaj listą sportów i sensorami"
    override val SETTINGS_WIDGETS_HOME = "Widgety ekranu głównego"
    override val SETTINGS_WIDGETS_HOME_TITLE = "Widok startowy"
    override val SETTINGS_WIDGETS_HOME_DESC = "Wierz i ustal kolejność"
    override val SETTINGS_WIDGETS_WATCH = "Statystyki na zegarku"
    override val SETTINGS_WIDGETS_WATCH_TITLE = "Pola statystyk"
    override val SETTINGS_WIDGETS_WATCH_DESC = "Wybierz i ustal kolejność na zegarku"
    override val SETTINGS_SAVE = "Zapisz"
    override val SETTINGS_CANCEL = "Anuluj"
    override val SETTINGS_CLOSE = "Zamknij"
    override val SETTINGS_PERIOD = "Domyślny okres"
    override val SETTINGS_PERIOD_HOME_DESC = "Dla jakiego okresu pokazywać widgety?"
    override val SETTINGS_PERIOD_WATCH_DESC = "Statystyki z jakiego okresu?"
    override val SETTINGS_CUSTOM_DAYS_LABEL = "Liczba dni"
    override val SETTINGS_WATCH_STATS_DAYS_LABEL = "Pokazuj statystyki z ilu dni?"
    override val SETTINGS_CUSTOM_DAYS_DESC = "Liczba dni dla okresu 'Inne'"
    override val SETTINGS_WATCH_STATS_DAYS_DESC = "Liczba dni statystyk na zegarku"
    override val SETTINGS_INTEGRATION = "Integracja"
    override val SETTINGS_SYNC = "Synchronizacja"
    override val SETTINGS_STRAVA = "Strava"
    override val SETTINGS_STRAVA_DESC = "Synchronizuj swoje treningi ze Strava"
    override val SETTINGS_GOOGLE_DRIVE = "Google Drive"
    override val SETTINGS_GOOGLE_DRIVE_DESC = "Zapisuj historię i podsumowania (Wkrótce)"
    override val SETTINGS_APPEARANCE = "Wygląd"
    override val SETTINGS_MY_PROFILE = "Mój profil"
    override val LANG_PL = "Polski"
    override val LANG_EN = "Angielski"

    // Health Connect Strings
    override val SETTINGS_HC_TITLE = "Health Connect"
    override val SETTINGS_HC_MANAGE_PERMISSIONS = "Zarządzaj uprawnieniami Health Connect"
    override val SETTINGS_HC_STATUS = "Status Health Connect"
    override val HC_STATUS_AVAILABLE = "Dostępny"
    override val HC_STATUS_UNAVAILABLE = "Niedostępny"
    override val HC_STATUS_NOT_INSTALLED = "Nie zainstalowano"
    override val HC_INSTALL = "Zainstaluj"
    override val HC_SYNC_HEALTH_DATA = "Synchronizuj z Health Connect"
    override val HC_SYNC_WORKOUTS = "Importuj treningi z Health Connect"
    override val HC_SYNC_CONFIRM_TITLE = "Synchronizacja danych"
    override val HC_SYNC_CONFIRM_DESC = "Czy chcesz zaktualizować swój profil o dane znalezione w Health Connect?"
    override val HC_SYNC_SUCCESS = "Synchronizacja pomyślna"
    override val HC_SYNC_ERROR = "Błąd synchronizacji"
    override val HC_SYNC_NO_DATA = "Nie znaleziono nowych danych"
    override fun hcSyncPreview(weight: String?, height: String?, vo2max: String?): String {
        val parts = mutableListOf<String>()
        weight?.let { parts.add("waga $it kg") }
        height?.let { parts.add("wzrost $it cm") }
        vo2max?.let { parts.add("VO2Max $it ml/kg/min") }
        return "Znaleziono w Health Connect: ${parts.joinToString(", ")} – aktualizować?"
    }

    // Stage 3 - Import Workouts
    override val HC_IMPORT_TITLE = "Importuj treningi"
    override val HC_IMPORT_ALREADY_IMPORTED = "Już zaimportowano"
    override val HC_IMPORT_EMPTY = "Nie znaleziono treningów w Health Connect z ostatnich 30 dni."
    override val HC_IMPORT_CONFIRM_DESC_PLURAL = "Czy na pewno chcesz zaimportować wybrane treningi?"
    override val HC_IMPORT_SELECT_ALL = "Zaznacz wszystkie"
    override fun hcImportSelected(count: Int) = "Importuj wybrane ($count)"
    override fun hcImportConfirmDesc(count: Int) = "Czy chcesz zaimportować $count treningów?"
    override fun hcImportProgress(current: Int, total: Int) = "Importowanie $current/$total treningów..."

    // Stage 5 - Export
    override val HC_EXPORT_TO = "Eksportuj do Health Connect"
    override val HC_EXPORTED_ON = "Zsynchronizowano z Health Connect"
    override val HC_EXPORT_SUCCESS = "Eksport zakończony pomyślnie"
    override val HC_EXPORT_ERROR = "Błąd eksportu: "
    override val HC_EXPORT_PERMISSION_DENIED = "Brak uprawnień do zapisu w Health Connect"
    override val SETTINGS_HC_AUTO_EXPORT = "Automatyczny eksport"
    override val SETTINGS_HC_AUTO_EXPORT_DESC = "Automatycznie eksportuj nowe treningi do Health Connect"

    // Stage 6 - Sync Status
    override val SYNC_STATUS_TITLE = "Status synchronizacji HC"
    override val SYNC_LAST_HEALTH = "Last health sync"
    override val SYNC_LAST_WORKOUT = "Last workout sync"
    override val SYNC_UNSYNCED_COUNT = "Unsynced records"
    override val SYNC_NOW = "Synchronizuj teraz"
    override val SYNC_HISTORY_TITLE = "Historia synchronizacji"
    override val SYNC_TYPE_IMPORT = "Import"
    override val SYNC_TYPE_EXPORT = "Eksport"
    override val SYNC_NEVER = "Nigdy"
    override val SYNC_CONFLICT_POLICY = "Polityka konfliktów"
    override val SYNC_CONFLICT_NEWER = "Nowszy wygrywa"
    override val SYNC_CONFLICT_LOCAL = "Lokalne wygrywają"
    override val SYNC_CONFLICT_HC = "Health Connect wygrywa"

    // Health Connect Permissions Dialog
    override val HC_PERMISSIONS_DIALOG_TITLE = "Wymagane uprawnienia"
    override val HC_PERMISSIONS_DIALOG_DESC = "Uprawnienia do zapisu są niezbędne, aby eksportować treningi do Health Connect. Możesz je nadać w ustawieniach systemowych."
    override val HC_OPEN_SETTINGS = "Otwórz ustawienia"

    // Health Data Screen
    override val HEALTH_TITLE = "Dane zdrowotne"
    override val HEALTH_GENDER = "Płeć"
    override val HEALTH_GENDER_MALE = "Mężczyzna"
    override val HEALTH_GENDER_FEMALE = "Kobieta"
    override val HEALTH_AGE = "Awiek"
    override val HEALTH_WEIGHT = "Waga"
    override val HEALTH_WEIGHT_KG = "Waga (kg)"
    override val HEALTH_HEIGHT = "Wzrost"
    override val HEALTH_HEIGHT_CM = "Wzrost (cm)"
    override val HEALTH_RESTING_HR = "Tętno spoczynkowe"
    override val HEALTH_MAX_HR = "Tętno maksymalne"
    override val HEALTH_MAX_HR_DESC = "Tętno maksymalne"
    override val HEALTH_STEP_LENGTH = "Długość kroku"
    override val HEALTH_STEP_LENGTH_CM = "Długość kroku (cm)"
    override val HEALTH_VO2_MAX = "VO2 Max"

    // Activity List
    override val ACTIVITY_LIST_TITLE = "Lista aktywności"
    override val ACTIVITY_EMPTY = "Brak aktywności"
    override val ACTIVITY_DELETE_CONFIRM = "Czy na pewno chcesz trwale usunąć wybrane aktywności z bazy danych?"
    override val ACTIVITY_COMPARE = "Porównaj"
    override val ACTIVITY_TRIM = "Przytnij"
    override val ACTIVITY_DETAIL = "Szczegóły"
    override val ACTIVITY_EDIT = "Edytuj"
    override val ACTIVITY_IMPORT_GPX = "Import GPX"
    override val ACTIVITY_EXPORT_GPX = "Eksportuj GPX"
    override val ACTIVITY_CHART_SETTINGS = "Ustawienia wykresów"
    override val ACTIVITY_FILTERS = "Filtry"
    override val ACTIVITY_ALL_TYPES = "Wszystkie typy"
    override val ACTIVITY_FROM = "Od"
    override val ACTIVITY_TO = "Do"
    override val ACTIVITY_TYPE = "Typ"
    override val ACTIVITY_DATE = "Date"
    override val ACTIVITY_DURATION = "Czas trwania"
    override val ACTIVITY_CALORIES = "Kalorie"
    override val ACTIVITY_DISTANCE_GPS = "Dystans (GPS)"
    override val ACTIVITY_DISTANCE_STEPS = "Dystans (kroki)"
    override val ACTIVITY_DELETE = "Usuń"
    override val ACTIVITY_IMPORT_SELECT_TYPE = "Wybierz typ aktywności"
    override val ACTIVITY_IMPORT_SELECT_DESC = "Wybierz typ treningu dla importowanego pliku GPX:"
    override val ACTIVITY_IMPORT_WARNING = "Ostrzeżenie"
    override val ACTIVITY_IMPORT_CONTINUE = "Kontynuuj"
    override val ACTIVITY_IMPORT_PROGRESS = "Importowanie danych..."
    override val ACTIVITY_EXPORT_ERROR = "Błąd eksportu"
    override val ACTIVITY_SHARE_TITLE = "Udostępnij trening(i)"
    override val ACTIVITY_OK = "OK"
    override val ACTIVITY_CONFIRM_DELETE_TITLE = "Usuń aktywności"
    override val ACTIVITY_ALL = "Wszystkie"
    override val ACTIVITY_NONE = "Żaden"

    // Activity Detail
    override val DETAIL_TITLE = "Szczegóły aktywności"
    override val DETAIL_MAP = "Mapa"
    override val DETAIL_CHARTS = "Wykresy"
    override val DETAIL_LAPS = "Okrążenia"
    override val DETAIL_STATISTICS = "Statystyki"
    override val DETAIL_DATA_ERROR_TITLE = "Błąd danych"
    override val DETAIL_ERROR_OK = "OK"
    override val DETAIL_INTERVALS = "Interwały"
    override fun detailLapsWithDistance(distance: String) = "Interwały ($distance)"
    override fun detailLapsCount(count: Int) = "Liczba okrążeń: $count"
    override val DETAIL_HEART_RATE = "Tętno (bpm)"
    override val DETAIL_HR_ZONES = "Strefy tętna"
    override val DETAIL_TRAINING_EFFECT = "Efekt treningu"
    override val DETAIL_TRAINING_EFFECT_DESC = "Dominujący efekt treningu"
    override val DETAIL_LAP_NR = "Nr"
    override val DETAIL_LAP_TIME = "Czas"
    override val DETAIL_LAP_AVG_PACE = "Śr. tempo"
    override val DETAIL_LAP_AVG_SPEED = "Śr. prędkość"
    override val DETAIL_LAP_MAX_SPEED = "Maks. prędkość"
    override val DETAIL_LAP_AVG_HR = "Śr. tętno"
    override val DETAIL_LAP_MAX_HR = "Maks. tętno"
    override val DETAIL_LAP_ASCENT_DESCENT = "Góra/Dół"
    override val DETAIL_MAP_START = "Start"
    override val DETAIL_MAP_FINISH = "Koniec"
    override val DETAIL_MAP_EXPAND = "Powiększ mapę"
    override val DETAIL_MAP_COLLAPSE = "Zmniejsz mapę"
    override val DETAIL_EXPAND = "Rozwiń"
    override val DETAIL_COLLAPSE = "Zwiń"
    override val DETAIL_PREDOMINANT_EFFECT = "Dominujący efekt treningu"

    // Stats
    override val STATS_TITLE = "Statystyki ogólne"
    override val STATS_CHARTS = "Wykresy"
    override val STATS_WIDGETS = "Widgety"
    override val STATS_NO_DATA = "Brak danych do wyświetlenia wykresów."
    override val STATS_TREND_CHARTS = "Wykresy trendów"
    override val STATS_FILTERS = "Filtry"
    override val STATS_ALL_TYPES = "Wszystkie typy"
    override val STATS_FROM = "Od"
    override val STATS_TO = "Do"
    override val STATS_NO_WIDGETS = "Brak aktywnych widgetów. Włącz je w opcjach."
    override val STATS_SETTINGS_TITLE = "Ustawienia statystyk ogólnych"
    override val STATS_SECTION_WIDGETS = "Sekcja: Widgety"
    override val STATS_SECTION_CHARTS = "Sekcja: Wykresy trendów"
    override val STATS_MOVE_UP = "Przesuń w górę"
    override val STATS_MOVE_DOWN = "Przesuń w dół"
    override fun chartDistanceGps(km: Boolean) = if (km) "Dystans (GPS) w km" else "Dystans (GPS) w m"
    override fun chartDistanceSteps(km: Boolean) = if (km) "Dystans (kroki) w km" else "Dystans (kroki) w m"
    override val CHART_STEPS = "Kroki"

    // Definitions
    override val DEF_TITLE = "Definicje treningu"
    override val DEF_ADD = "Dodaj definicję"
    override val DEF_EDIT = "Edytuj definicję"
    override val DEF_DELETE = "Usuń definicję"
    override val DEF_NAME = "Nazwa"
    override val DEF_ICON = "Ikona"
    override val DEF_SENSORS = "Sensory"
    override val DEF_LIST_TITLE = "Definicja aktywności"
    override val DEF_SENSORS_DESC = "Zarządzaj listą sportów i sensorami"
    override val DEF_RECORDING = "Nagrywanie"
    override val DEF_SELECT_ICON = "Wybierz ikonę"
    override val DEF_SAVE = "Zapisz"
    override val DEF_MOVE_UP = "Przesuń w górę"
    override val DEF_MOVE_DOWN = "Przesuń w dół"
    override val DEF_DELETE_TITLE = "Usuń aktywność"
    override fun defDeleteConfirm(name: String) = "Czy na pewno chcesz usunąć aktywność '$name'?"
    override val DEF_NEW_ACTIVITY = "Nowa aktywność"
    override val DEF_EDIT_ACTIVITY = "Edytuj aktywność"
    override val DEF_NAME_LABEL = "Nazwa aktywności"
    override val DEF_AUTO_LAP_LABEL = "Automatyczne okrążenie (metry, opcjonalnie)"
    override val DEF_WIDGET_IN_ACTIVITY = "Widget in activity"
    override val DEF_VISIBILITY = "Widoczność"
    override val DEF_RECORD = "Nagrywaj"
    override val DEF_BASE_TYPE = "Typ bazowy"
    override val DEF_FINISH = "Zakończ"
    override val DEF_SELECT_ICON_TITLE = "Wybierz ikonę"
    
    // Base Types
    override val DEF_WALKING = "Chodzenie"
    override val DEF_SPEED_WALKING = "Szybki marsz"
    override val DEF_RUNNING = "Bieganie"
    override val DEF_TREADMILL_RUNNING = "Bieganie na bieżni"
    override val DEF_STAIR_CLIMBING = "Wchodzenie po schodach"
    override val DEF_STAIR_CLIMBING_MACHINE = "Stepper"
    override val DEF_CYCLING = "Jazda na rowerze"
    override val DEF_CYCLING_STATIONARY = "Rower stacjonarny"
    override val DEF_MOUNTAIN_BIKING = "Kolarstwo górskie"
    override val DEF_ROAD_BIKING = "Kolarstwo szosowe"
    override val DEF_HIKING = "Wędrówka"
    override val DEF_ROCK_CLIMBING = "Wspinaczka skałkowa"
    override val DEF_BOULDERING = "Bouldering"
    override val DEF_HIIT = "HIIT"
    override val DEF_ELLIPTICAL = "Orbitrek"
    override val DEF_ROWING_MACHINE = "Wioślarz"
    override val DEF_STRENGTH_TRAINING = "Trening siłowy"
    override val DEF_CALISTHENICS = "Kalistenika"
    override val DEF_YOGA = "Joga"
    override val DEF_PILATES = "Pilates"
    override val DEF_AEROBICS = "Aerobik"
    override val DEF_DANCING = "Taniec"
    override val DEF_SWIMMING = "Pływanie"
    override val DEF_SWIMMING_POOL = "Pływanie (basen)"
    override val DEF_SWIMMING_OPEN_WATER = "Pływanie (wody otwarte)"
    override val DEF_KAYAKING = "Kajakarstwo"
    override val DEF_PADDLE_BOARDING = "SUP (Paddle boarding)"
    override val DEF_SURFING = "Surfing"
    override val DEF_SAILING = "Żeglarstwo"
    override val DEF_FOOTBALL = "Piłka nożna"
    override val DEF_BASKETBALL = "Koszykówka"
    override val DEF_TENNIS = "Tenis"
    override val DEF_SQUASH = "Squash"
    override val DEF_VOLLEYBALL = "Siatkówka"
    override val DEF_GOLF = "Golf"
    override val DEF_MARTIAL_ARTS = "Sztuki walki"
    override val DEF_SKIING = "Narciarstwo"
    override val DEF_SNOWBOARDING = "Snowboarding"
    override val DEF_SKATING = "Łyżwiarstwo"
    override val DEF_ICE_SKATING = "Łyżwiarstwa figurowe"
    
    override val DEF_GYM = "Siłownia"
    override val DEF_BASEBALL = "Baseball"
    override val DEF_SKATEBOARDING = "Deskorolka"
    override val DEF_COMPETITION = "Zawody"
    override val DEF_STOPWATCH = "Stoper"
    override val DEF_OTHER = "Andere"
    override val DEF_STANDARD_ACTIVITY = "Standardowa aktywność"

    // Activity Detail Settings
    override val AD_SETTINGS_LIST_TITLE = "Wybierz aktywność do modyfikacji"
    override val AD_SETTINGS_EDIT_TITLE = "Ustawienia"
    override val AD_SETTINGS_SECTION_WIDGETS = "Sekcja: Widgety"
    override val AD_SETTINGS_SECTION_CHARTS = "Sekcja: Wykresy"

    // Heart Rate Math
    override val HR_NO_DATA = "Brak danych tętna"
    override val HR_TOO_LITTLE_DATA = "Zbyt mało danych"
    override val HR_BELOW_ZONES = "Tętno poniżej stref"
    override val HR_EFFECT_Z0 = "Niska intensyność / Rozgrzewka"
    override val HR_EFFECT_Z1 = "Baza tlenowa i regeneracja"
    override val HR_EFFECT_Z2 = "Efektywne spalanie tłuszczu"
    override val HR_EFFECT_Z3 = "Poprawa wydolności tlenowej"
    override val HR_EFFECT_Z4 = "Zwiększenie progu mleczanowego"
    override val HR_EFFECT_Z5 = "Trening beztlenowy i VO2 Max"
    override val HR_EFFECT_NONE = "Brak dominującej strefy"

    // HR Zones Names
    override val ZONE_Z0 = "Rozgrzewka"
    override val ZONE_Z1 = "Bardzo lekki"
    override val ZONE_Z2 = "Lekki"
    override val ZONE_Z3 = "Umiarkowany"
    override val ZONE_Z4 = "Ciężki"
    override val ZONE_Z5 = "Maksymalny"

    // ViewModels Messages
    override val VM_EXPORT_INITIALIZING = "Inicjalizacja eksportu..."
    override fun vmExportGenerating(name: String, current: Int, total: Int) = "Generowanie: $name ($current/$total)"
    override val VM_EXPORT_NO_FILES = "Nie wygenerowano żadnych plików."
    override val VM_EXPORT_ZIPPING = "Pakowanie do ZIP..."
    override fun vmExportError(msg: String) = "Błąd podczas eksportu: $msg"
    override val VM_IMPORT_OPEN_ERROR = "Nie można otworzyć pliku"
    override val VM_IMPORT_DUPLICATE_WARNING = "Wykryto potencjalny duplikat (taka sama data startu i czas trwania)."
    override val VM_IMPORT_DUPLICATE_WARNING_DESC = "Czy chcesz mimo to kontynuować?"
    override val VM_IMPORT_SUCCESS = "Trening zaimportowany pomyślnie."
    override fun vmImportError(msg: String) = "Błąd importu: $msg"

    // Gpx Importer
    override val GPX_NO_POINTS = "Plik GPX nie zawiera punktów trasy."
    override val GPX_WARN_HR = "Plik zawiera dane tętna, ale wybrana aktywność ich nie obsługuje."
    override val GPX_WARN_ELE = "Plik zawiera dane wysokości, ale wybrana aktywność ich nie obsługuje."
    override val GPX_WARN_CADENCE = "Plik zawiera dane kadencji, ale wybrana aktywność ich nie obsługuje."

    // Periods
    override val PERIOD_TODAY = "Dziś"
    override val PERIOD_WEEK = "Tydzień"
    override val PERIOD_MONTH = "Miesiąc"
    override val PERIOD_YEAR = "Rok"
    override val PERIOD_CUSTOM = "Inne"
    override fun periodCustomDays(days: Int) = if (days == 1) "$days dzień" else "$days dni"

    // Widgets
    override val WIDGET_COUNT = "Liczba aktywności"
    override val WIDGET_CALORIES = "Spalone kalorie"
    override val WIDGET_DISTANCE_GPS = "Dystans (GPS)"
    override val WIDGET_DISTANCE_STEPS = "Dystans (kroki)"
    override val WIDGET_ASCENT = "W sumie w górę"
    override val WIDGET_DESCENT = "W sumie do dołu"
    override val WIDGET_STEPS = "Kroki"
    override val WIDGET_AVG_BPM = "Średnie tętno"
    override val WIDGET_AVG_CADENCE = "Średnia kadencja"
    override val WIDGET_MAX_SPEED = "Maks prędkość"
    override val WIDGET_MAX_ALTITUDE = "Maks wysokość"
    override val WIDGET_MAX_ELEVATION_GAIN = "Najwięcej przewyższeń"
    override val WIDGET_MAX_DISTANCE = "Największy dystans"
    override val WIDGET_MAX_DURATION = "Najdłuższy czas"
    override val WIDGET_MAX_CALORIES = "Najwięcej kalorii"
    override val WIDGET_MAX_AVG_CADENCE = "Najwyższa śr. kadencja"
    override val WIDGET_MAX_AVG_SPEED = "Najwyższa śr. prędkość"
    override val WIDGET_DURATION = "Czas trwania"
    override val WIDGET_MAX_BPM = "Maksymalne tętno"
    override val WIDGET_TOTAL_CALORIES = "Spalone kalorie"
    override val WIDGET_MAX_CALORIES_MIN = "Maks spalanie kalorii"
    override val WIDGET_AVG_PACE = "Średnie tempo"
    override val WIDGET_AVG_SPEED_GPS = "Średnia prędkość (GPS)"
    override val WIDGET_AVG_SPEED_STEPS = "Średnia prędkość (kroki)"
    override val WIDGET_MAX_ALTITUDE_DESC = "Maks wysokość"
    override val WIDGET_TOTAL_ASCENT = "Suma podejść"
    override val WIDGET_TOTAL_DESCENT = "Suma zejść"
    override val WIDGET_AVG_STEP_LENGTH = "Wyliczona długość kroku"
    override val WIDGET_AVG_CADENCE_DESC = "Śr. kadencja"
    override val WIDGET_MAX_CADENCE = "Maks. kadencja"
    override val WIDGET_TOTAL_STEPS = "Liczba kroków"
    override val WIDGET_PRESSURE_START = "Ciśnienie atm. (start)"
    override val WIDGET_PRESSURE_END = "Ciśnienie atm. (koniec)"
    override val WIDGET_MAX_PRESSURE = "Maks. ciśnienie"
    override val WIDGET_MIN_PRESSURE = "Min. ciśnienie"
    override val WIDGET_BEST_PACE_1KM = "Najlepsze tempo (1km)"
    override val WIDGET_WATCH_ASCENT = "Przewyższenia w górę"
    override val WIDGET_WATCH_DESCENT = "Przewyższenia w dół"

    // Sensors
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
    override val SENSOR_TOTAL_ASCENT = "Suma podejść"
    override val SENSOR_TOTAL_DESCENT = "Suma zejść"
    override val SENSOR_PRESSURE = "Ciśnienie atm."
    override val SENSOR_MAP = "Dane lokalizacji"
    override val SENSOR_AVG_STEP_LENGTH = "Średnia długość kroku"

    // Trim Screen
    override val TRIM_TITLE = "Edytuj trening (Przycinanie)"
    override val TRIM_CONFIRM_TITLE = "Potwierdź przycięcie"
    override val TRIM_CONFIRM_DESC = "Czy na pewno chcesz usunąć dane poza wybranym zakresem? Te dane zostaną trwale usunięte."
    override val TRIM_SAVE_BTN = "Przytnij i zapisz"
    override val TRIM_CHART_HR = "Wykres tętna"
    override val TRIM_RANGE_TITLE = "Wybierz zakres treningu"
    override val TRIM_PREVIEW_TITLE = "Podgląd nowych statystyk"
    override val TRIM_NEW_DURATION = "Nowy czas trwania:"
    override val TRIM_DISTANCE_GPS = "Dystans (GPS):"
    override val TRIM_DISTANCE_STEPS = "Dystans (Kroki):"
    override val TRIM_CALORIES = "Spalone kalorie:"
    override val TRIM_AVG_BPM = "Średnie tętno:"
    override val TRIM_START = "Start"
    override val TRIM_END = "Koniec"

    // Compare Screen
    override val COMPARE_TITLE = "Porównanie aktywności"
    override val COMPARE_VS = "Porównanie:"
    override val COMPARE_HIGHER_IS_BETTER = "Wyższy wynik jest lepszy"
    override val COMPARE_LOWER_IS_BETTER = "Niższy wynik jest lepszy"

    // Units
    override val UNIT_KCAL = "kcal"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_STEP_MIN = "kr/min"
    override val UNIT_KM_H = "km/h"
    override val UNIT_STEPS = "kroki"
    override val UNIT_HPA = "hPa"
    override val UNIT_MIN_KM = "min/km"
    override val UNIT_M_ASL = "m n.p.m."
    override val UNIT_MIN_KM_LABEL = "min/km"
    override val UNIT_BPM = "bpm"
    override val UNIT_KCAL_MIN = "kcal/min"
    override val UNIT_VO2_MAX = "ml/kg/min"

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
            "avg_cadence" -> WIDGET_AVG_BPM
            "avg_step_length_over_time" -> SENSOR_AVG_STEP_LENGTH
            else -> id
        }
    }

    // Strava Strings
    override val STRAVA_TITLE = "Synchronizacja ze Stravą"
    override val STRAVA_CONNECT = "Połącz z kontem Strava"
    override val STRAVA_DISCONNECT = "Odłącz konto Strava"
    override val STRAVA_CONNECTED = "Połączono ze Stravą"
    override val STRAVA_NOT_CONNECTED = "Nie połączono"
    override val STRAVA_SYNC_NOW = "Synchronizuj teraz"
    override val STRAVA_SYNC_SUCCESS = "Trening został wysłany!"
    override val STRAVA_SYNC_FAILED = "Błąd wysyłki"
    override val STRAVA_SYNCING = "Wysyłanie..."
    override val STRAVA_AUTH_ERROR = "Błąd autoryzacji"
    override val SETTINGS_STRAVA_AUTO_EXPORT = "Automatyczny eksport"
    override val SETTINGS_STRAVA_AUTO_EXPORT_DESC = "Automatycznie wysyłaj nowe treningi do Strava"
    override val STRAVA_SYNC_LOG = "Historia synchronizacji"
    override val STRAVA_SYNC_LOG_EMPTY = "Brak historii synchronizacji"

    // Live Tracking
    override val LIVE_TRACKING_TITLE = "Live Tracking"
    override val LIVE_TRACKING_SELECT_ACTIVITY = "Wybierz aktywność"
    override val LIVE_TRACKING_LOCK = "Zablokuj"
    override val LIVE_TRACKING_UNLOCK_SWIPE = "Przesuń w górę, aby odblokować"
    override val LIVE_TRACKING_MAP_NORTH = "Północ"
    override val LIVE_TRACKING_MAP_DIRECTION = "Kierunek"
    override val LIVE_TRACKING_WAITING_FOR_WATCH = "Oczekiwanie na sygnał z zegarka..."
    
    // Errors
    override val ERROR_WEARABLE_NOT_AVAILABLE = "Usługi Wearable są niedostępne na tym urządzeniu"
    override val ERROR_NO_WATCH_CONNECTED = "Nie znaleziono połączonego zegarka"
}
