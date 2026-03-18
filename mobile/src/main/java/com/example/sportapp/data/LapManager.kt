package com.example.sportapp.data

import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.WorkoutLap
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LapManager @Inject constructor() {

    fun processLaps(workoutId: Long, points: List<WorkoutPointEntity>, autoLapDistance: Double): List<WorkoutLap> {
        if (points.isEmpty() || autoLapDistance <= 0) return emptyList()

        val laps = mutableListOf<WorkoutLap>()
        var currentLapStartIdx = 0
        var lapNumber = 1
        var nextLapThreshold = autoLapDistance

        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

        points.forEachIndexed { index, point ->
            val currentDistance = point.distanceGps?.toDouble() ?: point.distanceSteps?.toDouble() ?: 0.0
            
            if (currentDistance >= nextLapThreshold || index == points.size - 1) {
                val lapPoints = points.subList(currentLapStartIdx, index + 1)
                if (lapPoints.isNotEmpty()) {
                    laps.add(calculateLapStats(workoutId, lapNumber, currentLapStartIdx, index, lapPoints, timeFormatter))
                    lapNumber++
                    nextLapThreshold += autoLapDistance
                    currentLapStartIdx = index + 1
                }
            }
        }

        return laps
    }

    private fun calculateLapStats(
        workoutId: Long,
        lapNumber: Int,
        startIdx: Int,
        endIdx: Int,
        points: List<WorkoutPointEntity>,
        timeFormatter: DateTimeFormatter
    ): WorkoutLap {
        val firstPoint = points.first()
        val lastPoint = points.last()

        val startTime = LocalTime.parse(firstPoint.time, timeFormatter)
        val endTime = LocalTime.parse(lastPoint.time, timeFormatter)
        
        val durationMillis = java.time.Duration.between(startTime, endTime).toMillis()
        
        val startDist = firstPoint.distanceGps?.toDouble() ?: firstPoint.distanceSteps?.toDouble() ?: 0.0
        val endDist = lastPoint.distanceGps?.toDouble() ?: lastPoint.distanceSteps?.toDouble() ?: 0.0
        val lapDistance = endDist - startDist

        val heartRates = points.mapNotNull { it.bpm }
        val avgHeartRate = if (heartRates.isNotEmpty()) heartRates.average().toInt() else 0
        val maxHeartRate = if (heartRates.isNotEmpty()) heartRates.maxOrNull() ?: 0 else 0

        val speeds = points.mapNotNull { it.speedGps ?: it.speedSteps }
        val avgSpeed = if (speeds.isNotEmpty()) speeds.average() else 0.0
        val maxSpeed = if (speeds.isNotEmpty()) speeds.maxOrNull() ?: 0.0 else 0.0

        val avgPaceSecondsPerKm = if (lapDistance > 0) {
            val pace = (durationMillis / 1000.0) / (lapDistance / 1000.0)
            pace.toInt()
        } else 0

        val startAscent = firstPoint.totalAscent ?: 0.0
        val endAscent = lastPoint.totalAscent ?: 0.0
        val startDescent = firstPoint.totalDescent ?: 0.0
        val endDescent = lastPoint.totalDescent ?: 0.0

        return WorkoutLap(
            workoutId = workoutId,
            lapNumber = lapNumber,
            durationMillis = durationMillis,
            distanceMeters = lapDistance,
            avgPaceSecondsPerKm = avgPaceSecondsPerKm,
            avgSpeed = avgSpeed,
            maxSpeed = maxSpeed,
            avgHeartRate = avgHeartRate,
            maxHeartRate = maxHeartRate,
            totalAscent = endAscent - startAscent,
            totalDescent = endDescent - startDescent,
            startLocationIndex = startIdx,
            endLocationIndex = endIdx
        )
    }
}
