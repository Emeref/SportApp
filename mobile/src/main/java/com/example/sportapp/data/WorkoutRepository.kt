package com.example.sportapp.data

import android.content.Context
import android.util.Log
import com.example.sportapp.presentation.activities.ActivityItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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

        val totalCalories = recentSummaries.sumOf { (it["srednie_bpm"]?.toDoubleOrNull() ?: 0.0) * 0.1 } // Uproszczony wzór
        val totalDistanceMeters = recentSummaries.sumOf { it["gps_dystans"]?.toDoubleOrNull() ?: 0.0 }
        val totalDistanceKm = totalDistanceMeters / 1000.0

        return mapOf(
            "count" to recentSummaries.size,
            "calories" to totalCalories.toInt(),
            "distance" to String.format(Locale.US, "%.2f", totalDistanceKm)
        )
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
            val distStepsM = map["odl_kroki"]?.toDoubleOrNull() ?: 0.0
            
            ActivityItem(
                id = index.toString(),
                type = map["nazwa aktywnosci"] ?: "Nieznana",
                date = map["data"] ?: "",
                duration = map["dlugosc"] ?: "",
                calories = "---",
                distanceGps = String.format(Locale.US, "%.2f km", distGpsM / 1000.0),
                distanceSteps = String.format(Locale.US, "%.2f km", distStepsM / 1000.0)
            )
        }.reversed()
    }
}
