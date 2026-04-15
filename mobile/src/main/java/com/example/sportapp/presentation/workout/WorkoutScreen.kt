package com.example.sportapp.presentation.workout

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.data.WorkoutTrackingService
import com.example.sportapp.data.model.WorkoutSensor
import com.example.sportapp.presentation.home.StatCard
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel,
    trackingService: WorkoutTrackingService?,
    onBack: () -> Unit
) {
    val texts = LocalMobileTexts.current
    val definition by viewModel.selectedDefinition.collectAsState()
    val isTracking by viewModel.isTracking.collectAsState()
    val trackingStats by (trackingService?.trackingState?.collectAsState() ?: remember { mutableStateOf(null) })
    val currentLocation by (trackingService?.currentLocation?.collectAsState() ?: remember { mutableStateOf(null) })

    if (definition == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF80CBC4))
        }
        return
    }

    val recordLocation = definition?.sensors?.find { it.sensorId == WorkoutSensor.MAP.id }?.isRecording == true

    Scaffold(
        bottomBar = {
            WorkoutControls(
                isTracking = isTracking,
                onStart = { viewModel.startWorkout() },
                onPause = { viewModel.pauseWorkout() },
                onResume = { viewModel.resumeWorkout() },
                onStop = { viewModel.stopWorkout() }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (recordLocation) {
                Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.75f)) {
                    val cameraPositionState = rememberCameraPositionState()
                    LaunchedEffect(currentLocation) {
                        currentLocation?.let {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                        }
                    }
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        currentLocation?.let {
                            Marker(state = MarkerState(position = LatLng(it.latitude, it.longitude)))
                        }
                    }
                }
                
                // 4 pierwsze widgety pod mapą
                val activeSensors = definition?.sensors?.filter { it.isVisible && it.sensorId != WorkoutSensor.MAP.id }?.take(4) ?: emptyList()
                LazyColumn(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    items(activeSensors.chunked(2)) { rowItems ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowItems.forEach { sensor ->
                                WorkoutStatWidget(sensor.sensorId, trackingStats, Modifier.weight(1f))
                            }
                            if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            } else {
                // Lista wszystkich widgetów
                val activeSensors = definition?.sensors?.filter { it.isVisible } ?: emptyList()
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(activeSensors.chunked(2)) { rowItems ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowItems.forEach { sensor ->
                                WorkoutStatWidget(sensor.sensorId, trackingStats, Modifier.weight(1f))
                            }
                            if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutStatWidget(sensorId: String, stats: com.example.sportapp.data.TrackingStats?, modifier: Modifier) {
    val texts = LocalMobileTexts.current
    val label = texts.getSensorLabel(sensorId)
    val value = when (sensorId) {
        WorkoutSensor.DISTANCE_GPS.id -> String.format(Locale.US, "%.2f km", (stats?.distanceMeters ?: 0.0) / 1000.0)
        "duration" -> formatDuration(stats?.durationSeconds ?: 0L)
        WorkoutSensor.SPEED_GPS.id -> String.format(Locale.US, "%.1f km/h", stats?.currentSpeedKmH ?: 0.0)
        WorkoutSensor.ALTITUDE.id -> String.format(Locale.US, "%.0f m", stats?.currentAltitude ?: 0.0)
        else -> "--"
    }
    StatCard(modifier, label, value)
}

@Composable
fun WorkoutControls(
    isTracking: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    val texts = LocalMobileTexts.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { /* Lock */ }) { Text("Zablokuj") }
        
        if (!isTracking) {
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green, contentColor = Color.Black)
            ) {
                Text("Start")
            }
        } else {
            Button(
                onClick = onStop,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(texts.DEF_FINISH)
            }
        }
        
        Button(onClick = onPause) { Text("Pauza") }
    }
}

private fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
    else String.format(Locale.US, "%02d:%02d", m, s)
}
