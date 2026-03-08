package com.example.sportapp.presentation.sensors

import androidx.compose.runtime.*
import com.example.sportapp.presentation.settings.Gender
import com.example.sportapp.presentation.settings.HealthData

object CalorieCalculator {

    /**
     * Model Keytel (2005) - oparty na HR, wieku, wadze i płci.
     * Zwraca spalone kalorie (kcal) na minutę.
     */
    fun calculateKeytel(hr: Float, data: HealthData): Double {
        if (hr <= 0) return 0.0
        val kjPerMin = if (data.gender == Gender.MALE) {
            (-55.0969 + (0.6309 * hr) + (0.1988 * data.weight) + (0.2017 * data.age))
        } else {
            (-20.4022 + (0.4472 * hr) - (0.1263 * data.weight) + (0.074 * data.age))
        }
        return kjPerMin / 4.184
    }

    /**
     * Model MET (Metabolic Equivalent of Task).
     * Zwraca spalone kalorie (kcal) na minutę.
     */
    fun calculateMET(met: Double, weight: Int): Double {
        return (met * weight) / 60.0
    }

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
 * Komponent śledzący kalorie w czasie rzeczywistym.
 * Akumuluje dane co sekundę na podstawie aktualnego tętna.
 */
@Composable
fun rememberCalorieTracker(
    status: WorkoutStatus,
    totalSeconds: Long,
    heartRate: Float,
    healthData: HealthData,
    metValue: Double
): CalorieTrackerState {
    var totalKeytel by remember { mutableDoubleStateOf(0.0) }
    var totalMET by remember { mutableDoubleStateOf(0.0) }
    var totalHRR by remember { mutableDoubleStateOf(0.0) }

    // Logika akumulacji co sekundę
    LaunchedEffect(totalSeconds) {
        if (status == WorkoutStatus.ACTIVE && totalSeconds > 0) {
            totalKeytel += CalorieCalculator.calculateKeytel(heartRate, healthData) / 60.0
            totalMET += CalorieCalculator.calculateMET(metValue, healthData.weight) / 60.0
            totalHRR += CalorieCalculator.calculateHRR(heartRate, healthData) / 60.0
        }
    }

    // Resetowanie przy stanie IDLE
    LaunchedEffect(status) {
        if (status == WorkoutStatus.IDLE) {
            totalKeytel = 0.0
            totalMET = 0.0
            totalHRR = 0.0
        }
    }

    return CalorieTrackerState(totalKeytel, totalMET, totalHRR)
}

data class CalorieTrackerState(
    val keytel: Double,
    val met: Double,
    val hrr: Double
)
