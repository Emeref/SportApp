package com.example.sportapp

import java.util.Calendar
import java.util.Locale

object TextsMobileDE : MobileTexts {
    // Navigation
    override val NAV_HOME = "Home"
    override val NAV_STATS = "Statistiken"
    override val NAV_ACTIVITIES = "Aktivitäten"
    override val NAV_SETTINGS = "Einstellungen"

    // Home Screen
    override val HOME_TITLE = "SportApp"
    override val HOME_NO_WIDGETS = "Keine Widgets ausgewählt"
    override val HOME_ADD_WIDGETS = "Widgets hinzufügen"
    override val HOME_LAST_ACTIVITY = "Letzte Aktivität"
    override val HOME_ACTIVITY_COUNT = "Anzahl der Aktivitäten"
    override val HOME_SYNC = "Synchronisieren"
    override val HOME_OPTIONS = "Optionen"
    override val HOME_GENERAL_STATS = "Allgemeine Statistiken"
    override val HOME_WORKOUT_DETAILS = "Trainingsdetails"
    override val HOME_LOGO_DESC = "App-Logo"
    override val HOME_SECRET_TITLE = "Schön, dass du klickst, aber hier ist nichts"
    override val HOME_CLOSE = "Schließen"

    override fun homeResultsToday() = "Heutige Ergebnisse:"
    override fun homeResultsWeek() = "Ergebnisse dieser Woche:"
    override fun homeResultsMonth(): String {
        val monthName = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.GERMAN)
        return "Ergebnisse im $monthName:"
    }
    override fun homeResultsYear() = "Ergebnisse dieses Jahres:"
    override fun homeResultsCustom(days: Int) = if (days == 1) "Ergebnisse vom letzten Tag:" else "Ergebnisse der letzten $days Tage:"

    // Settings Screen
    override val SETTINGS_TITLE = "Einstellungen"
    override val SETTINGS_GENERAL = "Allgemein"
    override val SETTINGS_THEME = "App-Design"
    override val SETTINGS_THEME_SYSTEM = "System"
    override val SETTINGS_THEME_LIGHT = "Hell"
    override val SETTINGS_THEME_DARK = "Dunkel"
    override val SETTINGS_LANGUAGE = "Sprache"
    override val SETTINGS_LANGUAGE_TITLE = "Sprache wählen"
    override val SETTINGS_HEALTH_DATA = "Gesundheitsdaten & HF"
    override val SETTINGS_HEALTH_DATA_DESC = "Alter, Gewicht, HF Max & Zonen"
    override val SETTINGS_DEFINITIONS = "Aktivitätsdefinitionen"
    override val SETTINGS_DEFINITIONS_DESC = "Sportliste und Sensoren verwalten"
    override val SETTINGS_WIDGETS_HOME = "Startbildschirm-Widgets"
    override val SETTINGS_WIDGETS_HOME_TITLE = "Startansicht"
    override val SETTINGS_WIDGETS_HOME_DESC = "Auswählen und Reihenfolge festlegen"
    override val SETTINGS_WIDGETS_WATCH = "Uhren-Statistiken"
    override val SETTINGS_WIDGETS_WATCH_TITLE = "Statistikfelder"
    override val SETTINGS_WIDGETS_WATCH_DESC = "Auswählen und Reihenfolge auf der Uhr festlegen"
    override val SETTINGS_SAVE = "Speichern"
    override val SETTINGS_CANCEL = "Abbrechen"
    override val SETTINGS_CLOSE = "Schließen"
    override val SETTINGS_PERIOD = "Standardzeitraum"
    override val SETTINGS_PERIOD_HOME_DESC = "Für welchen Zeitraum Widgets anzeigen?"
    override val SETTINGS_PERIOD_WATCH_DESC = "Statistiken aus welchem Zeitraum?"
    override val SETTINGS_CUSTOM_DAYS_LABEL = "Anzahl der Tage"
    override val SETTINGS_INTEGRATION = "Integration"
    override val SETTINGS_GOOGLE_DRIVE = "Google Drive"
    override val SETTINGS_GOOGLE_DRIVE_DESC = "Verlauf und Zusammenfassungen speichern (Demnächst)"
    override val SETTINGS_APPEARANCE = "Aussehen"
    override val SETTINGS_MY_PROFILE = "Mein Profil"
    override val LANG_PL = "Polnisch"
    override val LANG_EN = "Englisch"

    // Health Data Screen
    override val HEALTH_TITLE = "Gesundheitsdaten"
    override val HEALTH_GENDER = "Geschlecht"
    override val HEALTH_GENDER_MALE = "Männlich"
    override val HEALTH_GENDER_FEMALE = "Weiblich"
    override val HEALTH_AGE = "Alter"
    override val HEALTH_WEIGHT = "Gewicht"
    override val HEALTH_WEIGHT_KG = "Gewicht (kg)"
    override val HEALTH_HEIGHT = "Größe"
    override val HEALTH_HEIGHT_CM = "Größe (cm)"
    override val HEALTH_RESTING_HR = "Ruhepuls"
    override val HEALTH_MAX_HR = "Maximalpuls (HF Max)"
    override val HEALTH_MAX_HR_DESC = "Maximalpuls (HF Max)"
    override val HEALTH_STEP_LENGTH = "Schrittlänge"
    override val HEALTH_STEP_LENGTH_CM = "Schrittlänge (cm)"

    // Activity List
    override val ACTIVITY_LIST_TITLE = "Aktivitätsliste"
    override val ACTIVITY_EMPTY = "Keine Aktivitäten"
    override val ACTIVITY_DELETE_CONFIRM = "Möchten Sie die ausgewählten Aktivitäten wirklich dauerhaft aus der Datenbank löschen?"
    override val ACTIVITY_COMPARE = "Vergleichen"
    override val ACTIVITY_TRIM = "Kürzen"
    override val ACTIVITY_DETAIL = "Details"
    override val ACTIVITY_EDIT = "Bearbeiten"
    override val ACTIVITY_IMPORT_GPX = "GPX importieren"
    override val ACTIVITY_EXPORT_GPX = "GPX exportieren"
    override val ACTIVITY_CHART_SETTINGS = "Diagramm-Einstellungen"
    override val ACTIVITY_FILTERS = "Filter"
    override val ACTIVITY_ALL_TYPES = "Alle Typen"
    override val ACTIVITY_FROM = "Von"
    override val ACTIVITY_TO = "Bis"
    override val ACTIVITY_TYPE = "Typ"
    override val ACTIVITY_DATE = "Datum"
    override val ACTIVITY_DURATION = "Zeit"
    override val ACTIVITY_CALORIES = "Kalorien"
    override val ACTIVITY_DISTANCE_GPS = "Distanz (GPS)"
    override val ACTIVITY_DISTANCE_STEPS = "Distanz (Schritte)"
    override val ACTIVITY_DELETE = "Löschen"
    override val ACTIVITY_IMPORT_SELECT_TYPE = "Aktivitätstyp wählen"
    override val ACTIVITY_IMPORT_SELECT_DESC = "Wählen Sie den Trainingstyp für die importierte GPX-Datei:"
    override val ACTIVITY_IMPORT_WARNING = "Warnung"
    override val ACTIVITY_IMPORT_CONTINUE = "Fortfahren"
    override val ACTIVITY_IMPORT_PROGRESS = "Daten werden importiert..."
    override val ACTIVITY_EXPORT_ERROR = "Exportfehler"
    override val ACTIVITY_SHARE_TITLE = "Training(s) teilen"
    override val ACTIVITY_OK = "OK"
    override val ACTIVITY_CONFIRM_DELETE_TITLE = "Aktivitäten löschen"
    override val ACTIVITY_ALL = "Alle"

    // Activity Detail
    override val DETAIL_TITLE = "Aktivitätsdetails"
    override val DETAIL_MAP = "Karte"
    override val DETAIL_CHARTS = "Diagramme"
    override val DETAIL_LAPS = "Runden"
    override val DETAIL_STATISTICS = "Statistiken"
    override val DETAIL_DATA_ERROR_TITLE = "Datenfehler"
    override val DETAIL_ERROR_OK = "OK"
    override val DETAIL_INTERVALS = "Intervalle"
    override fun detailLapsWithDistance(distance: String) = "Intervalle ($distance)"
    override fun detailLapsCount(count: Int) = "Rundenanzahl: $count"
    override val DETAIL_HEART_RATE = "Herzfrequenz (bpm)"
    override val DETAIL_HR_ZONES = "HF-Zonen"
    override val DETAIL_TRAINING_EFFECT = "Trainingseffekt"
    override val DETAIL_LAP_NR = "Nr."
    override val DETAIL_LAP_TIME = "Zeit"
    override val DETAIL_LAP_AVG_PACE = "Durchschn. Pace"
    override val DETAIL_LAP_AVG_SPEED = "Durchschn. Geschw."
    override val DETAIL_LAP_MAX_SPEED = "Max. Geschw."
    override val DETAIL_LAP_AVG_HR = "Durchschn. HF"
    override val DETAIL_LAP_MAX_HR = "Max. HF"
    override val DETAIL_LAP_ASCENT_DESCENT = "Auf/Ab"
    override val DETAIL_MAP_START = "Start"
    override val DETAIL_MAP_FINISH = "Ziel"
    override val DETAIL_MAP_EXPAND = "Karte vergrößern"
    override val DETAIL_MAP_COLLAPSE = "Karte verkleinern"
    override val DETAIL_EXPAND = "Erweitern"
    override val DETAIL_COLLAPSE = "Einklappen"
    override val DETAIL_PREDOMINANT_EFFECT = "Vorherrschender Trainingseffekt"

    // Stats
    override val STATS_TITLE = "Allgemeine Statistiken"
    override val STATS_CHARTS = "Diagramme"
    override val STATS_WIDGETS = "Widgets"
    override val STATS_NO_DATA = "Keine Daten zur Anzeige von Diagrammen vorhanden."
    override val STATS_TREND_CHARTS = "Trend-Diagramme"
    override val STATS_FILTERS = "Filter"
    override val STATS_ALL_TYPES = "Alle Typen"
    override val STATS_FROM = "Von"
    override val STATS_TO = "Bis"
    override val STATS_NO_WIDGETS = "Keine aktiven Widgets. Aktivieren Sie diese in den Optionen."
    override val STATS_SETTINGS_TITLE = "Allgemeine Statistik-Einstellungen"
    override val STATS_SECTION_WIDGETS = "Abschnitt: Widgets"
    override val STATS_SECTION_CHARTS = "Abschnitt: Diagramme"
    override val STATS_MOVE_UP = "Nach oben verschieben"
    override val STATS_MOVE_DOWN = "Nach unten verschieben"
    override fun chartDistanceGps(km: Boolean) = if (km) "Distanz (GPS) in km" else "Distanz (GPS) in m"
    override fun chartDistanceSteps(km: Boolean) = if (km) "Distanz (Schritte) in km" else "Distanz (Schritte) in m"
    override val CHART_STEPS = "Schritte"

    // Definitions
    override val DEF_TITLE = "Workout-Definitionen"
    override val DEF_ADD = "Definition hinzufügen"
    override val DEF_EDIT = "Definition bearbeiten"
    override val DEF_DELETE = "Definition löschen"
    override val DEF_NAME = "Name"
    override val DEF_ICON = "Symbol"
    override val DEF_SENSORS = "Sensoren"
    override val DEF_LIST_TITLE = "Aktivitätsdefinition"
    override val DEF_SENSORS_DESC = "Sportliste und Sensoren verwalten"
    override val DEF_RECORDING = "Aufzeichnung"
    override val DEF_SELECT_ICON = "Symbol wählen"
    override val DEF_SAVE = "Speichern"
    override val DEF_MOVE_UP = "Nach oben"
    override val DEF_MOVE_DOWN = "Nach unten"
    override val DEF_DELETE_TITLE = "Aktivität löschen"
    override fun defDeleteConfirm(name: String) = "Möchten Sie die Aktivität '$name' wirklich löschen?"
    override val DEF_NEW_ACTIVITY = "Neue Aktivität"
    override val DEF_EDIT_ACTIVITY = "Aktivität bearbeiten"
    override val DEF_NAME_LABEL = "Aktivitätsname"
    override val DEF_AUTO_LAP_LABEL = "Auto-Runde (Meter, optional)"
    override val DEF_WIDGET_IN_ACTIVITY = "Widget in Aktivität"
    override val DEF_VISIBILITY = "Sichtbarkeit"
    override val DEF_RECORD = "Aufzeichnung"
    override val DEF_BASE_TYPE = "Basistyp"
    override val DEF_FINISH = "Beenden"
    override val DEF_SELECT_ICON_TITLE = "Symbol wählen"
    override val DEF_WALKING = "Gehen"
    override val DEF_RUNNING = "Laufen"
    override val DEF_CYCLING = "Radfahren"
    override val DEF_HIKING = "Wandern"
    override val DEF_SWIMMING = "Schwimmen"
    override val DEF_GYM = "Fitnessstudio"
    override val DEF_YOGA = "Yoga"
    override val DEF_TENNIS = "Tennis"
    override val DEF_KAYAKING = "Kajakfahren"
    override val DEF_SNOWBOARDING = "Snowboarden"
    override val DEF_SURFING = "Surfen"
    override val DEF_SKATING = "Eislaufen"
    override val DEF_GOLF = "Golf"
    override val DEF_FOOTBALL = "Fußball"
    override val DEF_BASKETBALL = "Basketball"
    override val DEF_VOLLEYBALL = "Volleyball"
    override val DEF_BASEBALL = "Baseball"
    override val DEF_SAILING = "Segeln"
    override val DEF_SKATEBOARDING = "Skateboarden"
    override val DEF_COMPETITION = "Wettbewerb"
    override val DEF_STOPWATCH = "Stoppuhr"
    override val DEF_OTHER = "Sonstiges"
    override val DEF_STANDARD_ACTIVITY = "Standardaktivität"

    // Activity Detail Settings
    override val AD_SETTINGS_LIST_TITLE = "Aktivität zum Ändern wählen"
    override val AD_SETTINGS_EDIT_TITLE = "Einstellungen"
    override val AD_SETTINGS_SECTION_WIDGETS = "Abschnitt: Widgets"
    override val AD_SETTINGS_SECTION_CHARTS = "Abschnitt: Diagramme"

    // Heart Rate Math
    override val HR_NO_DATA = "Keine HF-Daten"
    override val HR_TOO_LITTLE_DATA = "Zu wenige Daten"
    override val HR_BELOW_ZONES = "HF unterhalb der Zonen"
    override val HR_EFFECT_Z0 = "Niedrige Intensität / Aufwärmen"
    override val HR_EFFECT_Z1 = "Aerobe Basis und Erholung"
    override val HR_EFFECT_Z2 = "Effektive Fettverbrennung"
    override val HR_EFFECT_Z3 = "Verbesserung der aeroben Kapazität"
    override val HR_EFFECT_Z4 = "Erhöhung der Laktatschwelle"
    override val HR_EFFECT_Z5 = "Anaerobes Training und VO2 Max"
    override val HR_EFFECT_NONE = "Keine dominante Zone"

    // HR Zones Names
    override val ZONE_Z0 = "Aufwärmen"
    override val ZONE_Z1 = "Sehr leicht"
    override val ZONE_Z2 = "Leicht"
    override val ZONE_Z3 = "Moderat"
    override val ZONE_Z4 = "Hart"
    override val ZONE_Z5 = "Maximum"

    // ViewModels Messages
    override val VM_EXPORT_INITIALIZING = "Export wird initialisiert..."
    override fun vmExportGenerating(name: String, current: Int, total: Int) = "Generierung: $name ($current/$total)"
    override val VM_EXPORT_NO_FILES = "Keine Dateien generiert."
    override val VM_EXPORT_ZIPPING = "Zippen..."
    override fun vmExportError(msg: String) = "Fehler beim Export: $msg"
    override val VM_IMPORT_OPEN_ERROR = "Datei kann nicht geöffnet werden"
    override val VM_IMPORT_DUPLICATE_WARNING = "Potenzielles Duplikat erkannt (gleiche Startzeit und Dauer)."
    override val VM_IMPORT_SUCCESS = "Training erfolgreich importiert."
    override fun vmImportError(msg: String) = "Importfehler: $msg"

    // Gpx Importer
    override val GPX_NO_POINTS = "GPX-Datei enthält keine Trackpunkte."
    override val GPX_WARN_HR = "Datei enthält HF-Daten, aber die gewählte Aktivität unterstützt diese nicht."
    override val GPX_WARN_ELE = "Datei enthält Höhendaten, aber die gewählte Aktivität unterstützt diese nicht."
    override val GPX_WARN_CADENCE = "Datei enthält Trittfrequenzdaten, aber die gewählte Aktivität unterstützt diese nicht."

    // Periods
    override val PERIOD_TODAY = "Heute"
    override val PERIOD_WEEK = "Woche"
    override val PERIOD_MONTH = "Monat"
    override val PERIOD_YEAR = "Jahr"
    override val PERIOD_CUSTOM = "Benutzerdefiniert"
    override fun periodCustomDays(days: Int) = if (days == 1) "$days Tag" else "$days Tage"

    // Widgets
    override val WIDGET_COUNT = "Anzahl der Aktivitäten"
    override val WIDGET_CALORIES = "Verbrannte Kalorien"
    override val WIDGET_DISTANCE_GPS = "Distanz (GPS)"
    override val WIDGET_DISTANCE_STEPS = "Distanz (Schritte)"
    override val WIDGET_ASCENT = "Aufstieg insgesamt"
    override val WIDGET_DESCENT = "Abstieg insgesamt"
    override val WIDGET_STEPS = "Schritte"
    override val WIDGET_AVG_CADENCE = "Durchschn. Trittfrequenz"
    override val WIDGET_MAX_SPEED = "Max. Geschwindigkeit"
    override val WIDGET_MAX_ALTITUDE = "Max. Höhe"
    override val WIDGET_MAX_ELEVATION_GAIN = "Max. Höhengewinn"
    override val WIDGET_MAX_DISTANCE = "Max. Distanz"
    override val WIDGET_MAX_DURATION = "Max. Dauer"
    override val WIDGET_MAX_CALORIES = "Max. Kalorien"
    override val WIDGET_MAX_AVG_CADENCE = "Höchste durchschn. Trittfrequenz"
    override val WIDGET_MAX_AVG_SPEED = "Höchste durchschn. Geschwindigkeit"
    override val WIDGET_DURATION = "Dauer"
    override val WIDGET_MAX_BPM = "Max. HF"
    override val WIDGET_AVG_BPM = "Durchschn. HF"
    override val WIDGET_TOTAL_CALORIES = "Kalorien gesamt"
    override val WIDGET_MAX_CALORIES_MIN = "Max. Kalorienverbrauchsrata"
    override val WIDGET_AVG_PACE = "Durchschn. Pace"
    override val WIDGET_AVG_SPEED_GPS = "Durchschn. Geschw. (GPS)"
    override val WIDGET_AVG_SPEED_STEPS = "Durchschn. Geschw. (Schritte)"
    override val WIDGET_MAX_ALTITUDE_DESC = "Max. Höhe"
    override val WIDGET_TOTAL_ASCENT = "Aufstieg gesamt"
    override val WIDGET_TOTAL_DESCENT = "Abstieg gesamt"
    override val WIDGET_AVG_STEP_LENGTH = "Durchschn. Schrittlänge"
    override val WIDGET_AVG_CADENCE_DESC = "Durchschn. Trittfreq."
    override val WIDGET_MAX_CADENCE = "Max. Trittfreq."
    override val WIDGET_TOTAL_STEPS = "Schritte gesamt"
    override val WIDGET_PRESSURE_START = "Luftdruck (Start)"
    override val WIDGET_PRESSURE_END = "Luftdruck (Ende)"
    override val WIDGET_MAX_PRESSURE = "Max. Luftdruck"
    override val WIDGET_MIN_PRESSURE = "Min. Luftdruck"
    override val WIDGET_BEST_PACE_1KM = "Beste 1km Pace"
    override val WIDGET_WATCH_ASCENT = "Aufstieg gesamt"
    override val WIDGET_WATCH_DESCENT = "Abstieg gesamt"

    // Sensors
    override val SENSOR_HEART_RATE = "Herzfrequenz"
    override val SENSOR_CALORIES_SUM = "Kalorien gesamt"
    override val SENSOR_CALORIES_MIN = "Kalorien pro Minute"
    override val SENSOR_STEPS = "Schritte"
    override val SENSOR_STEPS_MIN = "Trittfrequenz (spm)"
    override val SENSOR_DISTANCE_STEPS = "Distanz (Schritte)"
    override val SENSOR_SPEED_GPS = "Geschwindigkeit"
    override val SENSOR_SPEED_STEPS = "Geschwindigkeit (Schritte)"
    override val SENSOR_DISTANCE_GPS = "Distanz"
    override val SENSOR_ALTITUDE = "Höhe"
    override val SENSOR_TOTAL_ASCENT = "Aufstieg gesamt"
    override val SENSOR_TOTAL_DESCENT = "Abstieg gesamt"
    override val SENSOR_PRESSURE = "Luftdruck"
    override val SENSOR_MAP = "Standortdaten"

    // Trim Screen
    override val TRIM_TITLE = "Training bearbeiten (Kürzen)"
    override val TRIM_CONFIRM_TITLE = "Kürzen bestätigen"
    override val TRIM_CONFIRM_DESC = "Möchten Sie die Daten außerhalb des gewählten Bereichs wirklich entfernen? Diese Daten werden dauerhaft gelöscht."
    override val TRIM_SAVE_BTN = "Kürzen und speichern"
    override val TRIM_CHART_HR = "HF-Diagramm"
    override val TRIM_RANGE_TITLE = "Trainingsbereich wählen"
    override val TRIM_PREVIEW_TITLE = "Vorschau der neuen Statistiken"
    override val TRIM_NEW_DURATION = "Neue Dauer:"
    override val TRIM_DISTANCE_GPS = "Distanz (GPS):"
    override val TRIM_DISTANCE_STEPS = "Distanz (Schritte):"
    override val TRIM_CALORIES = "Verbrannte Kalorien:"
    override val TRIM_AVG_BPM = "Durchschn. HF:"
    override val TRIM_START = "Start"
    override val TRIM_END = "Ende"

    // Compare Screen
    override val COMPARE_TITLE = "Aktivitätsvergleich"
    override val COMPARE_VS = "Vergleich:"
    override val COMPARE_HIGHER_IS_BETTER = "Höheres Ergebnis ist besser"
    override val COMPARE_LOWER_IS_BETTER = "Niedrigeres Ergebnis ist besser"

    // Units
    override val UNIT_KCAL = "kcal"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_STEP_MIN = "spm"
    override val UNIT_KM_H = "km/h"
    override val UNIT_STEPS = "Schritte"
    override val UNIT_HPA = "hPa"
    override val UNIT_MIN_KM = "min/km"
    override val UNIT_M_ASL = "m ü. NHN"
    override val UNIT_MIN_KM_LABEL = "min/km"
    override val UNIT_BPM = "bpm"
    override val UNIT_KCAL_MIN = "kcal/min"

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
            "avg_cadence" -> WIDGET_AVG_CADENCE
            "ascent" -> WIDGET_ASCENT
            "descent" -> WIDGET_DESCENT
            "calories" -> WIDGET_CALORIES
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
            else -> when (id) {
                "map" -> DETAIL_MAP
                "bpm" -> DETAIL_HEART_RATE
                else -> id
            }
        }
    }
}
