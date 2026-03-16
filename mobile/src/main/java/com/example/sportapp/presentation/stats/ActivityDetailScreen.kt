package com.example.sportapp.presentation.stats

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
import androidx.compose.ui.unit.dp
import com.example.sportapp.presentation.settings.WidgetItem
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(
    viewModel: ActivityDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val sessionData by viewModel.sessionData.collectAsState()
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
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .padding(horizontal = 16.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    GoogleMap(
                                        modifier = Modifier.fillMaxSize(),
                                        cameraPositionState = cameraPositionState
                                    ) {
                                        Polyline(
                                            points = data.route,
                                            color = Color(settings.trackColor),
                                            width = 10f
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                        else -> {
                            val producer = viewModel.chartProducers[widget.id]
                            // Klucze w SessionData.charts są zsynchronizowane z chartProducers
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
        "total_distance_steps" to ("Dystans (kroki)" to formatDistance(data.totalDistanceSteps))
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
        else -> ""
    }
}
