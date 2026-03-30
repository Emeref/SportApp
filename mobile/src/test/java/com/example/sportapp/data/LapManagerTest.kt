package com.example.sportapp.data

import com.example.sportapp.data.db.WorkoutPointEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LapManagerTest {

    private val lapManager = LapManager()

    private fun createPoint(time: String, distance: Int, bpm: Int? = null) = WorkoutPointEntity(
        id = 0, 
        workoutId = 1, 
        time = time, 
        latitude = 0.0, 
        longitude = 0.0,
        bpm = bpm, 
        steps = 0, 
        stepsMin = 0.0, 
        distanceSteps = distance, 
        distanceGps = distance,
        speedGps = 0.0, 
        speedSteps = 0.0, 
        altitude = 0.0, 
        totalAscent = 0.0,
        totalDescent = 0.0, 
        calorieMin = 0.0, 
        calorieSum = 0.0
    )

    @Test
    fun `processLaps returns empty list for empty points`() {
        val result = lapManager.processLaps(1L, emptyList(), 1000.0)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `processLaps creates laps based on distance threshold`() {
        // Given: distance 0, 1001, 2001
        // Threshold is 1000.
        // First point at 0.
        // Second point at 1001. Threshold 1000 is met. Lap 1: 0 to 1001. Dist = 1001.
        // Third point at 2001. Threshold 2000 is met. Lap 2: 1001 to 2001. Dist = 1000.
        val points = listOf(
            createPoint("10:00:00", 0),
            createPoint("10:05:00", 1001),
            createPoint("10:10:00", 2001)
        )
        val autoLapDistance = 1000.0

        // When
        val result = lapManager.processLaps(1L, points, autoLapDistance)

        // Then
        assertEquals(2, result.size)
        assertEquals(1, result[0].lapNumber)
        assertEquals(1001.0, result[0].distanceMeters, 0.1)
        assertEquals(2, result[1].lapNumber)
        assertEquals(1000.0, result[1].distanceMeters, 0.1)
    }

    @Test
    fun `processLaps calculates lap duration correctly`() {
        val points = listOf(
            createPoint("10:00:00", 0),
            createPoint("10:01:40", 1001)
        )
        val result = lapManager.processLaps(1L, points, 1000.0)

        assertEquals(1, result.size)
        // 1 minute 40 seconds = 100 seconds = 100 000 millis
        assertEquals(100000L, result[0].durationMillis)
    }

    @Test
    fun `processLaps handles partial last lap`() {
        // Point 0: 0m
        // Point 1: 1001m -> Triggers Lap 1 (0 to 1001). Next start index = 1.
        // Point 2: 1201m -> Last point. Triggers Lap 2 (1001 to 1201). Dist = 200.
        val points = listOf(
            createPoint("10:00:00", 0),
            createPoint("10:05:00", 1001),
            createPoint("10:06:00", 1201)
        )
        val result = lapManager.processLaps(1L, points, 1000.0)

        assertEquals(2, result.size)
        assertEquals(1001.0, result[0].distanceMeters, 0.1)
        assertEquals(200.0, result[1].distanceMeters, 0.1)
    }
}
