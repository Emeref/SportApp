package com.example.sportapp.presentation.workout

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.example.sportapp.presentation.sensors.WorkoutStatus

@Composable
fun WorkoutControlScreen(
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
            // Przycisk PAUZA / START
            Button(
                onClick = onTogglePause,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (status == WorkoutStatus.PAUSED) Color.Green else Color.Yellow
                ),
                modifier = Modifier.size(ButtonDefaults.DefaultButtonSize)
            ) {
                Icon(
                    imageVector = if (status == WorkoutStatus.PAUSED) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (status == WorkoutStatus.PAUSED) "Wznów" else "Pauza",
                    tint = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Przycisk ZAKOŃCZ
            Button(
                onClick = onEnd,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                modifier = Modifier.size(ButtonDefaults.DefaultButtonSize)
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Zakończ",
                    tint = Color.White
                )
            }
        } else {
            // Przycisk START (gdy IDLE)
            Button(
                onClick = onTogglePause,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),
                modifier = Modifier.size(ButtonDefaults.LargeButtonSize)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    tint = Color.Black
                )
            }
        }
    }
}
