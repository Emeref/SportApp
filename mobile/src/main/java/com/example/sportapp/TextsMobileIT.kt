package com.example.sportapp

import java.util.Calendar
import java.util.Locale

object TextsMobileIT : MobileTexts {
    // Navigation
    override val NAV_HOME = "Inizio"
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
    override val HOME_LOGO_DESC = "Logo dell'applicazione"
    override val HOME_SECRET_TITLE = "Bello che tu faccia clic, ma qui non c'è nulla"
    override val HOME_CLOSE = "Chiudi"
    override val HOME_START_LIVE = "Avvia Live Tracking"
    override val HOME_ACTIVE_WORKOUT = "Attività in corso"
    override val HOME_RESUME_TRACKING = "Segui attività"

    override fun homeResultsToday() = "Risultati di oggi:"
    override fun homeResultsWeek() = "Risultati della settimana:"
    override fun homeResultsMonth(): String {
        val monthName = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("it", "IT"))
        return "Risultati di $monthName:"
    }
    override fun homeResultsYear() = "Risultati di quest'anno:"
    override fun homeResultsCustom(days: Int) = if (days == 1) "Risultati dell'ultimo giorno:" else "Risultati degli ultimi $days giorni:"

    // Settings Screen
    override val SETTINGS_TITLE = "Impostazioni"
    override val SETTINGS_GENERAL = "Generali"
    override val SETTINGS_THEME = "Tema dell'app"
    override val SETTINGS_THEME_SYSTEM = "Di sistema"
    override val SETTINGS_THEME_LIGHT = "Chiaro"
    override val SETTINGS_THEME_DARK = "Scuro"
    override val SETTINGS_LANGUAGE = "Lingua"
    override val SETTINGS_LANGUAGE_TITLE = "Scegli la lingua"
    override val SETTINGS_HEALTH_DATA = "Dati sanitari e FC"
    override val SETTINGS_HEALTH_DATA_DESC = "Età, peso, FC Max e zone"
    override val SETTINGS_DEFINITIONS = "Definizioni attività"
    override val SETTINGS_DEFINITIONS_DESC = "Gestisci la lista degli sport e i sensori"
    override val SETTINGS_WIDGETS_HOME = "Widget schermata iniziale"
    override val SETTINGS_WIDGETS_HOME_TITLE = "Vista iniziale"
    override val SETTINGS_WIDGETS_HOME_DESC = "Scegli e imposta l'ordine"
    override val SETTINGS_WIDGETS_WATCH = "Statistiche sull'orologio"
    override val SETTINGS_WIDGETS_WATCH_TITLE = "Campi statistici"
    override val SETTINGS_WIDGETS_WATCH_DESC = "Scegli e imposta l'ordine sull'orologio"
    override val SETTINGS_SAVE = "Salva"
    override val SETTINGS_CANCEL = "Annulla"
    override val SETTINGS_CLOSE = "Chiudi"
    override val SETTINGS_PERIOD = "Periodo predefinito"
    override val SETTINGS_PERIOD_HOME_DESC = "Per quale periodo mostrare i widget?"
    override val SETTINGS_PERIOD_WATCH_DESC = "Statistiche di quale periodo?"
    override val SETTINGS_CUSTOM_DAYS_LABEL = "Numero di giorni"
    override val SETTINGS_WATCH_STATS_DAYS_LABEL = "Mostra le statistiche di quanti giorni?"
    override val SETTINGS_CUSTOM_DAYS_DESC = "Numero di giorni per il periodo 'Altro'"
    override val SETTINGS_WATCH_STATS_DAYS_DESC = "Numero di giorni di statistiche sull'orologio"
    override val SETTINGS_INTEGRATION = "Integrazione"
    override val SETTINGS_SYNC = "Sincronizzazione"
    override val SETTINGS_STRAVA = "Strava"
    override val SETTINGS_STRAVA_DESC = "Sincronizza i tuoi allenamenti con Strava"
    override val SETTINGS_GOOGLE_DRIVE = "Google Drive"
    override val SETTINGS_GOOGLE_DRIVE_DESC = "Salva la cronologia e i riepiloghi (Presto)"
    override val SETTINGS_APPEARANCE = "Aspetto"
    override val SETTINGS_MY_PROFILE = "Il mio profilo"
    override val LANG_PL = "Polacco"
    override val LANG_EN = "Inglese"

    // Health Connect Strings
    override val SETTINGS_HC_TITLE = "Health Connect"
    override val SETTINGS_HC_MANAGE_PERMISSIONS = "Gestisci i permessi Health Connect"
    override val SETTINGS_HC_STATUS = "Stato Health Connect"
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
    override val HC_IMPORT_SELECT_FIELDS_TITLE = "Scegli i dati da importare"
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
    override val HC_IMPORT_EMPTY = "Nessun allenamento trovato in Health Connect nell'ultima settimana."
    override val HC_IMPORT_CONFIRM_DESC_PLURAL = "Sei sicuro di voler importare gli allenamenti selezionati?"
    override val HC_IMPORT_SELECT_ALL = "Seleziona tutti"
    override fun hcImportSelected(count: Int) = "Importa selezionati ($count)"
    override fun hcImportConfirmDesc(count: Int) = "Vuoi importare $count allenamenti?"
    override fun hcImportProgress(current: Int, total: Int) = "Importazione di $current/$total allenamenti..."

    // Stage 5 - Export
    override val HC_EXPORT_TO = "Esporta in Health Connect"
    override val HC_EXPORTED_ON = "Sincronizzato con Health Connect"
    override val HC_EXPORT_SUCCESS = "Esportazione completata con successo"
    override val HC_EXPORT_ERROR = "Errore di esportazione: "
    override val HC_EXPORT_PERMISSION_DENIED = "Permesso di scrittura negato in Health Connect"
    override val SETTINGS_HC_AUTO_EXPORT = "Esportazione automatica"
    override val SETTINGS_HC_AUTO_EXPORT_DESC = "Esporta automaticamente i nuovi allenamenti in Health Connect"

    // Stage 6 - Sync Status
    override val SYNC_STATUS_TITLE = "Stato sincronizzazione HC"
    override val SYNC_LAST_HEALTH = "Ultima sincro salute"
    override val SYNC_LAST_WORKOUT = "Ultima sincro allenamento"
    override val SYNC_UNSYNCED_COUNT = "Record non sincronizzati"
    override val SYNC_NOW = "Sincronizza ora"
    override val SYNC_HISTORY_TITLE = "Cronologia sincronizzazione"
    override val SYNC_TYPE_IMPORT = "Importazione"
    override val SYNC_TYPE_EXPORT = "Esportazione"
    override val SYNC_NEVER = "Mai"
    override val SYNC_CONFLICT_POLICY = "Politica dei conflitti"
    override val SYNC_CONFLICT_NEWER = "Vince il più recente"
    override val SYNC_CONFLICT_LOCAL = "Vince il locale"
    override val SYNC_CONFLICT_HC = "Vince Health Connect"

    // Health Connect Permissions Dialog
    override val HC_PERMISSIONS_DIALOG_TITLE = "Permessi richiesti"
    override val HC_PERMISSIONS_DIALOG_DESC = "I permessi di scrittura sono necessari per esportare gli allenamenti in Health Connect. Puoi concederli nelle impostazioni di sistema."
    override val HC_OPEN_SETTINGS = "Apri impostazioni"

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
    override val HEALTH_RESTING_HR = "Frequenza cardiaca a riposo"
    override val HEALTH_MAX_HR = "Frequenza cardiaca massima"
    override val HEALTH_MAX_HR_DESC = "Frequenza cardiaca massima"
    override val HEALTH_STEP_LENGTH = "Lunghezza del passo"
    override val HEALTH_STEP_LENGTH_CM = "Lunghezza del passo (cm)"
    override val HEALTH_VO2_MAX = "VO2 Max"

    // Activity List
    override val ACTIVITY_LIST_TITLE = "Lista attività"
    override val ACTIVITY_EMPTY = "Nessuna attività"
    override val ACTIVITY_DELETE_CONFIRM = "Sei sicuro di voler eliminare permanentemente le attività selezionate dal database?"
    override val ACTIVITY_COMPARE = "Confronta"
    override val ACTIVITY_TRIM = "Taglia"
    override val ACTIVITY_DETAIL = "Dettagli"
    override val ACTIVITY_EDIT = "Modifica"
    override val ACTIVITY_IMPORT_GPX = "Importa GPX"
    override val ACTIVITY_EXPORT_GPX = "Esporta GPX"
    override val ACTIVITY_CHART_SETTINGS = "Impostazioni grafici"
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
    override val ACTIVITY_IMPORT_SELECT_TYPE = "Scegli il tipo di attività"
    override val ACTIVITY_IMPORT_SELECT_DESC = "Scegli il tipo di allenamento per il file GPX importato:"
    override val ACTIVITY_IMPORT_WARNING = "Avviso"
    override val ACTIVITY_IMPORT_CONTINUE = "Continua"
    override val ACTIVITY_IMPORT_PROGRESS = "Importazione dati..."
    override val ACTIVITY_EXPORT_ERROR = "Errore di esportazione"
    override val ACTIVITY_EXPORT_DIALOG_TITLE = "Esporta attività"
    override val STR_STRAVA = "Strava"
    override val STR_HEALTH_CONNECT = "Health Connect"
    override val ACTIVITY_ALREADY_SYNCED = "Sincronizzato"
    override val ACTIVITY_EXPORT = "Esporta"
    override val ACTIVITY_SHARE_TITLE = "Condividi allenamento/i"
    override val ACTIVITY_OK = "OK"
    override val ACTIVITY_CONFIRM_DELETE_TITLE = "Elimina attività"
    override val ACTIVITY_ALL = "Tutte"
    override val ACTIVITY_NONE = "Nessuna"

    // New Export/Import SAE
    override val ACTIVITY_EXPORT_SAE = "Esporta SAE"
    override val ACTIVITY_IMPORT_SAE = "Importa SAE"
    override val ACTIVITY_EXPORT_FORMAT_SELECT = "Scegli il format di esportazione"
    override val ACTIVITY_EXPORT_SAE_DESC = "Il formato SAE (.sae) consente un backup completo di tutti i dati di allenamento, inclusi gli allenamenti indoor (senza GPS)."
    override val ACTIVITY_EXPORT_INCOMPATIBLE_GPX = "Alcune attività selezionate non hanno dati GPS e non possono essere esportate nel formato GPX."

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
    override val DETAIL_HR_ZONES = "Zone cardio"
    override val DETAIL_TRAINING_EFFECT = "Effetto allenamento"
    override val DETAIL_TRAINING_EFFECT_DESC = "Effetto allenamento dominante"
    override val DETAIL_LAP_NR = "N."
    override val DETAIL_LAP_TIME = "Tempo"
    override val DETAIL_LAP_AVG_PACE = "Passo medio"
    override val DETAIL_LAP_AVG_SPEED = "Velocità media"
    override val DETAIL_LAP_MAX_SPEED = "Velocità max"
    override val DETAIL_LAP_AVG_HR = "FC media"
    override val DETAIL_LAP_MAX_HR = "FC max"
    override val DETAIL_LAP_ASCENT_DESCENT = "Su/Giù"
    override val DETAIL_MAP_START = "Partenza"
    override val DETAIL_MAP_FINISH = "Arrivo"
    override val DETAIL_MAP_EXPAND = "Ingrandisci mappa"
    override val DETAIL_MAP_COLLAPSE = "Rimpicciolisci mappa"
    override val DETAIL_EXPAND = "Espandi"
    override val DETAIL_COLLAPSE = "Comprimi"
    override val DETAIL_PREDOMINANT_EFFECT = "Effetto allenamento dominante"

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
    override val STATS_NO_WIDGETS = "Nessun widget attivo. Attivali nelle opzioni."
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
    override val DEF_LIST_TITLE = "Definizione attività"
    override val DEF_SENSORS_DESC = "Gestisci la lista degli sport e i sensori"
    override val DEF_RECORDING = "Registrazione"
    override val DEF_SELECT_ICON = "Scegli l'icona"
    override val DEF_SAVE = "Salva"
    override val DEF_MOVE_UP = "Sposta su"
    override val DEF_MOVE_DOWN = "Sposta giù"
    override val DEF_DELETE_TITLE = "Elimina attività"
    override fun defDeleteConfirm(name: String) = "Sei sicuro di voler eliminare l'attività '$name'?"
    override val DEF_NEW_ACTIVITY = "Nuova attività"
    override val DEF_EDIT_ACTIVITY = "Modifica attività"
    override val DEF_NAME_LABEL = "Nome attività"
    override val DEF_NAME_HINT = "Es. Tapis roulant"
    override val DEF_AUTO_LAP_LABEL = "Giro automatico (metri, opzionale)"
    override val DEF_WIDGET_IN_ACTIVITY = "Widget nell'attività"
    override val DEF_VISIBILITY = "Visibilità"
    override val DEF_RECORD = "Registra"
    override val DEF_BASE_TYPE = "Tipo base"
    override val DEF_FINISH = "Termina"
    override val DEF_SELECT_ICON_TITLE = "Scegli l'icona"

    // Base Types
    override val DEF_WALKING = "Camminata"
    override val DEF_SPEED_WALKING = "Camminata veloce"
    override val DEF_RUNNING = "Corsa"
    override val DEF_TREADMILL_RUNNING = "Corsa su tapis roulant"
    override val DEF_STAIR_CLIMBING = "Scale"
    override val DEF_STAIR_CLIMBING_MACHINE = "Stepper"
    override val DEF_CYCLING = "Ciclismo"
    override val DEF_CYCLING_STATIONARY = "Cyclette"
    override val DEF_MOUNTAIN_BIKING = "Mountain bike"
    override val DEF_ROAD_BIKING = "Ciclismo su strada"
    override val DEF_HIKING = "Escursionismo"
    override val DEF_ROCK_CLIMBING = "Arrampicata"
    override val DEF_BOULDERING = "Bouldering"
    override val DEF_HIIT = "HIIT"
    override val DEF_ELLIPTICAL = "Ellittica"
    override val DEF_ROWING_MACHINE = "Vogatore"
    override val DEF_STRENGTH_TRAINING = "Allenamento pesi"
    override val DEF_CALISTHENICS = "Calistenia"
    override val DEF_YOGA = "Yoga"
    override val DEF_PILATES = "Pilates"
    override val DEF_AEROBICS = "Aerobica"
    override val DEF_DANCING = "Danza"
    override val DEF_SWIMMING = "Nuoto"
    override val DEF_SWIMMING_POOL = "Nuoto (piscina)"
    override val DEF_SWIMMING_OPEN_WATER = "Nuoto (acque libere)"
    override val DEF_KAYAKING = "Canoa/Kayak"
    override val DEF_PADDLE_BOARDING = "SUP (Paddle boarding)"
    override val DEF_SURFING = "Surf"
    override val DEF_SAILING = "Vela"
    override val DEF_FOOTBALL = "Calcio"
    override val DEF_BASKETBALL = "Basket"
    override val DEF_TENNIS = "Tennis"
    override val DEF_SQUASH = "Squash"
    override val DEF_VOLLEYBALL = "Pallavolo"
    override val DEF_GOLF = "Golf"
    override val DEF_MARTIAL_ARTS = "Arti marziali"
    override val DEF_SKIING = "Sci"
    override val DEF_SNOWBOARDING = "Snowboard"
    override val DEF_SKATING = "Pattini"
    override val DEF_ICE_SKATING = "Pattinaggio artistico"

    override val DEF_GYM = "Palestra"
    override val DEF_BASEBALL = "Baseball"
    override val DEF_SKATEBOARDING = "Skateboard"
    override val DEF_COMPETITION = "Competizione"
    override val DEF_STOPWATCH = "Cronometro"
    override val DEF_OTHER = "Altro"
    override val DEF_STANDARD_ACTIVITY = "Attività standard"

    // Activity Detail Settings
    override val AD_SETTINGS_LIST_TITLE = "Scegli l'attività da modificare"
    override val AD_SETTINGS_EDIT_TITLE = "Impostazioni"
    override val AD_SETTINGS_SECTION_WIDGETS = "Sezione: Widget"
    override val AD_SETTINGS_SECTION_CHARTS = "Sezione: Grafici"

    // Heart Rate Math
    override val HR_NO_DATA = "Nessun dato di frequenza cardiaca"
    override val HR_TOO_LITTLE_DATA = "Troppo pochi dati"
    override val HR_BELOW_ZONES = "FC sotto le zone"
    override val HR_EFFECT_Z0 = "Bassa intensità / Riscaldamento"
    override val HR_EFFECT_Z1 = "Base aerobica e rigenerazione"
    override val HR_EFFECT_Z2 = "Brucia grassi efficace"
    override val HR_EFFECT_Z3 = "Miglioramento capacità aerobica"
    override val HR_EFFECT_Z4 = "Aumento soglia anaerobica"
    override val HR_EFFECT_Z5 = "Allenamento anaerobico e VO2 Max"
    override val HR_EFFECT_NONE = "Nessuna zona dominante"

    // HR Zones Names
    override val ZONE_Z0 = "Riscaldamento"
    override val ZONE_Z1 = "Molto leggero"
    override val ZONE_Z2 = "Leggero"
    override val ZONE_Z3 = "Moderato"
    override val ZONE_Z4 = "Intenso"
    override val ZONE_Z5 = "Massimo"

    // ViewModels Messages
    override val VM_EXPORT_INITIALIZING = "Inizializzazione esportazione..."
    override fun vmExportGenerating(name: String, current: Int, total: Int) = "Generazione: $name ($current/$total)"
    override val VM_EXPORT_NO_FILES = "Nessun file generato."
    override val VM_EXPORT_ZIPPING = "Compressione in ZIP..."
    override fun vmExportError(msg: String) = "Errore durante l'esportazione: $msg"
    override val VM_IMPORT_OPEN_ERROR = "Impossibile aprire il file"
    override val VM_IMPORT_DUPLICATE_WARNING = "Rilevato potenziale duplicato (stessa data di inizio e durata)."
    override val VM_IMPORT_DUPLICATE_WARNING_DESC = "Vuoi continuare comunque?"
    override val VM_IMPORT_SUCCESS = "Allenamento importato con successo."
    override fun vmImportError(msg: String) = "Errore di importazione: $msg"

    // Gpx Importer
    override val GPX_NO_POINTS = "Il file GPX non contiene punti."
    override val GPX_WARN_HR = "Il file contiene dati FC, ma l'attività scelta non li supporta."
    override val GPX_WARN_ELE = "Il file contiene dati altitudine, ma l'attività scelta non li supporta."
    override val GPX_WARN_CADENCE = "Il file contiene dati cadenza, ma l'attività scelta non li supporta."

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
    override val WIDGET_MAX_SPEED = "Velocità max"
    override val WIDGET_MAX_ALTITUDE = "Altitudine max"
    override val WIDGET_MAX_ELEVATION_GAIN = "Massimo dislivello"
    override val WIDGET_MAX_DISTANCE = "Distanza massima"
    override val WIDGET_MAX_DURATION = "Durata massima"
    override val WIDGET_MAX_CALORIES = "Massimo consumo calorico"
    override val WIDGET_MAX_AVG_CADENCE = "Massima cadenza media"
    override val WIDGET_MAX_AVG_SPEED = "Massima velocità media"
    override val WIDGET_DURATION = "Durata"
    override val WIDGET_MAX_BPM = "FC massima"
    override val WIDGET_TOTAL_CALORIES = "Calorie bruciate"
    override val WIDGET_MAX_CALORIES_MIN = "Massimo consumo kcal"
    override val WIDGET_AVG_PACE = "Passo medio"
    override val WIDGET_AVG_SPEED_GPS = "Velocità media (GPS)"
    override val WIDGET_AVG_SPEED_STEPS = "Velocità media (passi)"
    override val WIDGET_MAX_ALTITUDE_DESC = "Altitudine max"
    override val WIDGET_TOTAL_ASCENT = "Somma salite"
    override val WIDGET_TOTAL_DESCENT = "Somma discese"
    override val WIDGET_AVG_STEP_LENGTH = "Lunghezza passo calcolata"
    override val WIDGET_AVG_CADENCE_DESC = "Cadenza media"
    override val WIDGET_MAX_CADENCE = "Cadenza max."
    override val WIDGET_TOTAL_STEPS = "Numero di passi"
    override val WIDGET_PRESSURE_START = "Pressione atm. (partenza)"
    override val WIDGET_PRESSURE_END = "Pressione atm. (arrivo)"
    override val WIDGET_MAX_PRESSURE = "Pressione max."
    override val WIDGET_MIN_PRESSURE = "Pressione min."
    override val WIDGET_BEST_PACE_1KM = "Miglior passo (1km)"
    override val WIDGET_WATCH_ASCENT = "Dislivello in salita"
    override val WIDGET_WATCH_DESCENT = "Dislivello in discesa"

    // Sensors
    override val SENSOR_HEART_RATE = "Frequenza cardiaca"
    override val SENSOR_CALORIES_SUM = "Calorie bruciate"
    override val SENSOR_CALORIES_MIN = "Calorie al minuto"
    override val SENSOR_STEPS = "Passi"
    override val SENSOR_STEPS_MIN = "Cadenza (passi/min)"
    override val SENSOR_DISTANCE_STEPS = "Distanza (passi)"
    override val SENSOR_SPEED_GPS = "Velocità"
    override val SENSOR_SPEED_STEPS = "Velocità (passi)"
    override val SENSOR_DISTANCE_GPS = "Distanza"
    override val SENSOR_ALTITUDE = "Altitudine"
    override val SENSOR_TOTAL_ASCENT = "Somma salite"
    override val SENSOR_TOTAL_DESCENT = "Somma discese"
    override val SENSOR_PRESSURE = "Pressione atm."
    override val SENSOR_MAP = "Dati posizione"
    override val SENSOR_AVG_STEP_LENGTH = "Lunghezza passo media"

    // Trim Screen
    override val TRIM_TITLE = "Modifica allenamento (Taglio)"
    override val TRIM_CONFIRM_TITLE = "Conferma taglio"
    override val TRIM_CONFIRM_DESC = "Sei sicuro di voler eliminare i dati fuori dall'intervallo selezionato? Questi dati verranno rimossi permanentemente."
    override val TRIM_SAVE_BTN = "Taglia e salva"
    override val TRIM_CHART_HR = "Grafico FC"
    override val TRIM_RANGE_TITLE = "Scegli l'intervallo di allenamento"
    override val TRIM_PREVIEW_TITLE = "Anteprima nuove statistiche"
    override val TRIM_NEW_DURATION = "Nuova durata:"
    override val TRIM_DISTANCE_GPS = "Distanza (GPS):"
    override val TRIM_DISTANCE_STEPS = "Distanza (Passi):"
    override val TRIM_CALORIES = "Calorie bruciate:"
    override val TRIM_AVG_BPM = "FC media:"
    override val TRIM_START = "Inizio"
    override val TRIM_END = "Fine"

    // Compare Screen
    override val COMPARE_TITLE = "Confronto attività"
    override val COMPARE_VS = "Confronto:"
    override val COMPARE_HIGHER_IS_BETTER = "Il punteggio più alto è migliore"
    override val COMPARE_LOWER_IS_BETTER = "Il punteggio più basso è migliore"

    // Units
    override val UNIT_KCAL = "kcal"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_STEP_MIN = "passi/min"
    override val UNIT_KM_H = "km/h"
    override val UNIT_STEPS = "passi"
    override val UNIT_HPA = "hPa"
    override val UNIT_MIN_KM = "min/km"
    override val UNIT_M_ASL = "m s.l.m."
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
            "avg_cadence" -> WIDGET_AVG_CADENCE
            "avg_step_length_over_time" -> SENSOR_AVG_STEP_LENGTH
            else -> id
        }
    }

    // Strava Strings
    override val STRAVA_TITLE = "Sincronizzazione con Strava"
    override val STRAVA_CONNECT = "Collega account Strava"
    override val STRAVA_DISCONNECT = "Scollega account Strava"
    override val STRAVA_CONNECTED = "Collegato a Strava"
    override val STRAVA_NOT_CONNECTED = "Non collegato"
    override val STRAVA_SYNC_NOW = "Sincronizza ora"
    override val STRAVA_SYNC_SUCCESS = "Allenamento inviato!"
    override val STRAVA_SYNC_FAILED = "Errore invio"
    override val STRAVA_SYNCING = "Invio in corso..."
    override val STRAVA_AUTH_ERROR = "Errore autorizzazione"
    override val SETTINGS_STRAVA_AUTO_EXPORT = "Esportazione automatica"
    override val SETTINGS_STRAVA_AUTO_EXPORT_DESC = "Invia automaticamente i nuovi allenamenti a Strava"
    override val STRAVA_SYNC_LOG = "Cronologia sincronizzazione"
    override val STRAVA_SYNC_LOG_EMPTY = "Nessuna cronologia di sincronizzazione"

    // Live Tracking
    override val LIVE_TRACKING_TITLE = "Live Tracking"
    override val LIVE_TRACKING_SELECT_ACTIVITY = "Scegli l'attività"
    override val LIVE_TRACKING_LOCK = "Blocca"
    override val LIVE_TRACKING_UNLOCK_SWIPE = "Scorri verso l'alto per sbloccare"
    override val LIVE_TRACKING_MAP_NORTH = "Nord"
    override val LIVE_TRACKING_MAP_DIRECTION = "Direzione"
    override val LIVE_TRACKING_WAITING_FOR_WATCH = "In attesa del segnale dall'orologio..."
    override val LIVE_TRACKING_FINISHED_TITLE = "Attività terminata"
    override val LIVE_TRACKING_FINISHED_DESC = "Attività terminata con successo"
    override val LIVE_TRACKING_BTN_FINISH = "Termina"
    override val LIVE_TRACKING_BTN_VIEW_MAP = "Guarda la mappa"
    override val LIVE_TRACKING_PAUSED = "Pausa"

    // Errors
    override val ERROR_WEARABLE_NOT_AVAILABLE = "I servizi Wearable non sono disponibili su questo dispositivo"
    override val ERROR_NO_WATCH_CONNECTED = "Nessun orologio collegato trovato"

    // Map Types
    override val MAP_TYPE_TITLE = "Tipo di mappa"
    override val MAP_TYPE_NORMAL = "Normale"
    override val MAP_TYPE_SATELLITE = "Satellite"
    override val MAP_TYPE_HYBRID = "Ibrida"
    override val MAP_TYPE_TERRAIN = "Terreno"

    // Support System
    override val SUPPORT_TITLE = "Sostieni l'autore"
    override val SUPPORT_DISCLAIMER = "Tutte le funzioni dell'app sono gratuite. La scelta del livello è un sostegno volontario all'autore. Il cambio dell'icona è l'unico beneficio digitale."
    override val SUPPORT_LIFETIME_BUY = "A vita"
    override val SUPPORT_MONTHLY_SUB = "Mensilmente"
    override val SUPPORT_ICON_CHANGE_NOTICE = "Il cambio dell'icona potrebbe richiedere alcuni secondi o il riavvio del launcher."
}