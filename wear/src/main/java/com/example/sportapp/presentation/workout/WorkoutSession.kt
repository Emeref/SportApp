package com.example.sportapp.presentation.workout

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.sportapp.presentation.sensors.*
import com.example.sportapp.presentation.settings.HealthData
import java.util.*

@Composable
fun rememberWorkoutSession(
    activityName: String,
    healthData: HealthData,
    onEndWorkout: (List<Pair<String, String>>) -> Unit
): WorkoutSessionState {
    val context = LocalContext.current
    var status by remember { mutableStateOf(WorkoutStatus.ACTIVE) }
    val startTime = remember { Date() }
    
    // 1. Sensory
    val heartRate = rememberHeartRate()
    val stepCount = rememberStepCount(status)
    val distanceState = rememberDistance(status)
    val speedKmH = rememberSpeed()
    val workoutTimerState = rememberWorkoutTimer(status)
    val altitude = rememberAltitude()

    // 2. Logika kalorii (Tylko model HRR)
    val totalCalories = rememberCalorieTracker(
        status = status,
        totalSeconds = workoutTimerState.totalSeconds,
        heartRate = heartRate,
        healthData = healthData
    )

    // 3. Logger CSV
    val logger = remember { WorkoutLogger(context, activityName, healthData) }

    // Logowanie co sekundę
    LaunchedEffect(workoutTimerState.totalSeconds) {
        if (status == WorkoutStatus.ACTIVE) {
            val calorieMin = CalorieCalculator.calculateHRR(heartRate, healthData)
            logger.logData(
                lat = distanceState.currentLat,
                lon = distanceState.currentLon,
                bpm = heartRate,
                kroki = stepCount,
                gpsDystans = distanceState.totalDistance,
                predkoscGps = speedKmH,
                wysokosc = altitude,
                calorieMin = calorieMin,
                calorieSum = totalCalories
            )
        }
    }

    // Wymuszenie zapisu przy pauzie
    LaunchedEffect(status) {
        if (status == WorkoutStatus.PAUSED) {
            logger.flush()
        }
    }

    val togglePause = {
        status = if (status == WorkoutStatus.ACTIVE) WorkoutStatus.PAUSED else WorkoutStatus.ACTIVE
    }

    val endWorkout = {
        val stats = logger.getFinalStats()
        val durationHours = workoutTimerState.totalSeconds / 3600.0
        val distanceKm = distanceState.totalDistance / 1000.0
        val distanceStepsMeters = (stepCount * healthData.stepLength / 100.0)
        
        val avgSpeedSteps = if (durationHours > 0) (distanceStepsMeters / 1000.0) / durationHours else 0.0
        val avgSpeedGps = if (durationHours > 0) distanceKm / durationHours else 0.0

        // Przygotowanie danych do podsumowania UI
        val summary = mutableListOf<Pair<String, String>>()
        summary.add("Czas trwania" to workoutTimerState.formattedTime)
        
        if (stepCount > 0) summary.add("Kroki" to "$stepCount")
        if (distanceKm > 0) summary.add("Dystans (GPS)" to String.format(Locale.US, "%.2f km", distanceKm))
        
        val avgBpm = stats["avgBpm"] as? Double
        if (avgBpm != null) summary.add("Średnie tętno" to "${avgBpm.toInt()} BPM")
        
        val totalAscent = stats["totalAscent"] as Double
        if (totalAscent > 0) summary.add("Przewyższenie +" to String.format(Locale.US, "%.1f m", totalAscent))

        if (totalCalories > 0) {
            summary.add("Kalorie" to String.format(Locale.US, "%.1f kcal", totalCalories))
        }

        // Zapis do pliku zbiorczego (CSV)
        SummaryManager.saveSummary(
            context = context,
            activityName = activityName,
            startTime = startTime,
            durationFormatted = workoutTimerState.formattedTime,
            steps = if (stepCount > 0) stepCount else null,
            distanceSteps = if (distanceStepsMeters > 0) distanceStepsMeters else null,
            distanceGps = if (distanceState.totalDistance > 0) distanceState.totalDistance else null,
            avgSpeedSteps = if (avgSpeedSteps > 0) avgSpeedSteps else null,
            avgSpeedGps = if (avgSpeedGps > 0) avgSpeedGps else null,
            totalAscent = totalAscent,
            totalDescent = stats["totalDescent"] as Double,
            avgBpm = avgBpm,
            totalCalories = totalCalories,
            durationSeconds = workoutTimerState.totalSeconds
        )

        onEndWorkout(summary)
    }

    return WorkoutSessionState(
        status = status,
        heartRate = heartRate,
        stepCount = stepCount,
        distanceState = distanceState,
        speedKmH = speedKmH,
        workoutTimerState = workoutTimerState,
        totalCalories = totalCalories,
        togglePause = togglePause,
        endWorkout = endWorkout
    )
}

data class WorkoutSessionState(
    val status: WorkoutStatus,
    val heartRate: Float,
    val stepCount: Int,
    val distanceState: DistanceState,
    val speedKmH: Float,
    val workoutTimerState: WorkoutTimerState,
    val totalCalories: Double,
    val togglePause: () -> Unit,
    val endWorkout: () -> Unit
)
