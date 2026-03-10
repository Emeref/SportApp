package com.example.sportapp.data

import android.content.Context
import android.util.Log
import com.example.sportapp.presentation.activities.ActivityItem
import com.example.sportapp.presentation.settings.MobileSettingsManager
import com.example.sportapp.presentation.settings.ReportingPeriod
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

class WorkoutRepository(private val context: Context) : IWorkoutRepository {

    private val settingsManager = MobileSettingsManager(context)

    private fun getActivitiesDir(): File {
        val useTestData = runBlocking { settingsManager.settingsFlow.first().useTestData }
        val dirName = if (useTestData) "test_activities" else "activities"
        val dir = File(context.filesDir, dirName)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    override fun getUniqueActivityTypes(): List<String> {
        return getAllSummaries().map { it["nazwa aktywnosci"] ?: "" }.distinct().filter { it.isNotEmpty() }
    }

    fun getStatsForPeriod(period: ReportingPeriod, customDays: Int): Map<String, Any> {
        val now = Calendar.getInstance()
        val startDate: Date
        val endDate: Date

        when (period) {
            ReportingPeriod.TODAY -> {
                startDate = now.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }.time
                endDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1); set(Calendar.HOUR_OF_DAY, 0) }.time
            }
            ReportingPeriod.WEEK -> {
                startDate = now.apply { firstDayOfWeek = Calendar.MONDAY; set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); set(Calendar.HOUR_OF_DAY, 0) }.time
                endDate = Calendar.getInstance().apply { add(Calendar.WEEK_OF_YEAR, 1) }.time
            }
            ReportingPeriod.MONTH -> {
                startDate = now.apply { set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0) }.time
                endDate = Calendar.getInstance().apply { add(Calendar.MONTH, 1); set(Calendar.DAY_OF_MONTH, 1) }.time
            }
            ReportingPeriod.YEAR -> {
                startDate = now.apply { set(Calendar.DAY_OF_YEAR, 1); set(Calendar.HOUR_OF_DAY, 0) }.time
                endDate = Calendar.getInstance().apply { add(Calendar.YEAR, 1) }.time
            }
            ReportingPeriod.CUSTOM -> {
                endDate = Calendar.getInstance().time
                startDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -customDays) }.time
            }
        }
        return getFilteredStats(startDate = startDate, endDate = endDate)
    }

    override fun getFilteredStats(
        activityType: String?,
        startDate: Date?,
        endDate: Date?
    ): Map<String, Any> {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        
        val filtered = getAllSummaries().filter { summary ->
            val typeMatch = activityType == null || summary["nazwa aktywnosci"] == activityType
            val dateMatch = try {
                val date = sdf.parse(summary["data"] ?: "")
                (startDate == null || date?.after(startDate) == true) && (endDate == null || date?.before(endDate) == true)
            } catch (e: Exception) { false }

            typeMatch && dateMatch
        }

        return mapOf(
            "count" to filtered.size,
            "calories" to filtered.sumOf { it["kalorie"]?.toDoubleOrNull() ?: 0.0 },
            "distanceGpsM" to filtered.sumOf { it["gps_dystans"]?.toDoubleOrNull() ?: 0.0 },
            "distanceStepsM" to filtered.sumOf { it["kroki_dystans"]?.toDoubleOrNull() ?: 0.0 },
            "ascent" to filtered.sumOf { it["przewyzszenia_gora"]?.toDoubleOrNull() ?: 0.0 },
            "descent" to filtered.sumOf { it["przewyzszenia_dol"]?.toDoubleOrNull() ?: 0.0 },
            "steps" to filtered.sumOf { it["kroki"]?.toLongOrNull() ?: 0L },
            "raw_data" to filtered // Dodajemy surowe dane do wykresów
        )
    }

    override fun formatDistance(meters: Double): String {
        return when {
            meters < 1000 -> "${floor(meters).toInt()} m"
            meters < 10000 -> String.format(Locale.US, "%.2f km", floor(meters / 10.0) / 100.0)
            meters < 100000 -> String.format(Locale.US, "%.1f km", floor(meters / 100.0) / 10.0)
            else -> "${floor(meters / 1000.0).toInt()} km"
        }
    }

    override fun getAllSummaries(): List<Map<String, String>> {
        val file = File(getActivitiesDir(), "Podsumowanie_cwiczen.csv")
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
        return results.sortedBy { it["data"] } // Sortujemy od najstarszych do najnowszych
    }

    override fun getActivityItems(): List<ActivityItem> {
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
