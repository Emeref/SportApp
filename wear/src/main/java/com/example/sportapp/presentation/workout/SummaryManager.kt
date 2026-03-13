package com.example.sportapp.presentation.workout

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object SummaryManager {
    private const val SUMMARY_FILE_NAME = "Podsumowanie_cwiczen.csv"

    private fun formatVal(value: Any?, decimalPlaces: Int = -1): String {
        if (value == null) return ""
        val stringVal = when (value) {
            is Float -> if (value == 0f) "" else if (decimalPlaces >= 0) String.format(Locale.US, "%.${decimalPlaces}f", value) else value.toString()
            is Double -> if (value == 0.0) "" else if (decimalPlaces >= 0) String.format(Locale.US, "%.${decimalPlaces}f", value) else value.toString()
            is Int -> if (value == 0) "" else value.toString()
            is Long -> if (value == 0L) "" else value.toString()
            else -> value.toString()
        }
        return stringVal
    }

    fun saveSummary(
        context: Context,
        activityName: String,
        startTime: Date,
        durationFormatted: String,
        steps: Int?,
        distanceSteps: Double?,
        distanceGps: Float?,
        avgSpeedSteps: Double?,
        avgSpeedGps: Double?,
        totalAscent: Double?,
        totalDescent: Double?,
        avgBpm: Double?,
        totalCalories: Double?,
        maxCalorieMin: Double?,
        durationSeconds: Long
    ) {
        val activitiesDir = File(context.filesDir, "activities")
        if (!activitiesDir.exists()) {
            activitiesDir.mkdirs()
        }

        val file = File(activitiesDir, SUMMARY_FILE_NAME)
        val isNewFile = !file.exists()

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val dateStr = sdf.format(startTime)

        val distanceStepsRounded = distanceSteps?.roundToInt()
        val distanceGpsRounded = distanceGps?.roundToInt()

        val line = StringBuilder().apply {
            append(dateStr).append(";")
            append(activityName).append(";")
            append(durationFormatted).append(";")
            append(formatVal(steps)).append(";")
            append(formatVal(distanceStepsRounded)).append(";")
            append(formatVal(distanceGpsRounded)).append(";")
            append(formatVal(avgSpeedSteps, 2)).append(";")
            append(formatVal(avgSpeedGps, 2)).append(";")
            append(formatVal(totalAscent, 1)).append(";")
            append(formatVal(totalDescent, 1)).append(";")
            append(formatVal(avgBpm, 1)).append(";")
            append(formatVal(totalCalories, 1)).append(";") // nowa kolumna: kalorie
            append(formatVal(maxCalorieMin, 1)) // nowa kolumna: maks_kalorie_min (zastępuje średnią)
        }.toString()

        try {
            val fos = FileOutputStream(file, true)
            if (isNewFile) {
                val header = "data;nazwa aktywnosci;dlugosc;kroki;kroki_dystans;gps_dystans;srednia_predkosc_kroki;srednia_predkosc_gps;przewyzszenia_gora;przewyzszenia_dol;srednie_bpm;kalorie;maks_kalorie_min\n"
                fos.write(header.toByteArray())
            }
            fos.write((line + "\n").toByteArray())
            fos.close()
        } catch (e: Exception) {
            Log.e("SummaryManager", "Error saving summary", e)
        }
    }

    data class WeeklyStats(
        val totalSteps: Int,
        val totalDistanceMeters: Int,
        val totalCalories: Int
    )

    fun getWeeklyStats(context: Context): WeeklyStats {
        val activitiesDir = File(context.filesDir, "activities")
        val file = File(activitiesDir, SUMMARY_FILE_NAME)
        if (!file.exists()) return WeeklyStats(0, 0, 0)

        var totalSteps = 0
        var totalDistance = 0
        var totalCalories = 0.0

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val oneWeekAgo = calendar.time

        try {
            BufferedReader(FileReader(file)).use { reader ->
                val header = reader.readLine() // Skip header
                var line: String? = reader.readLine()
                while (line != null) {
                    val parts = line.split(";")
                    if (parts.size >= 12) {
                        try {
                            val date = sdf.parse(parts[0])
                            if (date != null && date.after(oneWeekAgo)) {
                                totalSteps += parts[3].toIntOrNull() ?: 0
                                // Preferujemy GPS dystans, jeśli brak to kroki_dystans
                                val distGps = parts[5].toIntOrNull() ?: 0
                                val distSteps = parts[4].toIntOrNull() ?: 0
                                totalDistance += if (distGps > 0) distGps else distSteps
                                totalCalories += parts[11].toDoubleOrNull() ?: 0.0
                            }
                        } catch (e: Exception) {
                            Log.e("SummaryManager", "Error parsing line: $line", e)
                        }
                    }
                    line = reader.readLine()
                }
            }
        } catch (e: Exception) {
            Log.e("SummaryManager", "Error reading summary file", e)
        }

        return WeeklyStats(totalSteps, totalDistance, totalCalories.toInt())
    }
}
