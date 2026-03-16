package com.example.sportapp.presentation.activities

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sportapp.presentation.stats.CommonChartSection
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityTrimScreen(
    viewModel: ActivityTrimViewModel,
    onNavigateBack: () -> Unit
) {
    val workout by viewModel.workout.collectAsState()
    val points by viewModel.points.collectAsState()
    val trimRange by viewModel.trimRange.collectAsState()
    val previewStats by viewModel.previewStats.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    var showConfirmDialog by remember { mutableStateOf(false) }

    val hrProducer = remember { ChartEntryModelProducer() }
    val sdf = remember { SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()) }

    LaunchedEffect(points) {
        if (points.isNotEmpty()) {
            hrProducer.setEntries(points.mapIndexed { index, p ->
                entryOf(index.toFloat(), (p.bpm ?: 0).toFloat())
            })
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Potwierdź przycięcie") },
            text = { Text("Czy na pewno chcesz usunąć dane poza wybranym zakresem? Te dane zostaną trwale usunięte z bazy danych.") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    viewModel.saveTrim {
                        onNavigateBack()
                    }
                }) {
                    Text("Przytnij i zapisz", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edytuj trening (Przycinanie)") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót")
                    }
                },

            )
        }
    ) { padding ->
        if (workout == null || points.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val w = workout!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Typ i czas startu
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = w.activityName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Start: ${sdf.format(Date(w.startTime))}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Wykres tętna",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.height(250.dp)) {
                    CommonChartSection(
                        title = "Tętno (BPM)",
                        producer = hrProducer,
                        unit = "bpm",
                        detailTimes = points.map { it.time },
                        isScrollEnabled = false
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Wybierz zakres treningu",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.fillMaxWidth()
                )

                RangeSlider(
                    value = trimRange,
                    onValueChange = { viewModel.onTrimRangeChanged(it) },
                    valueRange = 0f..(points.size - 1).toFloat().coerceAtLeast(1f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val startIndex = trimRange.start.toInt().coerceIn(points.indices)
                    val endIndex = trimRange.endInclusive.toInt().coerceIn(points.indices)
                    Text("Start: ${points[startIndex].time}", style = MaterialTheme.typography.labelSmall)
                    Text("Koniec: ${points[endIndex].time}", style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Podgląd nowych statystyk", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        previewStats?.let { stats ->
                            StatRow("Nowy czas trwania:", stats.duration)
                            StatRow("Dystans (GPS):", stats.distanceGps)
                            StatRow("Dystans (Kroki):", stats.distanceSteps)
                            StatRow("Spalone kalorie:", stats.calories)
                            StatRow("Średnie tętno:", stats.avgBpm)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving
                ) {
                    Text("Zatwierdź zmiany")
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}
