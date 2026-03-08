package com.example.sportapp.presentation.workout

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    var workoutStatus by remember { mutableStateOf(WorkoutStatus.ACTIVE) }
    val horizontalPagerState = rememberPagerState(initialPage = 1, pageCount = { 2 })
    val startTime = remember { Date() }

    // Sensory
    val heartRate = rememberHeartRate()
    val workoutTimerState = rememberWorkoutTimer(workoutStatus)
    val altitude = rememberAltitude()
    
    val calorieTracker = rememberCalorieTracker(
        status = workoutStatus,
        totalSeconds = workoutTimerState.totalSeconds,
        heartRate = heartRate,
        healthData = healthData,
        metValue = 8.0
    )

    // Logger dla CSV
    val logger = remember { WorkoutLogger(context, "Wspinaczka", healthData) }

    // Zapis co sekundę
    LaunchedEffect(workoutTimerState.totalSeconds) {
        if (workoutStatus == WorkoutStatus.ACTIVE) {
            logger.logData(
                bpm = heartRate,
                wysokosc = altitude
            )
        }
    }

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
                        val stats = logger.getFinalStats()
                        val summary = listOf(
                            "Czas trwania" to workoutTimerState.formattedTime,
                            "Średnie tętno" to "${(stats["avgBpm"] as? Double)?.toInt() ?: "--"} BPM",
                            "Przewyższenie +" to String.format(Locale.US, "%.1f m", stats["totalAscent"] as Double),
                            "Przewyższenie -" to String.format(Locale.US, "%.1f m", stats["totalDescent"] as Double),
                            "Kalorie (Keytel)" to String.format(Locale.US, "%.1f kcal", calorieTracker.keytel)
                        )
                        
                        // Zapis podsumowania do pliku zbiorczego
                        SummaryManager.saveSummary(
                            context = context,
                            activityName = "Wspinaczka",
                            startTime = startTime,
                            durationFormatted = workoutTimerState.formattedTime,
                            steps = null,
                            distanceSteps = null,
                            distanceGps = null,
                            avgSpeedSteps = null,
                            avgSpeedGps = null,
                            totalAscent = stats["totalAscent"] as Double,
                            totalDescent = stats["totalDescent"] as Double,
                            avgBpm = stats["avgBpm"] as? Double
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
