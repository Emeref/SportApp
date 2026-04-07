package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
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
import com.example.sportapp.TextsWearPL

@Composable
fun SettingsScreen(
    navController: NavHostController, 
    currentClockColor: Color?,
    currentScreenBehavior: ScreenBehavior
) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text(TextsWearPL.SETTINGS_TITLE) } }
        
        item {
            Chip(
                label = { Text(TextsWearPL.SETTINGS_HEALTH_DATA) },
                onClick = { navController.navigate("health_data") },
                icon = { Icon(Icons.Default.HealthAndSafety, contentDescription = TextsWearPL.SETTINGS_HEALTH_DATA) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }

        item {
            val behaviorLabel = when (currentScreenBehavior) {
                ScreenBehavior.KEEP_SCREEN_ON -> TextsWearPL.SETTINGS_SCREEN_ALWAYS_ON
                ScreenBehavior.AMBIENT -> TextsWearPL.SETTINGS_SCREEN_AMBIENT
                ScreenBehavior.SYSTEM -> TextsWearPL.SETTINGS_SCREEN_AUTO
            }
            Chip(
                label = { Text(TextsWearPL.SETTINGS_SCREEN) },
                secondaryLabel = { Text(behaviorLabel) },
                onClick = { navController.navigate("screen_behavior_selection") },
                icon = { Icon(Icons.Default.Watch, contentDescription = TextsWearPL.SETTINGS_SCREEN) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        item {
            val colorLabel = when (currentClockColor) {
                Color.Red -> TextsWearPL.COLOR_RED
                Color.White -> TextsWearPL.COLOR_WHITE
                Color.Green -> TextsWearPL.COLOR_GREEN
                Color.Yellow -> TextsWearPL.COLOR_YELLOW
                Color.Blue -> TextsWearPL.COLOR_BLUE
                Color.Black -> TextsWearPL.COLOR_BLACK
                null -> TextsWearPL.COLOR_NONE
                else -> TextsWearPL.COLOR_CUSTOM
            }
            Chip(
                label = { Text(TextsWearPL.SETTINGS_CLOCK_COLOR) },
                secondaryLabel = { Text(colorLabel) },
                onClick = { navController.navigate("clock_color_selection") },
                icon = { Icon(Icons.Default.Schedule, contentDescription = TextsWearPL.SETTINGS_CLOCK_COLOR) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
    }
}
