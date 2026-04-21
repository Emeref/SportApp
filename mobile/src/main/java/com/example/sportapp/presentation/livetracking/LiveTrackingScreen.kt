package com.example.sportapp.presentation.livetracking

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.view.WindowManager
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.sportapp.LocalMobileTexts
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveTrackingScreen(
    viewModel: LiveTrackingViewModel,
    onBack: () -> Unit
) {
    val texts = LocalMobileTexts.current
    val context = LocalContext.current
    val currentLocation by viewModel.currentLocation.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()
    val sensorData by viewModel.sensorData.collectAsState()
    val mapRotation by viewModel.mapRotation.collectAsState()
    val isLocked by viewModel.isLocked.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val autoCenter by viewModel.autoCenter.collectAsState()
    val isNorthOriented by viewModel.isNorthOriented.collectAsState()
    val zoomLevel by viewModel.zoomLevel.collectAsState()
    val activeDefinition by viewModel.activeDefinition.collectAsState()
    val isFinished by viewModel.isFinished.collectAsState()
    val formattedDuration by viewModel.formattedDuration.collectAsState()

    val cameraPositionState = rememberCameraPositionState()
    var isFullScreenMap by remember { mutableStateOf(false) }

    // Keep Screen On
    val activity = context as? Activity
    DisposableEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Check permissions for MyLocation layer
    val hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Update camera when autoCenter is enabled or when map attributes change
    LaunchedEffect(currentLocation, autoCenter, mapRotation, isNorthOriented, zoomLevel) {
        if (autoCenter && currentLocation != null) {
            val target = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            cameraPositionState.animate(
                com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition(
                    CameraPosition.builder()
                        .target(target)
                        .zoom(zoomLevel)
                        .bearing(if (isNorthOriented) 0f else mapRotation)
                        .build()
                )
            )
        }
    }

    // Detect manual map movement and zoom
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
            // Update zoom level in VM if user changed it manually
            viewModel.setZoomLevel(cameraPositionState.position.zoom)
        }
        if (cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
            viewModel.setAutoCenter(false)
        }
    }

    if (isFinished && !isFullScreenMap) {
        AlertDialog(
            onDismissRequest = { /* Wyłączamy dismiss poza przyciskami */ },
            title = { Text(texts.LIVE_TRACKING_FINISHED_TITLE) },
            text = { Text(texts.LIVE_TRACKING_FINISHED_DESC) },
            confirmButton = {
                Button(onClick = {
                    viewModel.clearLiveTrackingData()
                    onBack()
                }) {
                    Text(texts.LIVE_TRACKING_BTN_FINISH)
                }
            },
            dismissButton = {
                TextButton(onClick = { isFullScreenMap = true }) {
                    Text(texts.LIVE_TRACKING_BTN_VIEW_MAP)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Column {
                            val rawName = activeDefinition?.name ?: texts.LIVE_TRACKING_TITLE
                            val truncatedName = if (rawName.length > 15) {
                                rawName.take(14) + "..."
                            } else {
                                rawName
                            }
                            
                            Text(
                                text = truncatedName,
                                maxLines = 1,
                                overflow = TextOverflow.Clip
                            )
                            
                            if (isFullScreenMap) {
                                val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                                Text(
                                    text = sdf.format(Date()),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isFullScreenMap) {
                                viewModel.clearLiveTrackingData()
                            }
                            onBack()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        if (!isFullScreenMap) {
                            Text(
                                text = formattedDuration,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.End,
                                modifier = Modifier.widthIn(min = 90.dp).padding(end = 8.dp)
                            )
                            
                            IconButton(onClick = { viewModel.setLocked(true) }) {
                                Icon(Icons.Default.Lock, contentDescription = texts.LIVE_TRACKING_LOCK)
                            }
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
                // Map Section
                Box(modifier = Modifier.weight(if (isFullScreenMap) 1f else 3f)) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = false
                        ),
                        properties = MapProperties(
                            isMyLocationEnabled = hasLocationPermission
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

                    // Map Controls - Top
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

                    // Map Controls - Bottom Right (Zoom)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FloatingActionButton(
                            onClick = { viewModel.zoomIn() },
                            modifier = Modifier.size(40.dp),
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Zoom In")
                        }
                        FloatingActionButton(
                            onClick = { viewModel.zoomOut() },
                            modifier = Modifier.size(40.dp),
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
                        }
                    }
                }

                // Stats Section
                if (!isFullScreenMap) {
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
                                    val keys = sensorData.keys.filter { it != "duration" && it != "timestamp" && it != "definitionId" && it != "isFinished" && it != "startTime" && it != "status" }.take(4)
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
                        
                        // Pause Overlay
                        androidx.compose.animation.AnimatedVisibility(
                            visible = isPaused,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            modifier = Modifier.matchParentSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = texts.LIVE_TRACKING_PAUSED,
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
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
                    .background(Color.Transparent)
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
                contentAlignment = Alignment.CenterEnd
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    repeat(5) {
                        Icon(
                            imageVector = Icons.Default.KeyboardDoubleArrowUp,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        texts.LIVE_TRACKING_UNLOCK_SWIPE,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(100.dp)
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
