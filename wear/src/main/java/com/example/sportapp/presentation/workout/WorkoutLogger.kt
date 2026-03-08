package com.example.sportapp.presentation.workout

import android.content.Context
import android.util.Log
import com.example.sportapp.presentation.settings.Gender
import com.example.sportapp.presentation.settings.HealthData
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class WorkoutLogger(
    private val context: Context,
    private val activityName: String,
    private val healthData: HealthData
) {
    private var file: File? = null
    private var startTime: Long = 0
    private var lastHeight: Double? = null
    private var totalAscent: Double = 0.0
    private var totalDescent: Double = 0.0
    private val heartRates = mutableListOf<Float>()

    init {
        val sdf = SimpleDateFormat("yyyy_MM_dd_HH:mm:ss", Locale.US)
        val dateStr = sdf.format(Date())
        val genderStr = if (healthData.gender == Gender.MALE) "M" else "F"
        
        val fileName = "${activityName}_${dateStr}_${genderStr}_${healthData.age}_${healthData.height}_${healthData.weight}_${healthData.stepLength}_${healthData.restingHR}_${healthData.maxHR}.csv"
        
        file = File(context.filesDir, fileName)
        startTime = System.currentTimeMillis()
        
        // Write header
        writeLine("czas;lat;lon;bpm;srednie_bpm;kroki;kroki_min;odl_kroki;gps_dystans;predkosc;wysokosc;przewyzszenia_gora;przewyzszenia_dol")
    }

    fun logData(
        lat: Double? = null,
        lon: Double? = null,
        bpm: Float? = null,
        kroki: Int? = null,
        gpsDystans: Float? = null,
        predkosc: Float? = null,
        wysokosc: Double? = null
    ) {
        val currentTime = System.currentTimeMillis()
        val durationMillis = currentTime - startTime
        val seconds = (durationMillis / 1000) % 60
        val minutes = (durationMillis / (1000 * 60)) % 60
        val hours = (durationMillis / (1000 * 60 * 60))
        val timeFormatted = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

        // Average BPM
        if (bpm != null && bpm > 0) heartRates.add(bpm)
        val avgBpm = if (heartRates.isNotEmpty()) heartRates.average() else null

        // Steps per minute
        val stepsMin = if (kroki != null && durationMillis > 0) {
            (kroki.toDouble() / (durationMillis / 60000.0))
        } else null

        // Distance from steps
        val odlKroki = if (kroki != null) {
            (kroki * healthData.stepLength / 100.0)
        } else null

        // Elevation
        if (wysokosc != null) {
            lastHeight?.let { last ->
                val diff = wysokosc - last
                if (diff > 0) totalAscent += diff
                else if (diff < 0) totalDescent += Math.abs(diff)
            }
            lastHeight = wysokosc
        }

        val line = StringBuilder().apply {
            append(timeFormatted).append(";")
            append(lat ?: "null").append(";")
            append(lon ?: "null").append(";")
            append(bpm?.toInt() ?: "null").append(";")
            append(avgBpm?.let { String.format(Locale.US, "%.1f", it) } ?: "null").append(";")
            append(kroki ?: "null").append(";")
            append(stepsMin?.let { String.format(Locale.US, "%.1f", it) } ?: "null").append(";")
            append(odlKroki?.let { String.format(Locale.US, "%.2f", it) } ?: "null").append(";")
            append(gpsDystans?.let { String.format(Locale.US, "%.2f", it) } ?: "null").append(";")
            append(predkosc?.let { String.format(Locale.US, "%.1f", it) } ?: "null").append(";")
            append(wysokosc?.let { String.format(Locale.US, "%.1f", it) } ?: "null").append(";")
            append(String.format(Locale.US, "%.1f", totalAscent)).append(";")
            append(String.format(Locale.US, "%.1f", totalDescent))
        }.toString()

        writeLine(line)
    }

    private fun writeLine(line: String) {
        try {
            val fos = FileOutputStream(file, true)
            fos.write((line + "\n").toByteArray())
            fos.close()
        } catch (e: Exception) {
            Log.e("WorkoutLogger", "Error writing to file", e)
        }
    }
    
    fun getFinalStats(): Map<String, Any?> {
        return mapOf(
            "totalAscent" to totalAscent,
            "totalDescent" to totalDescent,
            "avgBpm" to if (heartRates.isNotEmpty()) heartRates.average() else null
        )
    }
}
