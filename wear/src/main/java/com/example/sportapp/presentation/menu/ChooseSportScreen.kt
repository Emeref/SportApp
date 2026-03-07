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
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text

@Composable
fun ChooseSportScreen(navController: NavHostController) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text("Wybierz sport") } }
        
        val sports = listOf(
            Triple("Spacer", Icons.Default.DirectionsWalk, "workout_walking"),
            Triple("Wspinaczka", Icons.Default.Terrain, "workout_climbing"),
            Triple("Tenis", Icons.Default.SportsTennis, "workout_tennis"),
            Triple("Siłownia", Icons.Default.FitnessCenter, "workout_gym"),
            Triple("Basen", Icons.Default.Pool, "workout_pool"),
            Triple("Kajak", Icons.Default.Rowing, "workout_kayak")
        )

        sports.forEach { (name, icon, route) ->
            item {
                Chip(
                    label = { Text(name) },
                    onClick = { navController.navigate(route) },
                    icon = { Icon(icon, contentDescription = name) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
