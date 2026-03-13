package com.example.sportapp.presentation.settings

enum class ReportingPeriod {
    TODAY, WEEK, MONTH, YEAR, CUSTOM
}

data class WidgetItem(
    val id: String,
    val label: String,
    val isEnabled: Boolean = true
)

data class SensorInfo(
    val id: String,
    val label: String,
    val isEnabled: Boolean = true
)

data class SportConfig(
    val id: String,
    val name: String,
    val sensors: List<SensorInfo> = listOf(
        SensorInfo("hr", "Tętno"),
        SensorInfo("steps", "Kroki"),
        SensorInfo("dist_steps", "Dystans (kroki)"),
        SensorInfo("dist_gps", "Dystans (GPS)"),
        SensorInfo("calories", "Kalorie"),
        SensorInfo("alt", "Wysokość"),
        SensorInfo("ascent", "Wzniosy"),
        SensorInfo("descent", "Spadki"),
        SensorInfo("kcal_min", "kcal/min"),
        SensorInfo("steps_min", "kroki/min"),
        SensorInfo("speed_gps", "Prędkość (GPS)"),
        SensorInfo("speed_steps", "Prędkość (kroki)"),
        SensorInfo("map", "Mapa")
    )
)

data class MobileSettingsState(
    val widgets: List<WidgetItem> = listOf(
        WidgetItem("count", "Liczba aktywności"),
        WidgetItem("calories", "Spalone kalorie"),
        WidgetItem("distanceGps", "Dystans (GPS)"),
        WidgetItem("distanceSteps", "Dystans (kroki)"),
        WidgetItem("ascent", "Przewyższenia w górę"),
        WidgetItem("descent", "Przewyższenia w dół"),
        WidgetItem("steps", "Wszystkie kroki")
    ),
    val period: ReportingPeriod = ReportingPeriod.WEEK,
    val customDays: Int = 7,
    val useTestData: Boolean = false,
    val sports: List<SportConfig> = listOf(
        SportConfig("default", "Default sport")
    )
)
