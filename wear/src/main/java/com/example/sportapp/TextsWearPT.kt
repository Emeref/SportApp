package com.example.sportapp

object TextsWearPT : WearTexts {
    // Main Menu
    override val MENU_SPORT = "Esporte"
    override val MENU_STATISTICS = "Estatísticas"
    override val MENU_SETTINGS = "Configurações"
    override val APP_LOGO_DESC = "Logo SportApp"

    // Choose Sport
    override val CHOOSE_SPORT_TITLE = "Escolher esporte"
    override val CHOOSE_SPORT_NO_DEFINITIONS = "Sem definições de atividade. Defina-as no aplicativo do telefone."
    override val CHOOSE_SPORT_DEFAULT_NAME = "Atividade padrão"

    // Statistics
    override val STATS_NO_WIDGETS = "Nenhum campo selecionado"
    override val STATS_PERIOD_TODAY = "Hoje"
    override val STATS_PERIOD_7_DAYS = "Últimos 7 dias"
    override val STATS_PERIOD_30_DAYS = "Últimos 30 dias"
    override val STATS_PERIOD_YEAR = "Último ano"
    override fun statsPeriodCustom(days: Int) = "Últimos $days dias"

    override val STATS_WIDGET_COUNT = "Número de atividades"
    override val STATS_WIDGET_CALORIES = "Calorias queimadas"
    override val STATS_WIDGET_DISTANCE_GPS = "Distância (GPS)"
    override val STATS_WIDGET_DISTANCE_STEPS = "Distância (passos)"
    override val STATS_WIDGET_ASCENT = "Ganho de elevação"
    override val STATS_WIDGET_DESCENT = "Perda de elevação"
    override val STATS_WIDGET_STEPS_ALL = "Total de passos"

    override val STATS_WIDGET_MAX_SPEED = "Velocidade máxima"
    override val STATS_WIDGET_MAX_ALTITUDE = "Altitude máxima"
    override val STATS_WIDGET_MAX_ELEVATION_GAIN = "Maior ganho de elevação"
    override val STATS_WIDGET_MAX_DISTANCE = "Maior distância"
    override val STATS_WIDGET_MAX_DURATION = "Maior duração"
    override val STATS_WIDGET_MAX_CALORIES = "Máximo de calorias queimadas"
    override val STATS_WIDGET_MAX_AVG_CADENCE = "Maior cadência média"
    override val STATS_WIDGET_MAX_AVG_SPEED = "Maior velocidade média"

    // Workout Ready
    override val WORKOUT_READY_START = "Iniciar"
    override val WORKOUT_READY_BACK = "Voltar"

    // Settings
    override val SETTINGS_TITLE = "Configurações"
    override val SETTINGS_HEALTH_DATA = "Dados de saúde"
    override val SETTINGS_SCREEN = "Tela"
    override val SETTINGS_SCREEN_ALWAYS_ON = "Sempre ligada"
    override val SETTINGS_SCREEN_AMBIENT = "Modo Ambiente"
    override val SETTINGS_SCREEN_AUTO = "Modo Automático"
    override val SETTINGS_CLOCK_COLOR = "Cor do relógio"
    override val SETTINGS_SCREEN_BEHAVIOR_TITLE = "Comportamento da tela"
    override val SETTINGS_LANGUAGE = "Idioma"
    override val SETTINGS_LANGUAGE_SELECTION_TITLE = "Escolher idioma"
    
    // Colors
    override val COLOR_RED = "Vermelho"
    override val COLOR_WHITE = "Branco"
    override val COLOR_GREEN = "Verde"
    override val COLOR_YELLOW = "Amarelo"
    override val COLOR_BLUE = "Azul"
    override val COLOR_BLACK = "Preto"
    override val COLOR_NONE = "Nenhum"
    override val COLOR_CUSTOM = "Personalizado"

    // Health Data
    override val HEALTH_GENDER = "Gênero"
    override val HEALTH_AGE = "Idade"
    override val HEALTH_WEIGHT = "Peso"
    override val HEALTH_HEIGHT = "Altura"
    override val HEALTH_STEP_LENGTH = "Comprimento do passo"
    override val HEALTH_RESTING_HR = "FC em repouso"
    override val HEALTH_MAX_HR = "FC máxima"
    override val HEALTH_SAVE = "Salvar"
    override val HEALTH_CHOOSE_GENDER = "Escolher gênero"
    override val GENDER_MALE = "Masculino"
    override val GENDER_FEMALE = "Feminino"
    
