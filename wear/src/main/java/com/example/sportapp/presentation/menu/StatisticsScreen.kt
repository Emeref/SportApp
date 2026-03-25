package com.example.sportapp.presentation.menu

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.example.sportapp.presentation.settings.ReportingPeriod
import com.example.sportapp.presentation.settings.UserSettings
import com.example.sportapp.presentation.workout.SummaryManager
import java.util.Locale

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val settings by viewModel.settings.collectAsState(initial = null)
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        settings?.let { userSettings ->
            item {
                Text(
                    text = getPeriodLabel(userSettings.watchStatsPeriod, userSettings.watchStatsCustomDays),
                    style = MaterialTheme.typography.caption1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            val enabledWidgets = userSettings.watchStatsWidgets.filter { it.isEnabled }
            
            items(enabledWidgets) { widget ->
                StatCardForWidget(widget.id, widget.label, stats)
            }
            
            if (enabledWidgets.isEmpty()) {
                item {
                    Text(
                        text = "Brak wybranych pól",
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } ?: item {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun StatCardForWidget(id: String, label: String, stats: SummaryManager.WatchStats) {
    val value = when (id) {
        "count" -> "${stats.count}"
        "calories" -> "${stats.calories} kcal"
        "distanceGps" -> formatDistance(stats.distanceGpsM)
        "distanceSteps" -> formatDistance(stats.distanceStepsM)
        "ascent" -> "${stats.ascent} m"
        "descent" -> "${stats.descent} m"
        "steps" -> "${stats.steps}"
        "maxPressure" -> String.format(Locale.US, "%.1f hPa", stats.maxPressure)
        "minPressure" -> String.format(Locale.US, "%.1f hPa", stats.minPressure)
        "bestPace1km" -> formatPace(stats.bestPace1km)
        else -> "-"
    }

    StatCard(label = label, value = value)
}

fun formatPace(pace: Double): String {
    if (pace <= 0.0) return "0:00"
    val minutes = pace.toInt()
    val seconds = ((pace - minutes) * 60).toInt()
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}

fun formatDistance(meters: Int): String {
    return if (meters >= 1000) 
        String.format(Locale.US, "%.2f km", meters / 1000.0)
    else 
        "$meters m"
}

fun getPeriodLabel(period: ReportingPeriod, customDays: Int): String {
    return when (period) {
        ReportingPeriod.TODAY -> "Dziś"
        ReportingPeriod.WEEK -> "Ostatnie 7 dni"
        ReportingPeriod.MONTH -> "Ostatnie 30 dni"
        ReportingPeriod.YEAR -> "Ostatni rok"
        ReportingPeriod.CUSTOM -> "Ostatnie $customDays dni"
    }
}

@Composable
fun StatCard(label: String, value: String) {
    Card(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        enabled = false
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.caption2,
                color = MaterialTheme.colors.secondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.title3
            )
        }
    }
}
