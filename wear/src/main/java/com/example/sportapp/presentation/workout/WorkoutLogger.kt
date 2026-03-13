package com.example.sportapp.presentation.workout

import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.presentation.settings.HealthData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.max
import kotlin.math.roundToInt

class WorkoutLogger(
    private val workoutDao: WorkoutDao,
    private val workoutId: Long,
    private val healthData: HealthData
) {
    private var startTime: Long = System.currentTimeMillis()
    
    // Logika przewyższeń
    private var lastAscentRef: Double? = null
    private var lastDescentRef: Double? = null
    private var totalAscent: Double = 0.0
    private var totalDescent: Double = 0.0
    private val ELEVATION_THRESHOLD = 2.0 // Próg zmiany wysokości w metrach

    private val heartRates = mutableListOf<Float>()
    private var maxCalorieMin: Double = 0.0

    suspend fun logData(
        lat: Double? = null,
        lon: Double? = null,
        bpm: Float? = null,
        kroki: Int? = null,
        gpsDystans: Float? = null,
        predkoscGps: Float? = null,
        wysokosc: Double? = null,
        calorieMin: Double? = null,
        calorieSum: Double? = null
    ) = withContext(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        val durationMillis = currentTime - startTime
        val timeFormatted = String.format(Locale.US, "%02d:%02d:%02d", (durationMillis / 3600000), (durationMillis / 60000) % 60, (durationMillis / 1000) % 60)

        if (bpm != null && bpm > 0) heartRates.add(bpm)
        val avgBpm = if (heartRates.isNotEmpty()) heartRates.average() else null
        val stepsMin = if (kroki != null && kroki > 0 && durationMillis > 0) (kroki.toDouble() / (durationMillis / 60000.0)) else null
        val predkoscKroki = if (stepsMin != null && stepsMin > 0) (stepsMin * healthData.stepLength * 60.0) / 100000.0 else null
        val odlKrokiRounded = (kroki?.times(healthData.stepLength / 100.0))?.roundToInt()
        val gpsDystansRounded = gpsDystans?.roundToInt()

        if (calorieMin != null) {
            maxCalorieMin = max(maxCalorieMin, calorieMin)
        }

        // Logika przewyższeń
        if (wysokosc != null) {
            if (lastAscentRef == null) lastAscentRef = wysokosc
            if (lastDescentRef == null) lastDescentRef = wysokosc

            if (wysokosc - lastAscentRef!! >= ELEVATION_THRESHOLD) {
                totalAscent += wysokosc - lastAscentRef!!
                lastAscentRef = wysokosc
                lastDescentRef = wysokosc
            }

            if (lastDescentRef!! - wysokosc >= ELEVATION_THRESHOLD) {
                totalDescent += lastDescentRef!! - wysokosc
                lastDescentRef = wysokosc
                lastAscentRef = wysokosc
            }
        }

        val point = WorkoutPointEntity(
            workoutId = workoutId,
            time = timeFormatted,
            latitude = lat,
            longitude = lon,
            bpm = bpm?.toInt(),
            avgBpm = avgBpm,
            steps = kroki,
            stepsMin = stepsMin,
            distanceSteps = odlKrokiRounded,
            distanceGps = gpsDystansRounded,
            speedGps = predkoscGps?.toDouble(),
            speedSteps = predkoscKroki,
            altitude = wysokosc,
            totalAscent = totalAscent,
            totalDescent = totalDescent,
            calorieMin = calorieMin,
            calorieSum = calorieSum
        )
        
        workoutDao.insertPoint(point)
    }

    suspend fun getFinalStats(): Map<String, Any?> {
        return mapOf(
            "totalAscent" to totalAscent,
            "totalDescent" to totalDescent,
            "avgBpm" to if (heartRates.isNotEmpty()) heartRates.average() else null,
            "maxCalorieMin" to maxCalorieMin,
            "maxBpm" to if (heartRates.isNotEmpty()) heartRates.maxOrNull()?.toInt() else null
        )
    }
}
