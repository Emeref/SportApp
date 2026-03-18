package com.example.sportapp.presentation.settings

enum class ReportingPeriod {
    TODAY, WEEK, MONTH, YEAR, CUSTOM
}

data class WidgetItem(
    val id: String,
    val label: String,
    val isEnabled: Boolean = true
)
