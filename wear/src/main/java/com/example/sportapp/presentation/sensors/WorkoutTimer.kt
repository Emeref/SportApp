package com.example.sportapp.presentation.sensors

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import java.util.*

enum class WorkoutStatus {
    ACTIVE, PAUSED, IDLE
}

@Composable
fun rememberWorkoutTimer(status: WorkoutStatus): WorkoutTimerState {
    var seconds by remember { mutableLongStateOf(0L) }

    LaunchedEffect(status) {
        if (status == WorkoutStatus.IDLE) {
            seconds = 0L
        }
    }

    LaunchedEffect(status) {
        while (status == WorkoutStatus.ACTIVE) {
            delay(1000)
            seconds++
        }
    }

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    val timeString = if (hours > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, secs)
    }
    
    return WorkoutTimerState(timeString, seconds)
}

data class WorkoutTimerState(
    val formattedTime: String,
    val totalSeconds: Long
)
