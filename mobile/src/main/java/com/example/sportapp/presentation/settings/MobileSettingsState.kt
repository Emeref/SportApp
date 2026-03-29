package com.example.sportapp.presentation.settings

import com.example.sportapp.core.i18n.AppLanguage
import com.example.sportapp.core.i18n.AppStrings

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
    val widgets: List<WidgetItem> = emptyList(),
    val period: ReportingPeriod = ReportingPeriod.WEEK,
    val customDays: Int = 7,
    // Sekcja: Statystyki na zegarku
    val watchStatsWidgets: List<WidgetItem> = emptyList(),
    val watchStatsPeriod: ReportingPeriod = ReportingPeriod.WEEK,
    val watchStatsCustomDays: Int = 7,
    val healthData: HealthData = HealthData(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val appLanguage: AppLanguage = AppLanguage.POLISH
) {
    companion object {
        fun getDefaultWidgets(strings: AppStrings) = listOf(
            WidgetItem("count", strings.activityCount),
            WidgetItem("calories", strings.totalCalories),
            WidgetItem("distanceGps", strings.distanceGps),
            WidgetItem("distanceSteps", strings.distanceSteps),
            WidgetItem("ascent", strings.totalAscentLabel),
            WidgetItem("descent", strings.totalDescentLabel),
            WidgetItem("steps", strings.allSteps),
            WidgetItem("max_speed", strings.maxSpeed),
            WidgetItem("max_altitude", strings.maxAltitude),
            WidgetItem("max_elevation_gain", strings.maxElevationGain),
            WidgetItem("max_distance", strings.maxDistance),
            WidgetItem("max_duration", strings.maxDuration),
            WidgetItem("max_calories", strings.maxCalories),
            WidgetItem("max_avg_cadence", strings.maxAvgCadence),
            WidgetItem("max_avg_speed", strings.maxAvgSpeed)
        )

        fun getDefaultWatchWidgets(strings: AppStrings) = listOf(
            WidgetItem("count", strings.activityCount),
            WidgetItem("calories", strings.totalCalories),
            WidgetItem("distanceGps", strings.distanceGps),
            WidgetItem("distanceSteps", strings.distanceSteps),
            WidgetItem("ascent", strings.ascent),
            WidgetItem("descent", strings.descent),
            WidgetItem("steps", strings.allSteps)
        )
    }
}
