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
import com.example.sportapp.core.i18n.AppStrings
import com.example.sportapp.core.i18n.LocalAppStrings
import com.example.sportapp.presentation.settings.ReportingPeriod
import com.example.sportapp.presentation.workout.SummaryManager
import java.util.Locale

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val settings by viewModel.settings.collectAsState(initial = null)
    val listState = rememberScalingLazyListState()
    val strings = LocalAppStrings.current

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        settings?.let { userSettings ->
            item {
                Text(
                    text = getPeriodLabel(userSettings.watchStatsPeriod, userSettings.watchStatsCustomDays, strings),
                    style = MaterialTheme.typography.caption1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            val enabledWidgets = userSettings.watchStatsWidgets.filter { it.isEnabled }
            
            items(enabledWidgets) { widget ->
                StatCardForWidget(widget.id, stats)
            }
            
            if (enabledWidgets.isEmpty()) {
                item {
                    Text(
                        text = strings.noFieldsSelected,
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
fun StatCardForWidget(id: String, stats: SummaryManager.WatchStats) {
    val strings = LocalAppStrings.current
    
    val label = when (id) {
        "count" -> strings.activityCount
        "calories" -> strings.totalCalories
        "distanceGps" -> strings.distanceGps
        "distanceSteps" -> strings.distanceSteps
        "ascent" -> strings.ascent
        "descent" -> strings.descent
        "steps" -> strings.allSteps
        else -> id
    }

    val value = when (id) {
        "count" -> "${stats.count}"
        "calories" -> "${stats.calories} ${strings.kcalUnit}"
        "distanceGps" -> formatDistance(stats.distanceGpsM, strings)
        "distanceSteps" -> formatDistance(stats.distanceStepsM, strings)
        "ascent" -> "${stats.ascent} ${strings.metersUnit}"
        "descent" -> "${stats.descent} ${strings.metersUnit}"
        "steps" -> "${stats.steps}"
        else -> "-"
    }

    StatCard(label = label, value = value)
}

fun formatDistance(meters: Int, strings: AppStrings): String {
    return if (meters >= 1000) 
        String.format(Locale.US, "%.2f %s", meters / 1000.0, strings.kmUnit)
    else 
        "$meters ${strings.metersUnit}"
}

fun getPeriodLabel(period: ReportingPeriod, customDays: Int, strings: AppStrings): String {
    return when (period) {
        ReportingPeriod.TODAY -> strings.today
        ReportingPeriod.WEEK -> strings.last7Days
        ReportingPeriod.MONTH -> strings.last30Days
        ReportingPeriod.YEAR -> strings.lastYear
        ReportingPeriod.CUSTOM -> strings.lastXDays(customDays)
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
