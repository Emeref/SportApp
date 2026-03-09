package com.example.sportapp.data

import android.content.Context
import android.util.Log
import com.example.sportapp.presentation.activities.ActivityItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

class WorkoutRepository(private val context: Context) {

    private val activitiesDir = File(context.filesDir, "activities")

    fun getSummaryStatsLast7Days(): Map<String, Any> {
        val summaries = getAllSummaries()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val sevenDaysAgo = calendar.time

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

        val recentSummaries = summaries.filter { 
            try {
                val date = sdf.parse(it["data"] ?: "")
                date?.after(sevenDaysAgo) ?: false
            } catch (e: Exception) {
                false
            }
        }

        val totalCalories = recentSummaries.sumOf { it["kalorie"]?.toDoubleOrNull() ?: 0.0 }
        val totalDistanceGpsMeters = recentSummaries.sumOf { it["gps_dystans"]?.toDoubleOrNull() ?: 0.0 }
        val totalDistanceStepsMeters = recentSummaries.sumOf { it["kroki_dystans"]?.toDoubleOrNull() ?: 0.0 }

        return mapOf(
            "count" to recentSummaries.size,
            "calories" to String.format(Locale.US, "%.1f", totalCalories),
            "distanceGps" to formatDistance(totalDistanceGpsMeters),
            "distanceSteps" to formatDistance(totalDistanceStepsMeters)
        )
    }

    private fun formatDistance(meters: Double): String {
        return when {
            meters < 1000 -> {
                "${floor(meters).toInt()} m"
            }
            meters < 10000 -> {
                val km = floor(meters / 10.0) / 100.0 // 2 decimal places, rounded down
                String.format(Locale.US, "%.2f km", km)
            }
            meters < 100000 -> {
                val km = floor(meters / 100.0) / 10.0 // 1 decimal place, rounded down
                String.format(Locale.US, "%.1f km", km)
            }
            else -> {
                "${floor(meters / 1000.0).toInt()} km"
            }
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
