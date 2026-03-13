package com.example.sportapp.presentation.workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.sportapp.R
import com.example.sportapp.presentation.components.SportDataRow
import com.example.sportapp.presentation.sensors.WorkoutTimerState
import java.util.*

@Composable
fun DynamicWorkoutScreen(
    sportConfig: SportConfig,
    heartRate: Float,
    stepCount: Int,
    distanceMeters: Float,
    speedKmH: Float,
    workoutTimerState: WorkoutTimerState,
    totalCalories: Double,
    altitude: Double
) {
    val listState = rememberScalingLazyListState()
    // Filtrujemy sensory, ale pomijamy 'map', bo mapa jest obsługiwana przez Pager, a nie w liście danych
    val enabledSensors = sportConfig.sensors.filter { it.isEnabled && it.id != "map" }
    
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo na samej górze
        item {
            Image(
                painter = painterResource(id = R.drawable.logo_emeref),
                contentDescription = "Logo Emeref",
                modifier = Modifier.size(40.dp).padding(bottom = 8.dp)
            )
        }

        // 1. Czas aktywności
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CZAS AKTYWNOŚCI",
                    style = MaterialTheme.typography.caption2,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = workoutTimerState.formattedTime,
                    style = MaterialTheme.typography.title1,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 2. Czujniki według konfiguracji
        if (enabledSensors.size <= 3) {
            // Jeden czujnik na wiersz
            enabledSensors.forEach { sensor ->
                item {
                    Box(modifier = Modifier.padding(vertical = 4.dp)) {
                        SensorItemDispatcher(sensor.id, heartRate, stepCount, distanceMeters, speedKmH, totalCalories, altitude, workoutTimerState)
                    }
                }
            }
        } else {
            // Po dwa czujniki na wiersz
            val chunks = enabledSensors.chunked(2)
            chunks.forEach { chunk ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        chunk.forEach { sensor ->
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                SensorItemDispatcher(sensor.id, heartRate, stepCount, distanceMeters, speedKmH, totalCalories, altitude, workoutTimerState)
                            }
                        }
                        if (chunk.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SensorItemDispatcher(
    id: String,
    heartRate: Float,
    stepCount: Int,
    distanceMeters: Float,
    speedKmH: Float,
    totalCalories: Double,
    altitude: Double,
    timerState: WorkoutTimerState
) {
    val minutes = if (timerState.totalSeconds > 0) timerState.totalSeconds / 60.0 else 1.0
    
    when (id) {
        "hr" -> SportDataRow("Tętno", if (heartRate > 0) "${heartRate.toInt()} BPM" else "--", Color.Red, true)
        "steps" -> SportDataRow("Kroki", "$stepCount", Color.Green)
        "dist_gps", "dist_steps" -> {
            val label = if (id == "dist_gps") "Dystans (GPS)" else "Dystans (K)"
            SportDataRow(label, String.format(Locale.US, "%.2f km", distanceMeters / 1000f), Color.Cyan)
        }
        "calories" -> SportDataRow("Kalorie", String.format(Locale.US, "%.0f kcal", totalCalories), Color.Magenta)
        "alt" -> SportDataRow("Wysokość", String.format(Locale.US, "%.0f m", altitude), Color.Yellow)
        "ascent" -> SportDataRow("Wzniosy", "-- m", Color.Yellow) // Do zaimplementowania w WorkoutService
        "descent" -> SportDataRow("Spadki", "-- m", Color.Yellow) // Do zaimplementowania w WorkoutService
        "speed_gps" -> SportDataRow("Prędkość (G)", String.format(Locale.US, "%.1f km/h", speedKmH), Color.Yellow)
        "speed_steps" -> SportDataRow("Prędkość (K)", "-- km/h", Color.Yellow) // Do wyliczenia z krokomierza
        "kcal_min" -> SportDataRow("kcal/min", String.format(Locale.US, "%.1f", totalCalories / minutes), Color.Magenta)
        "steps_min" -> SportDataRow("kroki/min", String.format(Locale.US, "%.0f", stepCount / minutes), Color.Green)
        else -> SportDataRow(id, "--", Color.Gray)
    }
}
