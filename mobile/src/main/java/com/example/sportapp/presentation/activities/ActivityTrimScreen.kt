package com.example.sportapp.presentation.activities

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sportapp.LocalMobileTexts
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
    val texts = LocalMobileTexts.current
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
            title = { Text(texts.TRIM_CONFIRM_TITLE) },
            text = { Text(texts.TRIM_CONFIRM_DESC) },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    viewModel.saveTrim {
                        onNavigateBack()
                    }
                }) {
                    Text(texts.TRIM_SAVE_BTN, color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(texts.SETTINGS_CANCEL)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(texts.TRIM_TITLE) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = texts.SETTINGS_CLOSE)
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
                            text = "${texts.ACTIVITY_FROM}: ${sdf.format(Date(w.startTime))}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = texts.TRIM_CHART_HR,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.height(250.dp)) {
                    CommonChartSection(
                        title = "${texts.DETAIL_HEART_RATE} (${texts.UNIT_BPM})",
                        producer = hrProducer,
                        unit = texts.UNIT_BPM,
                        detailTimes = points.map { it.time },
                        isScrollEnabled = false
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = texts.TRIM_RANGE_TITLE,
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
                    Text("${texts.TRIM_START}: ${points[startIndex].time}", style = MaterialTheme.typography.labelSmall)
                    Text("${texts.TRIM_END}: ${points[endIndex].time}", style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(texts.TRIM_PREVIEW_TITLE, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        previewStats?.let { stats ->
                            StatRow(texts.TRIM_NEW_DURATION, stats.duration)
                            StatRow(texts.TRIM_DISTANCE_GPS, stats.distanceGps)
                            StatRow(texts.TRIM_DISTANCE_STEPS, stats.distanceSteps)
                            StatRow(texts.TRIM_CALORIES, stats.calories)
                            StatRow(texts.TRIM_AVG_BPM, stats.avgBpm)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving
                ) {
                    Text(texts.SETTINGS_SAVE)
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
