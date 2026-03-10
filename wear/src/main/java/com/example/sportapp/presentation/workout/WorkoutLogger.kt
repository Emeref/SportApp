package com.example.sportapp.presentation.workout

import android.content.Context
import android.util.Log
import com.example.sportapp.presentation.settings.Gender
import com.example.sportapp.presentation.settings.HealthData
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

class WorkoutLogger(
    private val context: Context,
    private val activityName: String,
    private val healthData: HealthData
) {
    private var file: File? = null
    private var startTime: Long = 0
    
    // Logika przewyższeń
    private var lastAscentRef: Double? = null
    private var lastDescentRef: Double? = null
    private var totalAscent: Double = 0.0
    private var totalDescent: Double = 0.0
    private val ELEVATION_THRESHOLD = 2.0 // Próg zmiany wysokości w metrach

    private val heartRates = mutableListOf<Float>()
    private val logBuffer = mutableListOf<String>()
    private var lastFlushTime: Long = 0

    init {
        val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
        val dateStr = sdf.format(Date())
        val genderStr = if (healthData.gender == Gender.MALE) "M" else "F"
        
        val fileName = "${activityName}_${dateStr}_${genderStr}_${healthData.age}_${healthData.height}_${healthData.weight}_${healthData.stepLength}_${healthData.restingHR}_${healthData.maxHR}.csv"
        
        val activitiesDir = File(context.filesDir, "activities")
        if (!activitiesDir.exists()) activitiesDir.mkdirs()
        
        file = File(activitiesDir, fileName)
        startTime = System.currentTimeMillis()
        lastFlushTime = startTime
        
        writeLine("czas;lat;lon;bpm;srednie_bpm;kroki;kroki_min;kroki_dystans;gps_dystans;predkosc_gps;predkosc_kroki;wysokosc;przewyzszenia_gora;przewyzszenia_dol;kalorie_min;kalorie_suma")
    }

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

    fun logData(
        lat: Double? = null,
        lon: Double? = null,
        bpm: Float? = null,
        kroki: Int? = null,
        gpsDystans: Float? = null,
        predkoscGps: Float? = null,
        wysokosc: Double? = null,
        calorieMin: Double? = null,
        calorieSum: Double? = null,
        forceFlush: Boolean = false
    ) {
        val currentTime = System.currentTimeMillis()
        val durationMillis = currentTime - startTime
        val timeFormatted = String.format(Locale.US, "%02d:%02d:%02d", (durationMillis / 3600000), (durationMillis / 60000) % 60, (durationMillis / 1000) % 60)

        if (bpm != null && bpm > 0) heartRates.add(bpm)
        val avgBpm = if (heartRates.isNotEmpty()) heartRates.average() else null
        val stepsMin = if (kroki != null && kroki > 0 && durationMillis > 0) (kroki.toDouble() / (durationMillis / 60000.0)) else null
        val predkoscKroki = if (stepsMin != null && stepsMin > 0) (stepsMin * healthData.stepLength * 60.0) / 100000.0 else null
        val odlKrokiRounded = (kroki?.times(healthData.stepLength / 100.0))?.roundToInt()
        val gpsDystansRounded = gpsDystans?.roundToInt()

        // Nowa logika przewyższeń
        if (wysokosc != null) {
            if (lastAscentRef == null) lastAscentRef = wysokosc
            if (lastDescentRef == null) lastDescentRef = wysokosc

            // Sprawdzamy wzrost wysokości
            if (wysokosc - lastAscentRef!! >= ELEVATION_THRESHOLD) {
                totalAscent += wysokosc - lastAscentRef!!
                lastAscentRef = wysokosc
                lastDescentRef = wysokosc // Resetujemy punkt odniesienia dla spadku
            }

            // Sprawdzamy spadek wysokości
            if (lastDescentRef!! - wysokosc >= ELEVATION_THRESHOLD) {
                totalDescent += lastDescentRef!! - wysokosc
                lastDescentRef = wysokosc
                lastAscentRef = wysokosc // Resetujemy punkt odniesienia dla wzrostu
            }
        }

        val line = StringBuilder().apply {
            append(timeFormatted).append(";")
            append(formatVal(lat)).append(";")
            append(formatVal(lon)).append(";")
            append(formatVal(bpm?.toInt())).append(";")
            append(formatVal(avgBpm, 1)).append(";")
            append(formatVal(kroki)).append(";")
            append(formatVal(stepsMin, 1)).append(";")
            append(formatVal(odlKrokiRounded)).append(";")
            append(formatVal(gpsDystansRounded)).append(";")
            append(formatVal(predkoscGps, 1)).append(";")
            append(formatVal(predkoscKroki, 1)).append(";")
            append(formatVal(wysokosc, 1)).append(";")
            append(formatVal(totalAscent, 1)).append(";")
            append(formatVal(totalDescent, 1)).append(";")
            append(formatVal(calorieMin, 1)).append(";")
            append(formatVal(calorieSum, 1))
        }.toString()

        logBuffer.add(line)

        if (forceFlush || currentTime - lastFlushTime >= 30000) flush()
    }

    fun flush() {
        if (logBuffer.isEmpty()) return
        try {
            FileOutputStream(file, true).use { fos ->
                logBuffer.forEach { line -> fos.write((line + "\n").toByteArray()) }
            }
            logBuffer.clear()
            lastFlushTime = System.currentTimeMillis()
        } catch (e: Exception) {
            Log.e("WorkoutLogger", "Error flushing buffer", e)
        }
    }

    private fun writeLine(line: String) {
        try {
            FileOutputStream(file, true).use { it.write((line + "\n").toByteArray()) }
        } catch (e: Exception) {
            Log.e("WorkoutLogger", "Error writing line", e)
        }
    }
    
    fun getFinalStats(): Map<String, Any?> {
        flush()
        return mapOf(
            "totalAscent" to totalAscent,
            "totalDescent" to totalDescent,
            "avgBpm" to if (heartRates.isNotEmpty()) heartRates.average() else null
        )
    }
}
