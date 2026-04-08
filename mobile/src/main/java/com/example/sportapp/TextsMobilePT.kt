package com.example.sportapp

import java.util.Calendar
import java.util.Locale

object TextsMobilePT : MobileTexts {
    // Navigation
    override val NAV_HOME = "Início"
    override val NAV_STATS = "Estatísticas"
    override val NAV_ACTIVITIES = "Atividades"
    override val NAV_SETTINGS = "Configurações"

    // Home Screen
    override val HOME_TITLE = "SportApp"
    override val HOME_NO_WIDGETS = "Nenhum widget selecionado"
    override val HOME_ADD_WIDGETS = "Adicionar widgets"
    override val HOME_LAST_ACTIVITY = "Última atividade"
    override val HOME_ACTIVITY_COUNT = "Contagem de atividades"
    override val HOME_SYNC = "Sincronizar"
    override val HOME_OPTIONS = "Opções"
    override val HOME_GENERAL_STATS = "Estatísticas Gerais"
    override val HOME_WORKOUT_DETAILS = "Detalhes do Treino"
    override val HOME_LOGO_DESC = "Logotipo do App"
    override val HOME_SECRET_TITLE = "Legal que você está clicando, mas não há nada aqui"
    override val HOME_CLOSE = "Fechar"

    override fun homeResultsToday() = "Resultados de hoje:"
    override fun homeResultsWeek() = "Resultados da semana:"
    override fun homeResultsMonth(): String {
        val monthName = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "PT"))
        return "Resultados de $monthName:"
    }
    override fun homeResultsYear() = "Resultados deste ano:"
    override fun homeResultsCustom(days: Int) = if (days == 1) "Resultados do último dia:" else "Resultados dos últimos $days dias:"

    // Settings Screen
    override val SETTINGS_TITLE = "Configurações"
    override val SETTINGS_GENERAL = "Geral"
    override val SETTINGS_THEME = "Tema do App"
    override val SETTINGS_THEME_SYSTEM = "Sistema"
    override val SETTINGS_THEME_LIGHT = "Claro"
    override val SETTINGS_THEME_DARK = "Escuro"
    override val SETTINGS_LANGUAGE = "Idioma"
    override val SETTINGS_LANGUAGE_TITLE = "Selecionar Idioma"
    override val SETTINGS_HEALTH_DATA = "Dados de Saúde e FC"
    override val SETTINGS_HEALTH_DATA_DESC = "Idade, peso, FC Máx e zonas"
    override val SETTINGS_DEFINITIONS = "Definições de Atividade"
    override val SETTINGS_DEFINITIONS_DESC = "Gerenciar lista de esportes e sensores"
    override val SETTINGS_WIDGETS_HOME = "Widgets da Tela Inicial"
    override val SETTINGS_WIDGETS_HOME_TITLE = "Vista Inicial"
    override val SETTINGS_WIDGETS_HOME_DESC = "Selecionar e definir a ordem"
    override val SETTINGS_WIDGETS_WATCH = "Estatísticas do Relógio"
    override val SETTINGS_WIDGETS_WATCH_TITLE = "Campos de Estatísticas"
    override val SETTINGS_WIDGETS_WATCH_DESC = "Selecionar e definir a ordem no relógio"
    override val SETTINGS_SAVE = "Salvar"
    override val SETTINGS_CANCEL = "Cancelar"
    override val SETTINGS_CLOSE = "Fechar"
    override val SETTINGS_PERIOD = "Período Padrão"
    override val SETTINGS_PERIOD_HOME_DESC = "Para qual período mostrar widgets?"
    override val SETTINGS_PERIOD_WATCH_DESC = "Estatísticas de qual período?"
    override val SETTINGS_CUSTOM_DAYS_LABEL = "Número de dias"
    override val SETTINGS_INTEGRATION = "Integração"
    override val SETTINGS_GOOGLE_DRIVE = "Google Drive"
    override val SETTINGS_GOOGLE_DRIVE_DESC = "Salvar histórico e resumos (Em breve)"
    override val SETTINGS_APPEARANCE = "Aparência"
    override val SETTINGS_MY_PROFILE = "Meu Perfil"
    override val LANG_PL = "Polonês"
    override val LANG_EN = "Inglês"

    // Health Data Screen
    override val HEALTH_TITLE = "Dados de Saúde"
    override val HEALTH_GENDER = "Gênero"
    override val HEALTH_GENDER_MALE = "Masculino"
    override val HEALTH_GENDER_FEMALE = "Feminino"
    override val HEALTH_AGE = "Idade"
    override val HEALTH_WEIGHT = "Peso"
    override val HEALTH_WEIGHT_KG = "Peso (kg)"
    override val HEALTH_HEIGHT = "Altura"
    override val HEALTH_HEIGHT_CM = "Altura (cm)"
    override val HEALTH_RESTING_HR = "FC em repouso"
    override val HEALTH_MAX_HR = "FC máxima (FC Máx)"
    override val HEALTH_MAX_HR_DESC = "Frequência cardíaca máxima (FC Máx)"
    override val HEALTH_STEP_LENGTH = "Comprimento do passo"
    override val HEALTH_STEP_LENGTH_CM = "Comprimento do passo (cm)"

    // Activity List
    override val ACTIVITY_LIST_TITLE = "Lista de Atividades"
    override val ACTIVITY_EMPTY = "Nenhuma atividade"
    override val ACTIVITY_DELETE_CONFIRM = "Tem certeza de que deseja excluir permanentemente as atividades selecionadas do banco de dados?"
    override val ACTIVITY_COMPARE = "Comparar"
    override val ACTIVITY_TRIM = "Recortar"
    override val ACTIVITY_DETAIL = "Detalhes"
    override val ACTIVITY_EDIT = "Editar"
    override val ACTIVITY_IMPORT_GPX = "Importar GPX"
    override val ACTIVITY_EXPORT_GPX = "Exportar GPX"
    override val ACTIVITY_CHART_SETTINGS = "Configurações de Gráfico"
    override val ACTIVITY_FILTERS = "Filtros"
    override val ACTIVITY_ALL_TYPES = "Todos os tipos"
    override val ACTIVITY_FROM = "De"
    override val ACTIVITY_TO = "Para"
    override val ACTIVITY_TYPE = "Tipo"
    override val ACTIVITY_DATE = "Data"
    override val ACTIVITY_DURATION = "Tempo"
    override val ACTIVITY_CALORIES = "Calorias"
    override val ACTIVITY_DISTANCE_GPS = "Distância (GPS)"
    override val ACTIVITY_DISTANCE_STEPS = "Distância (Passos)"
    override val ACTIVITY_DELETE = "Excluir"
    override val ACTIVITY_IMPORT_SELECT_TYPE = "Selecionar tipo de atividade"
    override val ACTIVITY_IMPORT_SELECT_DESC = "Selecionar tipo de treino para o arquivo GPX importado:"
    override val ACTIVITY_IMPORT_WARNING = "Aviso"
    override val ACTIVITY_IMPORT_CONTINUE = "Continuar"
    override val ACTIVITY_IMPORT_PROGRESS = "Importando dados..."
    override val ACTIVITY_EXPORT_ERROR = "Erro na exportação"
    override val ACTIVITY_SHARE_TITLE = "Compartilhar treino(s)"
    override val ACTIVITY_OK = "OK"
    override val ACTIVITY_CONFIRM_DELETE_TITLE = "Excluir atividades"
    override val ACTIVITY_ALL = "Tudo"

    // Activity Detail
    override val DETAIL_TITLE = "Detalhes da Atividade"
    override val DETAIL_MAP = "Mapa"
    override val DETAIL_CHARTS = "Gráficos"
    override val DETAIL_LAPS = "Voltas"
    override val DETAIL_STATISTICS = "Estatísticas"
    override val DETAIL_DATA_ERROR_TITLE = "Erro de Dados"
    override val DETAIL_ERROR_OK = "OK"
    override val DETAIL_INTERVALS = "Intervalos"
    override fun detailLapsWithDistance(distance: String) = "Intervalos ($distance)"
    override fun detailLapsCount(count: Int) = "Contagem de voltas: $count"
    override val DETAIL_HEART_RATE = "Frequência Cardíaca (bpm)"
    override val DETAIL_HR_ZONES = "Zonas de FC"
    override val DETAIL_TRAINING_EFFECT = "Efeito do Treino"
    override val DETAIL_LAP_NR = "Nº"
    override val DETAIL_LAP_TIME = "Tempo"
    override val DETAIL_LAP_AVG_PACE = "Ritmo Méd."
    override val DETAIL_LAP_AVG_SPEED = "Veloc. Méd."
    override val DETAIL_LAP_MAX_SPEED = "Veloc. Máx."
    override val DETAIL_LAP_AVG_HR = "FC Méd."
    override val DETAIL_LAP_MAX_HR = "FC Máx."
    override val DETAIL_LAP_ASCENT_DESCENT = "Subida/Descida"
    override val DETAIL_MAP_START = "Início"
    override val DETAIL_MAP_FINISH = "Fim"
    override val DETAIL_MAP_EXPAND = "Expandir mapa"
    override val DETAIL_MAP_COLLAPSE = "Recolher mapa"
    override val DETAIL_EXPAND = "Expandir"
    override val DETAIL_COLLAPSE = "Recolher"
    override val DETAIL_PREDOMINANT_EFFECT = "Efeito de treino predominante"

    // Stats
    override val STATS_TITLE = "Estatísticas Gerais"
    override val STATS_CHARTS = "Gráficos"
    override val STATS_WIDGETS = "Widgets"
    override val STATS_NO_DATA = "Nenhum dado para exibir gráficos."
    override val STATS_TREND_CHARTS = "Gráficos de Tendência"
    override val STATS_FILTERS = "Filtros"
    override val STATS_ALL_TYPES = "Todos os tipos"
    override val STATS_FROM = "De"
    override val STATS_TO = "Para"
    override val STATS_NO_WIDGETS = "Nenhum widget ativo. Ative-os nas opções."
    override val STATS_SETTINGS_TITLE = "Configurações de Estatísticas Gerais"
    override val STATS_SECTION_WIDGETS = "Seção: Widgets"
    override val STATS_SECTION_CHARTS = "Seção: Gráficos"
    override val STATS_MOVE_UP = "Mover para cima"
    override val STATS_MOVE_DOWN = "Mover para baixo"
    override fun chartDistanceGps(km: Boolean) = if (km) "Distância (GPS) em km" else "Distância (GPS) em m"
    override fun chartDistanceSteps(km: Boolean) = if (km) "Distância (passos) em km" else "Distância (passos) em m"
    override val CHART_STEPS = "Passos"

    // Definitions
    override val DEF_TITLE = "Definições de Treino"
    override val DEF_ADD = "Adicionar Definição"
    override val DEF_EDIT = "Editar Definição"
    override val DEF_DELETE = "Excluir Definição"
    override val DEF_NAME = "Nome"
    override val DEF_ICON = "Ícone"
    override val DEF_SENSORS = "Sensores"
    override val DEF_LIST_TITLE = "Definição de Atividade"
    override val DEF_SENSORS_DESC = "Gerenciar lista de esportes e sensores"
    override val DEF_RECORDING = "Gravação"
    override val DEF_SELECT_ICON = "Selecionar Ícone"
    override val DEF_SAVE = "Salvar"
    override val DEF_MOVE_UP = "Mover para cima"
    override val DEF_MOVE_DOWN = "Mover para baixo"
    override val DEF_DELETE_TITLE = "Excluir atividade"
    override fun defDeleteConfirm(name: String) = "Tem certeza de que deseja excluir a atividade '$name'?"
    override val DEF_NEW_ACTIVITY = "Nova atividade"
    override val DEF_EDIT_ACTIVITY = "Editar atividade"
    override val DEF_NAME_LABEL = "Nome da atividade"
    override val DEF_AUTO_LAP_LABEL = "Volta automática (metros, opcional)"
    override val DEF_WIDGET_IN_ACTIVITY = "Widget na atividade"
    override val DEF_VISIBILITY = "Visibilidade"
    override val DEF_RECORD = "Gravar"
    override val DEF_BASE_TYPE = "Tipo base"
    override val DEF_FINISH = "Finalizar"
    override val DEF_SELECT_ICON_TITLE = "Selecionar ícone"
    override val DEF_WALKING = "Caminhada"
    override val DEF_RUNNING = "Corrida"
    override val DEF_CYCLING = "Ciclismo"
    override val DEF_HIKING = "Caminhada/Trilha"
    override val DEF_SWIMMING = "Natação"
    override val DEF_GYM = "Academia"
    override val DEF_YOGA = "Ioga"
    override val DEF_TENNIS = "Tênis"
    override val DEF_KAYAKING = "Canoagem"
    override val DEF_SNOWBOARDING = "Snowboard"
    override val DEF_SURFING = "Surfe"
    override val DEF_SKATING = "Patinação no gelo"
    override val DEF_GOLF = "Golfe"
    override val DEF_FOOTBALL = "Futebol"
    override val DEF_BASKETBALL = "Basquete"
    override val DEF_VOLLEYBALL = "Vôlei"
    override val DEF_BASEBALL = "Beisebol"
    override val DEF_SAILING = "Vela"
    override val DEF_SKATEBOARDING = "Skate"
    override val DEF_COMPETITION = "Competição"
    override val DEF_STOPWATCH = "Cronômetro"
    override val DEF_OTHER = "Outro"
    override val DEF_STANDARD_ACTIVITY = "Atividade padrão"

    // Activity Detail Settings
    override val AD_SETTINGS_LIST_TITLE = "Selecionar atividade para modificar"
    override val AD_SETTINGS_EDIT_TITLE = "Configurações"
    override val AD_SETTINGS_SECTION_WIDGETS = "Seção: Widgets"
    override val AD_SETTINGS_SECTION_CHARTS = "Seção: Gráficos"

    // Heart Rate Math
    override val HR_NO_DATA = "Sem dados de FC"
    override val HR_TOO_LITTLE_DATA = "Dados insuficientes"
    override val HR_BELOW_ZONES = "FC abaixo das zonas"
    override val HR_EFFECT_Z0 = "Intensidade baixa / Aquecimento"
    override val HR_EFFECT_Z1 = "Base aeróbica e recuperação"
    override val HR_EFFECT_Z2 = "Queima de gordura eficiente"
    override val HR_EFFECT_Z3 = "Melhoria da capacidade aeróbica"
    override val HR_EFFECT_Z4 = "Aumento do limiar de lactato"
    override val HR_EFFECT_Z5 = "Treino anaeróbico e VO2 Máx"
    override val HR_EFFECT_NONE = "Nenhuma zona dominante"

    // HR Zones Names
    override val ZONE_Z0 = "Aquecimento"
    override val ZONE_Z1 = "Muito leve"
    override val ZONE_Z2 = "Leve"
    override val ZONE_Z3 = "Moderado"
    override val ZONE_Z4 = "Difícil"
    override val ZONE_Z5 = "Máximo"

    // ViewModels Messages
    override val VM_EXPORT_INITIALIZING = "Inicializando exportação..."
    override fun vmExportGenerating(name: String, current: Int, total: Int) = "Gerando: $name ($current/$total)"
    override val VM_EXPORT_NO_FILES = "Nenhum arquivo gerado."
    override val VM_EXPORT_ZIPPING = "Compactando para ZIP..."
    override fun vmExportError(msg: String) = "Erro durante a exportação: $msg"
    override val VM_IMPORT_OPEN_ERROR = "Não é possível abrir o arquivo"
    override val VM_IMPORT_DUPLICATE_WARNING = "Potencial duplicata detectada (mesma hora de início e duração)."
    override val VM_IMPORT_SUCCESS = "Trening importado com sucesso."
    override fun vmImportError(msg: String) = "Erro na importação: $msg"

    // Gpx Importer
    override val GPX_NO_POINTS = "O arquivo GPX não contém pontos de trilha."
    override val GPX_WARN_HR = "O arquivo contém dados de FC, mas a atividade selecionada não os suporta."
    override val GPX_WARN_ELE = "O arquivo contém dados de altitude, mas a atividade selecionada não os suporta."
    override val GPX_WARN_CADENCE = "O arquivo contém dados de cadência, mas a atividade selecionada não os suporta."

    // Periods
    override val PERIOD_TODAY = "Hoje"
    override val PERIOD_WEEK = "Semana"
    override val PERIOD_MONTH = "Mês"
    override val PERIOD_YEAR = "Ano"
    override val PERIOD_CUSTOM = "Personalizado"
    override fun periodCustomDays(days: Int) = if (days == 1) "$days dia" else "$days dias"

    // Widgets
    override val WIDGET_COUNT = "Contagem de atividades"
    override val WIDGET_CALORIES = "Calorias queimadas"
    override val WIDGET_DISTANCE_GPS = "Distância (GPS)"
    override val WIDGET_DISTANCE_STEPS = "Distância (passos)"
    override val WIDGET_ASCENT = "Subida total"
    override val WIDGET_DESCENT = "Descida total"
    override val WIDGET_STEPS = "Passos"
    override val WIDGET_AVG_CADENCE = "Cadência média"
    override val WIDGET_MAX_SPEED = "Velocidade máxima"
    override val WIDGET_MAX_ALTITUDE = "Altitude máxima"
    override val WIDGET_MAX_ELEVATION_GAIN = "Maior ganho de elevação"
    override val WIDGET_MAX_DISTANCE = "Maior distância"
    override val WIDGET_MAX_DURATION = "Maior duração"
    override val WIDGET_MAX_CALORIES = "Máximo de calorias"
    override val WIDGET_MAX_AVG_CADENCE = "Maior cadência média"
    override val WIDGET_MAX_AVG_SPEED = "Maior velocidade média"
    override val WIDGET_DURATION = "Duração"
    override val WIDGET_MAX_BPM = "FC Máx"
    override val WIDGET_AVG_BPM = "FC Méd."
    override val WIDGET_TOTAL_CALORIES = "Total de calorias"
    override val WIDGET_MAX_CALORIES_MIN = "Taxa máx. de queima calórica"
    override val WIDGET_AVG_PACE = "Ritmo médio"
    override val WIDGET_AVG_SPEED_GPS = "Velocidade média (GPS)"
    override val WIDGET_AVG_SPEED_STEPS = "Velocidade média (passos)"
    override val WIDGET_MAX_ALTITUDE_DESC = "Altitude máxima"
    override val WIDGET_TOTAL_ASCENT = "Total de subida"
    override val WIDGET_TOTAL_DESCENT = "Total de descida"
    override val WIDGET_AVG_STEP_LENGTH = "Comprimento médio do passo"
    override val WIDGET_AVG_CADENCE_DESC = "Cadência méd."
    override val WIDGET_MAX_CADENCE = "Cadência máx."
    override val WIDGET_TOTAL_STEPS = "Contagem de passos"
    override val WIDGET_PRESSURE_START = "Pressão atm. (início)"
    override val WIDGET_PRESSURE_END = "Pressão atm. (fim)"
    override val WIDGET_MAX_PRESSURE = "Pressão atm. máx."
    override val WIDGET_MIN_PRESSURE = "Pressão atm. mín."
    override val WIDGET_BEST_PACE_1KM = "Melhor ritmo 1km"
    override val WIDGET_WATCH_ASCENT = "Total de subida"
    override val WIDGET_WATCH_DESCENT = "Total de descida"

    // Sensors
    override val SENSOR_HEART_RATE = "Frequência Cardíaca"
    override val SENSOR_CALORIES_SUM = "Total de Calorias"
    override val SENSOR_CALORIES_MIN = "Calorias por Minuto"
    override val SENSOR_STEPS = "Passos"
    override val SENSOR_STEPS_MIN = "Cadência (spm)"
    override val SENSOR_DISTANCE_STEPS = "Distância (Passos)"
    override val SENSOR_SPEED_GPS = "Velocidade"
    override val SENSOR_SPEED_STEPS = "Velocidade (Passos)"
    override val SENSOR_DISTANCE_GPS = "Distância"
    override val SENSOR_ALTITUDE = "Altitude"
    override val SENSOR_TOTAL_ASCENT = "Subida total"
    override val SENSOR_TOTAL_DESCENT = "Descida total"
    override val SENSOR_PRESSURE = "Pressão atm."
    override val SENSOR_MAP = "Dados de Localização"

    // Trim Screen
    override val TRIM_TITLE = "Editar treino (Recortar)"
    override val TRIM_CONFIRM_TITLE = "Confirmar recorte"
    override val TRIM_CONFIRM_DESC = "Tem certeza de que deseja remover os dados fora da faixa selecionada? Esses dados serão excluídos permanentemente."
    override val TRIM_SAVE_BTN = "Recortar e salvar"
    override val TRIM_CHART_HR = "Gráfico de FC"
    override val TRIM_RANGE_TITLE = "Selecionar faixa de treino"
    override val TRIM_PREVIEW_TITLE = "Visualização de novas estatísticas"
    override val TRIM_NEW_DURATION = "Nova duração:"
    override val TRIM_DISTANCE_GPS = "Distância (GPS):"
    override val TRIM_DISTANCE_STEPS = "Distância (Passos):"
    override val TRIM_CALORIES = "Calorias queimadas:"
    override val TRIM_AVG_BPM = "FC Média:"
    override val TRIM_START = "Início"
    override val TRIM_END = "Fim"

    // Compare Screen
    override val COMPARE_TITLE = "Comparação de Atividades"
    override val COMPARE_VS = "Comparação:"
    override val COMPARE_HIGHER_IS_BETTER = "Resultado maior é melhor"
    override val COMPARE_LOWER_IS_BETTER = "Resultado menor é melhor"

    // Units
    override val UNIT_KCAL = "kcal"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_STEP_MIN = "spm"
    override val UNIT_KM_H = "km/h"
    override val UNIT_STEPS = "passos"
    override val UNIT_HPA = "hPa"
    override val UNIT_MIN_KM = "min/km"
    override val UNIT_M_ASL = "m"
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
