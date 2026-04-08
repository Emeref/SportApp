package com.example.sportapp

import java.util.Calendar
import java.util.Locale

object TextsMobileFR : MobileTexts {
    // Navigation
    override val NAV_HOME = "Accueil"
    override val NAV_STATS = "Statistiques"
    override val NAV_ACTIVITIES = "Activités"
    override val NAV_SETTINGS = "Paramètres"

    // Home Screen
    override val HOME_TITLE = "SportApp"
    override val HOME_NO_WIDGETS = "Aucun widget sélectionné"
    override val HOME_ADD_WIDGETS = "Ajouter des widgets"
    override val HOME_LAST_ACTIVITY = "Dernière activité"
    override val HOME_ACTIVITY_COUNT = "Nombre d'activités"
    override val HOME_SYNC = "Synchroniser"
    override val HOME_OPTIONS = "Options"
    override val HOME_GENERAL_STATS = "Statistiques générales"
    override val HOME_WORKOUT_DETAILS = "Détails de l'entraînement"
    override val HOME_LOGO_DESC = "Logo de l'application"
    override val HOME_SECRET_TITLE = "C'est bien de cliquer, mais il n'y a rien ici"
    override val HOME_CLOSE = "Fermer"

    override fun homeResultsToday() = "Résultats d'aujourd'hui :"
    override fun homeResultsWeek() = "Résultats de cette semaine :"
    override fun homeResultsMonth(): String {
        val monthName = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRENCH)
        return "Résultats de $monthName :"
    }
    override fun homeResultsYear() = "Résultats de cette année :"
    override fun homeResultsCustom(days: Int) = if (days == 1) "Résultats du dernier jour :" else "Résultats des $days derniers jours :"

    // Settings Screen
    override val SETTINGS_TITLE = "Paramètres"
    override val SETTINGS_GENERAL = "Général"
    override val SETTINGS_THEME = "Thème de l'application"
    override val SETTINGS_THEME_SYSTEM = "Système"
    override val SETTINGS_THEME_LIGHT = "Clair"
    override val SETTINGS_THEME_DARK = "Sombre"
    override val SETTINGS_LANGUAGE = "Langue"
    override val SETTINGS_LANGUAGE_TITLE = "Choisir la langue"
    override val SETTINGS_HEALTH_DATA = "Données de santé & FC"
    override val SETTINGS_HEALTH_DATA_DESC = "Âge, poids, FC Max & zones"
    override val SETTINGS_DEFINITIONS = "Définitions d'activité"
    override val SETTINGS_DEFINITIONS_DESC = "Gérer la liste des sports et capteurs"
    override val SETTINGS_WIDGETS_HOME = "Widgets de l'écran d'accueil"
    override val SETTINGS_WIDGETS_HOME_TITLE = "Vue d'accueil"
    override val SETTINGS_WIDGETS_HOME_DESC = "Sélectionner et définir l'ordre"
    override val SETTINGS_WIDGETS_WATCH = "Stats de la montre"
    override val SETTINGS_WIDGETS_WATCH_TITLE = "Champs de stats"
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
    override val SETTINGS_GOOGLE_DRIVE_DESC = "Enregistrer l'historique et les résumés (Bientôt)"
    override val SETTINGS_APPEARANCE = "Apparence"
    override val SETTINGS_MY_PROFILE = "Mon Profil"
    override val LANG_PL = "Polonais"
    override val LANG_EN = "Anglais"

    // Health Data Screen
    override val HEALTH_TITLE = "Données de santé"
    override val HEALTH_GENDER = "Sexe"
    override val HEALTH_GENDER_MALE = "Homme"
    override val HEALTH_GENDER_FEMALE = "Femme"
    override val HEALTH_AGE = "Âge"
    override val HEALTH_WEIGHT = "Poids"
    override val HEALTH_WEIGHT_KG = "Poids (kg)"
    override val HEALTH_HEIGHT = "Taille"
    override val HEALTH_HEIGHT_CM = "Taille (cm)"
    override val HEALTH_RESTING_HR = "FC au repos"
    override val HEALTH_MAX_HR = "FC maximale (FC Max)"
    override val HEALTH_MAX_HR_DESC = "Fréquence cardiaque maximale (FC Max)"
    override val HEALTH_STEP_LENGTH = "Longueur de foulée"
    override val HEALTH_STEP_LENGTH_CM = "Longueur de foulée (cm)"

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
    override val ACTIVITY_DISTANCE_STEPS = "Distance (Pas)"
    override val ACTIVITY_DELETE = "Supprimer"
    override val ACTIVITY_IMPORT_SELECT_TYPE = "Choisir le type d'activité"
    override val ACTIVITY_IMPORT_SELECT_DESC = "Choisir le type d'entraînement pour le fichier GPX importé :"
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
    override val DETAIL_INTERVALS = "Intervalles"
    override fun detailLapsWithDistance(distance: String) = "Intervalle ($distance)"
    override fun detailLapsCount(count: Int) = "Nombre de tours : $count"
    override val DETAIL_HEART_RATE = "Fréquence cardiaque (bpm)"
    override val DETAIL_HR_ZONES = "Zones de FC"
    override val DETAIL_TRAINING_EFFECT = "Effet d'entraînement"
    override val DETAIL_LAP_NR = "N°"
    override val DETAIL_LAP_TIME = "Durée"
    override val DETAIL_LAP_AVG_PACE = "Allure moy."
    override val DETAIL_LAP_AVG_SPEED = "Vitesse moy."
    override val DETAIL_LAP_MAX_SPEED = "Vitesse max."
    override val DETAIL_LAP_AVG_HR = "FC moy."
    override val DETAIL_LAP_MAX_HR = "FC max."
    override val DETAIL_LAP_ASCENT_DESCENT = "Dénivelé +/-"
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
    override val STATS_NO_DATA = "Aucune donnée à afficher pour les graphiques."
    override val STATS_TREND_CHARTS = "Graphiques de tendance"
    override val STATS_FILTERS = "Filtres"
    override val STATS_ALL_TYPES = "Tous les types"
    override val STATS_FROM = "De"
    override val STATS_TO = "À"
    override val STATS_NO_WIDGETS = "Aucun widget actif. Activez-les dans les options."
    override val STATS_SETTINGS_TITLE = "Paramètres des statistiques générales"
    override val STATS_SECTION_WIDGETS = "Section : Widgets"
    override val STATS_SECTION_CHARTS = "Section : Graphiques"
    override val STATS_MOVE_UP = "Déplacer vers le haut"
    override val STATS_MOVE_DOWN = "Déplacer vers le bas"
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
    override val DEF_MOVE_UP = "Déplacer vers le haut"
    override val DEF_MOVE_DOWN = "Déplacer vers le bas"
    override val DEF_DELETE_TITLE = "Supprimer l'activité"
    override fun defDeleteConfirm(name: String) = "Voulez-vous vraiment supprimer l'activité '$name' ?"
    override val DEF_NEW_ACTIVITY = "Nouvelle activité"
    override val DEF_EDIT_ACTIVITY = "Modifier l'activité"
    override val DEF_NAME_LABEL = "Nom de l'activité"
    override val DEF_AUTO_LAP_LABEL = "Tour automatique (mètres, facultatif)"
    override val DEF_WIDGET_IN_ACTIVITY = "Widget dans l'activité"
    override val DEF_VISIBILITY = "Visibilité"
    override val DEF_RECORD = "Enregistrement"
    override val DEF_BASE_TYPE = "Type de base"
    override val DEF_FINISH = "Terminer"
    override val DEF_SELECT_ICON_TITLE = "Choisir une icône"
    override val DEF_WALKING = "Marche"
    override val DEF_RUNNING = "Course"
    override val DEF_CYCLING = "Cyclisme"
    override val DEF_HIKING = "Randonnée"
    override val DEF_SWIMMING = "Natation"
    override val DEF_GYM = "Gym"
    override val DEF_YOGA = "Yoga"
    override val DEF_TENNIS = "Tennis"
    override val DEF_KAYAKING = "Kayak"
    override val DEF_SNOWBOARDING = "Snowboard"
    override val DEF_SURFING = "Surf"
    override val DEF_SKATING = "Patinage"
    override val DEF_GOLF = "Golf"
    override val DEF_FOOTBALL = "Football"
    override val DEF_BASKETBALL = "Basket-ball"
    override val DEF_VOLLEYBALL = "Volley-ball"
    override val DEF_BASEBALL = "Baseball"
    override val DEF_SAILING = "Voile"
    override val DEF_SKATEBOARDING = "Skate"
    override val DEF_COMPETITION = "Compétition"
    override val DEF_STOPWATCH = "Chronomètre"
    override val DEF_OTHER = "Autre"
    override val DEF_STANDARD_ACTIVITY = "Activité standard"

    // Activity Detail Settings
    override val AD_SETTINGS_LIST_TITLE = "Choisir l'activité à modifier"
    override val AD_SETTINGS_EDIT_TITLE = "Paramètres"
    override val AD_SETTINGS_SECTION_WIDGETS = "Section : Widgets"
    override val AD_SETTINGS_SECTION_CHARTS = "Section : Graphiques"

    // Heart Rate Math
    override val HR_NO_DATA = "Pas de données FC"
    override val HR_TOO_LITTLE_DATA = "Trop peu de données"
    override val HR_BELOW_ZONES = "FC sous les zones"
    override val HR_EFFECT_Z0 = "Intensité faible / Échauffement"
    override val HR_EFFECT_Z1 = "Base aérobie et récupération"
    override val HR_EFFECT_Z2 = "Brûlage des graisses efficace"
    override val HR_EFFECT_Z3 = "Amélioration de la capacité aérobie"
    override val HR_EFFECT_Z4 = "Augmentation du seuil lactique"
    override val HR_EFFECT_Z5 = "Entraînement anaérobie et VO2 Max"
    override val HR_EFFECT_NONE = "Pas de zone dominante"

    // HR Zones Names
    override val ZONE_Z0 = "Échauffement"
    override val ZONE_Z1 = "Très léger"
    override val ZONE_Z2 = "Léger"
    override val ZONE_Z3 = "Modéré"
    override val ZONE_Z4 = "Difficile"
    override val ZONE_Z5 = "Maximum"

    // Compare Screen
    override val COMPARE_TITLE = "Comparaison d'activités"
    override val COMPARE_VS = "Comparaison :"
    override val COMPARE_HIGHER_IS_BETTER = "Un score plus élevé est préférable"
    override val COMPARE_LOWER_IS_BETTER = "Un score plus bas est préférable"

    // ViewModels Messages
    override val VM_EXPORT_INITIALIZING = "Initialisation de l'exportation..."
    override fun vmExportGenerating(name: String, current: Int, total: Int) = "Génération : $name ($current/$total)"
    override val VM_EXPORT_NO_FILES = "Aucun fichier généré."
    override val VM_EXPORT_ZIPPING = "Compression en ZIP..."
    override fun vmExportError(msg: String) = "Erreur lors de l'exportation : $msg"
    override val VM_IMPORT_OPEN_ERROR = "Impossible d'ouvrir le fichier"
    override val VM_IMPORT_DUPLICATE_WARNING = "Doublon potentiel détecté (même heure de début et durée)."
    override val VM_IMPORT_SUCCESS = "Entraînement importé avec succès."
    override fun vmImportError(msg: String) = "Erreur d'importation : $msg"

    // Gpx Importer
    override val GPX_NO_POINTS = "Le fichier GPX ne contient aucun point de trajet."
    override val GPX_WARN_HR = "Le fichier contient des données FC, mais l'activité sélectionnée ne les supporte pas."
    override val GPX_WARN_ELE = "Le fichier contient des données d'altitude, mais l'activité sélectionnée ne les supporte pas."
    override val GPX_WARN_CADENCE = "Le fichier contient des données de cadence, mais l'activité sélectionnée ne les supporte pas."

    // Periods
    override val PERIOD_TODAY = "Aujourd'hui"
    override val PERIOD_WEEK = "Semaine"
    override val PERIOD_MONTH = "Mois"
    override val PERIOD_YEAR = "Année"
    override val PERIOD_CUSTOM = "Personnalisé"
    override fun periodCustomDays(days: Int) = if (days == 1) "$days jour" else "$days jours"

    // Widgets
    override val WIDGET_COUNT = "Nombre d'activités"
    override val WIDGET_CALORIES = "Calories brûlées"
    override val WIDGET_DISTANCE_GPS = "Distance (GPS)"
    override val WIDGET_DISTANCE_STEPS = "Distance (pas)"
    override val WIDGET_ASCENT = "Dénivelé positif total"
    override val WIDGET_DESCENT = "Dénivelé négatif total"
    override val WIDGET_STEPS = "Pas"
    override val WIDGET_AVG_CADENCE = "Cadence moyenne"
    override val WIDGET_MAX_SPEED = "Vitesse max."
    override val WIDGET_MAX_ALTITUDE = "Altitude max."
    override val WIDGET_MAX_ELEVATION_GAIN = "Dénivelé max."
    override val WIDGET_MAX_DISTANCE = "Distance max."
    override val WIDGET_MAX_DURATION = "Durée max."
    override val WIDGET_MAX_CALORIES = "Calories max."
    override val WIDGET_MAX_AVG_CADENCE = "Cadence moy. max."
    override val WIDGET_MAX_AVG_SPEED = "Vitesse moy. max."
    override val WIDGET_DURATION = "Durée"
    override val WIDGET_MAX_BPM = "FC Max"
    override val WIDGET_AVG_BPM = "FC Moy."
    override val WIDGET_TOTAL_CALORIES = "Total des calories"
    override val WIDGET_MAX_CALORIES_MIN = "Débit calorique max."
    override val WIDGET_AVG_PACE = "Allure moyenne"
    override val WIDGET_AVG_SPEED_GPS = "Vitesse moyenne (GPS)"
    override val WIDGET_AVG_SPEED_STEPS = "Vitesse moyenne (pas)"
    override val WIDGET_MAX_ALTITUDE_DESC = "Altitude max."
    override val WIDGET_TOTAL_ASCENT = "Dénivelé positif"
    override val WIDGET_TOTAL_DESCENT = "Dénivelé négatif"
    override val WIDGET_AVG_STEP_LENGTH = "Longueur de foulée moy."
    override val WIDGET_AVG_CADENCE_DESC = "Cadence moy."
    override val WIDGET_MAX_CADENCE = "Cadence max."
    override val WIDGET_TOTAL_STEPS = "Nombre total de pas"
    override val WIDGET_PRESSURE_START = "Pression atm. (départ)"
    override val WIDGET_PRESSURE_END = "Pression atm. (fin)"
    override val WIDGET_MAX_PRESSURE = "Pression atm. max."
    override val WIDGET_MIN_PRESSURE = "Pression atm. min."
    override val WIDGET_BEST_PACE_1KM = "Meilleure allure 1km"
    override val WIDGET_WATCH_ASCENT = "Dénivelé + total"
    override val WIDGET_WATCH_DESCENT = "Dénivelé - total"

    // Sensors
    override val SENSOR_HEART_RATE = "Fréquence cardiaque"
    override val SENSOR_CALORIES_SUM = "Total des calories"
    override val SENSOR_CALORIES_MIN = "Calories par minute"
    override val SENSOR_STEPS = "Pas"
    override val SENSOR_STEPS_MIN = "Cadence (spm)"
    override val SENSOR_DISTANCE_STEPS = "Distance (Pas)"
    override val SENSOR_SPEED_GPS = "Vitesse"
    override val SENSOR_SPEED_STEPS = "Vitesse (Pas)"
    override val SENSOR_DISTANCE_GPS = "Distance"
    override val SENSOR_ALTITUDE = "Altitude"
    override val SENSOR_TOTAL_ASCENT = "Dénivelé positif total"
    override val SENSOR_TOTAL_DESCENT = "Dénivelé négatif total"
    override val SENSOR_PRESSURE = "Pression atm."
    override val SENSOR_MAP = "Données de localisation"

    // Units
    override val UNIT_KCAL = "kcal"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_STEP_MIN = "spm"
    override val UNIT_KM_H = "km/h"
    override val UNIT_STEPS = "pas"
    override val UNIT_HPA = "hPa"
    override val UNIT_MIN_KM = "min/km"
    override val UNIT_M_ASL = "m"
    override val UNIT_MIN_KM_LABEL = "min/km"
    override val UNIT_BPM = "bpm"
    override val UNIT_KCAL_MIN = "kcal/min"

    // Trim Screen
    override val TRIM_TITLE = "Modifier l'entraînement (Rognage)"
    override val TRIM_CONFIRM_TITLE = "Confirmer le rognage"
    override val TRIM_CONFIRM_DESC = "Voulez-vous vraiment supprimer les données en dehors de la plage sélectionnée ? Ces données seront définitivement supprimées."
    override val TRIM_SAVE_BTN = "Rogner et enregistrer"
    override val TRIM_CHART_HR = "Graphique FC"
    override val TRIM_RANGE_TITLE = "Choisir la plage d'entraînement"
    override val TRIM_PREVIEW_TITLE = "Aperçu des nouvelles stats"
    override val TRIM_NEW_DURATION = "Nouvelle durée :"
    override val TRIM_DISTANCE_GPS = "Distance (GPS) :"
    override val TRIM_DISTANCE_STEPS = "Distance (Pas) :"
    override val TRIM_CALORIES = "Calories brûlées :"
    override val TRIM_AVG_BPM = "FC Moyenne :"
    override val TRIM_START = "Départ"
    override val TRIM_END = "Fin"

    // New metric
    override val SENSOR_AVG_STEP_LENGTH = "Longueur de foulée moy."

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
            "avg_step_length_over_time" -> SENSOR_AVG_STEP_LENGTH
            else -> when (id) {
                "map" -> DETAIL_MAP
                "bpm" -> DETAIL_HEART_RATE
                else -> id
            }
        }
    }
}
