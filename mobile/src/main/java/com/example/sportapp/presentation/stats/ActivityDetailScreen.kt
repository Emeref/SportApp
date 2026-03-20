package com.example.sportapp.presentation.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sportapp.data.model.WorkoutLap
import com.example.sportapp.presentation.settings.WidgetItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(
    viewModel: ActivityDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val sessionData by viewModel.sessionData.collectAsState()
    val laps by viewModel.laps.collectAsState()
    val selectedLap by viewModel.selectedLap.collectAsState()
    val error by viewModel.error.collectAsState()
    val settings by viewModel.settings.collectAsState()

    // Error Dialog
    if (error != null) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Błąd danych") },
            text = { Text(error ?: "Wystąpił nieoczekiwany błąd podczas odczytu pliku.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.clearError()
                    onNavigateBack()
                }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły aktywności") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót")
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
                SummaryWidgetsGrid(data, settings.visibleWidgets)

                Spacer(modifier = Modifier.height(16.dp))

                // Wyświetlamy wykresy w kolejności zdefiniowanej w ustawieniach i tylko te zaznaczone
                settings.visibleCharts.filter { it.isEnabled }.forEach { widget ->
                    when (widget.id) {
                        "map" -> {
                            if (data.route.isNotEmpty()) {
                                val startPos = data.route.first()
                                val cameraPositionState = rememberCameraPositionState {
                                    position = CameraPosition.fromLatLngZoom(startPos, 15f)
                                }

                                // Update camera when lap is selected
                                LaunchedEffect(selectedLap) {
                                    selectedLap?.let { lap ->
                                        val lapPoints = data.route.subList(
                                            lap.startLocationIndex.coerceIn(data.route.indices),
                                            (lap.endLocationIndex + 1).coerceIn(data.route.indices)
                                        )
                                        if (lapPoints.isNotEmpty()) {
                                            val boundsBuilder = LatLngBounds.Builder()
                                            lapPoints.forEach { boundsBuilder.include(it) }
                                            val bounds = boundsBuilder.build()
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newLatLngBounds(bounds, 50)
                                            )
                                        }
                                    }
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
                                                    color = Color.Cyan,
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
                                        onLapClick = { viewModel.selectLap(it) }
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
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
                                        unit = getUnitForWidget(widget.id),
                                        detailTimes = data.times,
                                        isScrollEnabled = false
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))
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
fun LapsTable(
    laps: List<WorkoutLap>,
    selectedLap: WorkoutLap?,
    onLapClick: (WorkoutLap) -> Unit
) {
    val validLapsForPace = laps.filter { it.avgPaceSecondsPerKm > 0 }
    val fastestPace = validLapsForPace.minOfOrNull { it.avgPaceSecondsPerKm } ?: 0
    val slowestPace = validLapsForPace.maxOfOrNull { it.avgPaceSecondsPerKm } ?: 0
    
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Odcinki", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                        LapCell("Nr", width = 40.dp, isHeader = true)
                    }
                    
                    // Scrollable Headers
                    Box(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                        Row(modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(8.dp)
                        ) {
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

                // Scrollable Rows - limited to approx 8 records
                Column(
                    modifier = Modifier
                        .heightIn(max = 280.dp)
                        .verticalScroll(verticalScrollState)
                ) {
                    laps.forEach { lap ->
                        val isSelected = selectedLap?.lapNumber == lap.lapNumber
                        val bgColor = when {
                            isSelected -> Color.Cyan.copy(alpha = 0.3f)
                            lap.avgPaceSecondsPerKm > 0 && lap.avgPaceSecondsPerKm == fastestPace && laps.size > 1 -> Color(0xFFC8E6C9) // Green
                            lap.avgPaceSecondsPerKm > 0 && lap.avgPaceSecondsPerKm == slowestPace && laps.size > 1 -> Color(0xFFFFCDD2) // Red
                            else -> Color.Transparent
                        }

                        Row {
                            // Fixed "Nr" Cell
                            Box(modifier = Modifier
                                .background(if (isSelected) Color.Cyan.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
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
    Text(
        text = text,
        modifier = Modifier.width(width),
        style = if (isHeader) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodySmall,
        fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
        textAlign = TextAlign.Center
    )
}

private fun formatMillis(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}

private fun formatPaceFromSeconds(totalSeconds: Int): String {
    if (totalSeconds <= 0) return "--:--"
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d/km", minutes, seconds)
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
        "avg_step_length" to ("Śr. długość kroku" to String.format(Locale.US, "%.2f m", data.avgStepLength)),
        "avg_cadence" to ("Śr. kadencja" to String.format(Locale.US, "%.0f kr/min", data.avgCadence)),
        "max_cadence" to ("Maks. kadencja" to String.format(Locale.US, "%.0f kr/min", data.maxCadence)),
        "total_steps" to ("Liczba kroków" to "${data.totalSteps}"),
        "total_distance_gps" to ("Dystans (GPS)" to formatDistance(data.totalDistanceGps)),
        "total_distance_steps" to ("Dystans (kroki)" to formatDistance(data.totalDistanceSteps)),
        "pressure_start" to ("Ciśnienie (start)" to (data.pressureStart?.let { String.format(Locale.US, "%.1f hPa", it) } ?: "-- hPa")),
        "pressure_end" to ("Ciśnienie (koniec)" to (data.pressureEnd?.let { String.format(Locale.US, "%.1f hPa", it) } ?: "-- hPa"))
    )

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

private fun formatPace(paceDecimal: Double): String {
    if (paceDecimal <= 0 || paceDecimal > 60) return "--:--"
    val minutes = paceDecimal.toInt()
    val seconds = ((paceDecimal - minutes) * 60).toInt()
    return String.format(Locale.US, "%02d:%02d min/km", minutes, seconds)
}

private fun formatDistance(distanceMeters: Double): String {
    return if (distanceMeters >= 1000) {
        String.format(Locale.US, "%.2f km", distanceMeters / 1000.0)
    } else {
        String.format(Locale.US, "%.0f m", distanceMeters)
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
