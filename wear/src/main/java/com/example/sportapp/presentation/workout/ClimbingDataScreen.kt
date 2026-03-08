package com.example.sportapp.presentation.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.sportapp.presentation.components.SportDataRow
import com.example.sportapp.presentation.sensors.CalorieCalculator
import com.example.sportapp.presentation.sensors.WorkoutTimerState
import com.example.sportapp.presentation.settings.HealthData
import java.util.*

@Composable
fun ClimbingDataScreen(
    heartRate: Float,
    workoutTimerState: WorkoutTimerState,
    healthData: HealthData,
    totalCalories: Double
) {
    // Chwilowe spalanie (kcal/min) dla modelu HRR
    val currentKcalMin = CalorieCalculator.calculateHRR(heartRate, healthData)

    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CZAS WSPINACZKI",
                    style = MaterialTheme.typography.caption2,
                    color = Color.Gray
                )
                Text(
                    text = workoutTimerState.formattedTime,
                    style = MaterialTheme.typography.title1,
                    color = Color.White,
                    fontSize = 28.sp
                )
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            SportDataRow(
                label = "Spalone kalorie",
                value = String.format(Locale.US, "%.1f kcal", totalCalories),
                color = Color.Magenta,
                isBold = true
            )
        }

        item {
            SportDataRow(
                label = "Prędkość spalania",
                value = String.format(Locale.US, "%.2f kcal/min", currentKcalMin),
                color = Color.Cyan
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            SportDataRow(
                label = "Aktualne tętno",
                value = if (heartRate > 0) "${heartRate.toInt()} BPM" else "-- BPM",
                color = Color.Red
            )
        }
    }
}
