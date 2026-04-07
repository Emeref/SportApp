package com.example.sportapp.data

import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.WorkoutLap
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LapManager @Inject constructor() {

    fun processLaps(
        workoutId: Long,
        points: List<WorkoutPointEntity>,
        autoLapDistance: Double,
        existingLaps: List<WorkoutLap> = emptyList()
    ): List<WorkoutLap> {
        if (points.size < 2 || autoLapDistance <= 0) return existingLaps

        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        
        // Znajdź odcinki, które są "pełne" (osiągnęły wymagany dystans)
        // Ostatni odcinek w existingLaps mógł być "niepełny" (kończący trening), 
        // więc traktujemy go jako do przeliczenia, jeśli przybyło punktów.
        val completedLaps = existingLaps.filter { it.distanceMeters >= autoLapDistance * 0.999 }.toMutableList()
        
        var currentLapStartIdx = 0
        var lapNumber = 1
        
        if (completedLaps.isNotEmpty()) {
            val lastComplete = completedLaps.last()
            currentLapStartIdx = lastComplete.endLocationIndex
            lapNumber = lastComplete.lapNumber + 1
        }
        
        // Jeśli nie ma nowych punktów do przetworzenia w stosunku do ostatniego pełnego odcinka
        if (currentLapStartIdx >= points.size - 1 && completedLaps.size == existingLaps.size && existingLaps.isNotEmpty()) {
            return existingLaps
        }

        val resultLaps = completedLaps.toMutableList()
        
        // Oblicz skumulowany dystans do punktu startowego obecnego odcinka
        val startPoint = points[currentLapStartIdx]
        val startTotalDist = startPoint.distanceGps?.toDouble() ?: startPoint.distanceSteps?.toDouble() ?: 0.0
        
        // Ustal próg dla następnego odcinka (następna wielokrotność autoLapDistance)
        var nextLapThreshold = ((startTotalDist / autoLapDistance).toInt() + 1) * autoLapDistance

        for (index in currentLapStartIdx until points.size) {
            val point = points[index]
            val currentTotalDist = point.distanceGps?.toDouble() ?: point.distanceSteps?.toDouble() ?: 0.0
            val isLastPoint = index == points.size - 1
            
            if (currentTotalDist >= nextLapThreshold || isLastPoint) {
                if (index > currentLapStartIdx) {
                    val lapPoints = points.subList(currentLapStartIdx, index + 1)
                    val lap = calculateLapStats(workoutId, lapNumber, currentLapStartIdx, index, lapPoints, timeFormatter)
                    resultLaps.add(lap)
                    
                    if (currentTotalDist >= nextLapThreshold) {
                        lapNumber++
                        while (nextLapThreshold <= currentTotalDist) {
                            nextLapThreshold += autoLapDistance
                        }
                        currentLapStartIdx = index
                    }
                }
            }
        }

        return resultLaps
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

        val startTime = try { LocalTime.parse(firstPoint.time, timeFormatter) } catch (e: Exception) { LocalTime.MIN }
        val endTime = try { LocalTime.parse(lastPoint.time, timeFormatter) } catch (e: Exception) { LocalTime.MIN }
        
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
