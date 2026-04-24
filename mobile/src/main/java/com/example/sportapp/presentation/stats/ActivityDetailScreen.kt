package com.example.sportapp.presentation.stats

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.R
import com.example.sportapp.data.SessionData
import com.example.sportapp.data.model.WorkoutLap
import com.example.sportapp.data.model.HeartRateZoneResult
import com.example.sportapp.data.model.ZoneStat
import com.example.sportapp.healthconnect.ExportResult
import com.example.sportapp.presentation.activities.ExportState
import com.example.sportapp.presentation.settings.WidgetItem
import com.example.sportapp.presentation.settings.ThemeMode
import com.example.sportapp.presentation.settings.AppMapType
import com.example.sportapp.presentation.settings.MobileSettingsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(
    viewModel: ActivityDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val texts = LocalMobileTexts.current
    val sessionData by viewModel.sessionData.collectAsStateWithLifecycle()
    val laps by viewModel.laps.collectAsStateWithLifecycle()
    val selectedLap by viewModel.selectedLap.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val hrZoneResult by viewModel.hrZoneResult.collectAsStateWithLifecycle()
    val mobileSettings by viewModel.mobileSettings.collectAsStateWithLifecycle()
    val autoLapDistance by viewModel.autoLapDistance.collectAsStateWithLifecycle()
    val isExporting by viewModel.isExporting.collectAsStateWithLifecycle()
    val exportResult by viewModel.exportResult.collectAsStateWithLifecycle()
    val fileExportState by viewModel.fileExportState.collectAsStateWithLifecycle()
    val hcSessionId by viewModel.hcSessionId.collectAsStateWithLifecycle(null)
    val isExportedToStrava by viewModel.isExportedToStrava.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    var isIntervalsExpanded by remember { mutableStateOf(false) }
    var isHrZonesExpanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var isMapFullScreen by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    var showExportedText by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showMapTypeDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        MapsInitializer.initialize(context)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(viewModel.healthConnectManager.writePermissions)) {
            viewModel.resetHcDeniedCount()
            viewModel.exportActivity(viewModel.activityId, toStrava = false, toHealthConnect = true)
        } else {
            viewModel.incrementHcDeniedCount()
            Toast.makeText(context, texts.HC_EXPORT_PERMISSION_DENIED, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(exportResult) {
        exportResult?.let { result ->
            when (result) {
                is ExportResult.Success -> {
                    Toast.makeText(context, texts.HC_EXPORT_SUCCESS, Toast.LENGTH_SHORT).show()
                    showExportedText = true
                }
                is ExportResult.Error -> {
                    Toast.makeText(context, "${texts.HC_EXPORT_ERROR}${result.message}", Toast.LENGTH_LONG).show()
                }
                ExportResult.PermissionDenied -> {
                    Toast.makeText(context, texts.HC_EXPORT_PERMISSION_DENIED, Toast.LENGTH_LONG).show()
                }
            }
            viewModel.clearExportResult()
        }
    }

    LaunchedEffect(fileExportState) {
        if (fileExportState is ExportState.Success) {
            val state = fileExportState as ExportState.Success
            val mimeType = if (state.uri.toString().endsWith(".sae")) "application/json" else "application/gpx+xml"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, state.uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, texts.ACTIVITY_SHARE_TITLE))
            viewModel.resetFileExportState()
        }
    }

    LaunchedEffect(showExportedText) {
        if (showExportedText) {
            delay(3000)
            showExportedText = false
        }
    }

    val isDarkTheme = when (mobileSettings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    if (error != null) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(texts.DETAIL_DATA_ERROR_TITLE) },
            text = { Text(error ?: "") },
            confirmButton = { Button(onClick = { viewModel.clearError(); onNavigateBack() }) { Text(texts.DETAIL_ERROR_OK) } }
        )
    }

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text(texts.HC_PERMISSIONS_DIALOG_TITLE) },
            text = { Text(texts.HC_PERMISSIONS_DIALOG_DESC) },
            confirmButton = {
                Button(onClick = {
                    showPermissionRationale = false
                    viewModel.healthConnectManager.openHealthConnectSettings()
                }) {
                    Text(texts.HC_OPEN_SETTINGS)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text(texts.SETTINGS_CANCEL)
                }
            }
        )
    }

    if (showExportDialog) {
        ExportSelectionDialog(
            isExportedToHC = hcSessionId != null,
            isExportedToStrava = isExportedToStrava,
            supportsGpx = (sessionData?.totalDistanceGps ?: 0.0) > 0,
            onDismiss = { showExportDialog = false },
            onExport = { toStrava, toHC, toGpx, toSae ->
                showExportDialog = false
                if (toGpx) {
                    viewModel.exportToFile(useSae = false)
                } else if (toSae) {
                    viewModel.exportToFile(useSae = true)
                } else {
                    scope.launch {
                        if (toHC) {
                            if (viewModel.healthConnectManager.hasPermissions(viewModel.healthConnectManager.writePermissions)) {
                                viewModel.exportActivity(viewModel.activityId, toStrava = toStrava, toHealthConnect = true)
                            } else {
                                if (mobileSettings.hcPermissionsDeniedCount >= 2) {
                                    showPermissionRationale = true
                                } else {
                                    permissionLauncher.launch(viewModel.healthConnectManager.writePermissions)
                                }
                                if (toStrava) {
                                    viewModel.exportActivity(viewModel.activityId, toStrava = true, toHealthConnect = false)
                                }
                            }
                        } else if (toStrava) {
                            viewModel.exportActivity(viewModel.activityId, toStrava = true, toHealthConnect = false)
                        }
                    }
                }
            }
        )
    }

    if (fileExportState is ExportState.Exporting) {
        val state = fileExportState as ExportState.Exporting
        Dialog(onDismissRequest = {}) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(progress = { state.progress })
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(state.message, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    if (fileExportState is ExportState.Error) {
        AlertDialog(
            onDismissRequest = { viewModel.resetFileExportState() },
            title = { Text(texts.ACTIVITY_EXPORT_ERROR) },
            text = { Text((fileExportState as ExportState.Error).message) },
            confirmButton = {
                TextButton(onClick = { viewModel.resetFileExportState() }) {
                    Text(texts.ACTIVITY_OK)
                }
            }
        )
    }

    if (showMapTypeDialog) {
        MapTypeSelectionDialog(
            currentMapType = mobileSettings.mapType,
            onDismiss = { showMapTypeDialog = false },
            onSelect = { viewModel.setMapType(it) }
        )
    }

    if (isMapFullScreen && sessionData != null) {
        BackHandler { isMapFullScreen = false }
        FullScreenMap(
            data = sessionData!!,
            settings = settings,
            mobileSettings = mobileSettings,
            selectedLap = selectedLap,
            selectedIndex = selectedIndex,
            onClose = { isMapFullScreen = false },
            onMapTypeClick = { showMapTypeDialog = true }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(texts.DETAIL_TITLE) },
                    navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = texts.SETTINGS_CLOSE) } },
                    actions = {
                        val isFullyExported = hcSessionId != null && isExportedToStrava
                        if (isFullyExported) {
                            Surface(
                                onClick = { showExportDialog = true },
                                shape = RoundedCornerShape(16.dp),
                                color = Color.Transparent,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = Color(0xFF4CAF50)
                                    )
                                    AnimatedVisibility(visible = showExportedText) {
                                        Text(
                                            text = texts.ACTIVITY_ALREADY_SYNCED,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color(0xFF4CAF50),
                                            modifier = Modifier.padding(start = 4.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            if (isExporting) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(end = 16.dp), strokeWidth = 2.dp)
                            } else {
                                IconButton(onClick = { showExportDialog = true }) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = texts.ACTIVITY_EXPORT)
                                }
                            }
                        }
                    }
                )
            }
        ) { padding ->
            if (sessionData == null && error == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (sessionData != null) {
                val data = sessionData!!
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding)
                ) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(text = data.activityName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text(text = data.activityDate, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    item {
                        SummaryWidgetsGrid(data, settings.visibleWidgets)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    items(settings.visibleCharts.filter { it.isEnabled }) { widget ->
                        when (widget.id) {
                            "map" -> {
                                if (data.route.isNotEmpty()) {
                                    MapSection(
                                        data = data, 
                                        settings = settings, 
                                        mobileSettings = mobileSettings,
                                        selectedLap = selectedLap, 
                                        onMapClick = { viewModel.selectLap(null) }, 
                                        selectedIndex = selectedIndex,
                                        onExpandClick = { isMapFullScreen = true },
                                        onMapTypeClick = { showMapTypeDialog = true }
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                                if (laps.isNotEmpty()) {
                                    ExpandableLapsSection(
                                        laps = laps,
                                        selectedLap = selectedLap,
                                        onLapClick = { viewModel.selectLap(it) },
                                        autoLapDistance = autoLapDistance ?: data.autoLapDistance,
                                        isExpanded = isIntervalsExpanded,
                                        onToggleExpanded = { isIntervalsExpanded = !isIntervalsExpanded }
                                    )
                                }
                            }
                            "bpm" -> {
                                val producer = viewModel.chartProducers["bpm"]
                                if (producer != null && (data.charts["bpm"]?.filterNotNull()?.isNotEmpty() == true)) {
                                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                        HeartRateChartSection(texts.DETAIL_HEART_RATE, producer, data.times, hrZoneResult, useDataYRange = true) { selectedIndex = it }
                                    }
                                    hrZoneResult?.let { result ->
                                        Spacer(modifier = Modifier.height(24.dp))
                                        HeartRateZonesSection(
                                            result = result,
                                            isExpanded = isHrZonesExpanded,
                                            onToggleExpanded = { isHrZonesExpanded = !isHrZonesExpanded }
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }
                            else -> {
                                val producer = viewModel.chartProducers[widget.id]
                                if (producer != null && (data.charts[widget.id]?.filterNotNull()?.isNotEmpty() == true)) {
                                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                        CommonChartSection(
                                            title = texts.getSensorLabel(widget.id), 
                                            producer = producer, 
                                            unit = getUnitForWidget(widget.id, texts), 
                                            detailTimes = data.times,
                                            onMarkerShown = { selectedIndex = it },
                                            useDataYRange = widget.id == "wysokosc"
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
fun MapTypeSelectionDialog(
    currentMapType: AppMapType,
    onDismiss: () -> Unit,
    onSelect: (AppMapType) -> Unit
) {
    val texts = LocalMobileTexts.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(texts.MAP_TYPE_TITLE) },
        text = {
            Column {
                MapTypeOption(texts.MAP_TYPE_NORMAL, AppMapType.NORMAL, currentMapType == AppMapType.NORMAL) { onSelect(it); onDismiss() }
                MapTypeOption(texts.MAP_TYPE_SATELLITE, AppMapType.SATELLITE, currentMapType == AppMapType.SATELLITE) { onSelect(it); onDismiss() }
                MapTypeOption(texts.MAP_TYPE_HYBRID, AppMapType.HYBRID, currentMapType == AppMapType.HYBRID) { onSelect(it); onDismiss() }
                MapTypeOption(texts.MAP_TYPE_TERRAIN, AppMapType.TERRAIN, currentMapType == AppMapType.TERRAIN) { onSelect(it); onDismiss() }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(texts.SETTINGS_CLOSE) }
        }
    )
}

@Composable
fun MapTypeOption(label: String, type: AppMapType, selected: Boolean, onClick: (AppMapType) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(type) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = { onClick(type) })
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
    }
}

@Composable
fun ExportSelectionDialog(
    isExportedToHC: Boolean,
    isExportedToStrava: Boolean,
    supportsGpx: Boolean,
    onDismiss: () -> Unit,
    onExport: (toStrava: Boolean, toHC: Boolean, toGpx: Boolean, toSae: Boolean) -> Unit
) {
    val texts = LocalMobileTexts.current
    var exportStrava by remember { mutableStateOf(!isExportedToStrava) }
    var exportHC by remember { mutableStateOf(!isExportedToHC) }
    var exportGpx by remember { mutableStateOf(false) }
    var exportSae by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(texts.ACTIVITY_EXPORT_DIALOG_TITLE) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(texts.SETTINGS_SYNC, style = MaterialTheme.typography.titleSmall)
                ExportOptionRow(
                    label = texts.STR_HEALTH_CONNECT,
                    isExported = isExportedToHC,
                    checked = exportHC,
                    onCheckedChange = { exportHC = it; if (it) { exportGpx = false; exportSae = false } }
                )
                ExportOptionRow(
                    label = texts.STR_STRAVA,
                    isExported = isExportedToStrava,
                    checked = exportStrava,
                    onCheckedChange = { exportStrava = it; if (it) { exportGpx = false; exportSae = false } }
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text(texts.ACTIVITY_EXPORT, style = MaterialTheme.typography.titleSmall)
                
                ExportOptionRow(
                    label = "GPX (.gpx)",
                    isExported = false,
                    enabled = supportsGpx,
                    checked = exportGpx,
                    onCheckedChange = { exportGpx = it; if (it) { exportSae = false; exportStrava = false; exportHC = false } }
                )
                if (!supportsGpx) {
                    Text(texts.ACTIVITY_EXPORT_INCOMPATIBLE_GPX, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
                
                ExportOptionRow(
                    label = "SAE (.sae)",
                    isExported = false,
                    checked = exportSae,
                    onCheckedChange = { exportSae = it; if (it) { exportGpx = false; exportStrava = false; exportHC = false } }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onExport(exportStrava, exportHC, exportGpx, exportSae) },
                enabled = (exportStrava && !isExportedToStrava) || (exportHC && !isExportedToHC) || exportGpx || exportSae
            ) {
                Text(texts.ACTIVITY_EXPORT)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(texts.SETTINGS_CANCEL)
            }
        }
    )
}

@Composable
fun ExportOptionRow(
    label: String,
    isExported: Boolean,
    enabled: Boolean = true,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isExported && enabled) { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isExported || checked,
            onCheckedChange = if (isExported || !enabled) null else onCheckedChange,
            enabled = !isExported && enabled
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, modifier = Modifier.weight(1f), color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        if (isExported) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun MapSection(
    data: SessionData,
    settings: ActivityDetailSettings,
    mobileSettings: MobileSettingsState,
    selectedLap: WorkoutLap?,
    onMapClick: () -> Unit,
    selectedIndex: Int? = null,
    onExpandClick: () -> Unit = {},
    onMapTypeClick: () -> Unit = {}
) {
    val texts = LocalMobileTexts.current
    val cameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current
    val isDarkTheme = when (mobileSettings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    LaunchedEffect(selectedLap, data.route) {
        val pointsToShow = if (selectedLap != null) {
            val start = selectedLap.startLocationIndex.coerceIn(data.route.indices)
            val end = (selectedLap.endLocationIndex + 1).coerceIn(0, data.route.size)
            if (start < end) data.route.subList(start, end) else emptyList()
        } else data.route

        if (pointsToShow.isNotEmpty()) {
            val n = pointsToShow.maxBy { it.latitude }
            val s = pointsToShow.minBy { it.latitude }
            val e = pointsToShow.maxBy { it.longitude }
            val w = pointsToShow.minBy { it.longitude }
            
            val bounds = LatLngBounds.Builder()
                .include(n).include(s).include(e).include(w)
                .build()
                
            cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 80))
        }
    }

    Box(modifier = Modifier.fillMaxWidth().height(250.dp).padding(horizontal = 16.dp).clip(RoundedCornerShape(12.dp))) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapStyleOptions = if (isDarkTheme && mobileSettings.mapType == AppMapType.NORMAL) MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark) else null,
                mapType = when(mobileSettings.mapType) {
                    AppMapType.NORMAL -> MapType.NORMAL
                    AppMapType.SATELLITE -> MapType.SATELLITE
                    AppMapType.HYBRID -> MapType.HYBRID
                    AppMapType.TERRAIN -> MapType.TERRAIN
                }
            ),
            onMapClick = { onMapClick() }
        ) {
            val startIcon = remember { BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN) }
            val finishIcon = remember {
                val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.finish_flag)
                val scaled = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
                BitmapDescriptorFactory.fromBitmap(scaled)
            }

            Polyline(points = data.route, color = Color(settings.trackColor), width = 10f)
            
            if (data.route.isNotEmpty()) {
                Marker(
                    state = rememberMarkerState(position = data.route.first()),
                    icon = startIcon,
                    title = texts.DETAIL_MAP_START
                )
                Marker(
                    state = rememberMarkerState(position = data.route.last()),
                    icon = finishIcon,
                    title = texts.DETAIL_MAP_FINISH,
                    anchor = Offset(0.5f, 1.0f)
                )
            }

            selectedLap?.let { lap ->
                val start = lap.startLocationIndex.coerceIn(data.route.indices)
                val end = (lap.endLocationIndex + 1).coerceIn(0, data.route.size)
                val lapPoints = if (start < end) data.route.subList(start, end) else emptyList()
                if (lapPoints.isNotEmpty()) Polyline(points = lapPoints, color = MaterialTheme.colorScheme.tertiary, width = 15f, zIndex = 1f)
            }
            if (selectedIndex != null && selectedIndex in data.route.indices) {
                val zoom = cameraPositionState.position.zoom
                val adaptiveRadius = 20.0 * 2.0.pow((15.0 - zoom))
                Circle(
                    center = data.route[selectedIndex],
                    radius = adaptiveRadius,
                    fillColor = Color.White.copy(alpha = 0.7f),
                    strokeColor = Color.Black,
                    strokeWidth = 2f,
                    zIndex = 9f
                )
            }
        }
        
        Row(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = onMapTypeClick,
                modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
            ) {
                Icon(Icons.Default.Layers, contentDescription = texts.MAP_TYPE_TITLE)
            }
            IconButton(
                onClick = onExpandClick,
                modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
            ) {
                Icon(Icons.Default.Fullscreen, contentDescription = texts.DETAIL_MAP_EXPAND)
            }
        }
    }
}

@Composable
fun FullScreenMap(
    data: SessionData,
    settings: ActivityDetailSettings,
    mobileSettings: MobileSettingsState,
    selectedLap: WorkoutLap?,
    selectedIndex: Int?,
    onClose: () -> Unit,
    onMapTypeClick: () -> Unit = {}
) {
    val texts = LocalMobileTexts.current
    val cameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current
    val isDarkTheme = when (mobileSettings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    LaunchedEffect(selectedLap, data.route) {
        val pointsToShow = if (selectedLap != null) {
            val start = selectedLap.startLocationIndex.coerceIn(data.route.indices)
            val end = (selectedLap.endLocationIndex + 1).coerceIn(0, data.route.size)
            if (start < end) data.route.subList(start, end) else emptyList()
        } else data.route

        if (pointsToShow.isNotEmpty()) {
            val n = pointsToShow.maxBy { it.latitude }
            val s = pointsToShow.minBy { it.latitude }
            val e = pointsToShow.maxBy { it.longitude }
            val w = pointsToShow.minBy { it.longitude }
            
            val bounds = LatLngBounds.Builder()
                .include(n).include(s).include(e).include(w)
                .build()
                
            cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 80))
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapStyleOptions = if (isDarkTheme && mobileSettings.mapType == AppMapType.NORMAL) MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark) else null,
                mapType = when(mobileSettings.mapType) {
                    AppMapType.NORMAL -> MapType.NORMAL
                    AppMapType.SATELLITE -> MapType.SATELLITE
                    AppMapType.HYBRID -> MapType.HYBRID
                    AppMapType.TERRAIN -> MapType.TERRAIN
                }
            ),
        ) {
            val startIcon = remember { BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN) }
            val finishIcon = remember {
                val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.finish_flag)
                val scaled = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
                BitmapDescriptorFactory.fromBitmap(scaled)
            }

            Polyline(points = data.route, color = Color(settings.trackColor), width = 10f)

            if (data.route.isNotEmpty()) {
                Marker(
                    state = rememberMarkerState(position = data.route.first()),
                    icon = startIcon,
                    title = texts.DETAIL_MAP_START
                )
                Marker(
                    state = rememberMarkerState(position = data.route.last()),
                    icon = finishIcon,
                    title = texts.DETAIL_MAP_FINISH,
                    anchor = Offset(0.5f, 1.0f)
                )
            }

            selectedLap?.let { lap ->
                val start = lap.startLocationIndex.coerceIn(data.route.indices)
                val end = (lap.endLocationIndex + 1).coerceIn(0, data.route.size)
                val lapPoints = if (start < end) data.route.subList(start, end) else emptyList()
                if (lapPoints.isNotEmpty()) Polyline(points = lapPoints, color = MaterialTheme.colorScheme.tertiary, width = 15f, zIndex = 1f)
            }
            if (selectedIndex != null && selectedIndex in data.route.indices) {
                val zoom = cameraPositionState.position.zoom
                val adaptiveRadius = 20.0 * 2.0.pow((15.0 - zoom))
                Circle(
                    center = data.route[selectedIndex],
                    radius = adaptiveRadius,
                    fillColor = Color.White.copy(alpha = 0.7f),
                    strokeColor = Color.Black,
                    strokeWidth = 2f,
                    zIndex = 9f
                )
            }
        }
        
        Column(modifier = Modifier.padding(top = 50.dp, end = 16.dp).align(Alignment.TopEnd), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = onMapTypeClick,
                modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
            ) {
                Icon(Icons.Default.Layers, contentDescription = texts.MAP_TYPE_TITLE)
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
            ) {
                Icon(Icons.Default.FullscreenExit, contentDescription = texts.DETAIL_MAP_COLLAPSE)
            }
        }
    }
}

