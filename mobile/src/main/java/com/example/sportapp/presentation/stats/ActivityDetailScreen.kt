package com.example.sportapp.presentation.stats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sportapp.R
import com.example.sportapp.data.model.WorkoutLap
import com.example.sportapp.data.model.HeartRateZoneResult
import com.example.sportapp.data.model.ZoneStat
import com.example.sportapp.presentation.settings.WidgetItem
import com.example.sportapp.presentation.settings.ThemeMode
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import java.util.Locale
import kotlin.math.pow

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
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val hrZoneResult by viewModel.hrZoneResult.collectAsStateWithLifecycle()
    val mobileSettings by viewModel.mobileSettings.collectAsStateWithLifecycle()
    val autoLapDistance by viewModel.autoLapDistance.collectAsStateWithLifecycle()

    var isIntervalsExpanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val isDarkTheme = when (mobileSettings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    if (error != null) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Błąd danych") },
            text = { Text(error ?: "Błąd") },
            confirmButton = { Button(onClick = { viewModel.clearError(); onNavigateBack() }) { Text("OK") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły aktywności") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót") } }
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
                // Nagłówek
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = data.activityName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text(text = data.activityDate, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Widgety podsumowania
                item {
                    SummaryWidgetsGrid(data, settings.visibleWidgets)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Dynamiczne sekcje (Mapy, Wykresy)
                items(settings.visibleCharts.filter { it.isEnabled }) { widget ->
                    when (widget.id) {
                        "map" -> {
                            if (data.route.isNotEmpty()) {
                                MapSection(data, settings, isDarkTheme, selectedLap, selectedIndex)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            if (laps.isNotEmpty()) {
                                ExpandableLapsSection(
                                    laps = laps,
                                    selectedLap = selectedLap,
                                    onLapClick = { viewModel.selectLap(it) },
                                    autoLapDistance = autoLapDistance,
                                    isExpanded = isIntervalsExpanded,
                                    onToggleExpanded = { isIntervalsExpanded = !isIntervalsExpanded }
                                )
                            }
                        }
                        "bpm" -> {
                            val producer = viewModel.chartProducers["bpm"]
                            if (producer != null && (data.charts["bpm"]?.filterNotNull()?.isNotEmpty() == true)) {
                                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    HeartRateChartSection("Tętno (bpm)", producer, data.times, hrZoneResult) { selectedIndex = it }
                                }
                                hrZoneResult?.let { result ->
                                    Spacer(modifier = Modifier.height(24.dp))
                                    HeartRateZonesSection(result)
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                        else -> {
                            val producer = viewModel.chartProducers[widget.id]
                            if (producer != null && (data.charts[widget.id]?.filterNotNull()?.isNotEmpty() == true)) {
                                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    CommonChartSection(
                                        title = widget.label, 
                                        producer = producer, 
                                        unit = getUnitForWidget(widget.id), 
                                        detailTimes = data.times,
                                        onMarkerShown = { selectedIndex = it }
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

@Composable
fun MapSection(
    data: com.example.sportapp.data.SessionData,
    settings: ActivityDetailSettings,
    isDarkTheme: Boolean,
    selectedLap: WorkoutLap?,
    selectedIndex: Int? = null
) {
    val cameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current

    LaunchedEffect(selectedLap, data.route) {
        val pointsToShow = if (selectedLap != null) {
            val lap = selectedLap
            data.route.subList(lap.startLocationIndex.coerceIn(data.route.indices), (lap.endLocationIndex + 1).coerceIn(data.route.indices))
        } else data.route

        if (pointsToShow.isNotEmpty()) {
            val bounds = LatLngBounds.Builder().apply { pointsToShow.forEach { include(it) } }.build()
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex != null && selectedIndex in data.route.indices) {
            cameraPositionState.animate(CameraUpdateFactory.newLatLng(data.route[selectedIndex]))
        }
    }

    Box(modifier = Modifier.fillMaxWidth().height(250.dp).padding(horizontal = 16.dp).clip(RoundedCornerShape(12.dp))) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapStyleOptions = if (isDarkTheme) MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark) else null),
            onMapClick = { /* Opcjonalnie reset zaznaczenia lapu można przenieść wyżej */ }
        ) {
            Polyline(points = data.route, color = Color(settings.trackColor), width = 10f)
            selectedLap?.let { lap ->
                val lapPoints = data.route.subList(lap.startLocationIndex.coerceIn(data.route.indices), (lap.endLocationIndex + 1).coerceIn(data.route.indices))
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
                        contentDescription = if (isExpanded) "Zwiń" else "Rozwiń",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (autoLapDistance != null && autoLapDistance > 0) "Odcinki (${formatDistance(autoLapDistance)})" else "Odcinki",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (!isExpanded) {
                    Text(
                        text = "Liczba odcinków: ${laps.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                LapsTable(laps, selectedLap, onLapClick, autoLapDistance)
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
    onMarkerShown: (Int?) -> Unit
) {
    CommonChartSection(
        title = title, 
        producer = producer, 
        unit = "bpm", 
        detailTimes = detailTimes, 
        hrZoneResult = hrZoneResult,
        onMarkerShown = onMarkerShown
    )
}

@Composable
fun HeartRateZonesSection(result: HeartRateZoneResult) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Strefy tętna", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DonutChart(stats = result.zones, modifier = Modifier.size(100.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Przeważający efekt treningu", style = MaterialTheme.typography.labelMedium)
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

@Composable
fun ZoneRow(stat: ZoneStat) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(stat.zone.color))
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(stat.zone.displayName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text("${stat.minBpm}-${stat.maxBpm} bpm", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
fun LapsTable(laps: List<WorkoutLap>, selectedLap: WorkoutLap?, onLapClick: (WorkoutLap) -> Unit, autoLapDistance: Double?) {
    val validLapsForPace = laps.filter { it.avgPaceSecondsPerKm > 0 }
    val fastestPace = validLapsForPace.minOfOrNull { it.avgPaceSecondsPerKm } ?: 0
    val slowestPace = validLapsForPace.maxOfOrNull { it.avgPaceSecondsPerKm } ?: 0
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Column {
        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
            Column {
                Row {
                    Box(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer).padding(8.dp)) { LapCell("Nr", width = 40.dp, isHeader = true) }
                    Box(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                        Row(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer).padding(8.dp)) {
                            LapCell("Czas", width = 80.dp, isHeader = true)
                            LapCell("Śr. Tempo", width = 90.dp, isHeader = true)
                            LapCell("Śr. Prędkość", width = 100.dp, isHeader = true)
                            LapCell("Max Prędkość", width = 100.dp, isHeader = true)
                            LapCell("Śr. HR", width = 70.dp, isHeader = true)
                            LapCell("Max HR", width = 70.dp, isHeader = true)
                            LapCell("Góra/Dół", width = 100.dp, isHeader = true)
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
                                    LapCell(String.format(Locale.US, "%.1f km/h", lap.avgSpeed), width = 100.dp)
                                    LapCell(String.format(Locale.US, "%.1f km/h", lap.maxSpeed), width = 100.dp)
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
fun SummaryWidgetsGrid(data: com.example.sportapp.data.SessionData, visibleWidgets: List<WidgetItem>) {
    val enabledWidgets = visibleWidgets.filter { it.isEnabled }
    val widgetValues = mapOf(
        "duration" to ("Czas trwania" to data.duration),
        "max_bpm" to ("Maksymalne tętno" to "${data.maxBpm} bpm"),
        "avg_bpm" to ("Średnie tętno" to "${data.avgBpm} bpm"),
        "total_calories" to ("Spalone kalorie" to "${data.totalCalories} kcal"),
        "max_calories_min" to ("Maks spalanie kalorii" to String.format(Locale.US, "%.2f kcal/min", data.maxCaloriesMin)),
        "avg_pace" to ("Średnie tempo" to formatPace(data.avgPace)),
        "max_speed" to ("Maks prędkość" to String.format(Locale.US, "%.1f km/h", data.maxSpeed)),
        "max_altitude" to ("Maks wysokość" to String.format(Locale.US, "%.0f m n.p.m.", data.maxAltitude)),
        "total_ascent" to ("Suma podejść" to String.format(Locale.US, "+%.0f m", data.totalAscent)),
        "total_descent" to ("Suma zejść" to String.format(Locale.US, "-%.0f m", data.totalDescent)),
        "avg_step_length" to ("Wyliczona długość kroku" to String.format(Locale.US, "%.2f m", data.avgStepLength)),
        "avg_cadence" to ("Śr. kadencja" to String.format(Locale.US, "%.0f kr/min", data.avgCadence)),
        "max_cadence" to ("Maks. kadencja" to String.format(Locale.US, "%.0f kr/min", data.maxCadence)),
        "total_steps" to ("Liczba kroków" to "${data.totalSteps}"),
        "total_distance_gps" to ("Dystans (GPS)" to formatDistance(data.totalDistanceGps)),
        "total_distance_steps" to ("Dystans (kroki)" to formatDistance(data.totalDistanceSteps)),
        "pressure_start" to ("Ciśnienie atm. (start)" to (data.pressureStart?.let { String.format(Locale.US, "%.1f hPa", it) } ?: "-- hPa")),
        "pressure_end" to ("Ciśnienie atm. (koniec)" to (data.pressureEnd?.let { String.format(Locale.US, "%.1f hPa", it) } ?: "-- hPa")),
        "max_pressure" to ("Maks. ciśnienie" to (data.maxPressure?.let { String.format(Locale.US, "%.1f hPa", it) } ?: "-- hPa")),
        "min_pressure" to ("Min. ciśnienie" to (data.minPressure?.let { String.format(Locale.US, "%.1f hPa", it) } ?: "-- hPa")),
        "best_pace_1km" to ("Najlepsze tempo (1km)" to formatPace(data.bestPace1km ?: 0.0))
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

private fun formatPace(paceDecimal: Double): String {
    if (paceDecimal <= 0 || paceDecimal > 120) return "--:--"
    return String.format(Locale.US, "%02d:%02d min/km", paceDecimal.toInt(), ((paceDecimal - paceDecimal.toInt()) * 60).toInt())
}

private fun formatDistance(distanceMeters: Double): String {
    return if (distanceMeters >= 1000) String.format(Locale.US, "%.2f km", distanceMeters / 1000.0) else String.format(Locale.US, "%.0f m", distanceMeters)
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

private fun getUnitForWidget(id: String): String {
    return when(id) {
        "bpm", "srednie_bpm" -> "bpm"
        "kalorie_min", "kalorie_suma" -> "kcal"
        "kroki_min" -> "kroków/min"
        "odl_kroki", "gps_dystans" -> "m"
        "predkosc", "predkosc_kroki" -> "km/h"
        "wysokosc", "przewyzszenia_gora", "przewyzszenia_dol" -> "m"
        "pressure" -> "hPa"
        else -> ""
    }
}
