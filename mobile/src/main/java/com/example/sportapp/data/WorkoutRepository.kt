package com.example.sportapp.data

import android.content.Context
import android.util.Log
import com.example.sportapp.presentation.activities.ActivityItem
import com.example.sportapp.presentation.settings.ReportingPeriod
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

class WorkoutRepository(private val context: Context) {

    private val activitiesDir = File(context.filesDir, "activities")

    fun getStatsForPeriod(period: ReportingPeriod, customDays: Int): Map<String, Any> {
        val summaries = getAllSummaries()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val now = Calendar.getInstance()
        
        val filtered = summaries.filter { 
            try {
                val date = sdf.parse(it["data"] ?: "")
                if (date == null) false else {
                    val activityCal = Calendar.getInstance().apply { time = date }
                    when (period) {
                        ReportingPeriod.TODAY -> {
                            activityCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            activityCal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                        }
                        ReportingPeriod.WEEK -> {
                            // Tydzień od ostatniego poniedziałku
                            val monday = Calendar.getInstance().apply {
                                firstDayOfWeek = Calendar.MONDAY
                                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                            }
                            activityCal.after(monday)
                        }
                        ReportingPeriod.MONTH -> {
                            activityCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            activityCal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                        }
                        ReportingPeriod.YEAR -> {
                            activityCal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                        }
                        ReportingPeriod.CUSTOM -> {
                            val limit = Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_YEAR, -customDays)
                            }
                            activityCal.after(limit)
                        }
                    }
                }
            } catch (e: Exception) {
                false
            }
        }

        return mapOf(
            "count" to filtered.size,
            "calories" to String.format(Locale.US, "%.1f", filtered.sumOf { it["kalorie"]?.toDoubleOrNull() ?: 0.0 }),
            "distanceGpsM" to filtered.sumOf { it["gps_dystans"]?.toDoubleOrNull() ?: 0.0 },
            "distanceStepsM" to filtered.sumOf { it["kroki_dystans"]?.toDoubleOrNull() ?: 0.0 },
            "ascent" to String.format(Locale.US, "%.1f", filtered.sumOf { it["przewyzszenia_gora"]?.toDoubleOrNull() ?: 0.0 }),
            "descent" to String.format(Locale.US, "%.1f", filtered.sumOf { it["przewyzszenia_dol"]?.toDoubleOrNull() ?: 0.0 }),
            "steps" to filtered.sumOf { it["kroki"]?.toLongOrNull() ?: 0L }
        )
    }

    // Pozostawiam pomocniczą metodę do formatowania dla UI
    fun formatDistance(meters: Double): String {
        return when {
            meters < 1000 -> "${floor(meters).toInt()} m"
            meters < 10000 -> String.format(Locale.US, "%.2f km", floor(meters / 10.0) / 100.0)
            meters < 100000 -> String.format(Locale.US, "%.1f km", floor(meters / 100.0) / 10.0)
            else -> "${floor(meters / 1000.0).toInt()} km"
        }
    }

    fun getAllSummaries(): List<Map<String, String>> {
        val file = File(activitiesDir, "Podsumowanie_cwiczen.csv")
        if (!file.exists()) return emptyList()

        val results = mutableListOf<Map<String, String>>()
        try {
            val lines = file.readLines()
            if (lines.size < 2) return emptyList()
            
            val header = lines[0].split(";")
            for (i in 1 until lines.size) {
                val values = lines[i].split(";")
                val map = mutableMapOf<String, String>()
                header.forEachIndexed { index, name ->
                    if (index < values.size) {
                        map[name] = values[index]
                    }
                }
                results.add(map)
            }
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Error reading summary file", e)
        }
        return results
    }

    fun getActivityItems(): List<ActivityItem> {
        val summaries = getAllSummaries()
        return summaries.mapIndexed { index, map ->
            val distGpsM = map["gps_dystans"]?.toDoubleOrNull() ?: 0.0
            val distStepsM = map["kroki_dystans"]?.toDoubleOrNull() ?: 0.0
            
            ActivityItem(
                id = index.toString(),
                type = map["nazwa aktywnosci"] ?: "Nieznana",
                date = map["data"] ?: "",
                duration = map["dlugosc"] ?: "",
                calories = map["kalorie"] ?: "0",
                distanceGps = formatDistance(distGpsM),
                distanceSteps = formatDistance(distStepsM)
            )
        }.reversed()
    }
}