@Composable
fun ExpandableLapsSection(
    laps: List<WorkoutLap>,
    selectedLap: WorkoutLap?,
    onLapClick: (WorkoutLap) -> Unit,
    autoLapDistance: Double?,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    val texts = LocalMobileTexts.current
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onToggleExpanded() },
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) texts.DETAIL_COLLAPSE else texts.DETAIL_EXPAND,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (autoLapDistance != null && autoLapDistance > 0) texts.detailLapsWithDistance(formatDistance(autoLapDistance, texts.UNIT_M, texts.UNIT_KM)) else texts.DETAIL_INTERVALS,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (!isExpanded) {
                    Text(
                        text = texts.detailLapsCount(laps.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                LapsTable(laps, selectedLap, onLapClick)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun HeartRateChartSection(
    title: String,
    producer: com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer,
    detailTimes: List<String>,
    hrZoneResult: HeartRateZoneResult?,
    useDataYRange: Boolean = false,
    onMarkerShown: (Int?) -> Unit
) {
    val texts = LocalMobileTexts.current
    CommonChartSection(
        title = title, 
        producer = producer, 
        unit = texts.UNIT_BPM, 
        detailTimes = detailTimes, 
        hrZoneResult = hrZoneResult,
        onMarkerShown = onMarkerShown,
        useDataYRange = useDataYRange
    )
}

@Composable
fun HeartRateZonesSection(
    result: HeartRateZoneResult,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    val texts = LocalMobileTexts.current
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onToggleExpanded() },
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) texts.DETAIL_COLLAPSE else texts.DETAIL_EXPAND,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = texts.DETAIL_HR_ZONES,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (!isExpanded) {
                    Text(
                        text = result.trainingEffect,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                             DonutChart(stats = result.zones, modifier = Modifier.size(100.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(texts.DETAIL_PREDOMINANT_EFFECT, style = MaterialTheme.typography.labelMedium)
                                Text(result.trainingEffect, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        result.zones.reversed().forEach { stat ->
                            ZoneRow(stat)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun ZoneRow(stat: ZoneStat) {
    val texts = LocalMobileTexts.current
    val zoneName = when(stat.zone.name) {
        "Z0" -> texts.ZONE_Z0
        "Z1" -> texts.ZONE_Z1
        "Z2" -> texts.ZONE_Z2
        "Z3" -> texts.ZONE_Z3
        "Z4" -> texts.ZONE_Z4
        "Z5" -> texts.ZONE_Z5
        else -> stat.zone.displayName
    }
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(stat.zone.color))
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(zoneName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text("${stat.minBpm}-${stat.maxBpm} ${texts.UNIT_BPM}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(formatSeconds(stat.durationSeconds), style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(70.dp), textAlign = TextAlign.End)
        Text(String.format(Locale.US, "%.1f%%", stat.percentage), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(50.dp), textAlign = TextAlign.End)
    }
}

private fun formatSeconds(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) String.format(Locale.US, "%d:%02d:%02d", h, m, s)
    else String.format(Locale.US, "%02d:%02d", m, s)
}

@Composable
fun LapsTable(laps: List<WorkoutLap>, selectedLap: WorkoutLap?, onLapClick: (WorkoutLap) -> Unit) {
    val texts = LocalMobileTexts.current
    val fastestPace = laps.filter { it.avgPaceSecondsPerKm > 0 }.minOfOrNull { it.avgPaceSecondsPerKm } ?: 0
    val slowestPace = laps.filter { it.avgPaceSecondsPerKm > 0 }.maxOfOrNull { it.avgPaceSecondsPerKm } ?: 0
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Column {
        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
            Column {
                Row {
                    Box(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer).padding(8.dp)) { LapCell(texts.DETAIL_LAP_NR, width = 40.dp, isHeader = true) }
                    Box(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                        Row(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer).padding(8.dp)) {
                            LapCell(texts.DETAIL_LAP_TIME, width = 80.dp, isHeader = true)
                            LapCell(texts.DETAIL_LAP_AVG_PACE, width = 90.dp, isHeader = true)
                            LapCell(texts.DETAIL_LAP_AVG_SPEED, width = 100.dp, isHeader = true)
                            LapCell(texts.DETAIL_LAP_MAX_SPEED, width = 100.dp, isHeader = true)
                            LapCell(texts.DETAIL_LAP_AVG_HR, width = 70.dp, isHeader = true)
                            LapCell(texts.DETAIL_LAP_MAX_HR, width = 70.dp, isHeader = true)
                            LapCell(texts.DETAIL_LAP_ASCENT_DESCENT, width = 100.dp, isHeader = true)
                        }
                    }
                }
                Column(modifier = Modifier.heightIn(max = 280.dp).verticalScroll(verticalScrollState)) {
                    laps.forEach { lap ->
                        val isSelected = selectedLap?.lapNumber == lap.lapNumber
                        val bgColor = when {
                            isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            lap.avgPaceSecondsPerKm > 0 && lap.avgPaceSecondsPerKm == fastestPace && laps.size > 1 -> Color(0xFFC8E6C9).copy(alpha = if (isSystemInDarkTheme()) 0.5f else 1.0f)
                            lap.avgPaceSecondsPerKm > 0 && lap.avgPaceSecondsPerKm == slowestPace && laps.size > 1 -> Color(0xFFFFCDD2).copy(alpha = if (isSystemInDarkTheme()) 0.5f else 1.0f)
                            else -> Color.Transparent
                        }
                        Row {
                            Box(modifier = Modifier.background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)).clickable { onLapClick(lap) }.padding(8.dp)) { LapCell("${lap.lapNumber}", width = 40.dp) }
                            Box(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                                Row(modifier = Modifier.clickable { onLapClick(lap) }.background(bgColor).padding(8.dp)) {
                                    LapCell(formatMillis(lap.durationMillis), width = 80.dp)
                                    LapCell(formatPaceFromSeconds(lap.avgPaceSecondsPerKm), width = 90.dp)
                                    LapCell(String.format(Locale.US, "%.1f %s", lap.avgSpeed, texts.UNIT_KM_H), width = 100.dp)
                                    LapCell(String.format(Locale.US, "%.1f %s", lap.maxSpeed, texts.UNIT_KM_H), width = 100.dp)
                                    LapCell("${lap.avgHeartRate}", width = 70.dp)
                                    LapCell("${lap.maxHeartRate}", width = 70.dp)
                                    LapCell(String.format(Locale.US, "+%.0f/-%.0f", lap.totalAscent, lap.totalDescent), width = 100.dp)
                                }
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun LapCell(text: String, width: androidx.compose.ui.unit.Dp, isHeader: Boolean = false) {
    Text(text = text, modifier = Modifier.width(width), style = if (isHeader) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodySmall, fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center)
}

private fun formatMillis(millis: Long): String {
    val totalSeconds = millis / 1000
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) String.format(Locale.US, "%d:%02d:%02d", h, m, s) else String.format(Locale.US, "%02d:%02d", m, s)
}

private fun formatPaceFromSeconds(totalSeconds: Int): String {
    if (totalSeconds <= 0) return "--:--"
    return String.format(Locale.US, "%02d:%02d/km", totalSeconds / 60, totalSeconds % 60)
}

@Composable
fun SummaryWidgetsGrid(data: SessionData, visibleWidgets: List<WidgetItem>) {
    val texts = LocalMobileTexts.current
    val enabledWidgets = visibleWidgets.filter { it.isEnabled }
    
    val avgSpeedGps = if (data.durationSeconds > 0) (data.totalDistanceGps / 1000.0) / (data.durationSeconds / 3600.0) else 0.0
    val avgSpeedSteps = if (data.durationSeconds > 0) (data.totalDistanceSteps / 1000.0) / (data.durationSeconds / 3600.0) else 0.0

    val widgetValues: Map<String, Pair<String, String>> = mapOf(
        "duration" to (texts.WIDGET_DURATION to data.duration),
        "max_bpm" to (texts.WIDGET_MAX_BPM to "${data.maxBpm} ${texts.UNIT_BPM}"),
        "avg_bpm" to (texts.WIDGET_AVG_BPM to "${data.avgBpm} ${texts.UNIT_BPM}"),
        "total_calories" to (texts.WIDGET_TOTAL_CALORIES to "${data.totalCalories} ${texts.UNIT_KCAL}"),
        "max_calories_min" to (texts.WIDGET_MAX_CALORIES_MIN to String.format(Locale.US, "%.2f %s", data.maxCaloriesMin, texts.UNIT_KCAL_MIN)),
        "avg_pace" to (texts.WIDGET_AVG_PACE to formatPace(data.avgPace, texts.UNIT_MIN_KM)),
        "avg_speed_gps" to (texts.WIDGET_AVG_SPEED_GPS to String.format(Locale.US, "%.1f %s", avgSpeedGps, texts.UNIT_KM_H)),
        "avg_speed_steps" to (texts.WIDGET_AVG_SPEED_STEPS to String.format(Locale.US, "%.1f %s", avgSpeedSteps, texts.UNIT_KM_H)),
        "max_speed" to (texts.WIDGET_MAX_SPEED to String.format(Locale.US, "%.1f %s", data.maxSpeed, texts.UNIT_KM_H)),
        "max_altitude" to (texts.WIDGET_MAX_ALTITUDE_DESC to String.format(Locale.US, "%.0f %s", data.maxAltitude, texts.UNIT_M_ASL)),
        "total_ascent" to (texts.WIDGET_TOTAL_ASCENT to String.format(Locale.US, "+%.0f %s", data.totalAscent, texts.UNIT_M)),
        "total_descent" to (texts.WIDGET_TOTAL_DESCENT to String.format(Locale.US, "-%.0f %s", data.totalDescent, texts.UNIT_M)),
        "avg_step_length" to (texts.WIDGET_AVG_STEP_LENGTH to String.format(Locale.US, "%.2f %s", data.avgStepLength, texts.UNIT_M)),
        "avg_cadence" to (texts.WIDGET_AVG_CADENCE_DESC to String.format(Locale.US, "%.0f %s", data.avgCadence, texts.UNIT_STEP_MIN)),
        "max_cadence" to (texts.WIDGET_MAX_CADENCE to String.format(Locale.US, "%.0f %s", data.maxCadence, texts.UNIT_STEP_MIN)),
        "total_steps" to (texts.WIDGET_TOTAL_STEPS to "${data.totalSteps}"),
        "total_distance_gps" to (texts.WIDGET_DISTANCE_GPS to formatDistance(data.totalDistanceGps, texts.UNIT_M, texts.UNIT_KM)),
        "total_distance_steps" to (texts.WIDGET_DISTANCE_STEPS to formatDistance(data.totalDistanceSteps, texts.UNIT_M, texts.UNIT_KM)),
        "pressure_start" to (texts.WIDGET_PRESSURE_START to (data.pressureStart?.let { String.format(Locale.US, "%.1f %s", it, texts.UNIT_HPA) } ?: "-- ${texts.UNIT_HPA}")),
        "pressure_end" to (texts.WIDGET_PRESSURE_END to (data.pressureEnd?.let { String.format(Locale.US, "%.1f %s", it, texts.UNIT_HPA) } ?: "-- ${texts.UNIT_HPA}")),
        "max_pressure" to (texts.WIDGET_MAX_PRESSURE to (data.maxPressure?.let { String.format(Locale.US, "%.1f %s", it, texts.UNIT_HPA) } ?: "-- ${texts.UNIT_HPA}")),
        "min_pressure" to (texts.WIDGET_MIN_PRESSURE to (data.minPressure?.let { String.format(Locale.US, "%.1f %s", it, texts.UNIT_HPA) } ?: "-- ${texts.UNIT_HPA}")),
        "best_pace_1km" to (texts.WIDGET_BEST_PACE_1KM to formatPace(data.bestPace1km ?: 0.0, texts.UNIT_MIN_KM))
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        enabledWidgets.chunked(2).forEachIndexed { index, chunk ->
            if (index > 0) Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                chunk.forEach { widget ->
                    widgetValues[widget.id]?.let { (label, value) -> SummaryItem(label, value, Modifier.weight(1f)) }
                }
                if (chunk.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

private fun formatPace(paceDecimal: Double, unit: String): String {
    if (paceDecimal <= 0 || paceDecimal > 120) return "--:--"
    return String.format(Locale.US, "%02d:%02d %s", paceDecimal.toInt(), ((paceDecimal - paceDecimal.toInt()) * 60).toInt(), unit)
}

private fun formatDistance(distanceMeters: Double, unitM: String, unitKm: String): String {
    return if (distanceMeters >= 1000) String.format(Locale.US, "%.2f %s", distanceMeters / 1000.0, unitKm) else String.format(Locale.US, "%.0f %s", distanceMeters, unitM)
}

@Composable
fun SummaryItem(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.Start) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

private fun getUnitForWidget(id: String, texts: com.example.sportapp.MobileTexts): String {
    return when(id) {
        "bpm", "srednie_bpm" -> texts.UNIT_BPM
        "kalorie_min", "kalorie_suma" -> texts.UNIT_KCAL
        "kroki_min" -> texts.UNIT_STEP_MIN
        "odl_kroki", "gps_dystans", "avg_step_length_over_time" -> texts.UNIT_M
        "predkosc", "predkosc_kroki" -> texts.UNIT_KM_H
        "wysokosc", "przewyzszenia_gora", "przewyzszenia_dol" -> texts.UNIT_M
        "pressure" -> texts.UNIT_HPA
        else -> ""
    }
}
