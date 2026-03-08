package com.example.sportapp.presentation.workout

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object SummaryManager {
    private const val SUMMARY_FILE_NAME = "Podsumowanie_cwiczen.csv"

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
            append(steps ?: "null").append(";")
            append(distanceSteps?.let { String.format(Locale.US, "%.2f", it) } ?: "null").append(";")
            append(distanceGps?.let { String.format(Locale.US, "%.2f", it) } ?: "null").append(";")
            append(avgSpeedSteps?.let { String.format(Locale.US, "%.2f", it) } ?: "null").append(";")
            append(avgSpeedGps?.let { String.format(Locale.US, "%.2f", it) } ?: "null").append(";")
            append(totalAscent?.let { String.format(Locale.US, "%.1f", it) } ?: "null").append(";")
            append(totalDescent?.let { String.format(Locale.US, "%.1f", it) } ?: "null").append(";")
            append(avgBpm?.let { String.format(Locale.US, "%.1f", it) } ?: "null")
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
