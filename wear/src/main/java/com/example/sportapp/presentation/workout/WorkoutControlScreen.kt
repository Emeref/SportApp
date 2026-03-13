package com.example.sportapp.presentation.workout

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.*
import com.example.sportapp.presentation.sensors.WorkoutStatus
import com.example.sportapp.presentation.settings.HealthData
import com.google.maps.android.compose.MapType

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun WorkoutControlScreen(
    workoutType: String,
    healthData: HealthData,
    sportsConfig: List<SportConfig>,
    onWorkoutFinished: (String, List<Pair<String, String>>) -> Unit
) {
    val currentSport = sportsConfig.find { it.id == workoutType } ?: SportConfig(workoutType, "Sport")
    
    val session = rememberWorkoutSession(
        activityName = currentSport.name,
        healthData = healthData,
        onEndWorkout = { summary ->
            // Budujemy podsumowanie na podstawie aktywnych czujników
            val finalSummary = mutableListOf<Pair<String, String>>()
            
            // Zawsze dodajemy czas trwania na początku
            summary.find { it.first == "Czas trwania" }?.let { finalSummary.add(it) }

            currentSport.sensors.filter { it.isEnabled }.forEach { sensor ->
                val label = getSensorLabel(sensor.id)
                // Pomijamy "Czas trwania" bo dodaliśmy go ręcznie wyżej
                if (label != "Czas trwania") {
                    val value = summary.find { it.first == label }?.second
                    if (value != null) finalSummary.add(label to value)
                }
            }
            onWorkoutFinished(currentSport.name, finalSummary)
        }
    )

    val horizontalPagerState = rememberPagerState(initialPage = 1, pageCount = { 2 })
    val focusRequester = remember { FocusRequester() }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = horizontalPagerState) { hPage ->
            if (hPage == 0) {
                WorkoutControls(
                    status = session.status,
                    onTogglePause = session.togglePause,
                    onEnd = session.endWorkout
                )
            } else {
                // Pionowy pager: Dane / Mapa
                val verticalPagerState = rememberPagerState(pageCount = { 2 })
                VerticalPager(state = verticalPagerState) { vPage ->
                    if (vPage == 0) {
                        DynamicWorkoutScreen(
                            sportConfig = currentSport,
                            heartRate = session.heartRate,
                            stepCount = session.stepCount,
                            distanceMeters = session.distanceState.totalDistance,
                            speedKmH = session.speedKmH,
                            workoutTimerState = session.workoutTimerState,
                            totalCalories = session.totalCalories,
                            altitude = session.altitude
                        )
                    } else {
                        MapScreen(mapType = MapType.NORMAL, focusRequester = focusRequester)
                    }
                }
            }
        }
        
        // Systemowy czas na górze
        TimeText()
    }
}

private fun getSensorLabel(id: String): String = when(id) {
    "hr" -> "Tętno"
    "steps" -> "Kroki"
    "dist_gps" -> "Dystans"
    "dist_steps" -> "Dystans"
    "calories" -> "Kalorie"
    "alt" -> "Wysokość"
    "ascent" -> "Wzniosy"
    "descent" -> "Spadki"
    "speed" -> "Prędkość"
    else -> id
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
                    contentDescription = if (status == WorkoutStatus.PAUSED) "Wznów" else "Pauza",
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
                    contentDescription = "Zakończ",
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
                    contentDescription = "Start",
                    tint = Color.Black
                )
            }
        }
    }
}
