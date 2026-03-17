package com.example.sportapp.presentation.workout

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.example.sportapp.data.db.WorkoutPointEntity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    mapType: MapType,
    focusRequester: FocusRequester,
    lastPoint: WorkoutPointEntity? = null,
    autoCenterDelay: Int = 5
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
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
            position = CameraPosition.fromLatLngZoom(LatLng(52.2297, 21.0122), 15f)
        }

        // State for intelligent auto-centering
        var isAutoCenteringEnabled by remember { mutableStateOf(true) }
        var lastInteractionTime by remember { mutableLongStateOf(0L) }

        // Effect for new location data
        LaunchedEffect(lastPoint) {
            if (isAutoCenteringEnabled && lastPoint != null) {
                lastPoint.latitude?.let { lat ->
                    lastPoint.longitude?.let { lng ->
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLng(LatLng(lat, lng))
                        )
                    }
                }
            }
        }

        // Effect for returning to auto-centering after inactivity
        LaunchedEffect(isAutoCenteringEnabled, lastInteractionTime) {
            if (!isAutoCenteringEnabled) {
                delay(autoCenterDelay * 1000L)
                isAutoCenteringEnabled = true
            }
        }

        // Detect manual camera movement
        LaunchedEffect(cameraPositionState.isMoving) {
            if (cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
                isAutoCenteringEnabled = false
                lastInteractionTime = System.currentTimeMillis()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .focusable()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = true,
                    mapType = mapType
                ),
                uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
            )

            // Auto-centering Icon (visible when suspended)
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

            // Zoom Control
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                val zoomProgress = (cameraPositionState.position.zoom - 2f) / 18f
                CircularProgressIndicator(
                    progress = (1f - zoomProgress).coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxSize().padding(2.dp),
                    startAngle = 320f,
                    endAngle = 40f,
                    strokeWidth = 6.dp,
                    indicatorColor = Color.DarkGray.copy(alpha = 0.5f),
                    trackColor = Color.LightGray.copy(alpha = 0.9f)
                )

                Box(
                    modifier = Modifier.fillMaxHeight().width(40.dp).align(Alignment.CenterEnd)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { change, dragAmount ->
                                change.consume()
                                val newZoom = (cameraPositionState.position.zoom - dragAmount * 0.05f).coerceIn(2f, 20f)
                                scope.launch { cameraPositionState.move(CameraUpdateFactory.zoomTo(newZoom)) }
                                
                                // Zooming also suspends auto-centering
                                isAutoCenteringEnabled = false
                                lastInteractionTime = System.currentTimeMillis()
                            }
                        }
                )
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Brak uprawnień do lokalizacji", textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
        }
    }
}
