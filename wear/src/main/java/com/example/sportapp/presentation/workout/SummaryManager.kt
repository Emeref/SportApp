package com.example.sportapp.presentation.workout

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

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
        avgBpm: Double?
    ) {
        // Folder 'activities' w katalogu plików aplikacji
        val activitiesDir = File(context.filesDir, "activities")
        if (!activitiesDir.exists()) {
            activitiesDir.mkdirs()
        }

        val file = File(activitiesDir, SUMMARY_FILE_NAME)
        val isNewFile = !file.exists()

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val dateStr = sdf.format(startTime)

        val line = StringBuilder().apply {
            append(dateStr).append(";")
            append(activityName).append(";")
            append(durationFormatted).append(";")
            append(formatVal(steps)).append(";")
            append(formatVal(distanceSteps, 2)).append(";")
            append(formatVal(distanceGps, 2)).append(";")
            append(formatVal(avgSpeedSteps, 2)).append(";")
            append(formatVal(avgSpeedGps, 2)).append(";")
            append(formatVal(totalAscent, 1)).append(";")
            append(formatVal(totalDescent, 1)).append(";")
            append(formatVal(avgBpm, 1))
        }.toString()

        try {
            val fos = FileOutputStream(file, true)
            if (isNewFile) {
                val header = "data;nazwa aktywnosci;dlugosc;kroki;odl_kroki;gps_dystans;srednia_predkosc_kroki;srednia_predkosc_gps;przewyzszenia_gora;przewyzszenia_dol;srednie_bpm\n"
                fos.write(header.toByteArray())
            }
            fos.write((line + "\n").toByteArray())
            fos.close()
        } catch (e: Exception) {
            Log.e("SummaryManager", "Error saving summary", e)
        }
    }
}
