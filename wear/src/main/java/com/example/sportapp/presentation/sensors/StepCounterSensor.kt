package com.example.sportapp.presentation.sensors

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun rememberStepCount(status: WorkoutStatus): Int {
    val context = LocalContext.current
    var stepCount by remember { mutableIntStateOf(0) }
    var initialSteps by remember { mutableIntStateOf(-1) }
    var pausedSteps by remember { mutableIntStateOf(0) }
    
    val permission = Manifest.permission.ACTIVITY_RECOGNITION

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(permission)
        }
    }

    LaunchedEffect(status) {
        if (status == WorkoutStatus.IDLE) {
            stepCount = 0
            initialSteps = -1
            pausedSteps = 0
        }
    }

    if (hasPermission) {
        val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
        val stepSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) }

        DisposableEffect(status) {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let {
                        if (it.sensor.type == Sensor.TYPE_STEP_COUNTER && it.values.isNotEmpty()) {
                            val totalSteps = it.values[0].toInt()
                            
                            if (status == WorkoutStatus.ACTIVE) {
                                if (initialSteps == -1) {
                                    initialSteps = totalSteps
                                }
                                stepCount = (totalSteps - initialSteps) - pausedSteps
                            } else if (status == WorkoutStatus.PAUSED) {
                                // Obliczamy ile kroków przybyło podczas pauzy, aby odjąć je później
                                if (initialSteps != -1) {
                                    val currentWorkoutSteps = totalSteps - initialSteps
                                    pausedSteps = currentWorkoutSteps - stepCount
                                }
                            }
                        }
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            if (status != WorkoutStatus.IDLE) {
                sensorManager.registerListener(listener, stepSensor, SensorManager.SENSOR_DELAY_UI)
            }

            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    return stepCount
}
