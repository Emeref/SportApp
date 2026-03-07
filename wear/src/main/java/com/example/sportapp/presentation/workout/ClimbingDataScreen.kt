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
    healthData: HealthData
) {
    // Modele kalorii (kcal/min)
    // Stała MET dla wspinaczki: ok. 8.0
    val keytelKcalMin = CalorieCalculator.calculateKeytel(heartRate, healthData)
    val metKcalMin = CalorieCalculator.calculateMET(8.0, healthData.weight)
    val hrrKcalMin = CalorieCalculator.calculateHRR(heartRate, healthData)

    // Obliczanie całkowitych spalonych kalorii
    // (kcal/min * całkowity czas w sekundach) / 60
    val timeMinutes = workoutTimerState.totalSeconds / 60.0
    val totalKeytel = keytelKcalMin * timeMinutes
    val totalMET = metKcalMin * timeMinutes
    val totalHRR = hrrKcalMin * timeMinutes

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

        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                Text("MODEL", Modifier.weight(1f), style = MaterialTheme.typography.caption3, color = Color.Gray)
                Text("kcal/min", Modifier.weight(1f), style = MaterialTheme.typography.caption3, color = Color.Gray)
                Text("TOTAL", Modifier.weight(1f), style = MaterialTheme.typography.caption3, color = Color.Gray)
            }
        }

        item {
            CalorieRow("Keytel", keytelKcalMin, totalKeytel, Color.Yellow)
        }

        item {
            CalorieRow("MET", metKcalMin, totalMET, Color.Cyan)
        }

        item {
            CalorieRow("HRR", hrrKcalMin, totalHRR, Color.Magenta)
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            SportDataRow(
                label = "Aktualne tętno",
                value = if (heartRate > 0) "${heartRate.toInt()} BPM" else "-- BPM",
                color = Color.Red,
                isBold = true
            )
        }
    }
}

@Composable
fun CalorieRow(label: String, kcalMin: Double, total: Double, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.caption2, color = color)
        Text(String.format(Locale.US, "%.2f", kcalMin), Modifier.weight(1f), style = MaterialTheme.typography.body2)
        Text(String.format(Locale.US, "%.1f", total), Modifier.weight(1f), style = MaterialTheme.typography.body1, color = Color.White)
    }
}
