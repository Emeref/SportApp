package com.example.sportapp.presentation.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text

@Composable
fun MainMenu(
    onStartWorkout: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit
) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Chip(
                label = { Text("Sport") },
                onClick = onStartWorkout,
                icon = { Icon(Icons.Default.DirectionsRun, contentDescription = "Sport") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
        item {
            Chip(
                label = { Text("Statystyki") },
                onClick = onHistory,
                icon = { Icon(Icons.Default.BarChart, contentDescription = "Statystyki") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                colors = ChipDefaults.secondaryChipColors()
            )
        }
        item {
            Chip(
                label = { Text("Ustawienia") },
                onClick = onSettings,
                icon = { Icon(Icons.Default.Settings, contentDescription = "Ustawienia") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}
