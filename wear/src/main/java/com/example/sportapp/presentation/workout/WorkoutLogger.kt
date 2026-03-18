package com.example.sportapp.presentation.workout

import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.SensorConfig
import com.example.sportapp.data.model.WorkoutSensor
import com.example.sportapp.presentation.settings.HealthData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.max
import kotlin.math.roundToInt

class WorkoutLogger(
    private val workoutDao: WorkoutDao,
    private val workoutId: Long,
    private val healthData: HealthData,
    private val sensorConfigs: List<SensorConfig>
) {
    private val heartRates = mutableListOf<Float>()
    private var maxCalorieMin: Double = 0.0

    // Historia kroków do obliczania kadencji z ostatnich 20 sekund
    private val stepHistory = mutableListOf<Pair<Long, Int>>()
    private val CADENCE_WINDOW_MS = 20000L
    
    // Bufor punktów w RAM dla optymalizacji zapisu
    private val pointBuffer = mutableListOf<WorkoutPointEntity>()

    private fun Double?.round(decimals: Int = 2): Double? {
        if (this == null) return null
        return "%.${decimals}f".format(Locale.US, this).toDouble()
    }

    private fun isRecording(sensor: WorkoutSensor): Boolean {
        return sensorConfigs.find { it.sensorId == sensor.id }?.isRecording == true
    }

    fun getAvgBpm(): Int = if (heartRates.isNotEmpty()) heartRates.average().toInt() else 0
    
    fun getMaxCalorieMin(): Double = maxCalorieMin

    suspend fun logData(
        durationSeconds: Long,
        lat: Double? = null,
        lon: Double? = null,
        bpm: Float? = null,
        kroki: Int? = null,
        gpsDystans: Float? = null,
        predkoscGps: Float? = null,
        wysokosc: Double? = null,
        calorieMin: Double? = null,
        calorieSum: Double? = null,
        pressure: Double? = null,
        totalAscent: Double? = null,
        totalDescent: Double? = null
    ): WorkoutPointEntity = withContext(Dispatchers.Default) {
        val h = durationSeconds / 3600
        val m = (durationSeconds % 3600) / 60
        val s = durationSeconds % 60
        val timeFormatted = String.format(Locale.US, "%02d:%02d:%02d", h, m, s)

        if (bpm != null && bpm > 0) heartRates.add(bpm)

        val currentTime = System.currentTimeMillis()
        // Obliczanie stepsMin (kadencji) na podstawie okna 20 sekund
        if (kroki != null) {
            stepHistory.add(currentTime to kroki)
            // Usuwamy wpisy starsze niż 20 sekund
            stepHistory.removeAll { it.first < currentTime - CADENCE_WINDOW_MS }
        }

        val stepsMin = if (stepHistory.size >= 2) {
            val (oldTime, oldSteps) = stepHistory.first()
            val (newTime, newSteps) = stepHistory.last()
            val timeDiffMin = (newTime - oldTime) / 60000.0
            val stepDiff = newSteps - oldSteps
            if (timeDiffMin > 0) (stepDiff / timeDiffMin) else 0.0
        } else if (kroki != null && kroki > 0 && durationSeconds > 0) {
            // Fallback na średnią globalną, jeśli mamy za mało danych w oknie (początek treningu)
            (kroki.toDouble() / (durationSeconds / 60.0))
        } else null

        val predkoscKroki = if (stepsMin != null && stepsMin > 0) (stepsMin * healthData.stepLength * 60.0) / 100000.0 else null
        val odlKrokiRounded = (kroki?.times(healthData.stepLength / 100.0))?.roundToInt()
        val gpsDystansRounded = gpsDystans?.roundToInt()

        if (calorieMin != null) {
            maxCalorieMin = max(maxCalorieMin, calorieMin)
        }

        val point = WorkoutPointEntity(
            workoutId = workoutId,
            time = timeFormatted,
            latitude = if (isRecording(WorkoutSensor.MAP)) lat else null,
            longitude = if (isRecording(WorkoutSensor.MAP)) lon else null,
            bpm = if (isRecording(WorkoutSensor.HEART_RATE)) bpm?.toInt() else null,
            steps = if (isRecording(WorkoutSensor.STEPS)) kroki else null,
            stepsMin = if (isRecording(WorkoutSensor.STEPS_PER_MINUTE)) stepsMin.round(2) else null,
            distanceSteps = if (isRecording(WorkoutSensor.DISTANCE_STEPS)) odlKrokiRounded else null,
            distanceGps = if (isRecording(WorkoutSensor.DISTANCE_GPS)) gpsDystansRounded else null,
            speedGps = if (isRecording(WorkoutSensor.SPEED_GPS)) predkoscGps?.toDouble().round(2) else null,
            speedSteps = if (isRecording(WorkoutSensor.SPEED_STEPS)) predkoscKroki.round(2) else null,
            altitude = if (isRecording(WorkoutSensor.ALTITUDE)) wysokosc.round(2) else null,
            totalAscent = if (isRecording(WorkoutSensor.TOTAL_ASCENT)) totalAscent.round(2) else null,
            totalDescent = if (isRecording(WorkoutSensor.TOTAL_DESCENT)) totalDescent.round(2) else null,
            calorieMin = if (isRecording(WorkoutSensor.CALORIES_PER_MINUTE)) calorieMin.round(2) else null,
            calorieSum = if (isRecording(WorkoutSensor.CALORIES_SUM)) calorieSum.round(2) else null,
            pressure = if (isRecording(WorkoutSensor.PRESSURE)) pressure.round(2) else null
        )
        
        synchronized(pointBuffer) {
            pointBuffer.add(point)
        }
        point
    }

    /**
     * Zapisuje zbuforowane punkty do bazy danych.
     */
    suspend fun flushPoints() = withContext(Dispatchers.IO) {
        val pointsToSave = synchronized(pointBuffer) {
            val list = pointBuffer.toList()
            pointBuffer.clear()
            list
        }
        if (pointsToSave.isNotEmpty()) {
            workoutDao.insertPoints(pointsToSave)
        }
    }
}
