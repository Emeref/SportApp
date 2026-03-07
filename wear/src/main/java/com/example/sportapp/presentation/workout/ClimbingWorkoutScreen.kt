package com.example.sportapp.presentation.workout

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.wear.compose.material.*
import com.example.sportapp.presentation.sensors.*
import com.example.sportapp.presentation.settings.HealthData

@Composable
fun ClimbingWorkoutScreen(clockColor: Color?, healthData: HealthData) {
    // Sensory
    val heartRate = rememberHeartRate()
    val workoutTimerState = rememberWorkoutTimer()

    Box(modifier = Modifier.fillMaxSize()) {
        // Tylko jeden ekran - brak pagera i kropek
        ClimbingDataScreen(
            heartRate = heartRate,
            workoutTimerState = workoutTimerState,
            healthData = healthData
        )
        
        if (clockColor != null) {
            Box(modifier = Modifier.fillMaxWidth().wrapContentHeight(), contentAlignment = Alignment.TopCenter) {
                TimeText(timeTextStyle = MaterialTheme.typography.caption1.copy(color = clockColor, fontWeight = FontWeight.Bold))
            }
        }
    }
}
