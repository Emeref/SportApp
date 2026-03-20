package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.google.maps.android.compose.MapType

@Composable
fun MapSettingsScreen(
    navController: NavHostController,
    currentMapType: MapType,
    currentAutoCenterDelay: Int,
    showRoute: Boolean,
    onShowRouteToggle: (Boolean) -> Unit,
    routeColor: Color,
    currentZoom: Float
) {
    val listState = rememberScalingLazyListState()
    
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text("Ustawienia Mapy") } }
        
        item {
            val mapTypeLabel = when (currentMapType) {
                MapType.NORMAL -> "Normalna"
                MapType.SATELLITE -> "Satelitarna"
                MapType.TERRAIN -> "Terenowa"
                MapType.HYBRID -> "Hybrydowa"
                else -> currentMapType.name
            }
            Chip(
                label = { Text("Rodzaj mapy") },
                secondaryLabel = { Text(mapTypeLabel) },
                onClick = { navController.navigate("map_type_selection") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }

        item {
            Chip(
                label = { Text("Przybliżenie") },
                secondaryLabel = { Text("Poziom: ${currentZoom.toInt()}") },
                onClick = { navController.navigate("map_zoom_selection") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        item {
            Chip(
                label = { Text("Autocentrowanie") },
                secondaryLabel = { Text("${currentAutoCenterDelay}s bezczynności") },
                onClick = { navController.navigate("auto_center_delay_selection") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        item {
            ToggleChip(
                checked = showRoute,
                onCheckedChange = onShowRouteToggle,
                label = { Text("Pokaż ślad trasy") },
                toggleControl = {
                    Switch(checked = showRoute)
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }

        if (showRoute) {
            item {
                val colorLabel = when (routeColor) {
                    SettingsManager.Orange -> "Pomarańczowy"
                    Color.White -> "Biały"
                    Color.Black -> "Czarny"
                    Color.Red -> "Czerwony"
                    Color.Cyan -> "Niebieski"
                    Color.Green -> "Zielony"
                    Color.Yellow -> "Żółty"
                    else -> "Niestandardowy"
                }
                Chip(
                    label = { Text("Kolor śladu") },
                    secondaryLabel = { Text(colorLabel) },
                    onClick = { navController.navigate("route_color_selection") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
