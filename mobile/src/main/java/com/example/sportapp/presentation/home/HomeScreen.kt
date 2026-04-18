package com.example.sportapp.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.R
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
    onNavigateToLiveTracking: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val texts = LocalMobileTexts.current
    val stats by viewModel.stats.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val locationDefinitions by viewModel.locationDefinitions.collectAsState()
    
    var showSecretPopup by remember { mutableStateOf(false) }
    var showActivitySelection by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val activeWidgets = settings.widgets.filter { it.isEnabled }

    val title = if (activeWidgets.isEmpty()) {
        texts.HOME_NO_WIDGETS
    } else {
        when (settings.period) {
            ReportingPeriod.TODAY -> texts.homeResultsToday()
            ReportingPeriod.WEEK -> texts.homeResultsWeek()
            ReportingPeriod.MONTH -> texts.homeResultsMonth()
            ReportingPeriod.YEAR -> texts.homeResultsYear()
            ReportingPeriod.CUSTOM -> texts.homeResultsCustom(settings.customDays)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            if (event == "live_tracking") {
                onNavigateToLiveTracking()
            }
        }
    }

    if (showSecretPopup) {
        Dialog(onDismissRequest = { showSecretPopup = false }) {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = { showSecretPopup = false }) {
                            Icon(Icons.Default.Close, contentDescription = texts.HOME_CLOSE)
                        }
                    }
                    Text(texts.HOME_SECRET_TITLE, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

    if (showActivitySelection) {
        ModalBottomSheet(
            onDismissRequest = { showActivitySelection = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = texts.LIVE_TRACKING_SELECT_ACTIVITY,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyColumn {
                    items(locationDefinitions) { definition ->
                        ListItem(
                            headlineContent = { Text(definition.name) },
                            leadingContent = {
                                Icon(
                                    imageVector = getIconVector(definition.iconName),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            modifier = Modifier.clickable {
                                viewModel.startActivityOnWatch(definition)
                                showActivitySelection = false
                            }
                        )
                    }
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
                        Text(texts.HOME_TITLE)
                    }
                },
                navigationIcon = {
                    if (isSyncing) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        }
                    } else {
                        IconButton(onClick = { viewModel.triggerSync() }) {
                            Icon(Icons.Default.Sync, contentDescription = texts.HOME_SYNC)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = texts.HOME_OPTIONS)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showActivitySelection = true }) {
                Icon(Icons.Default.Add, contentDescription = texts.HOME_START_LIVE)
            }
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
                Text(texts.HOME_GENERAL_STATS)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onNavigateToActivityList, modifier = Modifier.fillMaxWidth()) {
                Text(texts.HOME_WORKOUT_DETAILS)
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Image(
                painter = painterResource(id = R.drawable.logo_apki_biale),
                contentDescription = texts.HOME_LOGO_DESC,
                modifier = Modifier
                    .height(40.dp)
                    .padding(vertical = 8.dp)
                    .clickable { showSecretPopup = true }
            )
        }
    }
}

fun getIconVector(iconName: String): ImageVector {
    return when (iconName) {
        "directions_run" -> Icons.AutoMirrored.Filled.DirectionsRun
        "directions_walk" -> Icons.AutoMirrored.Filled.DirectionsWalk
        "directions_bike" -> Icons.Default.DirectionsBike
        "hiking" -> Icons.Default.Hiking
        "pool" -> Icons.Default.Pool
        "fitness_center" -> Icons.Default.FitnessCenter
        "timer" -> Icons.Default.Timer
        else -> Icons.Default.FitnessCenter
    }
}

@Composable
fun WidgetFactory(id: String, stats: Map<String, Any>, modifier: Modifier) {
    val texts = LocalMobileTexts.current
    val label = texts.getWidgetLabel(id)
    
    val value = when (id) {
        "count" -> formatLargeNumber(stats["count"])
        "calories" -> "${formatLargeNumber(stats["calories"])} ${texts.UNIT_KCAL}"
        "distanceGps" -> formatDistanceUI(stats["distanceGpsM"] as? Double ?: 0.0, texts.UNIT_M, texts.UNIT_KM)
        "distanceSteps" -> formatDistanceUI(stats["distanceStepsM"] as? Double ?: 0.0, texts.UNIT_M, texts.UNIT_KM)
        "ascent" -> "${formatLargeNumber(stats["ascent"])} ${texts.UNIT_M}"
        "descent" -> "${formatLargeNumber(stats["descent"])} ${texts.UNIT_M}"
        "steps" -> formatLargeNumber(stats["steps"])
        "avg_cadence" -> String.format(Locale.US, "%.0f %s", stats["avg_cadence"] as? Double ?: 0.0, texts.UNIT_STEP_MIN)
        "max_speed" -> String.format(Locale.US, "%.1f %s", stats["max_speed"] as? Double ?: 0.0, texts.UNIT_KM_H)
        "max_altitude" -> String.format(Locale.US, "%.0f %s", stats["max_altitude"] as? Double ?: 0.0, texts.UNIT_M)
        "max_elevation_gain" -> String.format(Locale.US, "+%.0f %s", stats["max_elevation_gain"] as? Double ?: 0.0, texts.UNIT_M)
        "max_distance" -> formatDistanceUI(stats["max_distance"] as? Double ?: 0.0, texts.UNIT_M, texts.UNIT_KM)
        "max_duration" -> formatDuration(stats["max_duration"] as? Long ?: 0L)
        "max_calories" -> "${formatLargeNumber(stats["max_calories"])} ${texts.UNIT_KCAL}"
        "max_avg_cadence" -> String.format(Locale.US, "%.0f %s", stats["max_avg_cadence"] as? Double ?: 0.0, texts.UNIT_STEP_MIN)
        "max_avg_speed" -> String.format(Locale.US, "%.1f %s", stats["max_avg_speed"] as? Double ?: 0.0, texts.UNIT_KM_H)
        else -> ""
    }
    
    StatCard(modifier, label, value)
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

private fun formatDistanceUI(meters: Double, unitM: String, unitKm: String): String {
    return when {
        meters < 1000 -> "${formatLargeNumber(meters)} $unitM"
        meters < 10000 -> String.format(Locale.US, "%.2f $unitKm", Math.floor(meters / 10.0) / 100.0)
        meters < 100000 -> String.format(Locale.US, "%.1f $unitKm", Math.floor(meters / 100.0) / 10.0)
        else -> "${formatLargeNumber(Math.floor(meters / 1000.0))} $unitKm"
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
