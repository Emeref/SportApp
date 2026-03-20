package com.example.sportapp.presentation.workout

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.example.sportapp.data.db.WorkoutPointEntity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    mapType: MapType,
    @Suppress("UNUSED_PARAMETER") focusRequester: FocusRequester, // Parametr zachowany dla kompatybilności, ale nieużywany wewnątrz mapy
    lastPoint: WorkoutPointEntity? = null,
    allPoints: List<WorkoutPointEntity> = emptyList(),
    autoCenterDelay: Int = 5,
    showRoute: Boolean = true,
    routeColor: Color = Color.Cyan,
    mapZoomLevel: Float = 15f
) {
    val context = LocalContext.current
    
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasLocationPermission = it }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    if (hasLocationPermission) {
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(52.2297, 21.0122), mapZoomLevel)
        }

        var isAutoCenteringEnabled by remember { mutableStateOf(true) }
        
        // Optymalizacja: Obliczamy punkty trasy tylko gdy allPoints faktycznie się zmieni
        val routePoints = remember(allPoints) {
            allPoints.takeLast(100).mapNotNull { p ->
                if (p.latitude != null && p.longitude != null) {
                    LatLng(p.latitude, p.longitude)
                } else null
            }
        }

        // Efekt autocentrowania - używamy animate tylko przy pierwszej lokalizacji, 
        // potem move, aby oszczędzać procesor, lub bardzo krótką animację.
        LaunchedEffect(lastPoint, isAutoCenteringEnabled) {
            if (isAutoCenteringEnabled && lastPoint != null) {
                val lat = lastPoint.latitude
                val lng = lastPoint.longitude
                if (lat != null && lng != null) {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), mapZoomLevel),
                        500 // Krótka animacja (0.5s) jest lżejsza niż domyślna
                    )
                }
            }
        }

        LaunchedEffect(isAutoCenteringEnabled) {
            if (!isAutoCenteringEnabled) {
                delay(autoCenterDelay * 1000L)
                isAutoCenteringEnabled = true
            }
        }

        // Wykrywanie ręcznego przesunięcia (gestu)
        LaunchedEffect(cameraPositionState.isMoving) {
            if (cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
                isAutoCenteringEnabled = false
            }
        }

        // Optymalizacja parametrów mapy - używamy remember, aby uniknąć zbędnych rekompozycji obiektu MapProperties
        val mapProperties = remember(mapType) {
            MapProperties(
                isMyLocationEnabled = true,
                mapType = mapType
                // Usunięto minZoomPreference i maxZoomPreference - to one mogły powodować pętlę CPU
            )
        }
        val mapUiSettings = remember {
            MapUiSettings(
                zoomControlsEnabled = false, 
                myLocationButtonEnabled = false,
                zoomGesturesEnabled = false,
                scrollGesturesEnabled = true,
                tiltGesturesEnabled = false,
                rotationGesturesEnabled = false
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings
            ) {
                if (showRoute && routePoints.size >= 2) {
                    Polyline(
                        points = routePoints,
                        color = routeColor,
                        width = 6f,
                        jointType = JointType.ROUND,
                        startCap = RoundCap(),
                        endCap = RoundCap()
                    )
                }
            }

            if (!isAutoCenteringEnabled) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 12.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Button(
                        onClick = { isAutoCenteringEnabled = true },
                        modifier = Modifier.size(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Black.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Centruj",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Brak uprawnień do lokalizacji", textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
        }
    }
}
