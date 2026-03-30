package com.example.sportapp.presentation.workout

import com.example.sportapp.data.db.WorkoutPointEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WorkoutMathTest {

    private fun createPoint(
        bpm: Int? = null,
        distanceGps: Int? = null,
        altitude: Double? = null,
        stepsMin: Double? = null
    ) = WorkoutPointEntity(
        id = 0, workoutId = 0, time = "", latitude = 0.0, longitude = 0.0,
        bpm = bpm, steps = 0, stepsMin = stepsMin, distanceSteps = 0, distanceGps = distanceGps,
        speedGps = 0.0, speedSteps = 0.0, altitude = altitude, totalAscent = 0.0,
        totalDescent = 0.0, calorieMin = 0.0, calorieSum = 0.0
    )

    @Test
    fun `calculateSessionStats returns empty stats for empty points`() {
        val stats = WorkoutMath.calculateSessionStats(
            points = emptyList(),
            durationSeconds = 0,
            totalDistanceMeters = 0.0,
            totalSteps = 0
        )
        assertEquals(0, stats.maxHr)
        assertEquals(0.0, stats.avgPace, 0.001)
    }

    @Test
    fun `calculateSessionStats calculates HR correctly`() {
        val points = listOf(
            createPoint(bpm = 100),
            createPoint(bpm = 120),
            createPoint(bpm = 140)
        )
        val stats = WorkoutMath.calculateSessionStats(
            points = points,
            durationSeconds = 60,
            totalDistanceMeters = 1000.0,
            totalSteps = 1000,
            totalDistanceGpsMeters = 1000.0
        )
        
        assertEquals(140, stats.maxHr)
        assertEquals(120, stats.avgHr)
    }

    @Test
    fun `calculateSessionStats calculates elevation correctly based on threshold`() {
        // AppConstants.ELEVATION_THRESHOLD to 0.5
        val points = listOf(
            createPoint(altitude = 100.0),
            createPoint(altitude = 101.0), // diff 1.0 > 0.5 (ascent +1.0)
            createPoint(altitude = 100.0), // diff -1.0 < -0.5 (descent +1.0)
            createPoint(altitude = 99.0)   // diff -1.0 < -0.5 (descent +1.0)
        )
        
        val stats = WorkoutMath.calculateSessionStats(
            points = points,
            durationSeconds = 60,
            totalDistanceMeters = 1000.0,
            totalSteps = 1000,
            totalDistanceGpsMeters = 1000.0
        )
        
        assertEquals(1.0, stats.totalAscent, 0.1)
        assertEquals(2.0, stats.totalDescent, 0.1)
        assertEquals(101.0, stats.maxAltitude, 0.1)
    }

    @Test
    fun `calculateSessionStats calculates pace only from GPS distance`() {
        // Punkt jest wymagany, aby uniknąć points.isEmpty() return
        val points = listOf(createPoint())
        
        // 1000m w 600s (10 minut) -> Tempo 10:00 min/km
        val stats = WorkoutMath.calculateSessionStats(
            points = points,
            durationSeconds = 600, // 10 min
            totalDistanceMeters = 1500.0, // Dystans całkowity (np. z kroków) - ignorowany przy tempie
            totalSteps = 2000,
            totalDistanceGpsMeters = 1000.0 // Dystans GPS - podstawa tempa
        )
        
        assertEquals(10.0, stats.avgPace, 0.1)
    }

    @Test
    fun `calculateSessionStats best 1km split returns null for short distances`() {
        val points = List(20) { createPoint(distanceGps = it * 10) } // łącznie 200m
        val stats = WorkoutMath.calculateSessionStats(
            points = points,
            durationSeconds = 20,
            totalDistanceMeters = 200.0,
            totalSteps = 200,
            totalDistanceGpsMeters = 200.0
        )
        
        assertNull(stats.bestPace1km)
    }

    @Test
    fun `calculateSessionStats best 1km split calculates correctly`() {
        val points = mutableListOf<WorkoutPointEntity>()
        for (i in 0..1200) {
            points.add(createPoint(distanceGps = i))
        }
        
        val stats = WorkoutMath.calculateSessionStats(
            points = points,
            durationSeconds = 1200,
            totalDistanceMeters = 1200.0,
            totalSteps = 1200,
            totalDistanceGpsMeters = 1200.0
        )
        
        // 1000 sekund / 60 = 16.66 min/km
        assertEquals(16.66, stats.bestPace1km ?: 0.0, 0.1)
    }
}
