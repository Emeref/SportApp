package com.example.sportapp.presentation.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.sportapp.core.i18n.LocalAppStrings
import com.example.sportapp.presentation.components.SportDataRow
import com.example.sportapp.presentation.sensors.WorkoutTimerState
import java.util.*

@Composable
fun MainDataScreen(
    heartRate: Float,
    stepCount: Int,
    distanceMeters: Float,
    speedKmH: Float,
    workoutTimerState: WorkoutTimerState,
    pressure: Double? = null,
    altitude: Double? = null,
    configFileName: String = "workout_walking.xml"
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val config = remember(configFileName) {
        ActivityConfigParser.parse(context, configFileName)
    }

    val listState = rememberScalingLazyListState()
    
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        config?.rows?.forEach { row ->
            item {
                if (row.sensors.size == 1) {
                    // Jeden czujnik w wierszu
                    SensorDispatcher(row.sensors[0].id, heartRate, stepCount, distanceMeters, speedKmH, workoutTimerState, pressure, altitude)
                } else {
                    // Wiele czujników w wierszu (Row)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.sensors.forEach { sensor ->
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                SensorDispatcher(sensor.id, heartRate, stepCount, distanceMeters, speedKmH, workoutTimerState, pressure, altitude)
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(4.dp)) }
        } ?: item {
            Text(strings.configurationError, color = Color.Red)
        }
    }
}

@Composable
fun SensorDispatcher(
    id: String,
    heartRate: Float,
    stepCount: Int,
    distanceMeters: Float,
    speedKmH: Float,
    workoutTimerState: WorkoutTimerState,
    pressure: Double? = null,
    altitude: Double? = null
) {
    val strings = LocalAppStrings.current
    when (id) {
        "timer" -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(strings.activeTime.uppercase(), style = MaterialTheme.typography.caption2, color = Color.Gray)
                Text(workoutTimerState.formattedTime, style = MaterialTheme.typography.title1, fontSize = 28.sp)
            }
        }
        "steps" -> SportDataRow(strings.steps, "$stepCount", Color.Green)
        "distance" -> SportDataRow(strings.distance, String.format(Locale.US, "%.2f km", distanceMeters / 1000f), Color.Cyan)
        "speed" -> SportDataRow(strings.speed, String.format(Locale.US, "%.1f km/h", speedKmH), Color.Yellow)
        "heart_rate" -> SportDataRow(strings.heartRate, if (heartRate > 0) "${heartRate.toInt()} ${strings.bpmUnit}" else "-- ${strings.bpmUnit}", Color.Red, true)
        "pressure" -> SportDataRow(strings.pressure, if (pressure != null) String.format(Locale.US, "%.1f hPa", pressure) else "-- hPa", Color.LightGray)
        "altitude" -> SportDataRow(strings.altitude, if (altitude != null) "${altitude.toInt()} m" else "-- m", Color.Magenta)
    }
}
