package com.example.sportapp.presentation.settings

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.sportapp.MobileTexts
import com.example.sportapp.TextsMobileEN
import com.example.sportapp.TextsMobilePL
import com.example.sportapp.TextsMobileDE
import com.example.sportapp.TextsMobileFR
import com.example.sportapp.TextsMobileES
import com.example.sportapp.TextsMobileIT
import com.example.sportapp.TextsMobilePT
import com.example.sportapp.healthconnect.ConflictResolutionPolicy
import java.util.Locale

enum class ReportingPeriod {
    TODAY, WEEK, MONTH, YEAR, CUSTOM
}

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class AppMapType {
    NORMAL, SATELLITE, HYBRID, TERRAIN
}

enum class ExportFormat {
    GPX, FIT
}

enum class AppLanguage(val code: String, val label: String, val texts: MobileTexts, val locale: Locale) {
    ENGLISH("en", "English", TextsMobileEN, Locale.ENGLISH),
    SPANISH("es", "Español", TextsMobileES, Locale.forLanguageTag("es")),
    GERMAN("de", "Deutsch", TextsMobileDE, Locale.GERMAN),
    FRENCH("fr", "Français", TextsMobileFR, Locale.FRENCH),
    ITALIAN("it", "Italiano", TextsMobileIT, Locale.ITALIAN),
    POLISH("pl", "Polski", TextsMobilePL, Locale.forLanguageTag("pl")),
    PORTUGUESE("pt", "Português", TextsMobilePT, Locale.forLanguageTag("pt"))
}

val LocalAppLanguage = staticCompositionLocalOf<AppLanguage> {
    error("No AppLanguage provided")
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
        WidgetItem("steps", TextsMobilePL.WIDGET_STEPS)
    ),
    val period: ReportingPeriod = ReportingPeriod.WEEK,
    val customDays: Int = 14,
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
    val defaultExportFormat: ExportFormat = ExportFormat.GPX,
    val hcPermissionsDeniedCount: Int = 0,
    val conflictResolutionPolicy: ConflictResolutionPolicy = ConflictResolutionPolicy.NEWER_WINS,
    val activeIconTier: Int = 0
)
