package com.example.sportapp.presentation.workout

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.example.sportapp.TextsWearPL
import com.example.sportapp.presentation.sensors.WorkoutStatus
import com.example.sportapp.presentation.settings.HealthData

// This component seems redundant now that we have DynamicWorkoutScreen, 
// but I will fix it to keep the project compilable if it's still used.

@Composable
fun WorkoutControlScreen(
    workoutType: String,
    healthData: HealthData,
    onWorkoutFinished: (String, List<Pair<String, String>>) -> Unit
) {
    // Note: This is a placeholder for backward compatibility
    // In the new system, we should use DynamicWorkoutScreen directly from MainActivity
    
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(TextsWearPL.WORKOUT_READY_MSG)
    }
}

@Composable
fun WorkoutControls(
    status: WorkoutStatus,
    onTogglePause: () -> Unit,
    onEnd: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (status != WorkoutStatus.IDLE) {
            Button(
                onClick = onTogglePause,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (status == WorkoutStatus.PAUSED) Color.Green else Color.Yellow
                ),
                modifier = Modifier.size(ButtonDefaults.DefaultButtonSize)
            ) {
                Icon(
                    imageVector = if (status == WorkoutStatus.PAUSED) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (status == WorkoutStatus.PAUSED) TextsWearPL.WORKOUT_RESUME else TextsWearPL.WORKOUT_PAUSE,
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onEnd,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                modifier = Modifier.size(ButtonDefaults.DefaultButtonSize)
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = TextsWearPL.WORKOUT_FINISH,
                    tint = Color.White
                )
            }
        } else {
            Button(
                onClick = onTogglePause,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),
                modifier = Modifier.size(ButtonDefaults.LargeButtonSize)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = TextsWearPL.WORKOUT_START,
                    tint = Color.Black
                )
            }
        }
    }
}
