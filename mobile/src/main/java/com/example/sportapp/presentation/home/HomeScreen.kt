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

    val activeWidgets = settings.widgets.filter { it.isEnabled }

    val title = if (activeWidgets.isEmpty()) {
        "Nie masz wybranego żadnego widgeta"
    } else {
        when (settings.period) {
            ReportingPeriod.TODAY -> "Wyniki z dzisiaj:"
            ReportingPeriod.WEEK -> "Wyniki z tego tygodnia:"
            ReportingPeriod.MONTH -> {
                val monthName = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pl"))
                "Wyniki z $monthName:"
            }
            ReportingPeriod.YEAR -> "Wyniki z tego roku:"
            ReportingPeriod.CUSTOM -> if (settings.customDays == 1) "Wyniki z ostatniego dnia:" else "Wyniki z ostatnich ${settings.customDays} dni:"
        }
    }

    if (showSecretPopup) {
        Dialog(onDismissRequest = { showSecretPopup = false }) {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = { showSecretPopup = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Zamknij")
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
                            Icon(Icons.Default.Sync, contentDescription = "Synchronizuj")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Opcje")
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
                Text("Statystyki ogólne")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onNavigateToActivityList, modifier = Modifier.fillMaxWidth()) {
                Text("Szczegóły treningu")
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
    when (id) {
        "count" -> StatCard(modifier, "Liczba aktywności", formatLargeNumber(stats["count"]))
        "calories" -> StatCard(modifier, "Spalone kalorie", "${formatLargeNumber(stats["calories"])} kcal")
        "distanceGps" -> StatCard(modifier, "Dystans (GPS)", formatDistanceUI(stats["distanceGpsM"] as? Double ?: 0.0))
        "distanceSteps" -> StatCard(modifier, "Dystans (kroki)", formatDistanceUI(stats["distanceStepsM"] as? Double ?: 0.0))
        "ascent" -> StatCard(modifier, "W sumie w górę", "${formatLargeNumber(stats["ascent"])} m")
        "descent" -> StatCard(modifier, "W sumie do dołu", "${formatLargeNumber(stats["descent"])} m")
        "steps" -> StatCard(modifier, "Kroki", formatLargeNumber(stats["steps"]))
        "maxPressure" -> StatCard(modifier, "Maks. ciśnienie atm.", "${formatDecimal(stats["maxPressure"])} hPa")
        "minPressure" -> StatCard(modifier, "Min. ciśnienie atm.", "${formatDecimal(stats["minPressure"])} hPa")
        "bestPace1km" -> StatCard(modifier, "Najlepsze tempo (1km)", formatPace(stats["bestPace1km"] as? Double ?: 0.0))
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

private fun formatDecimal(value: Any?): String {
    val num = when (value) {
        is Number -> value.toDouble()
        is String -> value.toDoubleOrNull() ?: 0.0
        else -> 0.0
    }
    return String.format(Locale.US, "%.1f", num)
}

private fun formatPace(pace: Double): String {
    if (pace <= 0.0) return "0:00"
    val minutes = pace.toInt()
    val seconds = ((pace - minutes) * 60).toInt()
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}

private fun formatDistanceUI(meters: Double): String {
    return when {
        meters < 1000 -> "${formatLargeNumber(meters)} m"
        meters < 10000 -> String.format(Locale.US, "%.2f km", Math.floor(meters / 10.0) / 100.0)
        meters < 100000 -> String.format(Locale.US, "%.1f km", Math.floor(meters / 100.0) / 10.0)
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
