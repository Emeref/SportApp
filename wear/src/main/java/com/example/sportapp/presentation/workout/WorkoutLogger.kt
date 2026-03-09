package com.example.sportapp.presentation.workout

import android.content.Context
import android.util.Log
import com.example.sportapp.presentation.settings.Gender
import com.example.sportapp.presentation.settings.HealthData
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

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
    
    // Bufor w RAMie
    private val logBuffer = mutableListOf<String>()
    private var lastFlushTime: Long = 0

    init {
        val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
        val dateStr = sdf.format(Date())
        val genderStr = if (healthData.gender == Gender.MALE) "M" else "F"
        
        val fileName = "${activityName}_${dateStr}_${genderStr}_${healthData.age}_${healthData.height}_${healthData.weight}_${healthData.stepLength}_${healthData.restingHR}_${healthData.maxHR}.csv"
        
        val activitiesDir = File(context.filesDir, "activities")
        if (!activitiesDir.exists()) {
            activitiesDir.mkdirs()
        }
        
        file = File(activitiesDir, fileName)
        startTime = System.currentTimeMillis()
        lastFlushTime = startTime
        
        // Nagłówek zapisujemy od razu - ZAKTUALIZOWANE NAZWY I NOWA KOLUMNA
        writeLine("czas;lat;lon;bpm;srednie_bpm;kroki;kroki_min;kroki_dystans;gps_dystans;predkosc_gps;predkosc_kroki;wysokosc;przewyzszenia_gora;przewyzszenia_dol")
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
        forceFlush: Boolean = false
    ) {
        val currentTime = System.currentTimeMillis()
        val durationMillis = currentTime - startTime
        val seconds = (durationMillis / 1000) % 60
        val minutes = (durationMillis / (1000 * 60)) % 60
        val hours = (durationMillis / (1000 * 60 * 60))
        val timeFormatted = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

        if (bpm != null && bpm > 0) heartRates.add(bpm)
        val avgBpm = if (heartRates.isNotEmpty()) heartRates.average() else null

        val stepsMin = if (kroki != null && kroki > 0 && durationMillis > 0) {
            (kroki.toDouble() / (durationMillis / 60000.0))
        } else null

        // Prędkość z kroków (km/h)
        // kroki_min * stepLength (cm) = cm/min
        // (cm/min * 60) / 100,000 = km/h
        val predkoscKroki = if (stepsMin != null && stepsMin > 0) {
            (stepsMin * healthData.stepLength * 60.0) / 100000.0
        } else null

        // Obliczamy faktyczną odległość z kroków, ale do pliku zapisujemy zaokrąglone pełne metry
        val odlKrokiActual = if (kroki != null && kroki > 0) {
            (kroki * healthData.stepLength / 100.0)
        } else null
        val odlKrokiRounded = odlKrokiActual?.roundToInt()

        // Dla GPS dystans jest już przekazywany w metrach, zaokrąglamy go
        val gpsDystansRounded = gpsDystans?.roundToInt()

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
            append(formatVal(lat)).append(";")
            append(formatVal(lon)).append(";")
            append(formatVal(bpm?.toInt())).append(";")
            append(formatVal(avgBpm, 1)).append(";")
            append(formatVal(kroki)).append(";")
            append(formatVal(stepsMin, 1)).append(";")
            append(formatVal(odlKrokiRounded)).append(";") // Zmiana nazwy w nagłówku na kroki_dystans
            append(formatVal(gpsDystansRounded)).append(";")
            append(formatVal(predkoscGps, 1)).append(";") // Zmiana nazwy w nagłówku na predkosc_gps
            append(formatVal(predkoscKroki, 1)).append(";") // Nowa kolumna predkosc_kroki
            append(formatVal(wysokosc, 1)).append(";")
            append(formatVal(totalAscent, 1)).append(";")
            append(formatVal(totalDescent, 1))
        }.toString()

        // Dodaj do bufora zamiast pisać do pliku
        logBuffer.add(line)

        // Zapisuj co 30 sekund lub gdy wymuszono (pauza/koniec)
        if (forceFlush || currentTime - lastFlushTime >= 30000) {
            flush()
        }
    }

    fun flush() {
        if (logBuffer.isEmpty()) return
        
        try {
            val fos = FileOutputStream(file, true)
            logBuffer.forEach { line ->
                fos.write((line + "\n").toByteArray())
            }
            fos.close()
            logBuffer.clear()
            lastFlushTime = System.currentTimeMillis()
            Log.d("WorkoutLogger", "Buffer flushed to file")
        } catch (e: Exception) {
            Log.e("WorkoutLogger", "Error flushing buffer to file", e)
        }
    }

    private fun writeLine(line: String) {
        try {
            val fos = FileOutputStream(file, true)
            fos.write((line + "\n").toByteArray())
            fos.close()
        } catch (e: Exception) {
            Log.e("WorkoutLogger", "Error writing line to file", e)
        }
    }
    
    fun getFinalStats(): Map<String, Any?> {
        flush() // Upewnij się, że wszystko jest zapisane przed końcem
        return mapOf(
            "totalAscent" to totalAscent,
            "totalDescent" to totalDescent,
            "avgBpm" to if (heartRates.isNotEmpty()) heartRates.average() else null
        )
    }
}
