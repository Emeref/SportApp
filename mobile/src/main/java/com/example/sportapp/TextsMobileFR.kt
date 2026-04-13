package com.example.sportapp

import java.util.Calendar
import java.util.Locale

object TextsMobileFR : MobileTexts {
    // Navigation
    override val NAV_HOME = "Accueil"
    override val NAV_STATS = "Stats"
    override val NAV_ACTIVITIES = "Activités"
    override val NAV_SETTINGS = "Paramètres"

    // Home Screen
    override val HOME_TITLE = "SportApp"
    override val HOME_NO_WIDGETS = "Aucun widget sélectionné"
    override val HOME_ADD_WIDGETS = "Ajouter des widgets"
    override val HOME_LAST_ACTIVITY = "Dernière activité"
    override val HOME_ACTIVITY_COUNT = "Nombre d'activités"
    override val HOME_SYNC = "Sync"
    override val HOME_OPTIONS = "Options"
    override val HOME_GENERAL_STATS = "Statistiques générales"
    override val HOME_WORKOUT_DETAILS = "Détails de l'entraînement"
    override val HOME_LOGO_DESC = "Logo de l'application"
    override val HOME_SECRET_TITLE = "C'est super de cliquer, mais il n'y a rien ici"
    override val HOME_CLOSE = "Fermer"

