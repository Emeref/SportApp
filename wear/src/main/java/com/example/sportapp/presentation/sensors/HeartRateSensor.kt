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
fun rememberHeartRate(): Float {
    val context = LocalContext.current
    var heartRate by remember { mutableStateOf(0f) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.BODY_SENSORS)
        }
    }

    if (hasPermission) {
        val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
        val heartRateSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE) }

        DisposableEffect(Unit) {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let {
                        if (it.sensor.type == Sensor.TYPE_HEART_RATE && it.values.isNotEmpty()) {
                            heartRate = it.values[0]
                        }
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            sensorManager.registerListener(listener, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)

            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    return heartRate
}
