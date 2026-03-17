package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Watch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import com.google.maps.android.compose.MapType

@Composable
fun SettingsScreen(
    navController: NavHostController, 
    currentMapType: MapType, 
    currentClockColor: Color?,
    currentScreenBehavior: ScreenBehavior
) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text("Ustawienia") } }
        
        item {
            Chip(
                label = { Text("Dane zdrowotne") },
                onClick = { navController.navigate("health_data") },
                icon = { Icon(Icons.Default.HealthAndSafety, contentDescription = "Dane zdrowotne") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }

        item {
            Chip(
                label = { Text("Mapa") },
                onClick = { navController.navigate("map_settings") },
                icon = { Icon(Icons.Default.Map, contentDescription = "Mapa") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        item {
            val behaviorLabel = when (currentScreenBehavior) {
                ScreenBehavior.KEEP_SCREEN_ON -> "Zawsze włączony"
                ScreenBehavior.AMBIENT -> "Tryb Ambient"
                ScreenBehavior.SYSTEM -> "Systemowe"
            }
            Chip(
                label = { Text("Ekran") },
                secondaryLabel = { Text(behaviorLabel) },
                onClick = { navController.navigate("screen_behavior_selection") },
                icon = { Icon(Icons.Default.Watch, contentDescription = "Ekran") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        item {
            val colorLabel = when (currentClockColor) {
                Color.Red -> "Czerwony"
                Color.White -> "Biały"
                Color.Green -> "Zielony"
                Color.Yellow -> "Żółty"
                Color.Blue -> "Niebieski"
                Color.Black -> "Czarny"
                null -> "Brak"
                else -> "Niestandardowy"
            }
            Chip(
                label = { Text("Kolor zegara") },
                secondaryLabel = { Text(colorLabel) },
                onClick = { navController.navigate("clock_color_selection") },
                icon = { Icon(Icons.Default.Schedule, contentDescription = "Kolor zegara") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
    }
}
