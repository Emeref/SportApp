package com.example.sportapp

object TextsWearES : WearTexts {
    // Main Menu
    override val MENU_SPORT = "Deporte"
    override val MENU_STATISTICS = "Estadísticas"
    override val MENU_SETTINGS = "Ajustes"
    override val APP_LOGO_DESC = "Logo de SportApp"

    // Choose Sport
    override val CHOOSE_SPORT_TITLE = "Seleccionar deporte"
    override val CHOOSE_SPORT_NO_DEFINITIONS = "No hay definiciones de actividad. Defínalas en la aplicación del teléfono."
    override val CHOOSE_SPORT_DEFAULT_NAME = "Actividad estándar"

    // Statistics
    override val STATS_NO_WIDGETS = "No hay campos seleccionados"
    override val STATS_PERIOD_TODAY = "Hoy"
    override val STATS_PERIOD_7_DAYS = "Últimos 7 días"
    override val STATS_PERIOD_30_DAYS = "Últimos 30 días"
    override val STATS_PERIOD_YEAR = "Último año"
    override fun statsPeriodCustom(days: Int) = "Últimos $days días"

    override val STATS_WIDGET_COUNT = "Número de actividades"
    override val STATS_WIDGET_CALORIES = "Calorías quemadas"
    override val STATS_WIDGET_DISTANCE_GPS = "Distancia (GPS)"
    override val STATS_WIDGET_DISTANCE_STEPS = "Distancia (pasos)"
    override val STATS_WIDGET_ASCENT = "Ascenso total"
    override val STATS_WIDGET_DESCENT = "Descenso total"
    override val STATS_WIDGET_STEPS_ALL = "Todos los pasos"

    override val STATS_WIDGET_MAX_SPEED = "Velocidad máxima"
    override val STATS_WIDGET_MAX_ALTITUDE = "Altitud máxima"
    override val STATS_WIDGET_MAX_ELEVATION_GAIN = "Mayor ganancia de elevación"
    override val STATS_WIDGET_MAX_DISTANCE = "Distancia máxima"
    override val STATS_WIDGET_MAX_DURATION = "Duración máxima"
    override val STATS_WIDGET_MAX_CALORIES = "Máximo de calorías quemadas"
    override val STATS_WIDGET_MAX_AVG_CADENCE = "Cadencia media máxima"
    override val STATS_WIDGET_MAX_AVG_SPEED = "Velocidad media máxima"

    // Workout Ready
    override val WORKOUT_READY_START = "Iniciar"
    override val WORKOUT_READY_BACK = "Volver"

    // Settings
    override val SETTINGS_TITLE = "Ajustes"
    override val SETTINGS_HEALTH_DATA = "Datos de salud"
    override val SETTINGS_SCREEN = "Pantalla"
    override val SETTINGS_SCREEN_ALWAYS_ON = "Siempre encendida"
    override val SETTINGS_SCREEN_AMBIENT = "Modo Ambiente"
    override val SETTINGS_SCREEN_AUTO = "Modo automático"
    override val SETTINGS_CLOCK_COLOR = "Color del reloj"
    override val SETTINGS_SCREEN_BEHAVIOR_TITLE = "Comportamiento de pantalla"
    override val SETTINGS_LANGUAGE = "Idioma"
    override val SETTINGS_LANGUAGE_SELECTION_TITLE = "Seleccionar idioma"
    
    // Colors
    override val COLOR_RED = "Rojo"
    override val COLOR_WHITE = "Blanco"
    override val COLOR_GREEN = "Verde"
    override val COLOR_YELLOW = "Amarillo"
    override val COLOR_BLUE = "Azul"
    override val COLOR_BLACK = "Negro"
    override val COLOR_NONE = "Ninguno"
    override val COLOR_CUSTOM = "Personalizado"

    // Health Data
    override val HEALTH_GENDER = "Género"
    override val HEALTH_AGE = "Edad"
    override val HEALTH_WEIGHT = "Peso"
    override val HEALTH_HEIGHT = "Altura"
    override val HEALTH_STEP_LENGTH = "Longitud de paso"
    override val HEALTH_RESTING_HR = "FC en reposo"
    override val HEALTH_MAX_HR = "FC máxima"
    override val HEALTH_SAVE = "Guardar"
    override val HEALTH_CHOOSE_GENDER = "Seleccionar género"
    override val GENDER_MALE = "Hombre"
    override val GENDER_FEMALE = "Mujer"
    
