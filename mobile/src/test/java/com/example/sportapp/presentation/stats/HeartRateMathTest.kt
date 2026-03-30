package com.example.sportapp.presentation.stats

import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.HeartRateZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class HeartRateMathTest {

    private fun createPoint(bpm: Int?) = WorkoutPointEntity(
        id = 0, workoutId = 0, time = "", latitude = 0.0, longitude = 0.0,
        bpm = bpm, steps = 0, stepsMin = 0.0, distanceSteps = 0, distanceGps = 0,
        speedGps = 0.0, speedSteps = 0.0, altitude = 0.0, totalAscent = 0.0,
        totalDescent = 0.0, calorieMin = 0.0, calorieSum = 0.0
    )

    @Test
    fun `calculateZones returns error message when points list is empty`() {
        // Given
        val points = emptyList<WorkoutPointEntity>()
        val maxHr = 190

        // When
        val result = HeartRateMath.calculateZones(points, maxHr)

        // Then
        assertEquals("Brak danych tętna", result.trainingEffect)
        assertEquals(0, result.zones.size)
    }

    @Test
    fun `calculateZones returns error message when maxHr is invalid`() {
        // Given
        val points = listOf(createPoint(120))
        val maxHr = 0

        // When
        val result = HeartRateMath.calculateZones(points, maxHr)

        // Then
        assertEquals("Brak danych tętna", result.trainingEffect)
    }

    @Test
    fun `calculateZones correctly filters heart rate using moving average`() {
        // Given
        val points = listOf(
            createPoint(100),
            createPoint(110),
            createPoint(120), // Avg of [100, 110, 120] = 110
            createPoint(130)  // Avg of [110, 120, 130] = 120
        )
        val maxHr = 200

        // When
        val result = HeartRateMath.calculateZones(points, maxHr)

        // Then
        // Total points processed by windowed(3,1) is 2.
        val totalSeconds = result.zones.sumOf { it.durationSeconds }
        assertEquals(2L, totalSeconds)
    }

    @Test
    fun `calculateZones identifies dominant zone correctly`() {
        // Given
        // Zone 2 for maxHr 200 is 120-140 bpm (60-70%)
        // We create points that average out to Zone 2
        val points = mutableListOf<WorkoutPointEntity>()
        repeat(12) { points.add(createPoint(130)) }
        
        val maxHr = 200

        // When
        val result = HeartRateMath.calculateZones(points, maxHr)

        // Then
        assertEquals(HeartRateZone.Z2, result.dominantZone)
        assertNotNull(result.zones.find { it.zone == HeartRateZone.Z2 })
        // With 12 points and window 3, we get 10 averaged points.
        assertEquals(10L, result.zones.find { it.zone == HeartRateZone.Z2 }?.durationSeconds)
    }
}
