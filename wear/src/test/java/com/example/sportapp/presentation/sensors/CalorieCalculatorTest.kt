package com.example.sportapp.presentation.sensors

import com.example.sportapp.presentation.settings.Gender
import com.example.sportapp.presentation.settings.HealthData
import org.junit.Assert.assertEquals
import org.junit.Test

class CalorieCalculatorTest {

    private val testHealthData = HealthData(
        gender = Gender.MALE,
        age = 30,
        height = 180,
        weight = 80,
        restingHR = 60,
        maxHR = 190
    )

    @Test
    fun `calculateHRR returns 0 when heart rate is below resting HR`() {
        val result = CalorieCalculator.calculateHRR(50f, testHealthData)
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `calculateHRR returns 0 when heart rate is 0`() {
        val result = CalorieCalculator.calculateHRR(0f, testHealthData)
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `calculateHRR returns positive value for high heart rate`() {
        // intensity = (150 - 60) / (190 - 60) = 90 / 130 = 0.6923
        // estimatedVO2 = (0.6923 * 40.0) + 3.5 = 27.692 + 3.5 = 31.192
        // kcal/min = (31.192 * 80 / 1000) * 5.0 = 2.495 * 5 = 12.477
        val result = CalorieCalculator.calculateHRR(150f, testHealthData)
        assertEquals(12.477, result, 0.01)
    }

    @Test
    fun `calculateHRR returns 0 when weight is 0`() {
        val zeroWeightData = testHealthData.copy(weight = 0)
        val result = CalorieCalculator.calculateHRR(150f, zeroWeightData)
        assertEquals(0.0, result, 0.001)
    }
}
