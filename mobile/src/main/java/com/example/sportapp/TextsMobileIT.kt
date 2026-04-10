package com.example.sportapp

import java.util.Calendar
import java.util.Locale

object TextsMobileIT : MobileTexts {
    // Navigation
    override val NAV_HOME = "Home"
    override val NAV_STATS = "Statistiche"
    override val NAV_ACTIVITIES = "Attività"
    override val NAV_SETTINGS = "Impostazioni"

    // Home Screen
    override val HOME_TITLE = "SportApp"
    override val HOME_NO_WIDGETS = "Nessun widget selezionato"
    override val HOME_ADD_WIDGETS = "Aggiungi widget"
    override val HOME_LAST_ACTIVITY = "Ultima attività"
    override val HOME_ACTIVITY_COUNT = "Numero di attività"
    override val HOME_SYNC = "Sincronizza"
    override val HOME_OPTIONS = "Opzioni"
    override val HOME_GENERAL_STATS = "Statistiche generali"
    override val HOME_WORKOUT_DETAILS = "Dettagli allenamento"
    override val HOME_LOGO_DESC = "Logo dell'app"
    override val HOME_SECRET_TITLE = "Bello che tu stia cliccando, ma non c'è niente qui"
    override val HOME_CLOSE = "Chiudi"

