package com.example.sportapp.data

import com.example.sportapp.presentation.activities.ActivityItem
import com.example.sportapp.presentation.settings.ReportingPeriod
import java.util.*

class FakeWorkoutRepository : IWorkoutRepository {
    var summaries = mutableListOf<Map<String, String>>()

    override suspend fun getUniqueActivityTypes(): List<String> {
        return summaries.map { it["nazwa aktywnosci"] ?: "" }.distinct().filter { it.isNotEmpty() }
    }

    override suspend fun getFilteredStats(
        activityType: String?,
        startDate: Date?,
        endDate: Date?
    ): Map<String, Any> {
        val filtered = summaries.filter { summary ->
            val typeMatch = activityType == null || summary["nazwa aktywnosci"] == activityType
            // Dla prostoty testów pomijamy daty w Fake'u lub dodajemy prostą logikę jeśli potrzebna
            typeMatch
        }

        return mapOf(
            "count" to filtered.size,
            "calories" to filtered.sumOf { it["kalorie"]?.toDoubleOrNull() ?: 0.0 },
            "distanceGpsM" to filtered.sumOf { it["gps_dystans"]?.toDoubleOrNull() ?: 0.0 },
            "distanceStepsM" to filtered.sumOf { it["kroki_dystans"]?.toDoubleOrNull() ?: 0.0 },
            "ascent" to filtered.sumOf { it["przewyzszenia_gora"]?.toDoubleOrNull() ?: 0.0 },
            "descent" to filtered.sumOf { it["przewyzszenia_dol"]?.toDoubleOrNull() ?: 0.0 },
            "steps" to filtered.sumOf { it["kroki"]?.toLongOrNull() ?: 0L },
            "raw_data" to filtered
        )
    }

    override suspend fun getStatsForPeriod(period: ReportingPeriod, customDays: Int): Map<String, Any> {
        return getFilteredStats()
    }

    override fun formatDistance(meters: Double): String = "${meters.toInt()} m"

    override suspend fun getAllSummaries(): List<Map<String, String>> = summaries

    override suspend fun getActivityItems(): List<ActivityItem> = emptyList()
}
