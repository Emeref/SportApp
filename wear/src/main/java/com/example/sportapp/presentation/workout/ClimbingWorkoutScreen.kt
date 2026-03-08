package com.example.sportapp.presentation.workout

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.*
import com.example.sportapp.presentation.sensors.*
import com.example.sportapp.presentation.settings.HealthData
import java.util.*

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun ClimbingWorkoutScreen(
    clockColor: Color?, 
    healthData: HealthData, 
    onEndWorkout: (List<Pair<String, String>>) -> Unit
) {
    var workoutStatus by remember { mutableStateOf(WorkoutStatus.ACTIVE) }
    val horizontalPagerState = rememberPagerState(initialPage = 1, pageCount = { 2 })

    // Sensory
    val heartRate = rememberHeartRate()
    val workoutTimerState = rememberWorkoutTimer(workoutStatus)
    
    val calorieTracker = rememberCalorieTracker(
        status = workoutStatus,
        totalSeconds = workoutTimerState.totalSeconds,
        heartRate = heartRate,
        healthData = healthData,
        metValue = 8.0
    )

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = horizontalPagerState) { hPage ->
            if (hPage == 0) {
                WorkoutControlScreen(
                    status = workoutStatus,
                    onTogglePause = {
                        workoutStatus = if (workoutStatus == WorkoutStatus.ACTIVE) 
                            WorkoutStatus.PAUSED else WorkoutStatus.ACTIVE
                    },
                    onEnd = {
                        // Przygotowanie danych do podsumowania
                        val summary = listOf(
                            "Czas trwania" to workoutTimerState.formattedTime,
                            "Tętno (ostatnie)" to "${heartRate.toInt()} BPM",
                            "Kalorie (Keytel)" to String.format(Locale.US, "%.1f kcal", calorieTracker.keytel),
                            "Kalorie (MET)" to String.format(Locale.US, "%.1f kcal", calorieTracker.met),
                            "Kalorie (HRR)" to String.format(Locale.US, "%.1f kcal", calorieTracker.hrr)
                        )
                        onEndWorkout(summary)
                    }
                )
            } else {
                ClimbingDataScreen(
                    heartRate = heartRate,
                    workoutTimerState = workoutTimerState,
                    healthData = healthData,
                    calorieTracker = calorieTracker
                )
            }
        }
        
        if (clockColor != null) {
            Box(modifier = Modifier.fillMaxWidth().wrapContentHeight(), contentAlignment = Alignment.TopCenter) {
                TimeText(timeTextStyle = MaterialTheme.typography.caption1.copy(color = clockColor, fontWeight = FontWeight.Bold))
            }
        }
    }
}
