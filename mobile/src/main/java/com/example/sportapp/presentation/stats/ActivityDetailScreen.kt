package com.example.sportapp.presentation.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sportapp.R
import com.example.sportapp.core.i18n.AppStrings
import com.example.sportapp.core.i18n.LocalAppStrings
import com.example.sportapp.data.model.WorkoutLap
import com.example.sportapp.data.model.HeartRateZone
import com.example.sportapp.data.model.HeartRateZoneResult
import com.example.sportapp.data.model.ZoneStat
import com.example.sportapp.presentation.settings.WidgetItem
import com.example.sportapp.presentation.settings.ThemeMode
import com.example.sportapp.presentation.settings.MobileSettingsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(
    viewModel: ActivityDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val sessionData by viewModel.sessionData.collectAsStateWithLifecycle()
    val laps by viewModel.laps.collectAsStateWithLifecycle()
    val selectedLap by viewModel.selectedLap.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val strings = LocalAppStrings.current
    val settings by viewModel.getSettings(strings).collectAsStateWithLifecycle()
    val hrZoneResult by viewModel.hrZoneResult.collectAsStateWithLifecycle()
    val mobileSettings by viewModel.mobileSettings.collectAsStateWithLifecycle()

    val isDarkTheme = when (mobileSettings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    // Error Dialog
    if (error != null) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(strings.dataError) },
            text = { Text(error ?: strings.noData) },
            confirmButton = {
                Button(onClick = {
                    viewModel.clearError()
                    onNavigateBack()
                }) {
                    Text(strings.ok)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.activityDetails) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
                    }
                }
            )
        }
    ) { padding ->
        if (sessionData == null && error == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (sessionData != null) {
            val data = sessionData!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Nagłówek z nazwą i datą
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = data.activityName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = data.activityDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Widgety z podsumowaniem (Grid) uzależnione od ustawień
                SummaryWidgetsGrid(data, settings.visibleWidgets, strings)

                Spacer(modifier = Modifier.height(16.dp))

                // Wyświetlamy wykresy w kolejności zdefiniowanej w ustawieniach i tylko te zaznaczone
                for (widget in settings.visibleCharts.filter { it.isEnabled }) {
                    key(widget.id) {
                        when (widget.id) {
                            "map" -> {
                                if (data.route.isNotEmpty()) {
                                    val cameraPositionState = rememberCameraPositionState()

                                    // Update camera when lap is selected or to show whole route by default
                                    LaunchedEffect(selectedLap, data.route) {
                                        val pointsToShow = if (selectedLap != null) {
                                            val lap = selectedLap!!
                                            data.route.subList(
                                                lap.startLocationIndex.coerceIn(data.route.indices),
                                                (lap.endLocationIndex + 1).coerceIn(data.route.indices)
                                            )
                                        } else {
                                            data.route
                                        }

                                        if (pointsToShow.isNotEmpty()) {
                                            val boundsBuilder = LatLngBounds.Builder()
                                            pointsToShow.forEach { boundsBuilder.include(it) }
                                            val bounds = try {
                                                boundsBuilder.build()
                                            } catch (e: Exception) {
                                                null
                                            }
                                            if (bounds != null) {
                                                cameraPositionState.animate(
                                                    CameraUpdateFactory.newLatLngBounds(bounds, 100)
                                                )
                                            }
                                        }
                                    }

                                    val context = LocalContext.current
                                    val mapProperties = remember(isDarkTheme) {
                                        MapProperties(
                                            mapStyleOptions = if (isDarkTheme) {
                                                MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
                                            } else null
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp)
                                            .padding(horizontal = 16.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    ) {
                                        GoogleMap(
                                            modifier = Modifier.fillMaxSize(),
                                            cameraPositionState = cameraPositionState,
                                            properties = mapProperties,
                                            onMapClick = { viewModel.selectLap(null) }
                                        ) {
                                            Polyline(
                                                points = data.route,
                                                color = Color(settings.trackColor),
                                                width = 10f
                                            )

                                            selectedLap?.let { lap ->
                                                val lapPoints = data.route.subList(
                                                    lap.startLocationIndex.coerceIn(data.route.indices),
                                                    (lap.endLocationIndex + 1).coerceIn(data.route.indices)
                                                )
                                                if (lapPoints.isNotEmpty()) {
                                                    Polyline(
                                                        points = lapPoints,
                                                        color = MaterialTheme.colorScheme.tertiary,
                                                        width = 15f,
                                                        zIndex = 1f
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (laps.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        LapsTable(
                                            laps = laps,
                                            selectedLap = selectedLap,
                                            onLapClick = { viewModel.selectLap(it) },
                                            strings = strings
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                            "bpm" -> {
                                val producer = viewModel.chartProducers["bpm"]
                                if (producer != null && (data.charts["bpm"]?.filterNotNull()?.isNotEmpty() == true)) {
                                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                        HeartRateChartSection(
                                            title = "${strings.heartRate} (${strings.bpmUnit.lowercase()})",
                                            producer = producer,
                                            detailTimes = data.times,
                                            hrZoneResult = hrZoneResult,
                                            strings = strings
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))

                                    hrZoneResult?.let { result ->
                                        HeartRateZonesSection(result, strings)
                                        Spacer(modifier = Modifier.height(24.dp))
                                    }
                                }
                            }
                            else -> {
                                val producer = viewModel.chartProducers[widget.id]
                                val chartKey = widget.id

                                if (producer != null && (data.charts[chartKey]?.filterNotNull()?.isNotEmpty() == true)) {
                                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                        CommonChartSection(
                                            title = widget.label,
                                            producer = producer,
                                            unit = getUnitForWidget(widget.id, strings),
                                            detailTimes = data.times,
                                            isScrollEnabled = true,
                                            isZoomEnabled = true
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun HeartRateChartSection(
    title: String,
    producer: com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer,
    detailTimes: List<String>,
    hrZoneResult: HeartRateZoneResult?,
    strings: AppStrings
) {
    CommonChartSection(
        title = title,
        producer = producer,
        unit = strings.bpmUnit.lowercase(),
        detailTimes = detailTimes,
        isScrollEnabled = true,
        isZoomEnabled = true,
        hrZoneResult = hrZoneResult
    )
}

@Composable
fun HeartRateZonesSection(result: HeartRateZoneResult, strings: AppStrings) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(strings.heartRateZones, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DonutChart(
                        stats = result.zones,
                        strings = strings,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(strings.predominantEffect, style = MaterialTheme.typography.labelMedium)
                        Text(result.trainingEffect(strings), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tabela stref
                result.zones.reversed().forEach { stat ->
                    ZoneRow(stat, strings)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
fun ZoneRow(stat: ZoneStat, strings: AppStrings) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(stat.zone.color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(stat.zone.getName(strings), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text("${stat.minBpm}-${stat.maxBpm} ${strings.bpmUnit.lowercase()}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            formatDuration(stat.durationSeconds),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(70.dp),
            textAlign = TextAlign.End
        )
        Text(
            String.format(Locale.US, "%.1f%%", stat.percentage),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(50.dp),
            textAlign = TextAlign.End
        )
    }
}

private fun formatDuration(totalSeconds: Long): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) {
        String.format(Locale.US, "%d:%02d:%02d", h, m, s)
    } else {
        String.format(Locale.US, "%02d:%02d", m, s)
    }
}

@Composable
fun LapsTable(
    laps: List<WorkoutLap>,
    selectedLap: WorkoutLap?,
    onLapClick: (WorkoutLap) -> Unit,
    strings: AppStrings
) {
    val validLapsForPace = laps.filter { it.avgPaceSecondsPerKm > 0 }
    val fastestPace = validLapsForPace.minOfOrNull { it.avgPaceSecondsPerKm } ?: 0
    val slowestPace = validLapsForPace.maxOfOrNull { it.avgPaceSecondsPerKm } ?: 0
    
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(strings.laps, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column {
                // Header Row - stays fixed at the top
                Row {
                    // Fixed "Nr" Header
                    Box(modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp)
                    ) {
                        LapCell(strings.numberShortLabel, width = 40.dp, isHeader = true)
                    }
                    
                    // Scrollable Headers
                    Box(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                        Row(modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(8.dp)
                        ) {
                            LapCell(strings.time, width = 80.dp, isHeader = true)
                            LapCell(strings.avgPaceLabel, width = 90.dp, isHeader = true)
                            LapCell(strings.avgSpeed, width = 100.dp, isHeader = true)
                            LapCell(strings.maxSpeed, width = 100.dp, isHeader = true)
                            LapCell(strings.avgHeartRateLabel, width = 70.dp, isHeader = true)
                            LapCell(strings.maxHeartRateLabel, width = 70.dp, isHeader = true)
                            LapCell(strings.upDownShortLabel, width = 100.dp, isHeader = true)
                        }
                    }
                }

                // Scrollable Rows - limited to approx 8 records
                Column(
                    modifier = Modifier
                        .heightIn(max = 280.dp)
                        .verticalScroll(verticalScrollState)
                ) {
                    laps.forEach { lap ->
                        val isSelected = selectedLap?.lapNumber == lap.lapNumber
                        val selectionColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        val fastestColor = Color(0xFFC8E6C9).copy(alpha = if (isSystemInDarkTheme()) 0.5f else 1.0f)
                        val slowestColor = Color(0xFFFFCDD2).copy(alpha = if (isSystemInDarkTheme()) 0.5f else 1.0f)

                        val bgColor = when {
                            isSelected -> selectionColor
                            lap.avgPaceSecondsPerKm > 0 && lap.avgPaceSecondsPerKm == fastestPace && laps.size > 1 -> fastestColor
                            lap.avgPaceSecondsPerKm > 0 && lap.avgPaceSecondsPerKm == slowestPace && laps.size > 1 -> slowestColor
                            else -> Color.Transparent
                        }

                        Row {
                            // Fixed "Nr" Cell
                            Box(modifier = Modifier
                                .background(if (isSelected) selectionColor else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                .clickable { onLapClick(lap) }
                                .padding(8.dp)
                            ) {
                                LapCell("${lap.lapNumber}", width = 40.dp)
                            }

                            // Scrollable Content
                            Box(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                                Row(modifier = Modifier
                                    .clickable { onLapClick(lap) }
                                    .background(bgColor)
                                    .padding(8.dp)
                                ) {
                                    LapCell(formatDuration(lap.durationMillis / 1000), width = 80.dp)
                                    LapCell(formatPaceFromSeconds(lap.avgPaceSecondsPerKm, strings), width = 90.dp)
                                    LapCell(String.format(Locale.US, "%.1f %s", lap.avgSpeed, strings.kmhUnit), width = 100.dp)
                                    LapCell(String.format(Locale.US, "%.1f %s", lap.maxSpeed, strings.kmhUnit), width = 100.dp)
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
    Text(
        text = text,
        modifier = Modifier.width(width),
        style = if (isHeader) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodySmall,
        fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
        textAlign = TextAlign.Center
    )
}

private fun formatPaceFromSeconds(totalSeconds: Int, strings: AppStrings): String {
    if (totalSeconds <= 0) return "--:--"
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d/%s", minutes, seconds, strings.kmUnit)
}

@Composable
fun SummaryWidgetsGrid(data: com.example.sportapp.data.SessionData, visibleWidgets: List<WidgetItem>, strings: AppStrings) {
    val enabledWidgets = visibleWidgets.filter { it.isEnabled }
    
    val widgetValues = mutableMapOf<String, Pair<String, String>>()
    widgetValues["duration"] = strings.durationLabel to data.duration
    widgetValues["max_bpm"] = strings.maxHeartRateLabel to "${data.maxBpm} ${strings.bpmUnit.lowercase()}"
    widgetValues["avg_bpm"] = strings.avgHeartRateLabel to "${data.avgBpm} ${strings.bpmUnit.lowercase()}"
    widgetValues["total_calories"] = strings.totalCaloriesLabel to "${data.totalCalories} ${strings.kcalUnit}"
    widgetValues["max_calories_min"] = strings.maxCaloriesBurnLabel to String.format(Locale.US, "%.2f %s", data.maxCaloriesMin, strings.kcalPerMinUnit)
    widgetValues["avg_pace"] = strings.avgPaceLabel to formatPace(data.avgPace, strings)
    widgetValues["max_speed"] = strings.maxSpeedLabel to String.format(Locale.US, "%.1f %s", data.maxSpeed, strings.kmhUnit)
    widgetValues["max_altitude"] = strings.maxAltitudeLabel to String.format(Locale.US, "%.0f %s", data.maxAltitude, strings.metersUnit)
    widgetValues["total_ascent"] = strings.totalAscentLabel to String.format(Locale.US, "+%.0f %s", data.totalAscent, strings.metersUnit)
    widgetValues["total_descent"] = strings.totalDescentLabel to String.format(Locale.US, "-%.0f %s", data.totalDescent, strings.metersUnit)
    widgetValues["avg_step_length"] = strings.avgStepLengthLabel to String.format(Locale.US, "%.2f %s", data.avgStepLength, strings.metersUnit)
    widgetValues["avg_cadence"] = strings.avgCadenceLabel to String.format(Locale.US, "%.0f %s", data.avgCadence, strings.cadenceUnit)
    widgetValues["max_cadence"] = strings.maxCadenceLabel to String.format(Locale.US, "%.0f %s", data.maxCadence, strings.cadenceUnit)
    widgetValues["total_steps"] = strings.totalStepsLabel to "${data.totalSteps}"
    widgetValues["total_distance_gps"] = "${strings.distance} (GPS)" to formatDistance(data.totalDistanceGps, strings)
    widgetValues["total_distance_steps"] = "${strings.distance} (${strings.steps.lowercase()})" to formatDistance(data.totalDistanceSteps, strings)
    widgetValues["pressure_start"] = strings.pressureStartLabel to (data.pressureStart?.let { String.format(Locale.US, "%.1f %s", it, strings.hpaUnit) } ?: "-- ${strings.hpaUnit}")
    widgetValues["pressure_end"] = strings.pressureEndLabel to (data.pressureEnd?.let { String.format(Locale.US, "%.1f %s", it, strings.hpaUnit) } ?: "-- ${strings.hpaUnit}")
    widgetValues["max_pressure"] = strings.maxPressureLabel to (data.maxPressure?.let { String.format(Locale.US, "%.1f %s", it, strings.hpaUnit) } ?: "-- ${strings.hpaUnit}")
    widgetValues["min_pressure"] = strings.minPressureLabel to (data.minPressure?.let { String.format(Locale.US, "%.1f %s", it, strings.hpaUnit) } ?: "-- ${strings.hpaUnit}")
    widgetValues["best_pace_1km"] = strings.bestPace1kmLabel to formatPace(data.bestPace1km ?: 0.0, strings)

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        val chunks = enabledWidgets.chunked(2)
        chunks.forEachIndexed { index, chunk ->
            if (index > 0) Spacer(modifier = Modifier.height(8.dp))
            
            if (chunk.size == 2) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val w1 = widgetValues[chunk[0].id]
                    val w2 = widgetValues[chunk[1].id]
                    if (w1 != null) SummaryItem(label = w1.first, value = w1.second, modifier = Modifier.weight(1f))
                    if (w2 != null) SummaryItem(label = w2.first, value = w2.second, modifier = Modifier.weight(1f))
                }
            } else {
                val w = widgetValues[chunk[0].id]
                if (w != null) SummaryItem(label = w.first, value = w.second, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

private fun formatPace(paceDecimal: Double, strings: AppStrings): String {
    if (paceDecimal <= 0 || paceDecimal > 120) return "--:--"
    val minutes = paceDecimal.toInt()
    val seconds = ((paceDecimal - minutes) * 60).toInt()
    return String.format(Locale.US, "%02d:%02d %s", minutes, seconds, strings.paceUnit)
}

private fun formatDistance(distanceMeters: Double, strings: AppStrings): String {
    return if (distanceMeters >= 1000) {
        String.format(Locale.US, "%.2f %s", distanceMeters / 1000.0, strings.kmUnit)
    } else {
        String.format(Locale.US, "%.0f %s", distanceMeters, strings.metersUnit)
    }
}

@Composable
fun SummaryItem(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

private fun getUnitForWidget(id: String, strings: AppStrings): String {
    return when(id) {
        "bpm", "srednie_bpm" -> strings.bpmUnit.lowercase()
        "kalorie_min", "kalorie_suma" -> strings.kcalUnit
        "kroki_min" -> strings.cadenceUnit
        "odl_kroki", "gps_dystans" -> strings.metersUnit
        "predkosc", "predkosc_kroki" -> strings.kmhUnit
        "wysokosc", "przewyzszenia_gora", "przewyzszenia_dol" -> strings.metersUnit
        "pressure" -> strings.hpaUnit
        else -> ""
    }
}