    override fun healthAgeValue(age: Int) = "$age anos"
    override fun healthWeightValue(weight: Int) = "$weight kg"
    override fun healthHeightValue(height: Int) = "$height cm"
    override fun healthStepLengthValue(length: Int) = "$length cm"
    override fun healthHRValue(hr: Int) = "$hr BPM"

    // Units
    override val UNIT_YEARS = "anos"
    override val UNIT_KG = "kg"
    override val UNIT_CM = "cm"
    override val UNIT_BPM = "BPM"
    override val UNIT_M = "m"
    override val UNIT_KM = "km"
    override val UNIT_KMH = "km/h"
    override val UNIT_KCAL = "kcal"
    override val UNIT_HPA = "hPa"

    // Workout Data / Labels
    override val WORKOUT_ERROR_CONFIG = "Erro de configuração"
    override val WORKOUT_LABEL_TIMER = "TEMPO DE ATIVIDADE"
    override val WORKOUT_LABEL_STEPS = "Passos"
    override val WORKOUT_LABEL_DISTANCE = "Distância"
    override val WORKOUT_LABEL_SPEED = "Velocidade"
    override val WORKOUT_LABEL_HR = "Frequência cardíaca"
    override val WORKOUT_LABEL_PRESSURE = "Pressão"
    override val WORKOUT_LABEL_ALTITUDE = "Altitude"

    // Sensors Names (for WorkoutDefinition)
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
    override val SENSOR_TOTAL_ASCENT = "Ganho de elevação"
    override val SENSOR_TOTAL_DESCENT = "Perda de elevação"
    override val SENSOR_PRESSURE = "Pressão atm."
    override val SENSOR_MAP = "Dados de localização"

    // Workout Controls
    override val WORKOUT_RESUME = "Retomar"
    override val WORKOUT_PAUSE = "Pausar"
    override val WORKOUT_FINISH = "Terminar"
    override val WORKOUT_START = "Iniciar"
    override val WORKOUT_STOP = "Parar"
    override val WORKOUT_READY_MSG = "Use o DynamicWorkoutScreen"

    // Summary
    override val SUMMARY_TITLE = "RESUMO"
    override val SUMMARY_CONFIRM_DESC = "Confirmar"
    override val SUMMARY_DURATION = "Duração"
    override val SUMMARY_AVG_HR = "FC média"
    override val SUMMARY_MAX_HR = "FC máxima"
    override val SUMMARY_AVG_SPEED = "Velocidade média"
    override val SUMMARY_MAX_SPEED = "Velocidade máxima"
    override val SUMMARY_AVG_SPEED_STEPS = "Velocidade média (passos)"
    override val SUMMARY_MAX_SPEED_STEPS = "Velocidade máxima (passos)"
    override val SUMMARY_DISTANCE = "Distância"
    override val SUMMARY_DISTANCE_STEPS = "Distância (passos)"
    override val SUMMARY_STEPS = "Passos"
    override val SUMMARY_TOTAL_ASCENT = "Ganho de elevação"
    override val SUMMARY_TOTAL_DESCENT = "Perda de elevação"
    override val SUMMARY_CALORIES = "Calorias"

    // General
    override val GEN_ACTIVITY = "Atividade"
    override val VAL_EMPTY = "--"

    // Complication / Tile
    override val COMP_MON = "Seg"
    override val COMP_TUE = "Ter"
    override val COMP_WED = "Qua"
    override val COMP_THU = "Qui"
    override val COMP_FRI = "Sex"
    override val COMP_SAT = "Sáb"
    override val COMP_SUN = "Dom"
    
    override val COMP_MONDAY = "Segunda-feira"
    override val COMP_TUESDAY = "Terça-feira"
    override val COMP_WEDNESDAY = "Quarta-feira"
    override val COMP_THURSDAY = "Quinta-feira"
    override val COMP_FRIDAY = "Sexta-feira"
    override val COMP_SATURDAY = "Sábado"
    override val COMP_SUNDAY = "Domingo"

    override val TILE_HELLO = "Olá!"
}
