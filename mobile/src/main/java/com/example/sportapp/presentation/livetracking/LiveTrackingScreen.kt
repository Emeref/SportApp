package com.example.sportapp.presentation.livetracking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sportapp.LocalMobileTexts
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveTrackingScreen(
    viewModel: LiveTrackingViewModel,
    onBack: () -> Unit
) {
    val texts = LocalMobileTexts.current
    val currentLocation by viewModel.currentLocation.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()
    val sensorData by viewModel.sensorData.collectAsState()
    val mapRotation by viewModel.mapRotation.collectAsState()
    val isLocked by viewModel.isLocked.collectAsState()
    val autoCenter by viewModel.autoCenter.collectAsState()
    val isNorthOriented by viewModel.isNorthOriented.collectAsState()
    val activeDefinition by viewModel.activeDefinition.collectAsState()

    val cameraPositionState = rememberCameraPositionState()

    // Update camera when autoCenter is enabled
    LaunchedEffect(currentLocation, autoCenter, mapRotation, isNorthOriented) {
        if (autoCenter && currentLocation != null) {
            val target = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            cameraPositionState.animate(
                com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition(
                    CameraPosition.builder()
                        .target(target)
                        .zoom(17f)
                        .bearing(if (isNorthOriented) 0f else mapRotation)
                        .build()
                )
            )
        }
    }

    // Detect manual map movement
    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
            viewModel.setAutoCenter(false)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(activeDefinition?.name ?: texts.LIVE_TRACKING_TITLE) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.setLocked(true) }) {
                            Icon(Icons.Default.Lock, contentDescription = texts.LIVE_TRACKING_LOCK)
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Map Section (3/4 height)
                Box(modifier = Modifier.weight(3f)) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = false
                        ),
                        properties = MapProperties(
                            isMyLocationEnabled = true
                        )
                    ) {
                        if (routePoints.isNotEmpty()) {
                            Polyline(
                                points = routePoints.map { LatLng(it.latitude, it.longitude) },
                                color = Color.Blue,
                                width = 10f
                            )
                        }
                    }

                    // Map Controls
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Orientation Toggle
                        FloatingActionButton(
                            onClick = { viewModel.toggleOrientation() },
                            modifier = Modifier.size(48.dp),
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            Icon(
                                imageVector = if (isNorthOriented) Icons.Default.North else Icons.Default.Explore,
                                contentDescription = if (isNorthOriented) texts.LIVE_TRACKING_MAP_NORTH else texts.LIVE_TRACKING_MAP_DIRECTION
                            )
                        }

                        // Re-center button
                        if (!autoCenter) {
                            FloatingActionButton(
                                onClick = { viewModel.setAutoCenter(true) },
                                modifier = Modifier.size(48.dp),
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Icon(Icons.Default.MyLocation, contentDescription = null)
                            }
                        }
                    }
                }

                // Stats Section (1/4 height)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                ) {
                    if (sensorData.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(texts.LIVE_TRACKING_WAITING_FOR_WATCH, style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        val activeWidgets = activeDefinition?.sensors?.filter { it.isVisible }?.take(4) ?: emptyList()
                        
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (activeWidgets.isEmpty()) {
                                // Fallback to raw sensor keys if no definition is active yet
                                val keys = sensorData.keys.filter { it != "duration" && it != "timestamp" && it != "definitionId" }.take(4)
                                keys.chunked(2).forEach { rowKeys ->
                                    Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        rowKeys.forEach { key ->
                                            StatWidget(
                                                label = texts.getSensorLabel(key),
                                                value = sensorData[key] ?: "--",
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        if (rowKeys.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            } else {
                                activeWidgets.chunked(2).forEach { rowWidgets ->
                                    Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        rowWidgets.forEach { config ->
                                            StatWidget(
                                                label = texts.getSensorLabel(config.sensorId),
                                                value = sensorData[config.sensorId] ?: "--",
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        if (rowWidgets.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Lock Overlay
        AnimatedVisibility(
            visible = isLocked,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            var offsetY by remember { mutableStateOf(0f) }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                if (offsetY < -200f) {
                                    viewModel.setLocked(false)
                                }
                                offsetY = 0f
                            },
                            onVerticalDrag = { _, dragAmount ->
                                offsetY += dragAmount
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        texts.LIVE_TRACKING_UNLOCK_SWIPE,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatWidget(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
