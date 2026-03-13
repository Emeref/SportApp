package com.example.sportapp.presentation.menu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import com.example.sportapp.presentation.workout.SportConfig

@Composable
fun ChooseSportScreen(
    sportsConfig: List<SportConfig>,
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
        
        if (sportsConfig.isEmpty() || (sportsConfig.size == 1 && sportsConfig[0].id == "default")) {
            // Jeśli nie ma zsynchronizowanych sportów, pokażmy domyślne
            val defaultSports = listOf(
                Triple("Spacer", Icons.Default.DirectionsWalk, "walking"),
                Triple("Wspinaczka", Icons.Default.Terrain, "climbing")
            )
            defaultSports.forEach { (name, icon, type) ->
                item {
                    SportChip(name, icon) { onWorkoutSelected(type) }
                }
            }
        } else {
            sportsConfig.forEach { sport ->
                item {
                    SportChip(
                        name = sport.name,
                        icon = getIconForSport(sport.id)
                    ) { onWorkoutSelected(sport.id) }
                }
            }
        }
    }
}

@Composable
private fun SportChip(name: String, icon: ImageVector, onClick: () -> Unit) {
    Chip(
        label = { Text(name) },
        onClick = onClick,
        icon = { Icon(icon, contentDescription = name) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

private fun getIconForSport(id: String): ImageVector {
    return when {
        id.contains("walk", true) || id.contains("spacer", true) -> Icons.Default.DirectionsWalk
        id.contains("climb", true) || id.contains("wspin", true) -> Icons.Default.Terrain
        id.contains("run", true) || id.contains("bieg", true) -> Icons.Default.DirectionsRun
        id.contains("bike", true) || id.contains("rower", true) -> Icons.Default.DirectionsBike
        id.contains("tennis", true) -> Icons.Default.SportsTennis
        id.contains("gym", true) || id.contains("siłownia", true) -> Icons.Default.FitnessCenter
        id.contains("pool", true) || id.contains("basen", true) -> Icons.Default.Pool
        else -> Icons.Default.DirectionsRun // Domyślna ikona
    }
}
