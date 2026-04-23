package com.example.sportapp.presentation.settings

import com.example.sportapp.MobileTexts
import com.example.sportapp.TextsMobileEN
import com.example.sportapp.TextsMobilePL
import com.example.sportapp.TextsMobileDE
import com.example.sportapp.TextsMobileFR
import com.example.sportapp.TextsMobileES
import com.example.sportapp.TextsMobileIT
import com.example.sportapp.TextsMobilePT
import com.example.sportapp.healthconnect.ConflictResolutionPolicy

enum class ReportingPeriod {
    TODAY, WEEK, MONTH, YEAR, CUSTOM
}

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class AppMapType {
    NORMAL, SATELLITE, HYBRID, TERRAIN
}

enum class AppLanguage(val code: String, val label: String, val texts: MobileTexts) {
 //   ENGLISH("en", "English", TextsMobileEN),
 //   SPANISH("es", "Español", TextsMobileES),
 //   GERMAN("de", "Deutsch", TextsMobileDE),
//    FRENCH("fr", "Français", TextsMobileFR),
 //   ITALIAN("it", "Italiano", TextsMobileIT),
    POLISH("pl", "Polski", TextsMobilePL),
  //  PORTUGUESE("pt", "Português", TextsMobilePT)
}

data class WidgetItem(
    val id: String,
    val label: String,
    val isEnabled: Boolean = true
)

data class MobileSettingsState(
    val widgets: List<WidgetItem> = listOf(
        WidgetItem("count", TextsMobilePL.WIDGET_COUNT),
        WidgetItem("calories", TextsMobilePL.WIDGET_CALORIES),
        WidgetItem("distanceGps", TextsMobilePL.WIDGET_DISTANCE_GPS),
        WidgetItem("distanceSteps", TextsMobilePL.WIDGET_DISTANCE_STEPS),
        WidgetItem("ascent", TextsMobilePL.WIDGET_ASCENT),
        WidgetItem("descent", TextsMobilePL.WIDGET_DESCENT),
        WidgetItem("steps", TextsMobilePL.WIDGET_STEPS),
        WidgetItem("max_speed", TextsMobilePL.WIDGET_MAX_SPEED),
        WidgetItem("max_altitude", TextsMobilePL.WIDGET_MAX_ALTITUDE),
        WidgetItem("max_elevation_gain", TextsMobilePL.WIDGET_MAX_ELEVATION_GAIN),
        WidgetItem("max_distance", TextsMobilePL.WIDGET_MAX_DISTANCE),
        WidgetItem("max_duration", TextsMobilePL.WIDGET_MAX_DURATION),
        WidgetItem("max_calories", TextsMobilePL.WIDGET_MAX_CALORIES),
        WidgetItem("max_avg_cadence", TextsMobilePL.WIDGET_MAX_AVG_CADENCE),
        WidgetItem("max_avg_speed", TextsMobilePL.WIDGET_MAX_AVG_SPEED)
    ),
    val period: ReportingPeriod = ReportingPeriod.WEEK,
    val customDays: Int = 7,
    // Sekcja: Statystyki na zegarku
    val watchStatsWidgets: List<WidgetItem> = listOf(
        WidgetItem("count", TextsMobilePL.WIDGET_COUNT),
        WidgetItem("calories", TextsMobilePL.WIDGET_CALORIES),
        WidgetItem("distanceGps", TextsMobilePL.WIDGET_DISTANCE_GPS),
        WidgetItem("distanceSteps", TextsMobilePL.WIDGET_DISTANCE_STEPS),
        WidgetItem("ascent", TextsMobilePL.WIDGET_WATCH_ASCENT),
        WidgetItem("descent", TextsMobilePL.WIDGET_WATCH_DESCENT),
        WidgetItem("steps", TextsMobilePL.WIDGET_STEPS)
    ),
    val watchStatsPeriod: ReportingPeriod = ReportingPeriod.WEEK,
    val watchStatsCustomDays: Int = 7,
    val healthData: HealthData = HealthData(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.POLISH,
    val mapType: AppMapType = AppMapType.NORMAL,
    val autoExportToHC: Boolean = false,
    val autoExportToStrava: Boolean = false,
    val hcPermissionsDeniedCount: Int = 0,
    val conflictResolutionPolicy: ConflictResolutionPolicy = ConflictResolutionPolicy.NEWER_WINS
)
