package com.example.sportapp.presentation.menu

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.example.sportapp.presentation.workout.SummaryManager
import java.util.Locale

@Composable
fun StatisticsScreen() {
    val context = LocalContext.current
    val stats = remember { SummaryManager.getWeeklyStats(context) }
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text(
                text = "Ostatnie 7 dni",
                style = MaterialTheme.typography.caption1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        item {
            StatCard(
                label = "Dystans",
                value = if (stats.totalDistanceMeters >= 1000) 
                    String.format(Locale.US, "%.2f km", stats.totalDistanceMeters / 1000.0)
                else 
                    "${stats.totalDistanceMeters} m"
            )
        }
        item {
            StatCard(
                label = "Kroki",
                value = "${stats.totalSteps}"
            )
        }
        item {
            StatCard(
                label = "Kalorie",
                value = "${stats.totalCalories} kcal"
            )
        }
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
