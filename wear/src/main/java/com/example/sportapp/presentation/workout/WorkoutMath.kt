package com.example.sportapp.presentation.workout

import com.example.sportapp.AppConstants
import com.example.sportapp.data.db.WorkoutPointEntity
import kotlin.math.max
import kotlin.math.min

object WorkoutMath {

    data class SessionStats(
        val maxHr: Int = 0,
        val avgHr: Int = 0,
        val avgPace: Double = 0.0,
        val maxSpeed: Double = 0.0,
        val totalAscent: Double = 0.0,
        val totalDescent: Double = 0.0,
        val maxAltitude: Double = 0.0,
        val avgStepLength: Double = 0.0,
        val avgCadence: Double = 0.0,
        val maxCadence: Double = 0.0,
        val maxPressure: Double? = null,
        val minPressure: Double? = null,
        val bestPace1km: Double? = null
    )

    fun calculateSessionStats(
        points: List<WorkoutPointEntity>,
        durationSeconds: Long,
        totalDistanceMeters: Double,
        totalSteps: Int,
        totalDistanceGpsMeters: Double = 0.0
    ): SessionStats {
        if (points.isEmpty()) return SessionStats()

        var maxHr = 0
        var hrSum = 0
        var hrCount = 0
        var maxSpeed = 0.0
        var maxAltitude = -10000.0
        var maxCadence = 0.0
        
        var totalCadenceSum = 0.0
        var cadencePointsCount = 0

        var maxPressureValue = -1.0
        var minPressureValue = 100000.0

        // Elevation logic
        var totalAscent = 0.0
        var totalDescent = 0.0
        var lastAltRef: Double? = null
        val threshold = AppConstants.ELEVATION_THRESHOLD

        points.forEach { point ->
            // Max & Avg HR
            point.bpm?.let { 
                if (it > maxHr) maxHr = it 
                hrSum += it
                hrCount++
            }
            
            // Max Speed (GPS or Steps)
            val speedGps = point.speedGps ?: 0.0
            val speedSteps = point.speedSteps ?: 0.0
            val currentMaxSpeed = max(speedGps, speedSteps)
            if (currentMaxSpeed > maxSpeed) maxSpeed = currentMaxSpeed
            
            // Altitude & Elevation
            point.altitude?.let { alt ->
                if (alt > maxAltitude) maxAltitude = alt
                
                if (lastAltRef == null) {
                    lastAltRef = alt
                } else {
                    val diff = alt - lastAltRef!!
                    if (diff >= threshold) {
                        totalAscent += diff
                        lastAltRef = alt
                    } else if (diff <= -threshold) {
                        totalDescent += Math.abs(diff)
                        lastAltRef = alt
                    }
                }
            }
            
            // Cadence
            point.stepsMin?.let {
                if (it > maxCadence) maxCadence = it
                totalCadenceSum += it
                cadencePointsCount++
            }

            // Pressure
            point.pressure?.let {
                if (it > maxPressureValue) maxPressureValue = it
                if (it < minPressureValue) minPressureValue = it
            }
        }

        // Optimized Best 1km Split logic
        var bestPace1km: Double? = null
        if (points.size > 10) {
            var minTimeFor1km = Int.MAX_VALUE
            val hasGps = points.any { it.distanceGps != null && it.distanceGps!! > 0 }
            
            var right = 0
            for (left in points.indices) {
                val startDist = if (hasGps) (points[left].distanceGps ?: 0).toDouble() else (points[left].distanceSteps ?: 0).toDouble()
                
                while (right < points.size) {
                    val currentDist = if (hasGps) (points[right].distanceGps ?: 0).toDouble() else (points[right].distanceSteps ?: 0).toDouble()
                    if (currentDist - startDist >= 1000.0) {
                        val timeDiff = right - left
                        if (timeDiff < minTimeFor1km) {
                            minTimeFor1km = timeDiff
                        }
                        break
                    }
                    right++
                }
                if (right == points.size) break 
            }
            
            if (minTimeFor1km != Int.MAX_VALUE) {
                bestPace1km = minTimeFor1km / 60.0
            }
        }

        // Pace calculated ONLY based on GPS distance
        val avgPace = if (totalDistanceGpsMeters > 500.0) {
            (durationSeconds / 60.0) / (totalDistanceGpsMeters / 1000.0)
        } else 0.0

        val avgStepLength = if (totalSteps > 0) {
            totalDistanceMeters / totalSteps
        } else 0.0

        val avgCadence = if (cadencePointsCount > 0) {
            totalCadenceSum / cadencePointsCount
        } else 0.0
        
        val avgHr = if (hrCount > 0) hrSum / hrCount else 0

        return SessionStats(
            maxHr = maxHr,
            avgHr = avgHr,
            avgPace = avgPace,
            maxSpeed = maxSpeed,
            totalAscent = totalAscent,
            totalDescent = totalDescent,
            maxAltitude = if (maxAltitude == -10000.0) 0.0 else maxAltitude,
            avgStepLength = avgStepLength,
            avgCadence = avgCadence,
            maxCadence = maxCadence,
            maxPressure = if (maxPressureValue == -1.0) null else maxPressureValue,
            minPressure = if (minPressureValue == 100000.0) null else minPressureValue,
            bestPace1km = bestPace1km
        )
    }
}
