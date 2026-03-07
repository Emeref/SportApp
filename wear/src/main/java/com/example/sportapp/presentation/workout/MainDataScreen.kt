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
fun MainDataScreen(
    heartRate: Float,
    stepCount: Int,
    distanceMeters: Float,
    speedKmH: Float,
    workoutTimerState: WorkoutTimerState
) {
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
                    text = "CZAS AKTYWNOŚCI",
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
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SportDataRow(
                    label = "Kroki",
                    value = "$stepCount",
                    color = Color.Green
                )
                val distanceKm = distanceMeters / 1000f
                SportDataRow(
                    label = "Dystans",
                    value = String.format(Locale.US, "%.2f km", distanceKm),
                    color = Color.Cyan
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SportDataRow(
                    label = "Prędkość",
                    value = String.format(Locale.US, "%.1f km/h", speedKmH),
                    color = Color.Yellow
                )
                SportDataRow(
                    label = "Tętno",
                    value = if (heartRate > 0) "${heartRate.toInt()} BPM" else "-- BPM",
                    color = Color.Red,
                    isBold = true
                )
            }
        }
    }
}
