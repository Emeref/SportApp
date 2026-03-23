package com.example.sportapp.presentation.settings

enum class ReportingPeriod {
    TODAY, WEEK, MONTH, YEAR, CUSTOM
}

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

data class WidgetItem(
    val id: String,
    val label: String,
    val isEnabled: Boolean = true
)

data class MobileSettingsState(
    val widgets: List<WidgetItem> = listOf(
        WidgetItem("count", "Liczba aktywności"),
        WidgetItem("calories", "Spalone kalorie"),
        WidgetItem("distanceGps", "Dystans (GPS)"),
        WidgetItem("distanceSteps", "Dystans (kroki)"),
        WidgetItem("ascent", "W sumie w górę"),
        WidgetItem("descent", "W sumie do dołu"),
        WidgetItem("steps", "Wszystkie kroki")
    ),
    val period: ReportingPeriod = ReportingPeriod.WEEK,
    val customDays: Int = 7,
    // Sekcja: Statystyki na zegarku
    val watchStatsWidgets: List<WidgetItem> = listOf(
        WidgetItem("count", "Liczba aktywności"),
        WidgetItem("calories", "Spalone kalorie"),
        WidgetItem("distanceGps", "Dystans (GPS)"),
        WidgetItem("distanceSteps", "Dystans (kroki)"),
        WidgetItem("ascent", "Przewyższenia w górę"),
        WidgetItem("descent", "Przewyższenia w dół"),
        WidgetItem("steps", "Wszystkie kroki")
    ),
    val watchStatsPeriod: ReportingPeriod = ReportingPeriod.WEEK,
    val watchStatsCustomDays: Int = 7,
    val healthData: HealthData = HealthData(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