    override fun healthAgeValue(age: Int) = "$age años"
    override fun healthWeightValue(weight: Int) = "$weight kg"
    override fun healthHeightValue(height: Int) = "$height cm"
    override fun healthStepLengthValue(length: Int) = "$length cm"
    override fun healthHRValue(hr: Int) = "$hr LPM"

    // Units
    override val UNIT_YEARS = "años"
    override val UNIT_KG = "kg"
    override val UNIT_CM = "cm"
    override val UNIT_BPM = "LPM"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_KMH = "km/h"
    override val UNIT_KCAL = "kcal"
    override val UNIT_HPA = "hPa"

    // Workout Data / Labels
    override val WORKOUT_ERROR_CONFIG = "Error de configuración"
    override val WORKOUT_LABEL_TIMER = "TIEMPO DE ACTIVIDAD"
    override val WORKOUT_LABEL_STEPS = "Pasos"
    override val WORKOUT_LABEL_DISTANCE = "Distancia"
    override val WORKOUT_LABEL_SPEED = "Velocidad"
    override val WORKOUT_LABEL_HR = "Frecuencia cardíaca"
    override val WORKOUT_LABEL_PRESSURE = "Presión"
    override val WORKOUT_LABEL_ALTITUDE = "Altitud"

    // Sensors Names (for WorkoutDefinition)
    override val SENSOR_HEART_RATE = "Frecuencia cardíaca"
    override val SENSOR_CALORIES_SUM = "Calorías quemadas"
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

    // Workout Controls
    override val WORKOUT_RESUME = "Reanudar"
    override val WORKOUT_PAUSE = "Pausa"
    override val WORKOUT_FINISH = "Finalizar"
    override val WORKOUT_START = "Iniciar"
    override val WORKOUT_STOP = "Detener"
    override val WORKOUT_READY_MSG = "Use DynamicWorkoutScreen"

    // Summary
    override val SUMMARY_TITLE = "RESUMEN"
    override val SUMMARY_CONFIRM_DESC = "Confirmar"
    override val SUMMARY_DURATION = "Duración"
    override val SUMMARY_AVG_HR = "FC media"
    override val SUMMARY_MAX_HR = "FC máxima"
    override val SUMMARY_AVG_SPEED = "Velocidad media"
    override val SUMMARY_MAX_SPEED = "Velocidad máxima"
    override val SUMMARY_AVG_SPEED_STEPS = "Velocidad media (pasos)"
    override val SUMMARY_MAX_SPEED_STEPS = "Velocidad máxima (pasos)"
    override val SUMMARY_DISTANCE = "Distancia"
    override val SUMMARY_DISTANCE_STEPS = "Distancia (pasos)"
    override val SUMMARY_STEPS = "Pasos"
    override val SUMMARY_TOTAL_ASCENT = "Ascenso total"
    override val SUMMARY_TOTAL_DESCENT = "Descenso total"
    override val SUMMARY_CALORIES = "Calorías"

    // General
    override val GEN_ACTIVITY = "Actividad"
    override val VAL_EMPTY = "--"

    // Complication / Tile
    override val COMP_MON = "Lun"
    override val COMP_TUE = "Mar"
    override val COMP_WED = "Mié"
    override val COMP_THU = "Jue"
    override val COMP_FRI = "Vie"
    override val COMP_SAT = "Sáb"
    override val COMP_SUN = "Dom"
    
    override val COMP_MONDAY = "Lunes"
    override val COMP_TUESDAY = "Martes"
    override val COMP_WEDNESDAY = "Miércoles"
    override val COMP_THURSDAY = "Jueves"
    override val COMP_FRIDAY = "Viernes"
    override val COMP_SATURDAY = "Sábado"
    override val COMP_SUNDAY = "Domingo"

    override val TILE_HELLO = "¡Hola!"
}
