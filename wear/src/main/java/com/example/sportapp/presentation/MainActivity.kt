package com.example.sportapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.sportapp.presentation.components.PlaceholderScreen
import com.example.sportapp.presentation.menu.ChooseSportScreen
import com.example.sportapp.presentation.menu.MainMenuScreen
import com.example.sportapp.presentation.settings.ClockColorSelectionScreen
import com.example.sportapp.presentation.settings.MapTypeSelectionScreen
import com.example.sportapp.presentation.settings.SettingsScreen
import com.example.sportapp.presentation.theme.SportAppTheme
import com.example.sportapp.presentation.workout.WalkingWorkoutScreen
import com.google.maps.android.compose.MapType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberSwipeDismissableNavController()
            var selectedMapType by remember { mutableStateOf(MapType.NORMAL) }
            var selectedClockColor by remember { mutableStateOf<Color?>(Color.Red) }

            SportAppTheme {
                Scaffold(
                    timeText = { 
                        if (selectedClockColor != null) {
                            TimeText(
                                timeTextStyle = MaterialTheme.typography.caption1.copy(
                                    color = selectedClockColor!!
                                )
                            )
                        }
                    }
                ) {
                    SwipeDismissableNavHost(
                        navController = navController,
                        startDestination = "main_menu"
                    ) {
                        composable("main_menu") { MainMenuScreen(navController) }
                        composable("choose_sport") { ChooseSportScreen(navController) }
                        composable("statistics") { PlaceholderScreen("Statystyki") }
                        composable("settings") { SettingsScreen(navController, selectedMapType, selectedClockColor) }
                        composable("map_type_selection") { 
                            MapTypeSelectionScreen(selectedMapType) { selectedMapType = it; navController.popBackStack() } 
                        }
                        composable("clock_color_selection") {
                            ClockColorSelectionScreen(selectedClockColor) { selectedClockColor = it; navController.popBackStack() }
                        }
                        
                        // Sporty
                        composable("workout_walking") { WalkingWorkoutScreen(selectedMapType, selectedClockColor) }
                        composable("workout_climbing") { PlaceholderScreen("Wspinaczka") }
                        composable("workout_tennis") { PlaceholderScreen("Tenis") }
                        composable("workout_gym") { PlaceholderScreen("Siłownia") }
                        composable("workout_pool") { PlaceholderScreen("Basen") }
                        composable("workout_kayak") { PlaceholderScreen("Kajak") }
                    }
                }
            }
        }
    }
}
