package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Language
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
import com.example.sportapp.LocalWearTexts

@Composable
fun SettingsScreen(
    navController: NavHostController, 
    currentClockColor: Color?,
    currentScreenBehavior: ScreenBehavior,
    currentLanguage: AppLanguage
) {
    val texts = LocalWearTexts.current
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text(texts.SETTINGS_TITLE) } }
        
        item {
            Chip(
                label = { Text(texts.SETTINGS_HEALTH_DATA) },
                onClick = { navController.navigate("health_data") },
                icon = { Icon(Icons.Default.HealthAndSafety, contentDescription = texts.SETTINGS_HEALTH_DATA) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }

        item {
            val behaviorLabel = when (currentScreenBehavior) {
                ScreenBehavior.KEEP_SCREEN_ON -> texts.SETTINGS_SCREEN_ALWAYS_ON
                ScreenBehavior.AMBIENT -> texts.SETTINGS_SCREEN_AMBIENT
                ScreenBehavior.SYSTEM -> texts.SETTINGS_SCREEN_AUTO
            }
            Chip(
                label = { Text(texts.SETTINGS_SCREEN) },
                secondaryLabel = { Text(behaviorLabel) },
                onClick = { navController.navigate("screen_behavior_selection") },
                icon = { Icon(Icons.Default.Watch, contentDescription = texts.SETTINGS_SCREEN) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        item {
            val colorLabel = when (currentClockColor) {
                Color.Red -> texts.COLOR_RED
                Color.White -> texts.COLOR_WHITE
                Color.Green -> texts.COLOR_GREEN
                Color.Yellow -> texts.COLOR_YELLOW
                Color.Blue -> texts.COLOR_BLUE
                Color.Black -> texts.COLOR_BLACK
                null -> texts.COLOR_NONE
                else -> texts.COLOR_CUSTOM
            }
            Chip(
                label = { Text(texts.SETTINGS_CLOCK_COLOR) },
                secondaryLabel = { Text(colorLabel) },
                onClick = { navController.navigate("clock_color_selection") },
                icon = { Icon(Icons.Default.Schedule, contentDescription = texts.SETTINGS_CLOCK_COLOR) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        item {
            Chip(
                label = { Text(texts.SETTINGS_LANGUAGE) },
                secondaryLabel = { Text(currentLanguage.label) },
                onClick = { navController.navigate("language_selection") },
                icon = { Icon(Icons.Default.Language, contentDescription = texts.SETTINGS_LANGUAGE) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
    }
}
