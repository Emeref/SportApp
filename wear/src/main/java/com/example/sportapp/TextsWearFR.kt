package com.example.sportapp

object TextsWearFR : WearTexts {
    // Main Menu
    override val MENU_SPORT = "Sport"
    override val MENU_STATISTICS = "Statistiques"
    override val MENU_SETTINGS = "Paramètres"
    override val APP_LOGO_DESC = "Logo SportApp"

    // Choose Sport
    override val CHOOSE_SPORT_TITLE = "Choisir un sport"
    override val CHOOSE_SPORT_NO_DEFINITIONS = "Aucune définition d'activité. Définissez-les dans l'application mobile."
    override val CHOOSE_SPORT_DEFAULT_NAME = "Activité standard"

    // Statistics
    override val STATS_NO_WIDGETS = "Aucun champ sélectionné"
    override val STATS_PERIOD_TODAY = "Aujourd'hui"
    override val STATS_PERIOD_7_DAYS = "7 derniers jours"
    override val STATS_PERIOD_30_DAYS = "30 derniers jours"
    override val STATS_PERIOD_YEAR = "Dernière année"
    override fun statsPeriodCustom(days: Int) = "Les $days derniers jours"

    override val STATS_WIDGET_COUNT = "Nombre d'activités"
    override val STATS_WIDGET_CALORIES = "Calories brûlées"
    override val STATS_WIDGET_DISTANCE_GPS = "Distance (GPS)"
    override val STATS_WIDGET_DISTANCE_STEPS = "Distance (pas)"
    override val STATS_WIDGET_ASCENT = "Dénivelé positif"
    override val STATS_WIDGET_DESCENT = "Dénivelé négatif"
    override val STATS_WIDGET_STEPS_ALL = "Total des pas"

    override val STATS_WIDGET_MAX_SPEED = "Vitesse max"
    override val STATS_WIDGET_MAX_ALTITUDE = "Altitude max"
    override val STATS_WIDGET_MAX_ELEVATION_GAIN = "Gain d'élévation max"
    override val STATS_WIDGET_MAX_DISTANCE = "Distance max"
    override val STATS_WIDGET_MAX_DURATION = "Durée max"
    override val STATS_WIDGET_MAX_CALORIES = "Calories max brûlées"
    override val STATS_WIDGET_MAX_AVG_CADENCE = "Cadence moyenne max"
    override val STATS_WIDGET_MAX_AVG_SPEED = "Vitesse moyenne max"

    // Workout Ready
    override val WORKOUT_READY_START = "Démarrer"
    override val WORKOUT_READY_BACK = "Retour"

    // Settings
    override val SETTINGS_TITLE = "Paramètres"
    override val SETTINGS_HEALTH_DATA = "Données de santé"
    override val SETTINGS_SCREEN = "Écran"
    override val SETTINGS_SCREEN_ALWAYS_ON = "Toujours activé"
    override val SETTINGS_SCREEN_AMBIENT = "Mode Ambiant"
    override val SETTINGS_SCREEN_AUTO = "Mode Automatique"
    override val SETTINGS_CLOCK_COLOR = "Couleur de l'horloge"
    override val SETTINGS_SCREEN_BEHAVIOR_TITLE = "Comportement de l'écran"
    override val SETTINGS_LANGUAGE = "Langue"
    override val SETTINGS_LANGUAGE_SELECTION_TITLE = "Choisir la langue"
    
    // Colors
    override val COLOR_RED = "Rouge"
    override val COLOR_WHITE = "Blanc"
    override val COLOR_GREEN = "Vert"
    override val COLOR_YELLOW = "Jaune"
    override val COLOR_BLUE = "Bleu"
    override val COLOR_BLACK = "Noir"
    override val COLOR_NONE = "Aucun"
    override val COLOR_CUSTOM = "Personnalisé"

    // Health Data
    override val HEALTH_GENDER = "Genre"
    override val HEALTH_AGE = "Âge"
    override val HEALTH_WEIGHT = "Poids"
    override val HEALTH_HEIGHT = "Taille"
    override val HEALTH_STEP_LENGTH = "Longueur de pas"
    override val HEALTH_RESTING_HR = "FC au repos"
    override val HEALTH_MAX_HR = "FC maximale"
    override val HEALTH_SAVE = "Enregistrer"
    override val HEALTH_CHOOSE_GENDER = "Choisir le genre"
    override val GENDER_MALE = "Homme"
    override val GENDER_FEMALE = "Femme"
    
