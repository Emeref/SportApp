package com.example.sportapp.presentation.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
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

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        sportConfig.rows.forEach { row ->
            item {
                if (row.sensors.size == 1) {
                    DynamicSensorDispatcher(
                        row.sensors[0].id,
                        heartRate,
                        stepCount,
                        distanceMeters,
                        speedKmH,
                        workoutTimerState,
                        totalCalories,
                        altitude
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.sensors.forEach { sensor ->
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                DynamicSensorDispatcher(
                                    sensor.id,
                                    heartRate,
                                    stepCount,
                                    distanceMeters,
                                    speedKmH,
                                    workoutTimerState,
                                    totalCalories,
                                    altitude
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(4.dp)) }
        }
    }
}

@Composable
fun DynamicSensorDispatcher(
    id: String,
    heartRate: Float,
    stepCount: Int,
    distanceMeters: Float,
    speedKmH: Float,
    workoutTimerState: WorkoutTimerState,
    totalCalories: Double,
    altitude: Double
) {
    when (id) {
        "timer" -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("CZAS", style = MaterialTheme.typography.caption2, color = Color.Gray)
                Text(workoutTimerState.formattedTime, style = MaterialTheme.typography.title1, fontSize = 28.sp)
            }
        }
        "hr" -> SportDataRow("Tętno", if (heartRate > 0) "${heartRate.toInt()} BPM" else "-- BPM", Color.Red, true)
        "steps" -> SportDataRow("Kroki", "$stepCount", Color.Green)
        "dist_gps", "dist_steps" -> SportDataRow("Dystans", String.format(Locale.US, "%.2f km", distanceMeters / 1000f), Color.Cyan)
        "speed_gps", "speed_steps" -> SportDataRow("Prędkość", String.format(Locale.US, "%.1f km/h", speedKmH), Color.Yellow)
        "calories" -> SportDataRow("Kalorie", String.format(Locale.US, "%.1f kcal", totalCalories), Color.Magenta)
        "alt" -> SportDataRow("Wysokość", String.format(Locale.US, "%.0f m", altitude), Color.LightGray)
    }
}
