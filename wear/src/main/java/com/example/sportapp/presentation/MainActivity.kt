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
import com.example.sportapp.presentation.settings.*
import com.example.sportapp.presentation.theme.SportAppTheme
import com.example.sportapp.presentation.workout.ClimbingWorkoutScreen
import com.example.sportapp.presentation.workout.WalkingWorkoutScreen
import com.example.sportapp.presentation.workout.WorkoutSummaryScreen
import com.google.maps.android.compose.MapType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberSwipeDismissableNavController()
            var selectedMapType by remember { mutableStateOf(MapType.NORMAL) }
            var selectedClockColor by remember { mutableStateOf<Color?>(Color.Red) }
            var healthData by remember { mutableStateOf(HealthData()) }
            
            // Tymczasowy stan podsumowania
            var currentSummaryData by remember { mutableStateOf<Pair<String, List<Pair<String, String>>>?>(null) }

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
                            MapTypeSelectionScreen(selectedMapType) { selectedMapType = it } 
                        }
                        
                        composable("clock_color_selection") {
                            ClockColorSelectionScreen(selectedClockColor) { selectedClockColor = it }
                        }
                        
                        // Dane Zdrowotne
                        composable("health_data") { 
                            HealthDataScreen(
                                data = healthData,
                                onNavigateToGender = { navController.navigate("health_gender") },
                                onNavigateToAge = { navController.navigate("health_age") },
                                onNavigateToWeight = { navController.navigate("health_weight") },
                                onNavigateToHeight = { navController.navigate("health_height") },
                                onNavigateToRestingHR = { navController.navigate("health_resting_hr") },
                                onNavigateToMaxHR = { navController.navigate("health_max_hr") }
                            ) 
                        }
                        composable("health_gender") {
                            GenderSelectionScreen(healthData.gender) { healthData = healthData.copy(gender = it) }
                        }
                        composable("health_age") {
                            NumericInputScreen(
                                value = healthData.age,
                                range = 16..120,
                                onValueChange = { healthData = healthData.copy(age = it, maxHR = 220 - it) },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        composable("health_weight") {
                            NumericInputScreen(
                                value = healthData.weight,
                                range = 30..250,
                                onValueChange = { healthData = healthData.copy(weight = it) },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        composable("health_height") {
                            NumericInputScreen(
                                value = healthData.height,
                                range = 100..230,
                                onValueChange = { healthData = healthData.copy(height = it) },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        composable("health_resting_hr") {
                            NumericInputScreen(
                                value = healthData.restingHR,
                                range = 30..200,
                                onValueChange = { healthData = healthData.copy(restingHR = it) },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        composable("health_max_hr") {
                            NumericInputScreen(
                                value = healthData.maxHR,
                                range = 100..240,
                                onValueChange = { healthData = healthData.copy(maxHR = it) },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        
                        // Ekran Podsumowania
                        composable("workout_summary") {
                            currentSummaryData?.let { (title, data) ->
                                WorkoutSummaryScreen(
                                    title = title,
                                    summaryData = data,
                                    onConfirm = {
                                        navController.navigate("choose_sport") {
                                            popUpTo("main_menu") { inclusive = false }
                                        }
                                    }
                                )
                            }
                        }

                        // Sporty
                        composable("workout_walking") { 
                            WalkingWorkoutScreen(
                                mapType = selectedMapType, 
                                clockColor = selectedClockColor,
                                onEndWorkout = { summary ->
                                    currentSummaryData = "Spacer" to summary
                                    navController.navigate("workout_summary")
                                }
                            ) 
                        }
                        composable("workout_climbing") { 
                            ClimbingWorkoutScreen(
                                clockColor = selectedClockColor, 
                                healthData = healthData,
                                onEndWorkout = { summary ->
                                    currentSummaryData = "Wspinaczka" to summary
                                    navController.navigate("workout_summary")
                                }
                            )
                        }
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