    override fun homeResultsToday() = "Résultats d'aujourd'hui :"
    override fun homeResultsWeek() = "Résultats de la week :"
    override fun homeResultsMonth(): String {
        val monthName = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRENCH)
        return "Résultats de $monthName :"
    }
    override fun homeResultsYear() = "Résultats de cette année :"
    override fun homeResultsCustom(days: Int) = if (days == 1) "Résultats du dernier jour :" else "Résultats des $days derniers jours :"

    // Settings Screen
    override val SETTINGS_TITLE = "Paramètres"
    override val SETTINGS_GENERAL = "Géral"
    override val SETTINGS_THEME = "Thème de l'application"
    override val SETTINGS_THEME_SYSTEM = "Système"
    override val SETTINGS_THEME_LIGHT = "Clair"
    override val SETTINGS_THEME_DARK = "Sombre"
    override val SETTINGS_LANGUAGE = "Langue"
    override val SETTINGS_LANGUAGE_TITLE = "Choisir la langue"
    override val SETTINGS_HEALTH_DATA = "Données de santé et FC"
    override val SETTINGS_HEALTH_DATA_DESC = "Âge, poids, FC Max et zones"
    override val SETTINGS_DEFINITIONS = "Définitions d'activité"
    override val SETTINGS_DEFINITIONS_DESC = "Gérer la liste des sports et capteurs"
    override val SETTINGS_WIDGETS_HOME = "Widgets de l'écran d'accueil"
    override val SETTINGS_WIDGETS_HOME_TITLE = "Vue d'accueil"
    override val SETTINGS_WIDGETS_HOME_DESC = "Sélectionner et définir l'ordre"
    override val SETTINGS_WIDGETS_WATCH = "Stats de la montre"
    override val SETTINGS_WIDGETS_WATCH_TITLE = "Champs de statistiques"
    override val SETTINGS_WIDGETS_WATCH_DESC = "Sélectionner et définir l'ordre sur la montre"
    override val SETTINGS_SAVE = "Enregistrer"
    override val SETTINGS_CANCEL = "Annuler"
    override val SETTINGS_CLOSE = "Fermer"
    override val SETTINGS_PERIOD = "Période par défaut"
    override val SETTINGS_PERIOD_HOME_DESC = "Pour quelle période afficher les widgets ?"
    override val SETTINGS_PERIOD_WATCH_DESC = "Stats de quelle période ?"
    override val SETTINGS_CUSTOM_DAYS_LABEL = "Nombre de jours"
    override val SETTINGS_INTEGRATION = "Intégration"
    override val SETTINGS_GOOGLE_DRIVE = "Google Drive"
    override val SETTINGS_GOOGLE_DRIVE_DESC = "Sauvegarder l'historique et les résumés (Bientôt)"
    override val SETTINGS_APPEARANCE = "Apparence"
    override val SETTINGS_MY_PROFILE = "Mon profil"
    override val LANG_PL = "Polonais"
    override val LANG_EN = "Anglais"

    // Health Connect Strings
    override val SETTINGS_HC_MANAGE_PERMISSIONS = "Gérer les permissions Health Connect"
    override val SETTINGS_HC_STATUS = "Statut Health Connect"
    override val HC_STATUS_AVAILABLE = "Disponible"
    override val HC_STATUS_UNAVAILABLE = "Indisponible"
    override val HC_STATUS_NOT_INSTALLED = "Non installé"
    override val HC_INSTALL = "Installer"
    override val HC_SYNC_HEALTH_DATA = "Synchroniser avec Health Connect"
    override val HC_SYNC_WORKOUTS = "Importer les entraînements de Health Connect"
    override val HC_SYNC_CONFIRM_TITLE = "Synchronisation des données"
    override val HC_SYNC_CONFIRM_DESC = "Voulez-vous mettre à jour votre profil avec les données trouvées dans Health Connect ?"
    override val HC_SYNC_SUCCESS = "Synchronisation réussie"
    override val HC_SYNC_ERROR = "Erreur de synchronisation"
    override val HC_SYNC_NO_DATA = "Aucune nouvelle donnée trouvée"
    override fun hcSyncPreview(weight: String?, height: String?, vo2max: String?): String {
        val parts = mutableListOf<String>()
        weight?.let { parts.add("poids $it kg") }
        height?.let { parts.add("taille $it cm") }
        vo2max?.let { parts.add("VO2Max $it ml/kg/min") }
        return "Trouvé dans Health Connect : ${parts.joinToString(", ")} – mettre à jour ?"
    }

    // Stage 3 - Import Workouts
    override val HC_IMPORT_TITLE = "Importer les entraînements"
    override val HC_IMPORT_ALREADY_IMPORTED = "Déjà importé"
    override val HC_IMPORT_EMPTY = "Aucun entraînement trouvé dans Health Connect au cours des 30 derniers jours."
    override val HC_IMPORT_CONFIRM_DESC_PLURAL = "Êtes-vous sûr de vouloir importer les entraînements sélectionnés ?"
    override val HC_IMPORT_SELECT_ALL = "Sélectionner tout"
    override fun hcImportSelected(count: Int) = "Importer la sélection ($count)"
    override fun hcImportConfirmDesc(count: Int) = "Voulez-vous importer $count entraînements ?"
    override fun hcImportProgress(current: Int, total: Int) = "Importation de $current/$total entraînements..."

    // Stage 5 - Export
    override val HC_EXPORT_TO = "Exporter vers Health Connect"
    override val HC_EXPORTED_ON = "✓ Synchronisé avec Health Connect"
    override val HC_EXPORT_SUCCESS = "Exportation réussie"
    override val HC_EXPORT_ERROR = "Erreur d'exportation : "
    override val HC_EXPORT_PERMISSION_DENIED = "Pas de permission d'écriture Health Connect"
    override val SETTINGS_HC_AUTO_EXPORT = "Exportation automatique"
    override val SETTINGS_HC_AUTO_EXPORT_DESC = "Exporter automatiquement les nouveaux entraînements vers Health Connect"

    // Stage 6 - Sync Status
    override val SYNC_STATUS_TITLE = "Statut de synchronisation HC"
    override val SYNC_LAST_HEALTH = "Dernière sync données de santé"
    override val SYNC_LAST_WORKOUT = "Dernière sync entraînements"
    override val SYNC_UNSYNCED_COUNT = "Enregistrements non synchronisés"
    override val SYNC_NOW = "Synchroniser maintenant"
    override val SYNC_HISTORY_TITLE = "Historique de synchronisation"
    override val SYNC_TYPE_IMPORT = "Import"
    override val SYNC_TYPE_EXPORT = "Export"
    override val SYNC_NEVER = "Jamais"
    override val SYNC_CONFLICT_POLICY = "Politique de conflit"
    override val SYNC_CONFLICT_NEWER = "Le plus récent gagne"
    override val SYNC_CONFLICT_LOCAL = "Local gagne"
    override val SYNC_CONFLICT_HC = "Health Connect gagne"

    // Health Connect Permissions Dialog
    override val HC_PERMISSIONS_DIALOG_TITLE = "Permissions requises"
    override val HC_PERMISSIONS_DIALOG_DESC = "Les permissions d'écriture sont nécessaires pour exporter les entraînements vers Health Connect. Vous pouvez les accorder dans les paramètres du système."
    override val HC_OPEN_SETTINGS = "Ouvrir les paramètres"

    // Health Data Screen
    override val HEALTH_TITLE = "Données de santé"
    override val HEALTH_GENDER = "Genre"
    override val HEALTH_GENDER_MALE = "Homme"
    override val HEALTH_GENDER_FEMALE = "Femme"
    override val HEALTH_AGE = "Âge"
    override val HEALTH_WEIGHT = "Poids"
    override val HEALTH_WEIGHT_KG = "Poids (kg)"
    override val HEALTH_HEIGHT = "Taille"
    override val HEALTH_HEIGHT_CM = "Taille (cm)"
    override val HEALTH_RESTING_HR = "FC au repos"
    override val HEALTH_MAX_HR = "FC Max"
    override val HEALTH_MAX_HR_DESC = "FC Maximale"
    override val HEALTH_STEP_LENGTH = "Longueur de pas"
    override val HEALTH_STEP_LENGTH_CM = "Longueur de pas (cm)"
    override val HEALTH_VO2_MAX = "VO2 Max"

    // Activity List
    override val ACTIVITY_LIST_TITLE = "Liste des activités"
    override val ACTIVITY_EMPTY = "Aucune activité"
    override val ACTIVITY_DELETE_CONFIRM = "Êtes-vous sûr de vouloir supprimer définitivement les activités sélectionnées de la base de données ?"
    override val ACTIVITY_COMPARE = "Comparer"
    override val ACTIVITY_TRIM = "Rogner"
    override val ACTIVITY_DETAIL = "Détails"
    override val ACTIVITY_EDIT = "Modifier"
    override val ACTIVITY_IMPORT_GPX = "Importer GPX"
    override val ACTIVITY_EXPORT_GPX = "Exporter GPX"
    override val ACTIVITY_CHART_SETTINGS = "Paramètres du graphique"
    override val ACTIVITY_FILTERS = "Filtres"
    override val ACTIVITY_ALL_TYPES = "Tous les types"
    override val ACTIVITY_FROM = "De"
    override val ACTIVITY_TO = "À"
    override val ACTIVITY_TYPE = "Type"
    override val ACTIVITY_DATE = "Date"
    override val ACTIVITY_DURATION = "Durée"
    override val ACTIVITY_CALORIES = "Calories"
    override val ACTIVITY_DISTANCE_GPS = "Distance (GPS)"
    override val ACTIVITY_DISTANCE_STEPS = "Distance (pas)"
    override val ACTIVITY_DELETE = "Supprimer"
    override val ACTIVITY_IMPORT_SELECT_TYPE = "Choisir le type d'activité"
    override val ACTIVITY_IMPORT_SELECT_DESC = "Choisissez le type d'entraînement pour le fichier GPX importé :"
    override val ACTIVITY_IMPORT_WARNING = "Avertissement"
    override val ACTIVITY_IMPORT_CONTINUE = "Continuer"
    override val ACTIVITY_IMPORT_PROGRESS = "Importation des données..."
    override val ACTIVITY_EXPORT_ERROR = "Erreur d'exportation"
    override val ACTIVITY_SHARE_TITLE = "Partager l'entraînement"
    override val ACTIVITY_OK = "OK"
    override val ACTIVITY_CONFIRM_DELETE_TITLE = "Supprimer les activités"
    override val ACTIVITY_ALL = "Tout"

    // Activity Detail
    override val DETAIL_TITLE = "Détails de l'activité"
    override val DETAIL_MAP = "Carte"
    override val DETAIL_CHARTS = "Graphiques"
    override val DETAIL_LAPS = "Tours"
    override val DETAIL_STATISTICS = "Statistiques"
    override val DETAIL_DATA_ERROR_TITLE = "Erreur de données"
    override val DETAIL_ERROR_OK = "OK"
    override val DETAIL_INTERVALS = "Intervalle"
    override fun detailLapsWithDistance(distance: String) = "Intervalles ($distance)"
    override fun detailLapsCount(count: Int) = "Nombre de tours : $count"
    override val DETAIL_HEART_RATE = "Fréquence cardiaque (bpm)"
    override val DETAIL_HR_ZONES = "Zones de FC"
    override val DETAIL_TRAINING_EFFECT = "Effet d'entraînement"
    override val DETAIL_TRAINING_EFFECT_DESC = "Effet d'entraînement prédominant"
    override val DETAIL_LAP_NR = "N°"
    override val DETAIL_LAP_TIME = "Durée"
    override val DETAIL_LAP_AVG_PACE = "Allure moy."
    override val DETAIL_LAP_AVG_SPEED = "Vitesse moy."
    override val DETAIL_LAP_MAX_SPEED = "Vitesse max."
    override val DETAIL_LAP_AVG_HR = "FC moy."
    override val DETAIL_LAP_MAX_HR = "FC max."
    override val DETAIL_LAP_ASCENT_DESCENT = "Dénivelé"
    override val DETAIL_MAP_START = "Départ"
    override val DETAIL_MAP_FINISH = "Arrivée"
    override val DETAIL_MAP_EXPAND = "Agrandir la carte"
    override val DETAIL_MAP_COLLAPSE = "Réduire la carte"
    override val DETAIL_EXPAND = "Développer"
    override val DETAIL_COLLAPSE = "Réduire"
    override val DETAIL_PREDOMINANT_EFFECT = "Effet d'entraînement prédominant"

    // Stats
    override val STATS_TITLE = "Statistiques générales"
    override val STATS_CHARTS = "Graphiques"
    override val STATS_WIDGETS = "Widgets"
    override val STATS_NO_DATA = "Aucune donnée pour afficher les graphiques."
    override val STATS_TREND_CHARTS = "Graphiques de tendance"
    override val STATS_FILTERS = "Filtres"
    override val STATS_ALL_TYPES = "Tous les types"
    override val STATS_FROM = "De"
    override val STATS_TO = "À"
    override val STATS_NO_WIDGETS = "Aucun widget actif. Activez-les dans les options."
    override val STATS_SETTINGS_TITLE = "Paramètres des stats générales"
    override val STATS_SECTION_WIDGETS = "Section : Widgets"
    override val STATS_SECTION_CHARTS = "Section : Graphiques de tendance"
    override val STATS_MOVE_UP = "Monter"
    override val STATS_MOVE_DOWN = "Descendre"
    override fun chartDistanceGps(km: Boolean) = if (km) "Distance (GPS) en km" else "Distance (GPS) en m"
    override fun chartDistanceSteps(km: Boolean) = if (km) "Distance (pas) en km" else "Distance (pas) en m"
    override val CHART_STEPS = "Pas"

    // Definitions
    override val DEF_TITLE = "Définitions d'entraînement"
    override val DEF_ADD = "Ajouter une définition"
    override val DEF_EDIT = "Modifier la définition"
    override val DEF_DELETE = "Supprimer la définition"
    override val DEF_NAME = "Nom"
    override val DEF_ICON = "Icône"
    override val DEF_SENSORS = "Capteurs"
    override val DEF_LIST_TITLE = "Définition d'activité"
    override val DEF_SENSORS_DESC = "Gérer la liste des sports et capteurs"
    override val DEF_RECORDING = "Enregistrement"
    override val DEF_SELECT_ICON = "Choisir une icône"
    override val DEF_SAVE = "Enregistrer"
    override val DEF_MOVE_UP = "Monter"
    override val DEF_MOVE_DOWN = "Descendre"
    override val DEF_DELETE_TITLE = "Supprimer l'activité"
    override fun defDeleteConfirm(name: String) = "Êtes-vous sûr de vouloir supprimer l'activité '$name' ?"
    override val DEF_NEW_ACTIVITY = "Nouvelle activité"
    override val DEF_EDIT_ACTIVITY = "Modifier l'activité"
    override val DEF_NAME_LABEL = "Nom de l'activité"
    override val DEF_AUTO_LAP_LABEL = "Tour automatique (mètres, optionnel)"
    override val DEF_WIDGET_IN_ACTIVITY = "Widget dans l'activité"
    override val DEF_VISIBILITY = "Sichtbarkeit"
    override val DEF_RECORD = "Enregistrement"
    override val DEF_BASE_TYPE = "Type de base"
    override val DEF_FINISH = "Terminer"
    override val DEF_SELECT_ICON_TITLE = "Choisir une icône"
    
    // Base Types
    override val DEF_WALKING = "Marche"
    override val DEF_SPEED_WALKING = "Marche rapide"
    override val DEF_RUNNING = "Course"
    override val DEF_TREADMILL_RUNNING = "Course sur tapis"
    override val DEF_STAIR_CLIMBING = "Montée d'escaliers"
    override val DEF_STAIR_CLIMBING_MACHINE = "Stepper"
    override val DEF_CYCLING = "Cyclisme"
    override val DEF_CYCLING_STATIONARY = "Vélo d'appartement"
    override val DEF_MOUNTAIN_BIKING = "VTT"
    override val DEF_ROAD_BIKING = "Vélo de route"
    override val DEF_HIKING = "Randonnée"
    override val DEF_ROCK_CLIMBING = "Escalade"
    override val DEF_BOULDERING = "Bloc"
    override val DEF_HIIT = "HIIT"
    override val DEF_ELLIPTICAL = "Vélo elliptique"
    override val DEF_ROWING_MACHINE = "Rameur"
    override val DEF_STRENGTH_TRAINING = "Musculation"
    override val DEF_CALISTHENICS = "Callisthénie"
    override val DEF_YOGA = "Yoga"
    override val DEF_PILATES = "Pilates"
    override val DEF_AEROBICS = "Aérobic"
    override val DEF_DANCING = "Danse"
    override val DEF_SWIMMING = "Natación"
    override val DEF_SWIMMING_POOL = "Natación en piscina"
    override val DEF_SWIMMING_OPEN_WATER = "Natación en aguas abiertas"
    override val DEF_KAYAKING = "Piragüismo"
    override val DEF_PADDLE_BOARDING = "Paddle surf"
    override val DEF_SURFING = "Surf"
    override val DEF_SAILING = "Vela"
    override val DEF_FOOTBALL = "Fútbol"
    override val DEF_BASKETBALL = "Baloncesto"
    override val DEF_TENNIS = "Tenis"
    override val DEF_SQUASH = "Squash"
    override val DEF_VOLLEYBALL = "Voleibol"
    override val DEF_GOLF = "Golf"
    override val DEF_MARTIAL_ARTS = "Artes marciales"
    override val DEF_SKIING = "Esquí"
    override val DEF_SNOWBOARDING = "Snowboarding"
    override val DEF_SKATING = "Patinaje"
    override val DEF_ICE_SKATING = "Patinaje sobre hielo"

    override val DEF_GYM = "Gimnasio"
    override val DEF_BASEBALL = "Béisbol"
    override val DEF_SKATEBOARDING = "Skate"
    override val DEF_COMPETITION = "Competición"
    override val DEF_STOPWATCH = "Cronometro"
    override val DEF_OTHER = "Otro"
    override val DEF_STANDARD_ACTIVITY = "Actividad estándar"

    // Heart Rate Math
    override val HR_NO_DATA = "Sin datos de FC"
    override val HR_TOO_LITTLE_DATA = "Muy pocos datos"
    override val HR_BELOW_ZONES = "FC por debajo de las zonas"
    override val HR_EFFECT_Z0 = "Baja intensidad / Calentamiento"
    override val HR_EFFECT_Z1 = "Base aeróbica y recuperación"
    override val HR_EFFECT_Z2 = "Quema de grasa eficiente"
    override val HR_EFFECT_Z3 = "Mejora de la capacidad aeróbica"
    override val HR_EFFECT_Z4 = "Aumento del umbral de lactato"
    override val HR_EFFECT_Z5 = "Entrenamiento anaeróbico y VO2 Máx"
    override val HR_EFFECT_NONE = "Ninguna zona dominante"

    // HR Zones Names
    override val ZONE_Z0 = "Calentamiento"
    override val ZONE_Z1 = "Muy ligero"
    override val ZONE_Z2 = "Ligero"
    override val ZONE_Z3 = "Moderado"
    override val ZONE_Z4 = "Pesado"
    override val ZONE_Z5 = "Máximo"

    // Compare Screen
    override val COMPARE_TITLE = "Comparación de actividad"
    override val COMPARE_VS = "Comparación:"
    override val COMPARE_HIGHER_IS_BETTER = "Un resultado mayor es mejor"
    override val COMPARE_LOWER_IS_BETTER = "Un resultado menor es mejor"

    // ViewModels Messages
    override val VM_EXPORT_INITIALIZING = "Inicializando exportación..."
    override fun vmExportGenerating(name: String, current: Int, total: Int) = "Generando: $name ($current/$total)"
    override val VM_EXPORT_NO_FILES = "No se han generado archivos."
    override val VM_EXPORT_ZIPPING = "Comprimiendo en ZIP..."
    override fun vmExportError(msg: String) = "Error durante la exportación: $msg"
    override val VM_IMPORT_OPEN_ERROR = "No se puede abrir el archivo"
    override val VM_IMPORT_DUPLICATE_WARNING = "Se ha detectado un posible duplicado (misma hora de inicio y duración)."
    override val VM_IMPORT_DUPLICATE_WARNING_DESC = "¿Desea continuar de todos modos?"
    override val VM_IMPORT_SUCCESS = "Entrenamiento importado correctamente."
    override fun vmImportError(msg: String) = "Error de importación: $msg"

    // Gpx Importer
    override val GPX_NO_POINTS = "El archivo GPX no contiene puntos de track."
    override val GPX_WARN_HR = "El archivo contiene datos de FC, ale la actividad seleccionada no los admite."
    override val GPX_WARN_ELE = "El archivo contiene datos de altitud, ale la actividad seleccionada no los admite."
    override val GPX_WARN_CADENCE = "El archivo contiene datos de cadencia, ale la actividad seleccionada no los admite."

    // Periods
    override val PERIOD_TODAY = "Hoy"
    override val PERIOD_WEEK = "Semana"
    override val PERIOD_MONTH = "Mes"
    override val PERIOD_YEAR = "Año"
    override val PERIOD_CUSTOM = "Otro"
    override fun periodCustomDays(days: Int) = if (days == 1) "$days día" else "$days días"

    // Widgets
    override val WIDGET_COUNT = "Número de actividades"
    override val WIDGET_CALORIES = "Calorías quemadas"
    override val WIDGET_DISTANCE_GPS = "Distancia (GPS)"
    override val WIDGET_DISTANCE_STEPS = "Distancia (pasos)"
    override val WIDGET_ASCENT = "Ascenso total"
    override val WIDGET_DESCENT = "Descenso total"
    override val WIDGET_STEPS = "Pasos"
    override val WIDGET_AVG_BPM = "FC media"
    override val WIDGET_AVG_CADENCE = "Cadenza media"
    override val WIDGET_MAX_SPEED = "Velocidad máxima"
    override val WIDGET_MAX_ALTITUDE = "Altitud máxima"
    override val WIDGET_MAX_ELEVATION_GAIN = "Mayor ganancia de elevación"
    override val WIDGET_MAX_DISTANCE = "Distancia máxima"
    override val WIDGET_MAX_DURATION = "Tiempo máximo"
    override val WIDGET_MAX_CALORIES = "Más calorías"
    override val WIDGET_MAX_AVG_CADENCE = "Mayor cadencia media"
    override val WIDGET_MAX_AVG_SPEED = "Mayor velocidad media"
    override val WIDGET_DURATION = "Duración"
    override val WIDGET_MAX_BPM = "FC máxima"
    override val WIDGET_TOTAL_CALORIES = "Calorías totales"
    override val WIDGET_MAX_CALORIES_MIN = "Máxima tasa de quema de calorías"
    override val WIDGET_AVG_PACE = "Pace medio"
    override val WIDGET_AVG_SPEED_GPS = "Velocidad media (GPS)"
    override val WIDGET_AVG_SPEED_STEPS = "Velocidad media (pasos)"
    override val WIDGET_MAX_ALTITUDE_DESC = "Altitud máxima"
    override val WIDGET_TOTAL_ASCENT = "Suma de ascensos"
    override val WIDGET_TOTAL_DESCENT = "Suma de descensos"
    override val WIDGET_AVG_STEP_LENGTH = "Longitud de paso media"
    override val WIDGET_AVG_CADENCE_DESC = "Cadencia media"
    override val WIDGET_MAX_CADENCE = "Cadencia máxima"
    override val WIDGET_TOTAL_STEPS = "Número de pasos"
    override val WIDGET_PRESSURE_START = "Presión atm. (inicio)"
    override val WIDGET_PRESSURE_END = "Presión atm. final"
    override val WIDGET_MAX_PRESSURE = "Presión atm. máxima"
    override val WIDGET_MIN_PRESSURE = "Presión atm. mínima"
    override val WIDGET_BEST_PACE_1KM = "Mejor pace de 1 km"
    override val WIDGET_WATCH_ASCENT = "Ascenso acumulado"
    override val WIDGET_WATCH_DESCENT = "Descenso acumulado"

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
    override val SENSOR_HEART_RATE = "Frecuencia cardíaca"
    override val SENSOR_CALORIES_SUM = "Calorías totales"
    override val SENSOR_CALORIES_MIN = "Calorías por minuto"
    override val SENSOR_STEPS = "Pasos"
    override val SENSOR_STEPS_MIN = "Cadencia (pasos/min)"
    override val SENSOR_DISTANCE_STEPS = "Distancia (pasos)"
    override val SENSOR_SPEED_GPS = "Velocidad"
    override val SENSOR_SPEED_STEPS = "Velocidad (pasos)"
    override val SENSOR_DISTANCE_GPS = "Distancia"
    override val SENSOR_ALTITUDE = "Altitud"
    override val SENSOR_TOTAL_ASCENT = "Ascenso total"
    override val SENSOR_TOTAL_DESCENT = "Descenso total"
    override val SENSOR_PRESSURE = "Presión atm."
    override val SENSOR_MAP = "Datos de ubicación"

    // Units
    override val UNIT_KCAL = "kcal"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_STEP_MIN = "pas/min"
    override val UNIT_KM_H = "km/h"
    override val UNIT_STEPS = "pasos"
    override val UNIT_HPA = "hPa"
    override val UNIT_MIN_KM = "min/km"
    override val UNIT_M_ASL = "m s.n.m."
    override val UNIT_MIN_KM_LABEL = "min/km"
    override val UNIT_BPM = "bpm"
    override val UNIT_KCAL_MIN = "kcal/min"
    override val UNIT_VO2_MAX = "ml/kg/min"
    
    // New metric
    override val SENSOR_AVG_STEP_LENGTH = "Longitud de paso media"

    // Trim Screen
    override val TRIM_TITLE = "Editar entrenamiento (Recortar)"
    override val TRIM_CONFIRM_TITLE = "Confirmar recorte"
    override val TRIM_CONFIRM_DESC = "¿Está seguro de que desea eliminar los datos fuera del rango seleccionado? Estos datos se eliminarán permanentemente."
    override val TRIM_SAVE_BTN = "Recortar y guardar"
    override val TRIM_CHART_HR = "Gráfico de FC"
    override val TRIM_RANGE_TITLE = "Seleccionar rango de entrenamiento"
    override val TRIM_PREVIEW_TITLE = "Vista previa de nuevas estadísticas"
    override val TRIM_NEW_DURATION = "Nueva duración:"
    override val TRIM_DISTANCE_GPS = "Distancia (GPS):"
    override val TRIM_DISTANCE_STEPS = "Distancia (Pasos):"
    override val TRIM_CALORIES = "Calorías quemadas:"
    override val TRIM_AVG_BPM = "FC media:"
    override val TRIM_START = "Inicio"
    override val TRIM_END = "Fin"

    // Activity Detail Settings
    override val AD_SETTINGS_LIST_TITLE = "Seleccionar actividad para modificar"
    override val AD_SETTINGS_EDIT_TITLE = "Ajustes"
    override val AD_SETTINGS_SECTION_WIDGETS = "Sección: Widgets"
    override val AD_SETTINGS_SECTION_CHARTS = "Sección: Gráficos"
}
