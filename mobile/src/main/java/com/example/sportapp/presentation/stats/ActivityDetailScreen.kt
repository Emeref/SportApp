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
                SummaryWidgetsGrid(data, settings.visibleElements)

                Spacer(modifier = Modifier.height(16.dp))

                // Wyświetlamy elementy w kolejności zdefiniowanej w ustawieniach i tylko te zaznaczone
                settings.visibleElements.filter { it.isEnabled }.forEach { widget ->
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
                            // Mapowanie klucza na SessionData.charts (predkosc -> predkosc_gps, odl_kroki -> kroki_dystans)
                            val chartKey = when(widget.id) {
                                "predkosc" -> "predkosc_gps"
                                "odl_kroki" -> "kroki_dystans"
                                else -> widget.id
                            }
                            
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
fun SummaryWidgetsGrid(data: com.example.sportapp.data.SessionData, visibleElements: List<WidgetItem>) {
    val isEnabled = { id: String -> visibleElements.any { it.id == id && it.isEnabled } }
    
    // Lista wszystkich potencjalnych widgetów (z nowymi nazwami)
    val allPossibleWidgets = mutableListOf<Pair<String, String>>()
    
    if (isEnabled("bpm")) {
        allPossibleWidgets.add("Maksymalne tętno" to "${data.maxBpm} bpm")
        allPossibleWidgets.add("Średnie tętno" to "${data.avgBpm} bpm")
    }
    
    if (isEnabled("kalorie_min")) {
        allPossibleWidgets.add("Spalone kalorie" to "${data.totalCalories} kcal")
        allPossibleWidgets.add("Maks spalanie kalorii" to String.format(Locale.US, "%.2f kcal/min", data.maxCaloriesMin))
    }

    val showGpsStats = isEnabled("map") || isEnabled("gps_dystans") || isEnabled("wysokosc") || 
                       isEnabled("przewyzszenia_gora") || isEnabled("przewyzszenia_dol") || isEnabled("predkosc")
    
    if (showGpsStats) {
        allPossibleWidgets.add("Maks prędkość" to String.format(Locale.US, "%.1f km/h", data.maxSpeedGps))
        allPossibleWidgets.add("Dystans" to formatDistance(data.totalDistanceGps))
    }

    val showStepsStats = isEnabled("odl_kroki") || isEnabled("predkosc_kroki") || isEnabled("kroki_min")
    
    if (showStepsStats) {
        allPossibleWidgets.add("Maks prędkość (kroki)" to String.format(Locale.US, "%.1f km/h", data.maxSpeedSteps))
        allPossibleWidgets.add("Liczba kroków" to "${data.totalSteps}")
        allPossibleWidgets.add("Dystans (kroki)" to formatDistance(data.totalDistanceSteps))
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // 1. Pierwszy wiersz to zawsze 'Czas trwania'
        SummaryItem(label = "Czas trwania", value = data.duration, modifier = Modifier.fillMaxWidth())
        
        // 2. Kolejne wiersze (2 widgety na wiersz, ostatni na pełną szerokość jeśli nieparzyste)
        val chunks = allPossibleWidgets.chunked(2)
        chunks.forEach { chunk ->
            Spacer(modifier = Modifier.height(8.dp))
            if (chunk.size == 2) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryItem(label = chunk[0].first, value = chunk[0].second, modifier = Modifier.weight(1f))
                    SummaryItem(label = chunk[1].first, value = chunk[1].second, modifier = Modifier.weight(1f))
                }
            } else {
                // Ostatni widget w nieparzystym zestawie na całą długość
                SummaryItem(label = chunk[0].first, value = chunk[0].second, modifier = Modifier.fillMaxWidth())
            }
        }
    }
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
