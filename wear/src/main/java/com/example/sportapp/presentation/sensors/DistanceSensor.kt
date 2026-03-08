package com.example.sportapp.presentation.sensors

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

data class DistanceState(
    val totalDistance: Float = 0f,
    val currentLat: Double? = null,
    val currentLon: Double? = null
)

@SuppressLint("MissingPermission")
@Composable
fun rememberDistance(status: WorkoutStatus): DistanceState {
    val context = LocalContext.current
    var totalDistance by remember { mutableFloatStateOf(0f) }
    var lastLocation by remember { mutableStateOf<Location?>(null) }
    var currentLat by remember { mutableStateOf<Double?>(null) }
    var currentLon by remember { mutableStateOf<Double?>(null) }
    
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(status) {
        if (status == WorkoutStatus.IDLE) {
            totalDistance = 0f
            lastLocation = null
            currentLat = null
            currentLon = null
        }
    }

    if (hasPermission) {
        val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
        
        DisposableEffect(status) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
                .setMinUpdateDistanceMeters(2f)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    if (status != WorkoutStatus.ACTIVE) return
                    
                    for (location in result.locations) {
                        lastLocation?.let {
                            val distance = it.distanceTo(location)
                            totalDistance += distance
                        }
                        lastLocation = location
                        currentLat = location.latitude
                        currentLon = location.longitude
                    }
                }
            }

            if (status != WorkoutStatus.IDLE) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }

            onDispose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    return DistanceState(totalDistance, currentLat, currentLon)
}
