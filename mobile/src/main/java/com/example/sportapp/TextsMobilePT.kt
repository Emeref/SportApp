package com.example.sportapp

import java.util.Calendar
import java.util.Locale

object TextsMobilePT : MobileTexts {
    // Navigation
    override val NAV_HOME = "Início"
    override val NAV_STATS = "Estatísticas"
    override val NAV_ACTIVITIES = "Atividades"
    override val NAV_SETTINGS = "Definições"

    // Home Screen
    override val HOME_TITLE = "SportApp"
    override val HOME_NO_WIDGETS = "Nenhum widget selecionado"
    override val HOME_ADD_WIDGETS = "Adicionar widgets"
    override val HOME_LAST_ACTIVITY = "Última atividade"
    override val HOME_ACTIVITY_COUNT = "Número de atividades"
    override val HOME_SYNC = "Sincronizar"
    override val HOME_OPTIONS = "Opções"
    override val HOME_GENERAL_STATS = "Estatísticas gerais"
    override val HOME_WORKOUT_DETAILS = "Detalhes do treino"
    override val HOME_LOGO_DESC = "Logótipo da aplicação"
    override val HOME_SECRET_TITLE = "É bom ver-te a clicar, mas não há nada aqui"
    override val HOME_CLOSE = "Fechar"
    override val HOME_START_LIVE = "Iniciar Live Tracking"
    override val HOME_ACTIVE_WORKOUT = "Atividade em curso"
    override val HOME_RESUME_TRACKING = "Seguir atividade"

