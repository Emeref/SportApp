package com.example.sportapp.presentation.menu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text

@Composable
fun WorkoutTypeSelectionScreen(
    onWorkoutSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text("Wybierz sport") } }
        
        val sports = listOf(
            Triple("Spacer", Icons.Default.DirectionsWalk, "walking"),
            Triple("Wspinaczka", Icons.Default.Terrain, "climbing"),
            Triple("Tenis", Icons.Default.SportsTennis, "tennis"),
            Triple("Siłownia", Icons.Default.FitnessCenter, "gym"),
            Triple("Basen", Icons.Default.Pool, "pool"),
            Triple("Kajak", Icons.Default.Rowing, "kayak")
        )

        sports.forEach { (name, icon, type) ->
            item {
                Chip(
                    label = { Text(name) },
                    onClick = { onWorkoutSelected(type) },
                    icon = { Icon(icon, contentDescription = name) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