    override fun healthAgeValue(age: Int) = "$age ans"
    override fun healthWeightValue(weight: Int) = "$weight kg"
    override fun healthHeightValue(height: Int) = "$height cm"
    override fun healthStepLengthValue(length: Int) = "$length cm"
    override fun healthHRValue(hr: Int) = "$hr BPM"

    // Units
    override val UNIT_YEARS = "ans"
    override val UNIT_KG = "kg"
    override val UNIT_CM = "cm"
    override val UNIT_BPM = "BPM"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_KMH = "km/h"
    override val UNIT_KCAL = "kcal"
    override val UNIT_HPA = "hPa"

    // Workout Data / Labels
    override val WORKOUT_ERROR_CONFIG = "Erreur de configuration"
    override val WORKOUT_LABEL_TIMER = "TEMPS D'ACTIVITÉ"
    override val WORKOUT_LABEL_STEPS = "Pas"
    override val WORKOUT_LABEL_DISTANCE = "Distance"
    override val WORKOUT_LABEL_SPEED = "Vitesse"
    override val WORKOUT_LABEL_HR = "Fréquence cardiaque"
    override val WORKOUT_LABEL_PRESSURE = "Pression"
    override val WORKOUT_LABEL_ALTITUDE = "Altitude"

    // Sensors Names (for WorkoutDefinition)
    override val SENSOR_HEART_RATE = "Fréquence cardiaque"
    override val SENSOR_CALORIES_SUM = "Calories brûlées"
    override val SENSOR_CALORIES_MIN = "Calories par minute"
    override val SENSOR_STEPS = "Pas"
    override val SENSOR_STEPS_MIN = "Cadence (pas/min)"
    override val SENSOR_DISTANCE_STEPS = "Distance (pas)"
    override val SENSOR_SPEED_GPS = "Vitesse"
    override val SENSOR_SPEED_STEPS = "Vitesse (pas)"
    override val SENSOR_DISTANCE_GPS = "Distance"
    override val SENSOR_ALTITUDE = "Altitude"
    override val SENSOR_TOTAL_ASCENT = "Dénivelé positif"
    override val SENSOR_TOTAL_DESCENT = "Dénivelé négatif"
    override val SENSOR_PRESSURE = "Pression atm."
    override val SENSOR_MAP = "Données de localisation"

    // Workout Controls
    override val WORKOUT_RESUME = "Reprendre"
    override val WORKOUT_PAUSE = "Pause"
    override val WORKOUT_FINISH = "Terminer"
    override val WORKOUT_START = "Démarrer"
    override val WORKOUT_STOP = "Arrêter"
    override val WORKOUT_READY_MSG = "Utiliser DynamicWorkoutScreen"

    // Summary
    override val SUMMARY_TITLE = "RÉSUMÉ"
    override val SUMMARY_CONFIRM_DESC = "Confirmer"
    override val SUMMARY_DURATION = "Durée"
    override val SUMMARY_AVG_HR = "FC moyenne"
    override val SUMMARY_MAX_HR = "FC maximale"
    override val SUMMARY_AVG_SPEED = "Vitesse moyenne"
    override val SUMMARY_MAX_SPEED = "Vitesse maximale"
    override val SUMMARY_AVG_SPEED_STEPS = "Vitesse moyenne (pas)"
    override val SUMMARY_MAX_SPEED_STEPS = "Vitesse maximale (pas)"
    override val SUMMARY_DISTANCE = "Distance"
    override val SUMMARY_DISTANCE_STEPS = "Distance (pas)"
    override val SUMMARY_STEPS = "Pas"
    override val SUMMARY_TOTAL_ASCENT = "Dénivelé positif"
    override val SUMMARY_TOTAL_DESCENT = "Dénivelé négatif"
    override val SUMMARY_CALORIES = "Calories"

    // General
    override val GEN_ACTIVITY = "Activité"
    override val VAL_EMPTY = "--"

    // Complication / Tile
    override val COMP_MON = "Lun"
    override val COMP_TUE = "Mar"
    override val COMP_WED = "Mer"
    override val COMP_THU = "Jeu"
    override val COMP_FRI = "Ven"
    override val COMP_SAT = "Sam"
    override val COMP_SUN = "Dim"
    
    override val COMP_MONDAY = "Lundi"
    override val COMP_TUESDAY = "Mardi"
    override val COMP_WEDNESDAY = "Mercredi"
    override val COMP_THURSDAY = "Jeudi"
    override val COMP_FRIDAY = "Vendredi"
    override val COMP_SATURDAY = "Samedi"
    override val COMP_SUNDAY = "Dimanche"

    override val TILE_HELLO = "Bonjour !"
}
