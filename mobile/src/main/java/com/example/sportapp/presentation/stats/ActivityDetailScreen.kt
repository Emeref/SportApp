package com.example.sportapp.presentation.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Mapa
                if (sessionData!!.route.isNotEmpty()) {
                    val startPos = sessionData!!.route.first()
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(startPos, 15f)
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState
                        ) {
                            Polyline(
                                points = sessionData!!.route,
                                color = Color(settings.trackColor),
                                width = 10f
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Wykresy
                sessionData?.let { data ->
                    settings.visibleElements.forEach { widget ->
                        val producer = viewModel.chartProducers[widget.id]
                        if (producer != null && (data.charts[widget.id]?.filterNotNull()?.isNotEmpty() == true)) {
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                CommonChartSection(
                                    title = widget.label,
                                    producer = producer,
                                    unit = getUnitForWidget(widget.id),
                                    detailTimes = data.times,
                                    isScrollEnabled = false // Zmienione na false, aby wykres dopasował się do ekranu
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

private fun getUnitForWidget(id: String): String {
    return when(id) {
        "bpm", "srednie_bpm" -> "bpm"
        "kroki", "kroki_min" -> "kroków"
        "kroki_dystans", "gps_dystans" -> "m"
        "predkosc_gps" -> "km/h"
        "wysokosc", "przewyzszenia_gora", "przewyzszenia_dol" -> "m"
        else -> ""
    }
}
