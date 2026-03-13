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
import com.example.sportapp.presentation.settings.HealthData

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun ClimbingWorkoutScreen(
    clockColor: Color?, 
    healthData: HealthData, 
    onEndWorkout: (List<Pair<String, String>>) -> Unit
) {
    val session = rememberWorkoutSession(
        activityName = "Wspinaczka",
        healthData = healthData,
        onEndWorkout = onEndWorkout
    )

    val horizontalPagerState = rememberPagerState(initialPage = 1, pageCount = { 2 })

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = horizontalPagerState) { hPage ->
            if (hPage == 0) {
                WorkoutControls(
                    status = session.status,
                    onTogglePause = session.togglePause,
                    onEnd = session.endWorkout
                )
            } else {
                ClimbingDataScreen(
                    heartRate = session.heartRate,
                    workoutTimerState = session.workoutTimerState,
                    healthData = healthData,
                    totalCalories = session.totalCalories
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
