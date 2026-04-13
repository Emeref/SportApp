package com.example.sportapp

import java.util.Calendar
import java.util.Locale

object TextsMobileES : MobileTexts {
    // Navigation
    override val NAV_HOME = "Inicio"
    override val NAV_STATS = "Estadísticas"
    override val NAV_ACTIVITIES = "Actividades"
    override val NAV_SETTINGS = "Ajustes"

    // Home Screen
    override val HOME_TITLE = "SportApp"
    override val HOME_NO_WIDGETS = "No hay widgets seleccionados"
    override val HOME_ADD_WIDGETS = "Añadir widgets"
    override val HOME_LAST_ACTIVITY = "Última actividad"
    override val HOME_ACTIVITY_COUNT = "Número de actividades"
    override val HOME_SYNC = "Sincronizar"
    override val HOME_OPTIONS = "Opciones"
    override val HOME_GENERAL_STATS = "Estadísticas generales"
    override val HOME_WORKOUT_DETAILS = "Detalles del entrenamiento"
    override val HOME_LOGO_DESC = "Logotipo de la aplicación"
    override val HOME_SECRET_TITLE = "Es genial que estés haciendo clic, pero aquí no hay nada"
    override val HOME_CLOSE = "Cerrar"

    override fun homeResultsToday() = "Resultados de hoy:"
    override fun homeResultsWeek() = "Resultados semanales:"
    override fun homeResultsMonth(): String {
        val monthName = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("es"))
        return "Resultados de $monthName:"
    }
    override fun homeResultsYear() = "Resultados de este año:"
    override fun homeResultsCustom(days: Int) = if (days == 1) "Resultados del último día:" else "Resultados de los últimos $days dni:"

    // Settings Screen
    override val SETTINGS_TITLE = "Ajustes"
    override val SETTINGS_GENERAL = "General"
    override val SETTINGS_THEME = "Tema de la aplicación"
    override val SETTINGS_THEME_SYSTEM = "Sistema"
    override val SETTINGS_THEME_LIGHT = "Claro"
    override val SETTINGS_THEME_DARK = "Oscuro"
    override val SETTINGS_LANGUAGE = "Idioma"
    override val SETTINGS_LANGUAGE_TITLE = "Seleccionar idioma"
    override val SETTINGS_HEALTH_DATA = "Datos de salud y FC"
    override val SETTINGS_HEALTH_DATA_DESC = "Edad, peso, FC Máx y zonas"
    override val SETTINGS_DEFINITIONS = "Definicione de actividad"
    override val SETTINGS_DEFINITIONS_DESC = "Gestionar lista de deportes y sensores"
    override val SETTINGS_WIDGETS_HOME = "Widgets de la pantalla de inicio"
    override val SETTINGS_WIDGETS_HOME_TITLE = "Vista de inicio"
    override val SETTINGS_WIDGETS_HOME_DESC = "Seleccionar y establecer el orden"
    override val SETTINGS_WIDGETS_WATCH = "Estadísticas del reloj"
    override val SETTINGS_WIDGETS_WATCH_TITLE = "Campos de estadísticas"
    override val SETTINGS_WIDGETS_WATCH_DESC = "Seleccionar y establecer el orden en el reloj"
    override val SETTINGS_SAVE = "Guardar"
    override val SETTINGS_CANCEL = "Cancelar"
    override val SETTINGS_CLOSE = "Cerrar"
    override val SETTINGS_PERIOD = "Periodo predeterminado"
    override val SETTINGS_PERIOD_HOME_DESC = "¿Para qué periodo mostrar widgets?"
    override val SETTINGS_PERIOD_WATCH_DESC = "¿Estadísticas de qué periodo?"
    override val SETTINGS_CUSTOM_DAYS_LABEL = "Número de días"
    override val SETTINGS_INTEGRATION = "Integration"
    override val SETTINGS_SYNC = "Sincronización"
    override val SETTINGS_STRAVA = "Strava"
    override val SETTINGS_STRAVA_DESC = "Sincroniza tus entrenamientos con Strava (Próximamente)"
    override val SETTINGS_GOOGLE_DRIVE = "Google Drive"
    override val SETTINGS_GOOGLE_DRIVE_DESC = "Guardar historial y resúmenes (Próximamente)"
    override val SETTINGS_APPEARANCE = "Apariencia"
    override val SETTINGS_MY_PROFILE = "Mi perfil"
    override val LANG_PL = "Polaco"
    override val LANG_EN = "Inglés"

    // Health Connect Strings
    override val SETTINGS_HC_TITLE = "Health Connect"
    override val SETTINGS_HC_MANAGE_PERMISSIONS = "Gestionar permisos de Health Connect"
    override val SETTINGS_HC_STATUS = "Estado de Health Connect"
    override val HC_STATUS_AVAILABLE = "Disponible"
    override val HC_STATUS_UNAVAILABLE = "No disponible"
    override val HC_STATUS_NOT_INSTALLED = "No instalado"
    override val HC_INSTALL = "Instalar"
    override val HC_SYNC_HEALTH_DATA = "Sincronizar con Health Connect"
    override val HC_SYNC_WORKOUTS = "Importar entrenamientos de Health Connect"
    override val HC_SYNC_CONFIRM_TITLE = "Sincronización de datos"
    override val HC_SYNC_CONFIRM_DESC = "¿Desea actualizar su perfil con los datos encontrados en Health Connect?"
    override val HC_SYNC_SUCCESS = "Sincronización realizada correctamente"
    override val HC_SYNC_ERROR = "Error de sincronización"
    override val HC_SYNC_NO_DATA = "No se han encontrado datos nuevos"
    override fun hcSyncPreview(weight: String?, height: String?, vo2max: String?): String {
        val parts = mutableListOf<String>()
        weight?.let { parts.add("peso $it kg") }
        height?.let { parts.add("altura $it cm") }
        vo2max?.let { parts.add("VO2Máx $it ml/kg/min") }
        return "Encontrado en Health Connect: ${parts.joinToString(", ")} – ¿actualizar?"
    }

    // Stage 3 - Import Workouts
    override val HC_IMPORT_TITLE = "Importar entrenamientos"
    override val HC_IMPORT_ALREADY_IMPORTED = "Ya importado"
    override val HC_IMPORT_EMPTY = "No se han encontrado entrenamientos en Health Connect de los últimos 30 dni."
    override val HC_IMPORT_CONFIRM_DESC_PLURAL = "¿Está seguro de que desea importar los entrenamientos seleccionados?"
    override val HC_IMPORT_SELECT_ALL = "Seleccionar todos"
    override fun hcImportSelected(count: Int) = "Importar seleccionados ($count)"
    override fun hcImportConfirmDesc(count: Int) = "¿Desea importar $count entrenamientos?"
    override fun hcImportProgress(current: Int, total: Int) = "Importando $current/$total entrenamientos..."

    // Stage 5 - Export
    override val HC_EXPORT_TO = "Exportar a Health Connect"
    override val HC_EXPORTED_ON = "✓ Sincronizado con Health Connect"
    override val HC_EXPORT_SUCCESS = "Exportación realizada con éxito"
    override val HC_EXPORT_ERROR = "Error al exportar a Health Connect: "
    override val HC_EXPORT_PERMISSION_DENIED = "Permiso denegado para Health Connect"
    override val SETTINGS_HC_AUTO_EXPORT = "Exportación automática"
    override val SETTINGS_HC_AUTO_EXPORT_DESC = "Exportar automáticamente los entrenamientos finalizados a Health Connect"

    // Stage 6 - Sync Status
    override val SYNC_STATUS_TITLE = "Estado de sincronización de HC"
    override val SYNC_LAST_HEALTH = "Última sincronización de datos de salud"
    override val SYNC_LAST_WORKOUT = "Última sincronización de entrenamientos"
    override val SYNC_UNSYNCED_COUNT = "Registros no sincronizados"
    override val SYNC_NOW = "Sincronizar ahora"
    override val SYNC_HISTORY_TITLE = "Historial de sincronización"
    override val SYNC_TYPE_IMPORT = "Importar"
    override val SYNC_TYPE_EXPORT = "Exportar"
    override val SYNC_NEVER = "Nunca"
    override val SYNC_CONFLICT_POLICY = "Política de conflictos"
    override val SYNC_CONFLICT_NEWER = "El más reciente gana"
    override val SYNC_CONFLICT_LOCAL = "Los locales ganan"
    override val SYNC_CONFLICT_HC = "Health Connect gana"

    // Health Connect Permissions Dialog
    override val HC_PERMISSIONS_DIALOG_TITLE = "Permisos Requeridos"
    override val HC_PERMISSIONS_DIALOG_DESC = "Los permisos de escritura son necesarios para exportar entrenamientos a Health Connect. Puedes otorgarlos en los ajustes del sistema."
    override val HC_OPEN_SETTINGS = "Abrir ajustes"

    // Health Data Screen
    override val HEALTH_TITLE = "Datos de salud"
    override val HEALTH_GENDER = "Género"
    override val HEALTH_GENDER_MALE = "Hombre"
    override val HEALTH_GENDER_FEMALE = "Mujer"
    override val HEALTH_AGE = "Edad"
    override val HEALTH_WEIGHT = "Peso"
    override val HEALTH_WEIGHT_KG = "Peso (kg)"
    override val HEALTH_HEIGHT = "Altura"
    override val HEALTH_HEIGHT_CM = "Altura (cm)"
    override val HEALTH_RESTING_HR = "FC en reposo"
    override val HEALTH_MAX_HR = "FC Máxima"
    override val HEALTH_MAX_HR_DESC = "FC Máxima"
    override val HEALTH_STEP_LENGTH = "Longitud de paso"
    override val HEALTH_STEP_LENGTH_CM = "Longitud de paso (cm)"
    override val HEALTH_VO2_MAX = "VO2 Máx"

    // Activity List
    override val ACTIVITY_LIST_TITLE = "Lista de actividades"
    override val ACTIVITY_EMPTY = "No hay actividades"
    override val ACTIVITY_DELETE_CONFIRM = "¿Está seguro de que desea eliminar permanentemente las actividades seleccionadas de la base de datos?"
    override val ACTIVITY_COMPARE = "Comparar"
    override val ACTIVITY_TRIM = "Recortar"
    override val ACTIVITY_DETAIL = "Detalles"
    override val ACTIVITY_EDIT = "Editar"
    override val ACTIVITY_IMPORT_GPX = "Importar GPX"
    override val ACTIVITY_EXPORT_GPX = "Exportar GPX"
    override val ACTIVITY_CHART_SETTINGS = "Ajustes del gráfico"
    override val ACTIVITY_FILTERS = "Filtros"
    override val ACTIVITY_ALL_TYPES = "Todos los tipos"
    override val ACTIVITY_FROM = "Desde"
    override val ACTIVITY_TO = "Hasta"
    override val ACTIVITY_TYPE = "Tipo"
    override val ACTIVITY_DATE = "Fecha"
    override val ACTIVITY_DURATION = "Tiempo"
    override val ACTIVITY_CALORIES = "Calorías"
    override val ACTIVITY_DISTANCE_GPS = "Distancia (GPS)"
    override val ACTIVITY_DISTANCE_STEPS = "Distancia (pasos)"
    override val ACTIVITY_DELETE = "Eliminar"
    override val ACTIVITY_IMPORT_SELECT_TYPE = "Seleccionar tipo de actividad"
    override val ACTIVITY_IMPORT_SELECT_DESC = "Seleccione el tipo de entrenamiento para el archivo GPX importado:"
    override val ACTIVITY_IMPORT_WARNING = "Advertencia"
    override val ACTIVITY_IMPORT_CONTINUE = "Continuar"
    override val ACTIVITY_IMPORT_PROGRESS = "Importando datos..."
    override val ACTIVITY_EXPORT_ERROR = "Error de exportación"
    override val ACTIVITY_SHARE_TITLE = "Compartir entrenamiento(s)"
    override val ACTIVITY_OK = "Aceptar"
    override val ACTIVITY_CONFIRM_DELETE_TITLE = "Eliminar actividades"
    override val ACTIVITY_ALL = "Todas"
    override val ACTIVITY_NONE = "None"

    // Activity Detail
    override val DETAIL_TITLE = "Detalles de la actividad"
    override val DETAIL_MAP = "Mapa"
    override val DETAIL_CHARTS = "Gráficos"
    override val DETAIL_LAPS = "Vueltas"
    override val DETAIL_STATISTICS = "Estadísticas"
    override val DETAIL_DATA_ERROR_TITLE = "Error de datos"
    override val DETAIL_ERROR_OK = "Aceptar"
    override val DETAIL_INTERVALS = "Intervalos"
    override fun detailLapsWithDistance(distance: String) = "Intervalos ($distance)"
    override fun detailLapsCount(count: Int) = "Número de vueltas: $count"
    override val DETAIL_HEART_RATE = "Frequenza cardíaca (bpm)"
    override val DETAIL_HR_ZONES = "Zonas de FC"
    override val DETAIL_TRAINING_EFFECT = "Efeito do treinamento"
    override val DETAIL_TRAINING_EFFECT_DESC = "Efeito do treinamento predominante"
    override val DETAIL_LAP_NR = "Núm."
    override val DETAIL_LAP_TIME = "Tiempo"
    override val DETAIL_LAP_AVG_PACE = "Ritmo médio"
    override val DETAIL_LAP_AVG_SPEED = "Velocidad media"
    override val DETAIL_LAP_MAX_SPEED = "Velocidad máxima"
    override val DETAIL_LAP_AVG_HR = "FC media"
    override val DETAIL_LAP_MAX_HR = "FC máxima"
    override val DETAIL_LAP_ASCENT_DESCENT = "Subida/Bajada"
    override val DETAIL_MAP_START = "Inicio"
    override val DETAIL_MAP_FINISH = "Meta"
    override val DETAIL_MAP_EXPAND = "Ampliar mapa"
    override val DETAIL_MAP_COLLAPSE = "Contraer mapa"
    override val DETAIL_EXPAND = "Expandir"
    override val DETAIL_COLLAPSE = "Contraer"
    override val DETAIL_PREDOMINANT_EFFECT = "Efeito do treinamento predominante"

    // Stats
    override val STATS_TITLE = "Estadísticas generales"
    override val STATS_CHARTS = "Gráficos"
    override val STATS_WIDGETS = "Widgets"
    override val STATS_NO_DATA = "No hay datos para mostrar gráficos."
    override val STATS_TREND_CHARTS = "Gráficos de tendencias"
    override val STATS_FILTERS = "Filtros"
    override val STATS_ALL_TYPES = "Todos os tipos"
    override val STATS_FROM = "Desde"
    override val STATS_TO = "Hasta"
    override val STATS_NO_WIDGETS = "No hay widgets activos. Actívalos en las opciones."
    override val STATS_SETTINGS_TITLE = "Ajustes de estadísticas generales"
    override val STATS_SECTION_WIDGETS = "Sección: Widgets"
    override val STATS_SECTION_CHARTS = "Sección: Gráficos de tendencias"
    override val STATS_MOVE_UP = "Mover hacia arriba"
    override val STATS_MOVE_DOWN = "Mover hacia abajo"
    override fun chartDistanceGps(km: Boolean) = if (km) "Distancia (GPS) en km" else "Distancia (GPS) in m"
    override fun chartDistanceSteps(km: Boolean) = if (km) "Distancia (pasos) en km" else "Distancia (pasos) in m"
    override val CHART_STEPS = "Pasos"

    // Definitions
    override val DEF_TITLE = "Definicione de actividad"
    override val DEF_ADD = "Añadir definición"
    override val DEF_EDIT = "Editar definición"
    override val DEF_DELETE = "Eliminar definición"
    override val DEF_NAME = "Nombre"
    override val DEF_ICON = "Icono"
    override val DEF_SENSORS = "Sensores"
    override val DEF_LIST_TITLE = "Definicione de actividad"
    override val DEF_SENSORS_DESC = "Gestionar lista de deportes y sensores"
    override val DEF_RECORDING = "Grabación"
    override val DEF_SELECT_ICON = "Seleccionar icono"
    override val DEF_SAVE = "Guardar"
    override val DEF_MOVE_UP = "Mover hacia arriba"
    override val DEF_MOVE_DOWN = "Mover hacia abajo"
    override val DEF_DELETE_TITLE = "Eliminar actividad"
    override fun defDeleteConfirm(name: String) = "¿Está seguro de que desea eliminar la actividad '$name'?"
    override val DEF_NEW_ACTIVITY = "Nueva actividad"
    override val DEF_EDIT_ACTIVITY = "Editar actividad"
    override val DEF_NAME_LABEL = "Nombre de la actividad"
    override val DEF_AUTO_LAP_LABEL = "Vuelta automática (metros, opcional)"
    override val DEF_WIDGET_IN_ACTIVITY = "Widget en actividad"
    override val DEF_VISIBILITY = "Sichtbarkeit"
    override val DEF_RECORD = "Grabar"
    override val DEF_BASE_TYPE = "Tipo base"
    override val DEF_FINISH = "Finalizar"
    override val DEF_SELECT_ICON_TITLE = "Seleccionar icono"
    
    // Base Types
    override val DEF_WALKING = "Caminar"
    override val DEF_SPEED_WALKING = "Caminata rápida"
    override val DEF_RUNNING = "Correr"
    override val DEF_TREADMILL_RUNNING = "Carrera en cinta"
    override val DEF_STAIR_CLIMBING = "Subir escaleras"
    override val DEF_STAIR_CLIMBING_MACHINE = "Máquina de escaleras"
    override val DEF_CYCLING = "Ciclismo"
    override val DEF_CYCLING_STATIONARY = "Ciclismo estático"
    override val DEF_MOUNTAIN_BIKING = "Ciclismo de montaña"
    override val DEF_ROAD_BIKING = "Ciclismo de carretera"
    override val DEF_HIKING = "Senderismo"
    override val DEF_ROCK_CLIMBING = "Arrampicata"
    override val DEF_BOULDERING = "Búlder"
    override val DEF_HIIT = "HIIT"
    override val DEF_ELLIPTICAL = "Elíptica"
    override val DEF_ROWING_MACHINE = "Remo"
    override val DEF_STRENGTH_TRAINING = "Entrenamiento de fuerza"
    override val DEF_CALISTHENICS = "Calistenia"
    override val DEF_YOGA = "Yoga"
    override val DEF_PILATES = "Pilates"
    override val DEF_AEROBICS = "Aeróbic"
    override val DEF_DANCING = "Baile"
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