    override fun homeResultsToday() = "Risultati di oggi:"
    override fun homeResultsWeek() = "Risultati della settimana:"
    override fun homeResultsMonth(): String {
        val monthName = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ITALIAN)
        return "Risultati di $monthName:"
    }
    override fun homeResultsYear() = "Risultati di quest'anno:"
    override fun homeResultsCustom(days: Int) = if (days == 1) "Risultati dell'ultimo giorno:" else "Risultati degli ultimi $days giorni:"

    // Settings Screen
    override val SETTINGS_TITLE = "Impostazioni"
    override val SETTINGS_GENERAL = "Generale"
    override val SETTINGS_THEME = "Tema dell'app"
    override val SETTINGS_THEME_SYSTEM = "Sistema"
    override val SETTINGS_THEME_LIGHT = "Chiaro"
    override val SETTINGS_THEME_DARK = "Scuro"
    override val SETTINGS_LANGUAGE = "Lingua"
    override val SETTINGS_LANGUAGE_TITLE = "Seleziona lingua"
    override val SETTINGS_HEALTH_DATA = "Dati sanitari e FC"
    override val SETTINGS_HEALTH_DATA_DESC = "Età, peso, FC Max e zone"
    override val SETTINGS_DEFINITIONS = "Definizioni attività"
    override val SETTINGS_DEFINITIONS_DESC = "Gestisci elenco sport e sensori"
    override val SETTINGS_WIDGETS_HOME = "Widget schermata iniziale"
    override val SETTINGS_WIDGETS_HOME_TITLE = "Vista Home"
    override val SETTINGS_WIDGETS_HOME_DESC = "Seleziona e imposta l'ordine"
    override val SETTINGS_WIDGETS_WATCH = "Statistiche orologio"
    override val SETTINGS_WIDGETS_WATCH_TITLE = "Campi statistiche"
    override val SETTINGS_WIDGETS_WATCH_DESC = "Seleziona e imposta l'ordine sull'orologio"
    override val SETTINGS_SAVE = "Salva"
    override val SETTINGS_CANCEL = "Anulla"
    override val SETTINGS_CLOSE = "Chiudi"
    override val SETTINGS_PERIOD = "Periodo predefinito"
    override val SETTINGS_PERIOD_HOME_DESC = "Per quale periodo mostrare i widget?"
    override val SETTINGS_PERIOD_WATCH_DESC = "Statistiche di quale periodo?"
    override val SETTINGS_CUSTOM_DAYS_LABEL = "Numero di giorni"
    override val SETTINGS_INTEGRATION = "Integrazione"
    override val SETTINGS_GOOGLE_DRIVE = "Google Drive"
    override val SETTINGS_GOOGLE_DRIVE_DESC = "Salva cronologia e riepiloghi (Presto)"
    override val SETTINGS_APPEARANCE = "Aspetto"
    override val SETTINGS_MY_PROFILE = "Il mio profilo"
    override val LANG_PL = "Polacco"
    override val LANG_EN = "Inglese"

    // Health Connect Strings
    override val SETTINGS_HC_MANAGE_PERMISSIONS = "Gestisci i permessi di Health Connect"
    override val SETTINGS_HC_STATUS = "Stato di Health Connect"
    override val HC_STATUS_AVAILABLE = "Disponibile"
    override val HC_STATUS_UNAVAILABLE = "Non disponibile"
    override val HC_STATUS_NOT_INSTALLED = "Non installato"
    override val HC_INSTALL = "Installa"
    override val HC_SYNC_HEALTH_DATA = "Sincronizza con Health Connect"
    override val HC_SYNC_WORKOUTS = "Importa allenamenti da Health Connect"
    override val HC_SYNC_CONFIRM_TITLE = "Sincronizzazione dati"
    override val HC_SYNC_CONFIRM_DESC = "Vuoi aggiornare il tuo profilo con i dati trovati in Health Connect?"
    override val HC_SYNC_SUCCESS = "Sincronizzazione riuscita"
    override val HC_SYNC_ERROR = "Errore di sincronizzazione"
    override val HC_SYNC_NO_DATA = "Nessun nuovo dato trovato"
    override fun hcSyncPreview(weight: String?, height: String?, vo2max: String?): String {
        val parts = mutableListOf<String>()
        weight?.let { parts.add("peso $it kg") }
        height?.let { parts.add("altezza $it cm") }
        vo2max?.let { parts.add("VO2Max $it ml/kg/min") }
        return "Trovato in Health Connect: ${parts.joinToString(", ")} – aggiornare?"
    }

    // Stage 3 - Import Workouts
    override val HC_IMPORT_TITLE = "Importa allenamenti"
    override val HC_IMPORT_ALREADY_IMPORTED = "Già importato"
    override val HC_IMPORT_EMPTY = "Nessun allenamento trovato in Health Connect negli ultimi 30 giorni."
    override val HC_IMPORT_CONFIRM_DESC_PLURAL = "Sei sicuro di voler importare gli allenamenti selezionati?"
    override fun hcImportSelected(count: Int) = "Importa selezionati ($count)"
    override fun hcImportConfirmDesc(count: Int) = "Vuoi importare $count allenamenti?"
    override fun hcImportProgress(current: Int, total: Int) = "Importazione di $current/$total allenamenti..."

    // Health Data Screen
    override val HEALTH_TITLE = "Dati sanitari"
    override val HEALTH_GENDER = "Genere"
    override val HEALTH_GENDER_MALE = "Uomo"
    override val HEALTH_GENDER_FEMALE = "Donna"
    override val HEALTH_AGE = "Età"
    override val HEALTH_WEIGHT = "Peso"
    override val HEALTH_WEIGHT_KG = "Peso (kg)"
    override val HEALTH_HEIGHT = "Altezza"
    override val HEALTH_HEIGHT_CM = "Altezza (cm)"
    override val HEALTH_RESTING_HR = "FC a riposo"
    override val HEALTH_MAX_HR = "FC Max"
    override val HEALTH_MAX_HR_DESC = "FC Massima"
    override val HEALTH_STEP_LENGTH = "Lunghezza passo"
    override val HEALTH_STEP_LENGTH_CM = "Lunghezza passo (cm)"
    override val HEALTH_VO2_MAX = "VO2 Max"

    // Activity List
    override val ACTIVITY_LIST_TITLE = "Elenco attività"
    override val ACTIVITY_EMPTY = "Nessuna attività"
    override val ACTIVITY_DELETE_CONFIRM = "Sei sicuro di voler eliminare permanentemente le attività selezionate dal database?"
    override val ACTIVITY_COMPARE = "Confronta"
    override val ACTIVITY_TRIM = "Taglia"
    override val ACTIVITY_DETAIL = "Dettagli"
    override val ACTIVITY_EDIT = "Modifica"
    override val ACTIVITY_IMPORT_GPX = "Importa GPX"
    override val ACTIVITY_EXPORT_GPX = "Esporta GPX"
    override val ACTIVITY_CHART_SETTINGS = "Impostazioni grafico"
    override val ACTIVITY_FILTERS = "Filtri"
    override val ACTIVITY_ALL_TYPES = "Tutti i tipi"
    override val ACTIVITY_FROM = "Da"
    override val ACTIVITY_TO = "A"
    override val ACTIVITY_TYPE = "Tipo"
    override val ACTIVITY_DATE = "Data"
    override val ACTIVITY_DURATION = "Durata"
    override val ACTIVITY_CALORIES = "Calorie"
    override val ACTIVITY_DISTANCE_GPS = "Distanza (GPS)"
    override val ACTIVITY_DISTANCE_STEPS = "Distanza (passi)"
    override val ACTIVITY_DELETE = "Elimina"
    override val ACTIVITY_IMPORT_SELECT_TYPE = "Seleziona tipo attività"
    override val ACTIVITY_IMPORT_SELECT_DESC = "Seleziona il tipo di allenamento per il file GPX importato:"
    override val ACTIVITY_IMPORT_WARNING = "Avviso"
    override val ACTIVITY_IMPORT_CONTINUE = "Continua"
    override val ACTIVITY_IMPORT_PROGRESS = "Importazione dati..."
    override val ACTIVITY_EXPORT_ERROR = "Errore di esportazione"
    override val ACTIVITY_SHARE_TITLE = "Condividi allenamento/i"
    override val ACTIVITY_OK = "OK"
    override val ACTIVITY_CONFIRM_DELETE_TITLE = "Elimina attività"
    override val ACTIVITY_ALL = "Tutto"

    // Activity Detail
    override val DETAIL_TITLE = "Dettagli attività"
    override val DETAIL_MAP = "Mappa"
    override val DETAIL_CHARTS = "Grafici"
    override val DETAIL_LAPS = "Giri"
    override val DETAIL_STATISTICS = "Statistiche"
    override val DETAIL_DATA_ERROR_TITLE = "Errore dati"
    override val DETAIL_ERROR_OK = "OK"
    override val DETAIL_INTERVALS = "Intervalli"
    override fun detailLapsWithDistance(distance: String) = "Intervalli ($distance)"
    override fun detailLapsCount(count: Int) = "Numero di giri: $count"
    override val DETAIL_HEART_RATE = "Frequenza cardiaca (bpm)"
    override val DETAIL_HR_ZONES = "Zone FC"
    override val DETAIL_TRAINING_EFFECT = "Effetto allenamento"
    override val DETAIL_TRAINING_EFFECT_DESC = "Effetto allenamento predominante"
    override val DETAIL_LAP_NR = "N."
    override val DETAIL_LAP_TIME = "Tempo"
    override val DETAIL_LAP_AVG_PACE = "Pace medio"
    override val DETAIL_LAP_AVG_SPEED = "Velocità media"
    override val DETAIL_LAP_MAX_SPEED = "Velocità massima"
    override val DETAIL_LAP_AVG_HR = "FC media"
    override val DETAIL_LAP_MAX_HR = "FC massima"
    override val DETAIL_LAP_ASCENT_DESCENT = "Salita/Discesa"
    override val DETAIL_MAP_START = "Inizio"
    override val DETAIL_MAP_FINISH = "Fine"
    override val DETAIL_MAP_EXPAND = "Ingrandisci mappa"
    override val DETAIL_MAP_COLLAPSE = "Riduci mappa"
    override val DETAIL_EXPAND = "Espandi"
    override val DETAIL_COLLAPSE = "Riduci"
    override val DETAIL_PREDOMINANT_EFFECT = "Effetto allenamento predominante"

    // Stats
    override val STATS_TITLE = "Statistiche generali"
    override val STATS_CHARTS = "Grafici"
    override val STATS_WIDGETS = "Widget"
    override val STATS_NO_DATA = "Nessun dato per visualizzare i grafici."
    override val STATS_TREND_CHARTS = "Grafici di tendenza"
    override val STATS_FILTERS = "Filtri"
    override val STATS_ALL_TYPES = "Tutti i tipi"
    override val STATS_FROM = "Da"
    override val STATS_TO = "A"
    override val STATS_NO_WIDGETS = "Nessun widget attivo. Abilitali nelle opzioni."
    override val STATS_SETTINGS_TITLE = "Impostazioni statistiche generali"
    override val STATS_SECTION_WIDGETS = "Sezione: Widget"
    override val STATS_SECTION_CHARTS = "Sezione: Grafici di tendenza"
    override val STATS_MOVE_UP = "Sposta su"
    override val STATS_MOVE_DOWN = "Sposta giù"
    override fun chartDistanceGps(km: Boolean) = if (km) "Distanza (GPS) in km" else "Distanza (GPS) in m"
    override fun chartDistanceSteps(km: Boolean) = if (km) "Distanza (passi) in km" else "Distanza (passi) in m"
    override val CHART_STEPS = "Passi"

    // Definitions
    override val DEF_TITLE = "Definizioni allenamento"
    override val DEF_ADD = "Aggiungi definizione"
    override val DEF_EDIT = "Modifica definizione"
    override val DEF_DELETE = "Elimina definizione"
    override val DEF_NAME = "Nome"
    override val DEF_ICON = "Icona"
    override val DEF_SENSORS = "Sensori"
    override val DEF_LIST_TITLE = "Definizioni attività"
    override val DEF_SENSORS_DESC = "Gestisci elenco sport e sensori"
    override val DEF_RECORDING = "Registrazione"
    override val DEF_SELECT_ICON = "Seleziona icona"
    override val DEF_SAVE = "Salva"
    override val DEF_MOVE_UP = "Sposta su"
    override val DEF_MOVE_DOWN = "Sposta giù"
    override val DEF_DELETE_TITLE = "Elimina attività"
    override fun defDeleteConfirm(name: String) = "Sei sicuro di voler eliminare l'attività '$name'?"
    override val DEF_NEW_ACTIVITY = "Nuova attività"
    override val DEF_EDIT_ACTIVITY = "Modifica attività"
    override val DEF_NAME_LABEL = "Nome attività"
    override val DEF_AUTO_LAP_LABEL = "Giro automatico (metri, opcjonalnie)"
    override val DEF_WIDGET_IN_ACTIVITY = "Widget in attività"
    override val DEF_VISIBILITY = "Visibilità"
    override val DEF_RECORD = "Registra"
    override val DEF_BASE_TYPE = "Tipo base"
    override val DEF_FINISH = "Fine"
    override val DEF_SELECT_ICON_TITLE = "Seleziona icona"
    override val DEF_WALKING = "Camminata"
    override val DEF_RUNNING = "Corsa"
    override val DEF_CYCLING = "Ciclismo"
    override val DEF_HIKING = "Escursionismo"
    override val DEF_SWIMMING = "Nuoto"
    override val DEF_GYM = "Palestra"
    override val DEF_YOGA = "Yoga"
    override val DEF_TENNIS = "Tennis"
    override val DEF_KAYAKING = "Kayak"
    override val DEF_SNOWBOARDING = "Snowboard"
    override val DEF_SURFING = "Surf"
    override val DEF_SKATING = "Pattinaggio"
    override val DEF_GOLF = "Golf"
    override val DEF_FOOTBALL = "Calcio"
    override val DEF_BASKETBALL = "Basket"
    override val DEF_VOLLEYBALL = "Pallavolo"
    override val DEF_BASEBALL = "Baseball"
    override val DEF_SAILING = "Vela"
    override val DEF_SKATEBOARDING = "Skate"
    override val DEF_COMPETITION = "Competizione"
    override val DEF_STOPWATCH = "Cronometro"
    override val DEF_OTHER = "Altro"
    override val DEF_STANDARD_ACTIVITY = "Attività standard"

    // Activity Detail Settings
    override val AD_SETTINGS_LIST_TITLE = "Seleziona attività da modificare"
    override val AD_SETTINGS_EDIT_TITLE = "Impostazioni"
    override val AD_SETTINGS_SECTION_WIDGETS = "Sezione: Widget"
    override val AD_SETTINGS_SECTION_CHARTS = "Sezione: Grafici"

    // Heart Rate Math
    override val HR_NO_DATA = "Nessun dato FC"
    override val HR_TOO_LITTLE_DATA = "Troppi pochi dati"
    override val HR_BELOW_ZONES = "FC sotto le zone"
    override val HR_EFFECT_Z0 = "Bassa intensità / Riscaldamento"
    override val HR_EFFECT_Z1 = "Base aerobica e recupero"
    override val HR_EFFECT_Z2 = "Brucia grassi efficiente"
    override val HR_EFFECT_Z3 = "Miglioramento capacità aerobica"
    override val HR_EFFECT_Z4 = "Aumento soglia lattacida"
    override val HR_EFFECT_Z5 = "Allenamento anaerobico e VO2 Max"
    override val HR_EFFECT_NONE = "Nessuna zona dominante"

    // HR Zones Names
    override val ZONE_Z0 = "Riscaldamento"
    override val ZONE_Z1 = "Molto leggero"
    override val ZONE_Z2 = "Leggero"
    override val ZONE_Z3 = "Moderato"
    override val ZONE_Z4 = "Difficile"
    override val ZONE_Z5 = "Massimo"

    // Compare Screen
    override val COMPARE_TITLE = "Confronto attività"
    override val COMPARE_VS = "Confronto:"
    override val COMPARE_HIGHER_IS_BETTER = "Un risultato più alto è migliore"
    override val COMPARE_LOWER_IS_BETTER = "Un risultato più basso è migliore"

    // ViewModels Messages
    override val VM_EXPORT_INITIALIZING = "Inizializzazione esportazione..."
    override fun vmExportGenerating(name: String, current: Int, total: Int) = "Generazione: $name ($current/$total)"
    override val VM_EXPORT_NO_FILES = "Nessun file generato."
    override val VM_EXPORT_ZIPPING = "Compressione in ZIP..."
    override fun vmExportError(msg: String) = "Errore durante l'esportazione: $msg"
    override val VM_IMPORT_OPEN_ERROR = "Impossibile aprire il file"
    override val VM_IMPORT_DUPLICATE_WARNING = "Rilevato potenziale duplicato (stessa ora di inizio e durata)."
    override val VM_IMPORT_DUPLICATE_WARNING_DESC = "Vuoi continuare comunque?"
    override val VM_IMPORT_SUCCESS = "Allenamento importato con successo."
    override fun vmImportError(msg: String) = "Errore di importazione: $msg"

    // Gpx Importer
    override val GPX_NO_POINTS = "Il file GPX non contiene punti di traccia."
    override val GPX_WARN_HR = "Il file contiene dati FC, ma l'attività selezionata non li supporta."
    override val GPX_WARN_ELE = "Il file contiene dati di altitudine, ma l'attività selezionata non li supporta."
    override val GPX_WARN_CADENCE = "Il file contiene dati di cadenza, ma l'attività selezionata non li supporta."

    // Periods
    override val PERIOD_TODAY = "Oggi"
    override val PERIOD_WEEK = "Settimana"
    override val PERIOD_MONTH = "Mese"
    override val PERIOD_YEAR = "Anno"
    override val PERIOD_CUSTOM = "Altro"
    override fun periodCustomDays(days: Int) = if (days == 1) "$days giorno" else "$days giorni"

    // Widgets
    override val WIDGET_COUNT = "Numero di attività"
    override val WIDGET_CALORIES = "Calorie bruciate"
    override val WIDGET_DISTANCE_GPS = "Distanza (GPS)"
    override val WIDGET_DISTANCE_STEPS = "Distanza (passi)"
    override val WIDGET_ASCENT = "Ascesa totale"
    override val WIDGET_DESCENT = "Discesa totale"
    override val WIDGET_STEPS = "Passi"
    override val WIDGET_AVG_BPM = "FC media"
    override val WIDGET_AVG_CADENCE = "Cadenza media"
    override val WIDGET_MAX_SPEED = "Velocità massima"
    override val WIDGET_MAX_ALTITUDE = "Altitudine massima"
    override val WIDGET_MAX_ELEVATION_GAIN = "Massimo guadagno di quota"
    override val WIDGET_MAX_DISTANCE = "Distanza massima"
    override val WIDGET_MAX_DURATION = "Durata massima"
    override val WIDGET_MAX_CALORIES = "Più calorie"
    override val WIDGET_MAX_AVG_CADENCE = "Massima cadenza media"
    override val WIDGET_MAX_AVG_SPEED = "Massima velocità media"
    override val WIDGET_DURATION = "Durata"
    override val WIDGET_MAX_BPM = "FC massima"
    override val WIDGET_TOTAL_CALORIES = "Calorie totali"
    override val WIDGET_MAX_CALORIES_MIN = "Massima velocità di combustione calorie"
    override val WIDGET_AVG_PACE = "Pace medio"
    override val WIDGET_AVG_SPEED_GPS = "Velocità media (GPS)"
    override val WIDGET_AVG_SPEED_STEPS = "Velocità media (passos)"
    override val WIDGET_MAX_ALTITUDE_DESC = "Altitudine massima"
    override val WIDGET_TOTAL_ASCENT = "Somma ascese"
    override val WIDGET_TOTAL_DESCENT = "Somma discese"
    override val WIDGET_AVG_STEP_LENGTH = "Lunghezza passo media"
    override val WIDGET_AVG_CADENCE_DESC = "Cadenza media"
    override val WIDGET_MAX_CADENCE = "Cadenza massima"
    override val WIDGET_TOTAL_STEPS = "Conteggio passi"
    override val WIDGET_PRESSURE_START = "Pressione atm. (inizio)"
    override val WIDGET_PRESSURE_END = "Pressione atm. (fine)"
    override val WIDGET_MAX_PRESSURE = "Pressione atm. massima"
    override val WIDGET_MIN_PRESSURE = "Pressione atm. minima"
    override val WIDGET_BEST_PACE_1KM = "Miglior passo (1km)"
    override val WIDGET_WATCH_ASCENT = "Dislivello positivo"
    override val WIDGET_WATCH_DESCENT = "Dislivello negativo"

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

    // Sensors
    override val SENSOR_HEART_RATE = "Frequenza cardiaca"
    override val SENSOR_CALORIES_SUM = "Calorie totali"
    override val SENSOR_CALORIES_MIN = "Calorie al minuto"
    override val SENSOR_STEPS = "Passi"
    override val SENSOR_STEPS_MIN = "Cadenza (passi/min)"
    override val SENSOR_DISTANCE_STEPS = "Distanza (passi)"
    override val SENSOR_SPEED_GPS = "Velocità"
    override val SENSOR_SPEED_STEPS = "Velocità (passi)"
    override val SENSOR_DISTANCE_GPS = "Distanza"
    override val SENSOR_ALTITUDE = "Altitudine"
    override val SENSOR_TOTAL_ASCENT = "Ascesa totale"
    override val SENSOR_TOTAL_DESCENT = "Discesa totale"
    override val SENSOR_PRESSURE = "Pressione atm."
    override val SENSOR_MAP = "Dati posizione"

    // Units
    override val UNIT_KCAL = "kcal"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_STEP_MIN = "pas/min"
    override val UNIT_KM_H = "km/h"
    override val UNIT_STEPS = "passi"
    override val UNIT_HPA = "hPa"
    override val UNIT_MIN_KM = "min/km"
    override val UNIT_M_ASL = "m s.l.m."
    override val UNIT_MIN_KM_LABEL = "min/km"
    override val UNIT_BPM = "bpm"
    override val UNIT_KCAL_MIN = "kcal/min"
    override val UNIT_VO2_MAX = "ml/kg/min"
    
    // New metric
    override val SENSOR_AVG_STEP_LENGTH = "Lunghezza passo media"

    // Trim Screen
    override val TRIM_TITLE = "Modifica allenamento (Taglia)"
    override val TRIM_CONFIRM_TITLE = "Confirmer taglio"
    override val TRIM_CONFIRM_DESC = "Sei sicuro di voler rimuovere i dati fuori dall'intervallo selezionato? Questi dati verranno eliminati permanentemente."
    override val TRIM_SAVE_BTN = "Taglia e salva"
    override val TRIM_CHART_HR = "Grafico FC"
    override val TRIM_RANGE_TITLE = "Seleziona intervallo allenamento"
    override val TRIM_PREVIEW_TITLE = "Anteprima nuove statistiche"
    override val TRIM_NEW_DURATION = "Nuova durata:"
    override val TRIM_DISTANCE_GPS = "Distanza (GPS):"
    override val TRIM_DISTANCE_STEPS = "Distanza (Passi):"
    override val TRIM_CALORIES = "Calorie bruciate:"
    override val TRIM_AVG_BPM = "FC media:"
    override val TRIM_START = "Inizio"
    override val TRIM_END = "Fine"
}
