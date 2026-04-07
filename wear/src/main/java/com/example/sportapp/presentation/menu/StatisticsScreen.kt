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
import com.example.sportapp.LocalWearTexts
import com.example.sportapp.WearTexts
import com.example.sportapp.presentation.settings.ReportingPeriod
import com.example.sportapp.presentation.workout.SummaryManager
import java.util.Locale

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val texts = LocalWearTexts.current
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
                    text = getPeriodLabel(userSettings.watchStatsPeriod, userSettings.watchStatsCustomDays, texts),
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
                        text = texts.STATS_NO_WIDGETS,
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
        else -> "-"
    }

    StatCard(label = label, value = value)
}

fun formatDistance(meters: Int): String {
    return if (meters >= 1000) 
        String.format(Locale.US, "%.2f km", meters / 1000.0)
    else 
        "$meters m"
}

fun getPeriodLabel(period: ReportingPeriod, customDays: Int, texts: WearTexts): String {
    return when (period) {
        ReportingPeriod.TODAY -> texts.STATS_PERIOD_TODAY
        ReportingPeriod.WEEK -> texts.STATS_PERIOD_7_DAYS
        ReportingPeriod.MONTH -> texts.STATS_PERIOD_30_DAYS
        ReportingPeriod.YEAR -> texts.STATS_PERIOD_YEAR
        ReportingPeriod.CUSTOM -> texts.statsPeriodCustom(customDays)
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