    override fun homeResultsToday() = "Resultados de hoje:"
    override fun homeResultsWeek() = "Resultados da semana:"
    override fun homeResultsMonth(): String {
        val monthName = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "PT"))
        return "Resultados de $monthName:"
    }
    override fun homeResultsYear() = "Resultados deste ano:"
    override fun homeResultsCustom(days: Int) = if (days == 1) "Resultados do último dia:" else "Resultados dos últimos $days dias:"

    // Settings Screen
    override val SETTINGS_TITLE = "Definições"
    override val SETTINGS_GENERAL = "Geral"
    override val SETTINGS_THEME = "Tema da aplicação"
    override val SETTINGS_THEME_SYSTEM = "Sistema"
    override val SETTINGS_THEME_LIGHT = "Claro"
    override val SETTINGS_THEME_DARK = "Escuro"
    override val SETTINGS_LANGUAGE = "Idioma"
    override val SETTINGS_LANGUAGE_TITLE = "Escolher idioma"
    override val SETTINGS_HEALTH_DATA = "Dados de saúde e FC"
    override val SETTINGS_HEALTH_DATA_DESC = "Idade, peso, FC Máx e zonas"
    override val SETTINGS_DEFINITIONS = "Definições de atividade"
    override val SETTINGS_DEFINITIONS_DESC = "Gerir lista de desportos e sensores"
    override val SETTINGS_WIDGETS_HOME = "Widgets do ecrã inicial"
    override val SETTINGS_WIDGETS_HOME_TITLE = "Vista inicial"
    override val SETTINGS_WIDGETS_HOME_DESC = "Escolher e definir a ordem"
    override val SETTINGS_WIDGETS_WATCH = "Estatísticas no relógio"
    override val SETTINGS_WIDGETS_WATCH_TITLE = "Campos de estatística"
    override val SETTINGS_WIDGETS_WATCH_DESC = "Escolher e ordenar no relógio"
    override val SETTINGS_SAVE = "Guardar"
    override val SETTINGS_CANCEL = "Cancelar"
    override val SETTINGS_CLOSE = "Fechar"
    override val SETTINGS_PERIOD = "Período predefinido"
    override val SETTINGS_PERIOD_HOME_DESC = "Para que período mostrar widgets?"
    override val SETTINGS_PERIOD_WATCH_DESC = "Estatísticas de que período?"
    override val SETTINGS_CUSTOM_DAYS_LABEL = "Número de dias"
    override val SETTINGS_WATCH_STATS_DAYS_LABEL = "Mostrar estatísticas de quantos dias?"
    override val SETTINGS_CUSTOM_DAYS_DESC = "Número de dias para o período 'Outros'"
    override val SETTINGS_WATCH_STATS_DAYS_DESC = "Número de dias de estatísticas no relógio"
    override val SETTINGS_INTEGRATION = "Integração"
    override val SETTINGS_SYNC = "Sincronização"
    override val SETTINGS_STRAVA = "Strava"
    override val SETTINGS_STRAVA_DESC = "Sincronize os seus treinos com o Strava"
    override val SETTINGS_GOOGLE_DRIVE = "Google Drive"
    override val SETTINGS_GOOGLE_DRIVE_DESC = "Guardar histórico e resumos (Brevemente)"
    override val SETTINGS_APPEARANCE = "Aparência"
    override val SETTINGS_MY_PROFILE = "O meu perfil"
    override val LANG_PL = "Polaco"
    override val LANG_EN = "Inglês"

    // Health Connect Strings
    override val SETTINGS_HC_TITLE = "Health Connect"
    override val SETTINGS_HC_MANAGE_PERMISSIONS = "Gerir permissões do Health Connect"
    override val SETTINGS_HC_STATUS = "Estado do Health Connect"
    override val HC_STATUS_AVAILABLE = "Disponível"
    override val HC_STATUS_UNAVAILABLE = "Indisponível"
    override val HC_STATUS_NOT_INSTALLED = "Não instalado"
    override val HC_INSTALL = "Instalar"
    override val HC_SYNC_HEALTH_DATA = "Sincronizar com o Health Connect"
    override val HC_SYNC_WORKOUTS = "Importar treinos do Health Connect"
    override val HC_SYNC_CONFIRM_TITLE = "Sincronização de dados"
    override val HC_SYNC_CONFIRM_DESC = "Pretende atualizar o seu perfil com os dados encontrados no Health Connect?"
    override val HC_SYNC_SUCCESS = "Sincronização bem-sucedida"
    override val HC_SYNC_ERROR = "Erro de sincronização"
    override val HC_SYNC_NO_DATA = "Não foram encontrados novos dados"
    override val HC_IMPORT_SELECT_FIELDS_TITLE = "Escolher dados para importar"
    override fun hcSyncPreview(weight: String?, height: String?, vo2max: String?): String {
        val parts = mutableListOf<String>()
        weight?.let { parts.add("peso $it kg") }
        height?.let { parts.add("altura $it cm") }
        vo2max?.let { parts.add("VO2Max $it ml/kg/min") }
        return "Encontrado no Health Connect: ${parts.joinToString(", ")} – atualizar?"
    }

    // Stage 3 - Import Workouts
    override val HC_IMPORT_TITLE = "Importar treinos"
    override val HC_IMPORT_ALREADY_IMPORTED = "Já importado"
    override val HC_IMPORT_EMPTY = "Não foram encontrados treinos no Health Connect na última semana."
    override val HC_IMPORT_CONFIRM_DESC_PLURAL = "Tem a certeza de que pretende importar os treinos selecionados?"
    override val HC_IMPORT_SELECT_ALL = "Selecionar todos"
    override fun hcImportSelected(count: Int) = "Importar selecionados ($count)"
    override fun hcImportConfirmDesc(count: Int) = "Pretende importar $count treinos?"
    override fun hcImportProgress(current: Int, total: Int) = "A importar $current/$total treinos..."

    // Stage 5 - Export
    override val HC_EXPORT_TO = "Exportar para o Health Connect"
    override val HC_EXPORTED_ON = "Sincronizado com o Health Connect"
    override val HC_EXPORT_SUCCESS = "Exportação concluída com sucesso"
    override val HC_EXPORT_ERROR = "Erro de exportação: "
    override val HC_EXPORT_PERMISSION_DENIED = "Sem permissão de escrita no Health Connect"
    override val SETTINGS_HC_AUTO_EXPORT = "Exportação automática"
    override val SETTINGS_HC_AUTO_EXPORT_DESC = "Exportar automaticamente novos treinos para o Health Connect"

    // Stage 6 - Sync Status
    override val SYNC_STATUS_TITLE = "Estado de sincronização HC"
    override val SYNC_LAST_HEALTH = "Última sincr. de saúde"
    override val SYNC_LAST_WORKOUT = "Última sincr. de treinos"
    override val SYNC_UNSYNCED_COUNT = "Registos não sincronizados"
    override val SYNC_NOW = "Sincronizar agora"
    override val SYNC_HISTORY_TITLE = "Histórico de sincronização"
    override val SYNC_TYPE_IMPORT = "Importação"
    override val SYNC_TYPE_EXPORT = "Exportação"
    override val SYNC_NEVER = "Nunca"
    override val SYNC_CONFLICT_POLICY = "Política de conflitos"
    override val SYNC_CONFLICT_NEWER = "O mais recente vence"
    override val SYNC_CONFLICT_LOCAL = "Os locais vencem"
    override val SYNC_CONFLICT_HC = "O Health Connect vence"

    // Health Connect Permissions Dialog
    override val HC_PERMISSIONS_DIALOG_TITLE = "Permissões necessárias"
    override val HC_PERMISSIONS_DIALOG_DESC = "As permissões de escrita são necessárias para exportar treinos para o Health Connect. Pode concedê-las nas definições do sistema."
    override val HC_OPEN_SETTINGS = "Abrir definições"

    // Health Data Screen
    override val HEALTH_TITLE = "Dados de saúde"
    override val HEALTH_GENDER = "Género"
    override val HEALTH_GENDER_MALE = "Homem"
    override val HEALTH_GENDER_FEMALE = "Mulher"
    override val HEALTH_AGE = "Idade"
    override val HEALTH_WEIGHT = "Peso"
    override val HEALTH_WEIGHT_KG = "Peso (kg)"
    override val HEALTH_HEIGHT = "Altura"
    override val HEALTH_HEIGHT_CM = "Altura (cm)"
    override val HEALTH_RESTING_HR = "Frequência cardíaca em repouso"
    override val HEALTH_MAX_HR = "Frequência cardíaca máxima"
    override val HEALTH_MAX_HR_DESC = "Frequência cardíaca máxima"
    override val HEALTH_STEP_LENGTH = "Comprimento do passo"
    override val HEALTH_STEP_LENGTH_CM = "Comprimento do passo (cm)"
    override val HEALTH_VO2_MAX = "VO2 Max"

    // Activity List
    override val ACTIVITY_LIST_TITLE = "Lista de atividades"
    override val ACTIVITY_EMPTY = "Nenhuma atividade"
    override val ACTIVITY_DELETE_CONFIRM = "Tem a certeza de que pretende eliminar permanentemente as atividades selecionadas da base de dados?"
    override val ACTIVITY_COMPARE = "Comparar"
    override val ACTIVITY_TRIM = "Recortar"
    override val ACTIVITY_DETAIL = "Detalhes"
    override val ACTIVITY_EDIT = "Editar"
    override val ACTIVITY_IMPORT_GPX = "Importar GPX"
    override val ACTIVITY_EXPORT_GPX = "Exportar GPX"
    override val ACTIVITY_CHART_SETTINGS = "Definições de gráficos"
    override val ACTIVITY_FILTERS = "Filtros"
    override val ACTIVITY_ALL_TYPES = "Todos os tipos"
    override val ACTIVITY_FROM = "De"
    override val ACTIVITY_TO = "Até"
    override val ACTIVITY_TYPE = "Tipo"
    override val ACTIVITY_DATE = "Data"
    override val ACTIVITY_DURATION = "Duração"
    override val ACTIVITY_CALORIES = "Calorias"
    override val ACTIVITY_DISTANCE_GPS = "Distância (GPS)"
    override val ACTIVITY_DISTANCE_STEPS = "Distância (passos)"
    override val ACTIVITY_DELETE = "Eliminar"
    override val ACTIVITY_IMPORT_SELECT_TYPE = "Escolher tipo de atividade"
    override val ACTIVITY_IMPORT_SELECT_DESC = "Escolha o tipo de treino para o ficheiro GPX importado:"
    override val ACTIVITY_IMPORT_WARNING = "Aviso"
    override val ACTIVITY_IMPORT_CONTINUE = "Continuar"
    override val ACTIVITY_IMPORT_PROGRESS = "A importar dados..."
    override val ACTIVITY_EXPORT_ERROR = "Erro de exportação"
    override val ACTIVITY_EXPORT_DIALOG_TITLE = "Exportar atividade"
    override val STR_STRAVA = "Strava"
    override val STR_HEALTH_CONNECT = "Health Connect"
    override val ACTIVITY_ALREADY_SYNCED = "Sincronizado"
    override val ACTIVITY_EXPORT = "Exportar"
    override val ACTIVITY_SHARE_TITLE = "Partilhar treino(s)"
    override val ACTIVITY_OK = "OK"
    override val ACTIVITY_CONFIRM_DELETE_TITLE = "Eliminar atividades"
    override val ACTIVITY_ALL = "Todas"
    override val ACTIVITY_NONE = "Nenhuma"

    // New Export/Import SAE
    override val ACTIVITY_EXPORT_SAE = "Exportar SAE"
    override val ACTIVITY_IMPORT_SAE = "Importar SAE"
    override val ACTIVITY_EXPORT_FORMAT_SELECT = "Escolher formato de exportação"
    override val ACTIVITY_EXPORT_SAE_DESC = "O formato SAE (.sae) permite uma cópia de segurança completa de todos os dados de treino, incluindo treinos indoor (sem GPS)."
    override val ACTIVITY_EXPORT_INCOMPATIBLE_GPX = "Algumas atividades selecionadas não possuem dados de GPS e não podem ser exportadas para o formato GPX."

    // Activity Detail
    override val DETAIL_TITLE = "Detalhes da atividade"
    override val DETAIL_MAP = "Mapa"
    override val DETAIL_CHARTS = "Gráficos"
    override val DETAIL_LAPS = "Voltas"
    override val DETAIL_STATISTICS = "Estatísticas"
    override val DETAIL_DATA_ERROR_TITLE = "Erro de dados"
    override val DETAIL_ERROR_OK = "OK"
    override val DETAIL_INTERVALS = "Intervalos"
    override fun detailLapsWithDistance(distance: String) = "Intervalos ($distance)"
    override fun detailLapsCount(count: Int) = "Número de voltas: $count"
    override val DETAIL_HEART_RATE = "Frequência cardíaca (bpm)"
    override val DETAIL_HR_ZONES = "Zonas de FC"
    override val DETAIL_TRAINING_EFFECT = "Efeito do treino"
    override val DETAIL_TRAINING_EFFECT_DESC = "Efeito de treino dominante"
    override val DETAIL_LAP_NR = "N.º"
    override val DETAIL_LAP_TIME = "Tempo"
    override val DETAIL_LAP_AVG_PACE = "Ritmo médio"
    override val DETAIL_LAP_AVG_SPEED = "Velocidade média"
    override val DETAIL_LAP_MAX_SPEED = "Velocidade máx."
    override val DETAIL_LAP_AVG_HR = "FC média"
    override val DETAIL_LAP_MAX_HR = "FC máx."
    override val DETAIL_LAP_ASCENT_DESCENT = "Subida/Descida"
    override val DETAIL_MAP_START = "Início"
    override val DETAIL_MAP_FINISH = "Fim"
    override val DETAIL_MAP_EXPAND = "Ampliar mapa"
    override val DETAIL_MAP_COLLAPSE = "Reduzir mapa"
    override val DETAIL_EXPAND = "Expandir"
    override val DETAIL_COLLAPSE = "Recolher"
    override val DETAIL_PREDOMINANT_EFFECT = "Efeito de treino dominante"

    // Stats
    override val STATS_TITLE = "Estatísticas gerais"
    override val STATS_CHARTS = "Gráficos"
    override val STATS_WIDGETS = "Widgets"
    override val STATS_NO_DATA = "Sem dados para apresentar gráficos."
    override val STATS_TREND_CHARTS = "Gráficos de tendências"
    override val STATS_FILTERS = "Filtros"
    override val STATS_ALL_TYPES = "Todos os tipos"
    override val STATS_FROM = "De"
    override val STATS_TO = "Até"
    override val STATS_NO_WIDGETS = "Sem widgets ativos. Ative-os nas opções."
    override val STATS_SETTINGS_TITLE = "Definições de estatísticas gerais"
    override val STATS_SECTION_WIDGETS = "Secção: Widgets"
    override val STATS_SECTION_CHARTS = "Secção: Gráficos de tendências"
    override val STATS_MOVE_UP = "Mover para cima"
    override val STATS_MOVE_DOWN = "Mover para baixo"
    override fun chartDistanceGps(km: Boolean) = if (km) "Distância (GPS) em km" else "Distância (GPS) em m"
    override fun chartDistanceSteps(km: Boolean) = if (km) "Distância (passos) em km" else "Distância (passos) em m"
    override val CHART_STEPS = "Passos"

    // Definitions
    override val DEF_TITLE = "Definições de treino"
    override val DEF_ADD = "Adicionar definição"
    override val DEF_EDIT = "Editar definição"
    override val DEF_DELETE = "Eliminar definição"
    override val DEF_NAME = "Nome"
    override val DEF_ICON = "Ícone"
    override val DEF_SENSORS = "Sensores"
    override val DEF_LIST_TITLE = "Definição de atividade"
    override val DEF_SENSORS_DESC = "Gerir lista de desportos e sensores"
    override val DEF_RECORDING = "Gravação"
    override val DEF_SELECT_ICON = "Escolher ícone"
    override val DEF_SAVE = "Guardar"
    override val DEF_MOVE_UP = "Mover para cima"
    override val DEF_MOVE_DOWN = "Mover para baixo"
    override val DEF_DELETE_TITLE = "Eliminar atividade"
    override fun defDeleteConfirm(name: String) = "Tem a certeza de que pretende eliminar a atividade '$name'?"
    override val DEF_NEW_ACTIVITY = "Nova atividade"
    override val DEF_EDIT_ACTIVITY = "Editar atividade"
    override val DEF_NAME_LABEL = "Nome da atividade"
    override val DEF_NAME_HINT = "Ex: Passadeira"
    override val DEF_AUTO_LAP_LABEL = "Volta automática (metros, opcional)"
    override val DEF_WIDGET_IN_ACTIVITY = "Widget na atividade"
    override val DEF_VISIBILITY = "Visibilidade"
    override val DEF_RECORD = "Gravar"
    override val DEF_BASE_TYPE = "Tipo base"
    override val DEF_FINISH = "Concluir"
    override val DEF_SELECT_ICON_TITLE = "Escolher ícone"

    // Base Types
    override val DEF_WALKING = "Caminhada"
    override val DEF_SPEED_WALKING = "Marcha rápida"
    override val DEF_RUNNING = "Corrida"
    override val DEF_TREADMILL_RUNNING = "Corrida em passadeira"
    override val DEF_STAIR_CLIMBING = "Subida de escadas"
    override val DEF_STAIR_CLIMBING_MACHINE = "Stepper"
    override val DEF_CYCLING = "Ciclismo"
    override val DEF_CYCLING_STATIONARY = "Bicicleta estática"
    override val DEF_MOUNTAIN_BIKING = "BTT (Bicicleta de montanha)"
    override val DEF_ROAD_BIKING = "Ciclismo de estrada"
    override val DEF_HIKING = "Caminhada / Trekking"
    override val DEF_ROCK_CLIMBING = "Escalada"
    override val DEF_BOULDERING = "Bouldering"
    override val DEF_HIIT = "HIIT"
    override val DEF_ELLIPTICAL = "Elíptica"
    override val DEF_ROWING_MACHINE = "Remo"
    override val DEF_STRENGTH_TRAINING = "Treino de força"
    override val DEF_CALISTHENICS = "Calistenia"
    override val DEF_YOGA = "Ioga"
    override val DEF_PILATES = "Pilates"
    override val DEF_AEROBICS = "Aeróbica"
    override val DEF_DANCING = "Dança"
    override val DEF_SWIMMING = "Natação"
    override val DEF_SWIMMING_POOL = "Natação (piscina)"
    override val DEF_SWIMMING_OPEN_WATER = "Natação (águas abertas)"
    override val DEF_KAYAKING = "Canoagem"
    override val DEF_PADDLE_BOARDING = "SUP (Stand Up Paddle)"
    override val DEF_SURFING = "Surf"
    override val DEF_SAILING = "Vela"
    override val DEF_FOOTBALL = "Futebol"
    override val DEF_BASKETBALL = "Basquetebol"
    override val DEF_TENNIS = "Ténis"
    override val DEF_SQUASH = "Squash"
    override val DEF_VOLLEYBALL = "Voleibol"
    override val DEF_GOLF = "Golfe"
    override val DEF_MARTIAL_ARTS = "Artes marciais"
    override val DEF_SKIING = "Esqui"
    override val DEF_SNOWBOARDING = "Snowboard"
    override val DEF_SKATING = "Patinagem"
    override val DEF_ICE_SKATING = "Patinagem artística"

    override val DEF_GYM = "Ginásio"
    override val DEF_BASEBALL = "Beisebol"
    override val DEF_SKATEBOARDING = "Skate"
    override val DEF_COMPETITION = "Competição"
    override val DEF_STOPWATCH = "Cronómetro"
    override val DEF_OTHER = "Outro"
    override val DEF_STANDARD_ACTIVITY = "Atividade padrão"

    // Activity Detail Settings
    override val AD_SETTINGS_LIST_TITLE = "Escolher atividade para modificar"
    override val AD_SETTINGS_EDIT_TITLE = "Definições"
    override val AD_SETTINGS_SECTION_WIDGETS = "Secção: Widgets"
    override val AD_SETTINGS_SECTION_CHARTS = "Secção: Gráficos"

    // Heart Rate Math
    override val HR_NO_DATA = "Sem dados de frequência cardíaca"
    override val HR_TOO_LITTLE_DATA = "Dados insuficientes"
    override val HR_BELOW_ZONES = "FC abaixo das zonas"
    override val HR_EFFECT_Z0 = "Baixa intensidade / Aquecimento"
    override val HR_EFFECT_Z1 = "Base aeróbica e recuperação"
    override val HR_EFFECT_Z2 = "Queima de gordura eficaz"
    override val HR_EFFECT_Z3 = "Melhoria da capacidade aeróbica"
    override val HR_EFFECT_Z4 = "Aumento do limiar de lactato"
    override val HR_EFFECT_Z5 = "Treino anaeróbico e VO2 Máx"
    override val HR_EFFECT_NONE = "Nenhuma zona dominante"

    // HR Zones Names
    override val ZONE_Z0 = "Aquecimento"
    override val ZONE_Z1 = "Muito leve"
    override val ZONE_Z2 = "Leve"
    override val ZONE_Z3 = "Moderado"
    override val ZONE_Z4 = "Intenso"
    override val ZONE_Z5 = "Máximo"

    // ViewModels Messages
    override val VM_EXPORT_INITIALIZING = "A inicializar exportação..."
    override fun vmExportGenerating(name: String, current: Int, total: Int) = "A gerar: $name ($current/$total)"
    override val VM_EXPORT_NO_FILES = "Nenhum ficheiro gerado."
    override val VM_EXPORT_ZIPPING = "A comprimir para ZIP..."
    override fun vmExportError(msg: String) = "Erro durante a exportação: $msg"
    override val VM_IMPORT_OPEN_ERROR = "Não é possível abrir o ficheiro"
    override val VM_IMPORT_DUPLICATE_WARNING = "Detetado potencial duplicado (mesma data de início e duração)."
    override val VM_IMPORT_DUPLICATE_WARNING_DESC = "Deseja continuar mesmo assim?"
    override val VM_IMPORT_SUCCESS = "Treino importado com sucesso."
    override fun vmImportError(msg: String) = "Erro de importação: $msg"

    // Gpx Importer
    override val GPX_NO_POINTS = "O ficheiro GPX não contém pontos de rota."
    override val GPX_WARN_HR = "O ficheiro contém dados de frequência cardíaca, mas a atividade selecionada não os suporta."
    override val GPX_WARN_ELE = "O ficheiro contém dados de altitude, mas a atividade selecionada não os suporta."
    override val GPX_WARN_CADENCE = "O ficheiro contém dados de cadência, mas a atividade selecionada não os suporta."

    // Periods
    override val PERIOD_TODAY = "Hoje"
    override val PERIOD_WEEK = "Semana"
    override val PERIOD_MONTH = "Mês"
    override val PERIOD_YEAR = "Ano"
    override val PERIOD_CUSTOM = "Outro"
    override fun periodCustomDays(days: Int) = if (days == 1) "$days dia" else "$days dias"

    // Widgets
    override val WIDGET_COUNT = "Número de atividades"
    override val WIDGET_CALORIES = "Calorias queimadas"
    override val WIDGET_DISTANCE_GPS = "Distância (GPS)"
    override val WIDGET_DISTANCE_STEPS = "Distância (passos)"
    override val WIDGET_ASCENT = "Subida total"
    override val WIDGET_DESCENT = "Descida total"
    override val WIDGET_STEPS = "Passos"
    override val WIDGET_AVG_BPM = "FC média"
    override val WIDGET_AVG_CADENCE = "Cadência média"
    override val WIDGET_MAX_SPEED = "Velocidade máx."
    override val WIDGET_MAX_ALTITUDE = "Altitude máx."
    override val WIDGET_MAX_ELEVATION_GAIN = "Maior ganho de elevação"
    override val WIDGET_MAX_DISTANCE = "Maior distância"
    override val WIDGET_MAX_DURATION = "Maior duração"
    override val WIDGET_MAX_CALORIES = "Mais calorias"
    override val WIDGET_MAX_AVG_CADENCE = "Cadência média mais alta"
    override val WIDGET_MAX_AVG_SPEED = "Velocidade média mais alta"
    override val WIDGET_DURATION = "Duração"
    override val WIDGET_MAX_BPM = "FC máxima"
    override val WIDGET_TOTAL_CALORIES = "Calorias queimadas"
    override val WIDGET_MAX_CALORIES_MIN = "Queima calórica máx."
    override val WIDGET_AVG_PACE = "Ritmo médio"
    override val WIDGET_AVG_SPEED_GPS = "Velocidade média (GPS)"
    override val WIDGET_AVG_SPEED_STEPS = "Velocidade média (passos)"
    override val WIDGET_MAX_ALTITUDE_DESC = "Altitude máx."
    override val WIDGET_TOTAL_ASCENT = "Total de subidas"
    override val WIDGET_TOTAL_DESCENT = "Total de descidas"
    override val WIDGET_AVG_STEP_LENGTH = "Comprimento de passo calculado"
    override val WIDGET_AVG_CADENCE_DESC = "Cadência média"
    override val WIDGET_MAX_CADENCE = "Cadência máx."
    override val WIDGET_TOTAL_STEPS = "Número de passos"
    override val WIDGET_PRESSURE_START = "Pressão atm. (início)"
    override val WIDGET_PRESSURE_END = "Pressão atm. (fim)"
    override val WIDGET_MAX_PRESSURE = "Pressão máx."
    override val WIDGET_MIN_PRESSURE = "Pressão mín."
    override val WIDGET_BEST_PACE_1KM = "Melhor ritmo (1km)"
    override val WIDGET_WATCH_ASCENT = "Ganho de elevação"
    override val WIDGET_WATCH_DESCENT = "Perda de elevação"

    // Sensors
    override val SENSOR_HEART_RATE = "Frequência cardíaca"
    override val SENSOR_CALORIES_SUM = "Calorias queimadas"
    override val SENSOR_CALORIES_MIN = "Calorias por minuto"
    override val SENSOR_STEPS = "Passos"
    override val SENSOR_STEPS_MIN = "Cadência (passos/min)"
    override val SENSOR_DISTANCE_STEPS = "Distância (passos)"
    override val SENSOR_SPEED_GPS = "Velocidade"
    override val SENSOR_SPEED_STEPS = "Velocidade (passos)"
    override val SENSOR_DISTANCE_GPS = "Distância"
    override val SENSOR_ALTITUDE = "Altitude"
    override val SENSOR_TOTAL_ASCENT = "Total de subidas"
    override val SENSOR_TOTAL_DESCENT = "Total de descidas"
    override val SENSOR_PRESSURE = "Pressão atm."
    override val SENSOR_MAP = "Dados de localização"
    override val SENSOR_AVG_STEP_LENGTH = "Comprimento de passo médio"

    // Trim Screen
    override val TRIM_TITLE = "Editar treino (Recortar)"
    override val TRIM_CONFIRM_TITLE = "Confirmar recorte"
    override val TRIM_CONFIRM_DESC = "Tem a certeza de que pretende eliminar os dados fora do intervalo selecionado? Estes dados serão eliminados permanentemente."
    override val TRIM_SAVE_BTN = "Recortar e guardar"
    override val TRIM_CHART_HR = "Gráfico de FC"
    override val TRIM_RANGE_TITLE = "Escolher intervalo do treino"
    override val TRIM_PREVIEW_TITLE = "Pré-visualização das novas estatísticas"
    override val TRIM_NEW_DURATION = "Nova duração:"
    override val TRIM_DISTANCE_GPS = "Distância (GPS):"
    override val TRIM_DISTANCE_STEPS = "Distância (Passos):"
    override val TRIM_CALORIES = "Calorias queimadas:"
    override val TRIM_AVG_BPM = "FC média:"
    override val TRIM_START = "Início"
    override val TRIM_END = "Fim"

    // Compare Screen
    override val COMPARE_TITLE = "Comparação de atividades"
    override val COMPARE_VS = "Comparação:"
    override val COMPARE_HIGHER_IS_BETTER = "Um resultado mais alto é melhor"
    override val COMPARE_LOWER_IS_BETTER = "Um resultado mais baixo é melhor"

    // Units
    override val UNIT_KCAL = "kcal"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_STEP_MIN = "passos/min"
    override val UNIT_KM_H = "km/h"
    override val UNIT_STEPS = "passos"
    override val UNIT_HPA = "hPa"
    override val UNIT_MIN_KM = "min/km"
    override val UNIT_M_ASL = "m (alt.)"
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
    override val STRAVA_TITLE = "Sincronização com Strava"
    override val STRAVA_CONNECT = "Ligar à conta Strava"
    override val STRAVA_DISCONNECT = "Desligar conta Strava"
    override val STRAVA_CONNECTED = "Ligado ao Strava"
    override val STRAVA_NOT_CONNECTED = "Não ligado"
    override val STRAVA_SYNC_NOW = "Sincronizar agora"
    override val STRAVA_SYNC_SUCCESS = "O treino foi enviado!"
    override val STRAVA_SYNC_FAILED = "Erro de envio"
    override val STRAVA_SYNCING = "A enviar..."
    override val STRAVA_AUTH_ERROR = "Erro de autorização"
    override val SETTINGS_STRAVA_AUTO_EXPORT = "Exportação automática"
    override val SETTINGS_STRAVA_AUTO_EXPORT_DESC = "Enviar automaticamente novos treinos para o Strava"
    override val STRAVA_SYNC_LOG = "Histórico de sincronização"
    override val STRAVA_SYNC_LOG_EMPTY = "Nenhum histórico de sincronização"

    // Live Tracking
    override val LIVE_TRACKING_TITLE = "Live Tracking"
    override val LIVE_TRACKING_SELECT_ACTIVITY = "Escolher atividade"
    override val LIVE_TRACKING_LOCK = "Bloquear"
    override val LIVE_TRACKING_UNLOCK_SWIPE = "Deslize para cima para desbloquear"
    override val LIVE_TRACKING_MAP_NORTH = "Norte"
    override val LIVE_TRACKING_MAP_DIRECTION = "Direção"
    override val LIVE_TRACKING_WAITING_FOR_WATCH = "A aguardar sinal do relógio..."
    override val LIVE_TRACKING_FINISHED_TITLE = "Atividade concluída"
    override val LIVE_TRACKING_FINISHED_DESC = "Atividade concluída com sucesso"
    override val LIVE_TRACKING_BTN_FINISH = "Terminar"
    override val LIVE_TRACKING_BTN_VIEW_MAP = "Ver mapa"
    override val LIVE_TRACKING_PAUSED = "Pausa"

    // Errors
    override val ERROR_WEARABLE_NOT_AVAILABLE = "Serviços Wearable indisponíveis neste dispositivo"
    override val ERROR_NO_WATCH_CONNECTED = "Nenhum relógio ligado encontrado"

    // Map Types
    override val MAP_TYPE_TITLE = "Tipo de mapa"
    override val MAP_TYPE_NORMAL = "Normal"
    override val MAP_TYPE_SATELLITE = "Satélite"
    override val MAP_TYPE_HYBRID = "Híbrida"
    override val MAP_TYPE_TERRAIN = "Terreno"

    // Support System
    override val SUPPORT_TITLE = "Apoiar o Autor"
    override val SUPPORT_DISCLAIMER = "Todas as funcionalidades da aplicação são gratuitas. A escolha do nível é um apoio voluntário ao autor. A alteração do ícone é o único benefício digital."
    override val SUPPORT_LIFETIME_BUY = "Vitalício"
    override val SUPPORT_MONTHLY_SUB = "Mensalmente"
    override val SUPPORT_ICON_CHANGE_NOTICE = "A alteração do ícone pode demorar alguns segundos ou exigir o reinício do launcher."
}