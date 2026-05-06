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
    override val HOME_LOGO_DESC = "Logo de la aplicación"
    override val HOME_SECRET_TITLE = "Es genial que hagas clic, pero aquí no hay nada"
    override val HOME_CLOSE = "Cerrar"
    override val HOME_START_LIVE = "Iniciar Live Tracking"
    override val HOME_ACTIVE_WORKOUT = "Actividad en curso"
    override val HOME_RESUME_TRACKING = "Seguir actividad"

    override fun homeResultsToday() = "Resultados de hoy:"
    override fun homeResultsWeek() = "Resultados de la semana:"
    override fun homeResultsMonth(): String {
        val monthName = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("es", "ES"))
        return "Resultados de $monthName:"
    }
    override fun homeResultsYear() = "Resultados de este año:"
    override fun homeResultsCustom(days: Int) = if (days == 1) "Resultados del último día:" else "Resultados de los últimos $days días:"

    // Settings Screen
    override val SETTINGS_TITLE = "Ajustes"
    override val SETTINGS_GENERAL = "General"
    override val SETTINGS_THEME = "Tema de la aplicación"
    override val SETTINGS_THEME_SYSTEM = "Sistema"
    override val SETTINGS_THEME_LIGHT = "Claro"
    override val SETTINGS_THEME_DARK = "Oscuro"
    override val SETTINGS_LANGUAGE = "Idioma"
    override val SETTINGS_LANGUAGE_TITLE = "Elige el idioma"
    override val SETTINGS_HEALTH_DATA = "Datos de salud y FC"
    override val SETTINGS_HEALTH_DATA_DESC = "Edad, peso, FC Máx y zonas"
    override val SETTINGS_DEFINITIONS = "Definic. de actividad"
    override val SETTINGS_DEFINITIONS_DESC = "Gestionar deportes y sensores"
    override val SETTINGS_WIDGETS_HOME = "Widgets de pantalla de inicio"
    override val SETTINGS_WIDGETS_HOME_TITLE = "Vista de inicio"
    override val SETTINGS_WIDGETS_HOME_DESC = "Elige y establece el orden"
    override val SETTINGS_WIDGETS_WATCH = "Estadísticas en el reloj"
    override val SETTINGS_WIDGETS_WATCH_TITLE = "Campos de estadísticas"
    override val SETTINGS_WIDGETS_WATCH_DESC = "Elige y ordena en el reloj"
    override val SETTINGS_SAVE = "Guardar"
    override val SETTINGS_CANCEL = "Cancelar"
    override val SETTINGS_CLOSE = "Cerrar"
    override val SETTINGS_PERIOD = "Periodo por defecto"
    override val SETTINGS_PERIOD_HOME_DESC = "¿Para qué periodo mostrar widgets?"
    override val SETTINGS_PERIOD_WATCH_DESC = "¿Estadísticas de qué periodo?"
    override val SETTINGS_CUSTOM_DAYS_LABEL = "Número de días"
    override val SETTINGS_WATCH_STATS_DAYS_LABEL = "¿Mostrar estadísticas de cuántos días?"
    override val SETTINGS_CUSTOM_DAYS_DESC = "Días para el periodo 'Otros'"
    override val SETTINGS_WATCH_STATS_DAYS_DESC = "Días de estadísticas en el reloj"
    override val SETTINGS_INTEGRATION = "Integración"
    override val SETTINGS_SYNC = "Sincronización"
    override val SETTINGS_STRAVA = "Strava"
    override val SETTINGS_STRAVA_DESC = "Sincroniza tus entrenamientos con Strava"
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
    override val HC_SYNC_CONFIRM_DESC = "¿Quieres actualizar tu perfil con los datos de Health Connect?"
    override val HC_SYNC_SUCCESS = "Sincronización exitosa"
    override val HC_SYNC_ERROR = "Error de sincronización"
    override val HC_SYNC_NO_DATA = "No se encontraron nuevos datos"
    override val HC_IMPORT_SELECT_FIELDS_TITLE = "Elige los datos a importar"
    override fun hcSyncPreview(weight: String?, height: String?, vo2max: String?): String {
        val parts = mutableListOf<String>()
        weight?.let { parts.add("peso $it kg") }
        height?.let { parts.add("altura $it cm") }
        vo2max?.let { parts.add("VO2Max $it ml/kg/min") }
        return "Encontrado en Health Connect: ${parts.joinToString(", ")} – ¿actualizar?"
    }

    // Stage 3 - Import Workouts
    override val HC_IMPORT_TITLE = "Importar entrenamientos"
    override val HC_IMPORT_ALREADY_IMPORTED = "Ya importado"
    override val HC_IMPORT_EMPTY = "No hay entrenamientos en Health Connect de la última semana."
    override val HC_IMPORT_CONFIRM_DESC_PLURAL = "¿Seguro que quieres importar los entrenamientos?"
    override val HC_IMPORT_SELECT_ALL = "Seleccionar todos"
    override fun hcImportSelected(count: Int) = "Importar seleccionados ($count)"
    override fun hcImportConfirmDesc(count: Int) = "¿Quieres importar $count entrenamientos?"
    override fun hcImportProgress(current: Int, total: Int) = "Importando $current/$total entrenamientos..."

    // Stage 5 - Export
    override val HC_EXPORT_TO = "Exportar a Health Connect"
    override val HC_EXPORTED_ON = "Sincronizado con Health Connect"
    override val HC_EXPORT_SUCCESS = "Exportación completada con éxito"
    override val HC_EXPORT_ERROR = "Error de exportación: "
    override val HC_EXPORT_PERMISSION_DENIED = "Permiso de escritura denegado en Health Connect"
    override val SETTINGS_HC_AUTO_EXPORT = "Exportación automática"
    override val SETTINGS_HC_AUTO_EXPORT_DESC = "Exportar automáticamente nuevos entrenamientos a Health Connect"

    // Stage 6 - Sync Status
    override val SYNC_STATUS_TITLE = "Estado de sincronización HC"
    override val SYNC_LAST_HEALTH = "Última sincr. salud"
    override val SYNC_LAST_WORKOUT = "Última sincr. entrenamiento"
    override val SYNC_UNSYNCED_COUNT = "Registros no sincronizados"
    override val SYNC_NOW = "Sincronizar ahora"
    override val SYNC_HISTORY_TITLE = "Historial de sincronización"
    override val SYNC_TYPE_IMPORT = "Importar"
    override val SYNC_TYPE_EXPORT = "Exportar"
    override val SYNC_NEVER = "Nunca"
    override val SYNC_CONFLICT_POLICY = "Política de conflictos"
    override val SYNC_CONFLICT_NEWER = "Gana el más reciente"
    override val SYNC_CONFLICT_LOCAL = "Gana el local"
    override val SYNC_CONFLICT_HC = "Gana Health Connect"

    // Health Connect Permissions Dialog
    override val HC_PERMISSIONS_DIALOG_TITLE = "Permisos necesarios"
    override val HC_PERMISSIONS_DIALOG_DESC = "Los permisos son necesarios para exportar a Health Connect. Puedes darlos en ajustes del sistema."
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
    override val HEALTH_RESTING_HR = "Pulso en reposo"
    override val HEALTH_MAX_HR = "Pulso máximo"
    override val HEALTH_MAX_HR_DESC = "Pulso máximo"
    override val HEALTH_STEP_LENGTH = "Longitud de paso"
    override val HEALTH_STEP_LENGTH_CM = "Longitud de paso (cm)"
    override val HEALTH_VO2_MAX = "VO2 Máx"

    // Activity List
    override val ACTIVITY_LIST_TITLE = "Lista de actividades"
    override val ACTIVITY_EMPTY = "Sin actividades"
    override val ACTIVITY_DELETE_CONFIRM = "¿Seguro que quieres borrar permanentemente las actividades seleccionadas?"
    override val ACTIVITY_COMPARE = "Comparar"
    override val ACTIVITY_TRIM = "Recortar"
    override val ACTIVITY_DETAIL = "Detalles"
    override val ACTIVITY_EDIT = "Editar"
    override val ACTIVITY_IMPORT_GPX = "Importar GPX"
    override val ACTIVITY_EXPORT_GPX = "Exportar GPX"
    override val ACTIVITY_CHART_SETTINGS = "Ajustes de gráficos"
    override val ACTIVITY_FILTERS = "Filtros"
    override val ACTIVITY_ALL_TYPES = "Todos los tipos"
    override val ACTIVITY_FROM = "Desde"
    override val ACTIVITY_TO = "Hasta"
    override val ACTIVITY_TYPE = "Tipo"
    override val ACTIVITY_DATE = "Fecha"
    override val ACTIVITY_DURATION = "Duración"
    override val ACTIVITY_CALORIES = "Calorías"
    override val ACTIVITY_DISTANCE_GPS = "Distancia (GPS)"
    override val ACTIVITY_DISTANCE_STEPS = "Distancia (pasos)"
    override val ACTIVITY_DELETE = "Borrar"
    override val ACTIVITY_IMPORT_SELECT_TYPE = "Elegir tipo de actividad"
    override val ACTIVITY_IMPORT_SELECT_DESC = "Elige el tipo de entrenamiento para el archivo GPX:"
    override val ACTIVITY_IMPORT_WARNING = "Aviso"
    override val ACTIVITY_IMPORT_CONTINUE = "Continuar"
    override val ACTIVITY_IMPORT_PROGRESS = "Importando datos..."
    override val ACTIVITY_EXPORT_ERROR = "Error de exportación"
    override val ACTIVITY_EXPORT_DIALOG_TITLE = "Exportar actividad"
    override val STR_STRAVA = "Strava"
    override val STR_HEALTH_CONNECT = "Health Connect"
    override val ACTIVITY_ALREADY_SYNCED = "Sincronizado"
    override val ACTIVITY_EXPORT = "Exportar"
    override val ACTIVITY_SHARE_TITLE = "Compartir entrenamiento(s)"
    override val ACTIVITY_OK = "OK"
    override val ACTIVITY_CONFIRM_DELETE_TITLE = "Borrar actividades"
    override val ACTIVITY_ALL = "Todas"
    override val ACTIVITY_NONE = "Ninguna"

    // New Export/Import SAE
    override val ACTIVITY_EXPORT_SAE = "Exportar SAE"
    override val ACTIVITY_IMPORT_SAE = "Importar SAE"
    override val ACTIVITY_EXPORT_FORMAT_SELECT = "Elegir formato de exportación"
    override val ACTIVITY_EXPORT_SAE_DESC = "El formato SAE (.sae) permite un respaldo completo, incluyendo entrenamientos indoor (sin GPS)."
    override val ACTIVITY_EXPORT_INCOMPATIBLE_GPX = "Algunas actividades no tienen datos GPS y no pueden exportarse a GPX."

    // Activity Detail
    override val DETAIL_TITLE = "Detalles de actividad"
    override val DETAIL_MAP = "Mapa"
    override val DETAIL_CHARTS = "Gráficos"
    override val DETAIL_LAPS = "Vueltas"
    override val DETAIL_STATISTICS = "Estadísticas"
    override val DETAIL_DATA_ERROR_TITLE = "Error de datos"
    override val DETAIL_ERROR_OK = "OK"
    override val DETAIL_INTERVALS = "Intervalos"
    override fun detailLapsWithDistance(distance: String) = "Intervalos ($distance)"
    override fun detailLapsCount(count: Int) = "Número de vueltas: $count"
    override val DETAIL_HEART_RATE = "Pulso (bpm)"
    override val DETAIL_HR_ZONES = "Zonas de FC"
    override val DETAIL_TRAINING_EFFECT = "Efecto entrenamiento"
    override val DETAIL_TRAINING_EFFECT_DESC = "Efecto dominante"
    override val DETAIL_LAP_NR = "Nº"
    override val DETAIL_LAP_TIME = "Tiempo"
    override val DETAIL_LAP_AVG_PACE = "Ritmo med."
    override val DETAIL_LAP_AVG_SPEED = "Veloc. med."
    override val DETAIL_LAP_MAX_SPEED = "Veloc. máx."
    override val DETAIL_LAP_AVG_HR = "FC media"
    override val DETAIL_LAP_MAX_HR = "FC máx."
    override val DETAIL_LAP_ASCENT_DESCENT = "Sub/Baj"
    override val DETAIL_MAP_START = "Inicio"
    override val DETAIL_MAP_FINISH = "Fin"
    override val DETAIL_MAP_EXPAND = "Ampliar mapa"
    override val DETAIL_MAP_COLLAPSE = "Reducir mapa"
    override val DETAIL_EXPAND = "Expandir"
    override val DETAIL_COLLAPSE = "Contraer"
    override val DETAIL_PREDOMINANT_EFFECT = "Efecto dominante del entrenamiento"

    // Stats
    override val STATS_TITLE = "Estadísticas generales"
    override val STATS_CHARTS = "Gráficos"
    override val STATS_WIDGETS = "Widgets"
    override val STATS_NO_DATA = "No hay datos para mostrar gráficos."
    override val STATS_TREND_CHARTS = "Gráficos de tendencia"
    override val STATS_FILTERS = "Filtros"
    override val STATS_ALL_TYPES = "Todos los tipos"
    override val STATS_FROM = "Desde"
    override val STATS_TO = "Hasta"
    override val STATS_NO_WIDGETS = "Widgets inactivos. Actívalos en opciones."
    override val STATS_SETTINGS_TITLE = "Ajustes de estadísticas generales"
    override val STATS_SECTION_WIDGETS = "Sección: Widgets"
    override val STATS_SECTION_CHARTS = "Sección: Tendencias"
    override val STATS_MOVE_UP = "Mover arriba"
    override val STATS_MOVE_DOWN = "Mover abajo"
    override fun chartDistanceGps(km: Boolean) = if (km) "Distancia (GPS) en km" else "Distancia (GPS) en m"
    override fun chartDistanceSteps(km: Boolean) = if (km) "Distancia (pasos) en km" else "Distancia (pasos) en m"
    override val CHART_STEPS = "Pasos"

    // Definitions
    override val DEF_TITLE = "Definiciones de entreno"
    override val DEF_ADD = "Añadir definición"
    override val DEF_EDIT = "Editar definición"
    override val DEF_DELETE = "Borrar definición"
    override val DEF_NAME = "Nombre"
    override val DEF_ICON = "Icono"
    override val DEF_SENSORS = "Sensores"
    override val DEF_LIST_TITLE = "Definición de actividad"
    override val DEF_SENSORS_DESC = "Gestionar lista de deportes y sensores"
    override val DEF_RECORDING = "Grabación"
    override val DEF_SELECT_ICON = "Elegir icono"
    override val DEF_SAVE = "Guardar"
    override val DEF_MOVE_UP = "Mover arriba"
    override val DEF_MOVE_DOWN = "Mover abajo"
    override val DEF_DELETE_TITLE = "Borrar actividad"
    override fun defDeleteConfirm(name: String) = "¿Seguro que quieres borrar la actividad '$name'?"
    override val DEF_NEW_ACTIVITY = "Nueva actividad"
    override val DEF_EDIT_ACTIVITY = "Editar actividad"
    override val DEF_NAME_LABEL = "Nombre de la actividad"
    override val DEF_NAME_HINT = "Ej. Cinta"
    override val DEF_AUTO_LAP_LABEL = "Vuelta automática (metros, opcional)"
    override val DEF_WIDGET_IN_ACTIVITY = "Widget en actividad"
    override val DEF_VISIBILITY = "Visibilidad"
    override val DEF_RECORD = "Grabar"
    override val DEF_BASE_TYPE = "Tipo base"
    override val DEF_FINISH = "Terminar"
    override val DEF_SELECT_ICON_TITLE = "Elegir icono"

    // Base Types
    override val DEF_WALKING = "Caminar"
    override val DEF_SPEED_WALKING = "Caminata rápida"
    override val DEF_RUNNING = "Correr"
    override val DEF_TREADMILL_RUNNING = "Cinta de correr"
    override val DEF_STAIR_CLIMBING = "Subir escaleras"
    override val DEF_STAIR_CLIMBING_MACHINE = "Step"
    override val DEF_CYCLING = "Ciclismo"
    override val DEF_CYCLING_STATIONARY = "Bicicleta estática"
    override val DEF_MOUNTAIN_BIKING = "Ciclismo de montaña"
    override val DEF_ROAD_BIKING = "Ciclismo de carretera"
    override val DEF_HIKING = "Senderismo"
    override val DEF_ROCK_CLIMBING = "Escalada"
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
    override val DEF_SWIMMING_POOL = "Natación (piscina)"
    override val DEF_SWIMMING_OPEN_WATER = "Natación (aguas abiertas)"
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
    override val DEF_SNOWBOARDING = "Snowboard"
    override val DEF_SKATING = "Patinaje"
    override val DEF_ICE_SKATING = "Patinaje artístico"

    override val DEF_GYM = "Gimnasio"
    override val DEF_BASEBALL = "Béisbol"
    override val DEF_SKATEBOARDING = "Skate"
    override val DEF_COMPETITION = "Competición"
    override val DEF_STOPWATCH = "Cronómetro"
    override val DEF_OTHER = "Otro"
    override val DEF_STANDARD_ACTIVITY = "Actividad estándar"

    // Activity Detail Settings
    override val AD_SETTINGS_LIST_TITLE = "Elige actividad para modificar"
    override val AD_SETTINGS_EDIT_TITLE = "Ajustes"
    override val AD_SETTINGS_SECTION_WIDGETS = "Sección: Widgets"
    override val AD_SETTINGS_SECTION_CHARTS = "Sección: Gráficos"

    // Heart Rate Math
    override val HR_NO_DATA = "Sin datos de pulso"
    override val HR_TOO_LITTLE_DATA = "Datos insuficientes"
    override val HR_BELOW_ZONES = "Pulso bajo zonas"
    override val HR_EFFECT_Z0 = "Baja intensidad / Calentamiento"
    override val HR_EFFECT_Z1 = "Base aeróbica y recuperación"
    override val HR_EFFECT_Z2 = "Quema grasas eficiente"
    override val HR_EFFECT_Z3 = "Mejora capacidad aeróbica"
    override val HR_EFFECT_Z4 = "Aumento del umbral láctico"
    override val HR_EFFECT_Z5 = "Entreno anaeróbico y VO2 Máx"
    override val HR_EFFECT_NONE = "Sin zona dominante"

    // HR Zones Names
    override val ZONE_Z0 = "Calentamiento"
    override val ZONE_Z1 = "Muy ligero"
    override val ZONE_Z2 = "Ligero"
    override val ZONE_Z3 = "Moderado"
    override val ZONE_Z4 = "Difícil"
    override val ZONE_Z5 = "Máximo"

    // ViewModels Messages
    override val VM_EXPORT_INITIALIZING = "Iniciando exportación..."
    override fun vmExportGenerating(name: String, current: Int, total: Int) = "Generando: $name ($current/$total)"
    override val VM_EXPORT_NO_FILES = "No se generaron archivos."
    override val VM_EXPORT_ZIPPING = "Comprimiendo a ZIP..."
    override fun vmExportError(msg: String) = "Error al exportar: $msg"
    override val VM_IMPORT_OPEN_ERROR = "No se pudo abrir el archivo"
    override val VM_IMPORT_DUPLICATE_WARNING = "Duplicado detectado (misma fecha y duración)."
    override val VM_IMPORT_DUPLICATE_WARNING_DESC = "¿Quieres continuar?"
    override val VM_IMPORT_SUCCESS = "Entrenamiento importado con éxito."
    override fun vmImportError(msg: String) = "Error de importación: $msg"

    // Gpx Importer
    override val GPX_NO_POINTS = "El archivo GPX no tiene puntos de ruta."
    override val GPX_WARN_HR = "El archivo tiene datos de pulso, pero la actividad no los soporta."
    override val GPX_WARN_ELE = "El archivo tiene altitud, pero la actividad no los soporta."
    override val GPX_WARN_CADENCE = "El archivo tiene cadencia, pero la actividad no los soporta."

    // Periods
    override val PERIOD_TODAY = "Hoy"
    override val PERIOD_WEEK = "Semana"
    override val PERIOD_MONTH = "Mes"
    override val PERIOD_YEAR = "Año"
    override val PERIOD_CUSTOM = "Otros"
    override fun periodCustomDays(days: Int) = if (days == 1) "$days día" else "$days días"

    // Widgets
    override val WIDGET_COUNT = "Número de actividades"
    override val WIDGET_CALORIES = "Calorías quemadas"
    override val WIDGET_DISTANCE_GPS = "Distancia (GPS)"
    override val WIDGET_DISTANCE_STEPS = "Distancia (pasos)"
    override val WIDGET_ASCENT = "Ascenso total"
    override val WIDGET_DESCENT = "Descenso total"
    override val WIDGET_STEPS = "Pasos"
    override val WIDGET_AVG_BPM = "Pulso medio"
    override val WIDGET_AVG_CADENCE = "Cadencia media"
    override val WIDGET_MAX_SPEED = "Veloc. máx"
    override val WIDGET_MAX_ALTITUDE = "Altitud máx"
    override val WIDGET_MAX_ELEVATION_GAIN = "Máximo desnivel"
    override val WIDGET_MAX_DISTANCE = "Mayor distancia"
    override val WIDGET_MAX_DURATION = "Tiempo más largo"
    override val WIDGET_MAX_CALORIES = "Más calorías"
    override val WIDGET_MAX_AVG_CADENCE = "Mayor cadencia med."
    override val WIDGET_MAX_AVG_SPEED = "Mayor veloc. med."
    override val WIDGET_DURATION = "Duración"
    override val WIDGET_MAX_BPM = "Pulso máximo"
    override val WIDGET_TOTAL_CALORIES = "Calorías quemadas"
    override val WIDGET_MAX_CALORIES_MIN = "Máx. quema de kcal"
    override val WIDGET_AVG_PACE = "Ritmo medio"
    override val WIDGET_AVG_SPEED_GPS = "Velocidad media (GPS)"
    override val WIDGET_AVG_SPEED_STEPS = "Velocidad media (pasos)"
    override val WIDGET_MAX_ALTITUDE_DESC = "Altitud máx"
    override val WIDGET_TOTAL_ASCENT = "Suma de ascensos"
    override val WIDGET_TOTAL_DESCENT = "Suma de descensos"
    override val WIDGET_AVG_STEP_LENGTH = "Longitud de paso calc."
    override val WIDGET_AVG_CADENCE_DESC = "Cadencia med."
    override val WIDGET_MAX_CADENCE = "Cadencia máx."
    override val WIDGET_TOTAL_STEPS = "Número de pasos"
    override val WIDGET_PRESSURE_START = "Presión atm. (inicio)"
    override val WIDGET_PRESSURE_END = "Presión atm. (fin)"
    override val WIDGET_MAX_PRESSURE = "Máx. presión"
    override val WIDGET_MIN_PRESSURE = "Mín. presión"
    override val WIDGET_BEST_PACE_1KM = "Mejor ritmo (1km)"
    override val WIDGET_WATCH_ASCENT = "Desnivel positivo"
    override val WIDGET_WATCH_DESCENT = "Desnivel negativo"

    // Sensors
    override val SENSOR_HEART_RATE = "Pulso"
    override val SENSOR_CALORIES_SUM = "Calorías quemadas"
    override val SENSOR_CALORIES_MIN = "Calorías por minuto"
    override val SENSOR_STEPS = "Pasos"
    override val SENSOR_STEPS_MIN = "Cadencia (pasos/min)"
    override val SENSOR_DISTANCE_STEPS = "Distancia (pasos)"
    override val SENSOR_SPEED_GPS = "Velocidad"
    override val SENSOR_SPEED_STEPS = "Velocidad (pasos)"
    override val SENSOR_DISTANCE_GPS = "Distancia"
    override val SENSOR_ALTITUDE = "Altitud"
    override val SENSOR_TOTAL_ASCENT = "Suma de ascensos"
    override val SENSOR_TOTAL_DESCENT = "Suma de descensos"
    override val SENSOR_PRESSURE = "Presión atm."
    override val SENSOR_MAP = "Datos de ubicación"
    override val SENSOR_AVG_STEP_LENGTH = "Longitud de paso media"

    // Trim Screen
    override val TRIM_TITLE = "Recortar entrenamiento"
    override val TRIM_CONFIRM_TITLE = "Confirmar recorte"
    override val TRIM_CONFIRM_DESC = "¿Seguro que quieres borrar los datos fuera del rango? Se borrarán para siempre."
    override val TRIM_SAVE_BTN = "Recortar y guardar"
    override val TRIM_CHART_HR = "Gráfico de pulso"
    override val TRIM_RANGE_TITLE = "Elegir rango de entreno"
    override val TRIM_PREVIEW_TITLE = "Vista previa de nuevas stats"
    override val TRIM_NEW_DURATION = "Nueva duración:"
    override val TRIM_DISTANCE_GPS = "Distancia (GPS):"
    override val TRIM_DISTANCE_STEPS = "Distancia (Pasos):"
    override val TRIM_CALORIES = "Calorías quemadas:"
    override val TRIM_AVG_BPM = "Pulso medio:"
    override val TRIM_START = "Inicio"
    override val TRIM_END = "Fin"

    // Compare Screen
    override val COMPARE_TITLE = "Comparar actividades"
    override val COMPARE_VS = "Comparación:"
    override val COMPARE_HIGHER_IS_BETTER = "Mayor es mejor"
    override val COMPARE_LOWER_IS_BETTER = "Menor es mejor"

    // Units
    override val UNIT_KCAL = "kcal"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_STEP_MIN = "pas/min"
    override val UNIT_KM_H = "km/h"
    override val UNIT_STEPS = "pasos"
    override val UNIT_HPA = "hPa"
    override val UNIT_MIN_KM = "min/km"
    override val UNIT_M_ASL = "m s. n. m."
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
    override val STRAVA_TITLE = "Sincronización con Strava"
    override val STRAVA_CONNECT = "Conectar cuenta de Strava"
    override val STRAVA_DISCONNECT = "Desconectar cuenta de Strava"
    override val STRAVA_CONNECTED = "Conectado a Strava"
    override val STRAVA_NOT_CONNECTED = "No conectado"
    override val STRAVA_SYNC_NOW = "Sincronizar ahora"
    override val STRAVA_SYNC_SUCCESS = "¡Entrenamiento enviado!"
    override val STRAVA_SYNC_FAILED = "Error de envío"
    override val STRAVA_SYNCING = "Enviando..."
    override val STRAVA_AUTH_ERROR = "Error de autorización"
    override val SETTINGS_STRAVA_AUTO_EXPORT = "Exportación automática"
    override val SETTINGS_STRAVA_AUTO_EXPORT_DESC = "Enviar automáticamente nuevos entrenamientos a Strava"
    override val STRAVA_SYNC_LOG = "Historial de sincronización"
    override val STRAVA_SYNC_LOG_EMPTY = "No hay historial de sincronización"

    // Live Tracking
    override val LIVE_TRACKING_TITLE = "Live Tracking"
    override val LIVE_TRACKING_SELECT_ACTIVITY = "Elegir actividad"
    override val LIVE_TRACKING_LOCK = "Bloquear"
    override val LIVE_TRACKING_UNLOCK_SWIPE = "Desliza hacia arriba para desbloquear"
    override val LIVE_TRACKING_MAP_NORTH = "Norte"
    override val LIVE_TRACKING_MAP_DIRECTION = "Dirección"
    override val LIVE_TRACKING_WAITING_FOR_WATCH = "Esperando señal del reloj..."
    override val LIVE_TRACKING_FINISHED_TITLE = "Actividad terminada"
    override val LIVE_TRACKING_FINISHED_DESC = "Actividad terminada con éxito"
    override val LIVE_TRACKING_BTN_FINISH = "Terminar"
    override val LIVE_TRACKING_BTN_VIEW_MAP = "Ver mapa"
    override val LIVE_TRACKING_PAUSED = "Pausa"

    // Errors
    override val ERROR_WEARABLE_NOT_AVAILABLE = "Servicios Wearable no disponibles en este dispositivo"
    override val ERROR_NO_WATCH_CONNECTED = "No se encontró ningún reloj conectado"

    // Map Types
    override val MAP_TYPE_TITLE = "Tipo de mapa"
    override val MAP_TYPE_NORMAL = "Normal"
    override val MAP_TYPE_SATELLITE = "Satélite"
    override val MAP_TYPE_HYBRID = "Híbrido"
    override val MAP_TYPE_TERRAIN = "Terreno"

    // Support System
    override val SUPPORT_TITLE = "Apoyar al autor"
    override val SUPPORT_DISCLAIMER = "Todas las funciones son gratuitas. El nivel es un apoyo voluntario. Cambiar el icono es el único beneficio digital."
    override val SUPPORT_LIFETIME_BUY = "De por vida"
    override val SUPPORT_MONTHLY_SUB = "Mensual"
    override val SUPPORT_ICON_CHANGE_NOTICE = "Cambiar el icono puede tardar unos segundos o requerir reiniciar el launcher."
}