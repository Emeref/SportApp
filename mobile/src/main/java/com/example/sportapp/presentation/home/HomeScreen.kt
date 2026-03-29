package com.example.sportapp.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.sportapp.R
import com.example.sportapp.core.i18n.LocalAppStrings
import com.example.sportapp.presentation.settings.ReportingPeriod
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToStats: () -> Unit,
    onNavigateToActivityList: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val settings by viewModel.settings.collectAsState()
    var showSecretPopup by remember { mutableStateOf(false) }
    val strings = LocalAppStrings.current

    val activeWidgets = settings.widgets.filter { it.isEnabled }

    val title = if (activeWidgets.isEmpty()) {
        strings.noWidgetsSelected
    } else {
        when (settings.period) {
            ReportingPeriod.TODAY -> strings.resultsFromToday
            ReportingPeriod.WEEK -> strings.resultsFromWeek
            ReportingPeriod.MONTH -> strings.resultsFromMonth
            ReportingPeriod.YEAR -> strings.resultsFromYear
            ReportingPeriod.CUSTOM -> strings.resultsFromLastDays(settings.customDays)
        }
    }

    if (showSecretPopup) {
        Dialog(onDismissRequest = { showSecretPopup = false }) {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = { showSecretPopup = false }) {
                            Icon(Icons.Default.Close, contentDescription = strings.stop)
                        }
                    }
                    Text("Super, że klikasz, ale tu nic nie ma", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_apki_biale),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).padding(end = 8.dp)
                        )
                        Text("SportApp")
                    }
                },
                navigationIcon = {
                    if (isSyncing) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        }
                    } else {
                        IconButton(onClick = { viewModel.triggerSync() }) {
                            Icon(Icons.Default.Sync, contentDescription = strings.sync)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = strings.options)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // Dynamiczne Widgety
            activeWidgets.chunked(2).forEachIndexed { _, rowItems ->
                if (rowItems.size == 2) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        WidgetFactory(rowItems[0].id, stats, Modifier.weight(1f))
                        WidgetFactory(rowItems[1].id, stats, Modifier.weight(1f))
                    }
                } else {
                    WidgetFactory(rowItems[0].id, stats, Modifier.fillMaxWidth())
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onNavigateToStats, modifier = Modifier.fillMaxWidth()) {
                Text(strings.generalStats)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onNavigateToActivityList, modifier = Modifier.fillMaxWidth()) {
                Text(strings.workoutDetails)
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Image(
                painter = painterResource(id = R.drawable.logo_apki_biale),
                contentDescription = "Logo Apki",
                modifier = Modifier
                    .height(40.dp)
                    .padding(vertical = 8.dp)
                    .clickable { showSecretPopup = true }
            )
        }
    }
}

@Composable
fun WidgetFactory(id: String, stats: Map<String, Any>, modifier: Modifier) {
    val strings = LocalAppStrings.current
    when (id) {
        "count" -> StatCard(modifier, strings.activityCount, formatLargeNumber(stats["count"]))
        "calories" -> StatCard(modifier, strings.totalCalories, "${formatLargeNumber(stats["calories"])} kcal")
        "distanceGps" -> StatCard(modifier, "${strings.distance} (GPS)", formatDistanceUI(stats["distanceGpsM"] as? Double ?: 0.0))
        "distanceSteps" -> StatCard(modifier, "${strings.distance} (${strings.steps.lowercase()})", formatDistanceUI(stats["distanceStepsM"] as? Double ?: 0.0))
        "ascent" -> StatCard(modifier, strings.ascent, "${formatLargeNumber(stats["ascent"])} m")
        "descent" -> StatCard(modifier, strings.descent, "${formatLargeNumber(stats["descent"])} m")
        "steps" -> StatCard(modifier, strings.steps, formatLargeNumber(stats["steps"]))
        "avg_cadence" -> StatCard(modifier, strings.avgCadence, String.format(Locale.US, "%.0f kr/min", stats["avg_cadence"] as? Double ?: 0.0))
        "max_speed" -> StatCard(modifier, strings.maxSpeed, String.format(Locale.US, "%.1f km/h", stats["max_speed"] as? Double ?: 0.0))
        "max_altitude" -> StatCard(modifier, strings.maxAltitude, String.format(Locale.US, "%.0f m", stats["max_altitude"] as? Double ?: 0.0))
        "max_elevation_gain" -> StatCard(modifier, strings.maxElevationGain, String.format(Locale.US, "+%.0f m", stats["max_elevation_gain"] as? Double ?: 0.0))
        "max_distance" -> StatCard(modifier, strings.maxDistance, formatDistanceUI(stats["max_distance"] as? Double ?: 0.0))
        "max_duration" -> StatCard(modifier, strings.maxDuration, formatDuration(stats["max_duration"] as? Long ?: 0L))
        "max_calories" -> StatCard(modifier, strings.maxCalories, "${formatLargeNumber(stats["max_calories"])} kcal")
        "max_avg_cadence" -> StatCard(modifier, strings.maxAvgCadence, String.format(Locale.US, "%.0f kr/min", stats["max_avg_cadence"] as? Double ?: 0.0))
        "max_avg_speed" -> StatCard(modifier, strings.maxAvgSpeed, String.format(Locale.US, "%.1f km/h", stats["max_avg_speed"] as? Double ?: 0.0))
    }
}

private fun formatLargeNumber(value: Any?): String {
    val num = when (value) {
        is Number -> value.toLong()
        is String -> value.toDoubleOrNull()?.toLong() ?: 0L
        else -> 0L
    }
    
    val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ' '
    }
    val formatter = DecimalFormat("#,###", symbols)
    return formatter.format(num)
}

private fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
    else String.format(Locale.US, "%02d:%02d", m, s)
}

private fun formatDistanceUI(meters: Double): String {
    return when {
        meters < 1000 -> "${formatLargeNumber(meters)} m"
        meters < 10000 -> String.format(Locale.US, "%.2f km", Math.floor(meters / 10.0) / 100.0)
        meters < 100000 -> String.format(Locale.US, "%.1f km", Math.floor(meters / 10.0) / 10.0)
        else -> "${formatLargeNumber(Math.floor(meters / 1000.0))} km"
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, label: String, value: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}
