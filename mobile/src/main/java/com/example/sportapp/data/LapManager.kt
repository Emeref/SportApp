package com.example.sportapp.data

import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.WorkoutLap
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LapManager @Inject constructor() {

    /**
     * Oblicza listę odcinków na podstawie listy punktów i zadanego dystansu autolapa.
     * Zawsze przelicza wszystko od początku, aby zapewnić spójność danych i uniknąć "dziur" w numeracji.
     */
    fun processLaps(
        workoutId: Long,
        points: List<WorkoutPointEntity>,
        autoLapDistance: Double,
        existingLaps: List<WorkoutLap> = emptyList() // Parametr zachowany dla kompatybilności, ale ignorowany
    ): List<WorkoutLap> {
        if (points.size < 2 || autoLapDistance <= 0) return emptyList()

        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val resultLaps = mutableListOf<WorkoutLap>()
        
        var currentLapStartIdx = 0
        var lapNumber = 1
        
        // Pobierz dystans początkowy, aby obsłużyć przesunięcia (np. start od 500m w logu)
        val initialDist = points.first().let { it.distanceGps?.toDouble() ?: it.distanceSteps?.toDouble() ?: 0.0 }
        
        // Próg dla pierwszego odcinka to kolejna wielokrotność autoLapDistance
        var nextLapThreshold = ((initialDist / autoLapDistance).toInt() + 1) * autoLapDistance

        for (index in 0 until points.size) {
            val point = points[index]
            val currentTotalDist = point.distanceGps?.toDouble() ?: point.distanceSteps?.toDouble() ?: 0.0
            val isLastPoint = index == points.size - 1
            
            // Warunek zakończenia odcinka: przekroczenie dystansu LUB ostatni punkt trasy
            if (currentTotalDist >= nextLapThreshold || isLastPoint) {
                // Dodajemy odcinek tylko jeśli zawiera jakieś punkty (index > currentLapStartIdx)
                if (index > currentLapStartIdx) {
                    val lapPoints = points.subList(currentLapStartIdx, index + 1)
                    val lap = calculateLapStats(workoutId, lapNumber, currentLapStartIdx, index, lapPoints, timeFormatter)
                    resultLaps.add(lap)
                    
                    // Jeśli to był próg dystansu (a nie tylko koniec trasy), przygotuj kolejny odcinek
                    if (currentTotalDist >= nextLapThreshold && !isLastPoint) {
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
