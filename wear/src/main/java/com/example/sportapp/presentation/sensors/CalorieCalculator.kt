package com.example.sportapp.presentation.sensors

import androidx.compose.runtime.*
import com.example.sportapp.presentation.settings.HealthData

object CalorieCalculator {

    /**
     * Model HRR (Heart Rate Reserve) - Zaawansowana estymacja.
     * Zwraca spalone kalorie (kcal) na minutę.
     */
    fun calculateHRR(hr: Float, data: HealthData): Double {
        if (hr <= 0 || hr < data.restingHR) return 0.0
        val reserve = data.maxHR - data.restingHR
        if (reserve <= 0) return 0.0
        val intensity = (hr - data.restingHR) / reserve.toDouble()
        val estimatedVO2 = (intensity * 40.0) + 3.5 
        return (estimatedVO2 * data.weight / 1000.0) * 5.0
    }
}

/**
 * Sensor kalorii śledzący spalanie w czasie rzeczywistym (Model HRR).
 * Akumuluje dane co sekundę na podstawie aktualnego tętna.
 */
@Composable
fun rememberCalorieTracker(
    status: WorkoutStatus,
    totalSeconds: Long,
    heartRate: Float,
    healthData: HealthData
): Double {
    var totalCalories by remember { mutableDoubleStateOf(0.0) }

    // Logika akumulacji co sekundę
    LaunchedEffect(totalSeconds) {
        if (status == WorkoutStatus.ACTIVE && totalSeconds > 0) {
            totalCalories += CalorieCalculator.calculateHRR(heartRate, healthData) / 60.0
        }
    }

    // Resetowanie przy stanie IDLE
    LaunchedEffect(status) {
        if (status == WorkoutStatus.IDLE) {
            totalCalories = 0.0
        }
    }

    return totalCalories
}
