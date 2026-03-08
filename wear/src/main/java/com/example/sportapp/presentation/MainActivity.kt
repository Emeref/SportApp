package com.example.sportapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val navController = rememberSwipeDismissableNavController()
            val settingsManager = remember { SettingsManager(context) }
            val scope = rememberCoroutineScope()
            
            val settingsState by settingsManager.settingsFlow.collectAsState(initial = UserSettings(MapType.NORMAL, Color.Red, HealthData()))
            
            var selectedMapType by remember { mutableStateOf(MapType.NORMAL) }
            var selectedClockColor by remember { mutableStateOf<Color?>(Color.Red) }
            var healthData by remember { mutableStateOf(HealthData()) }

            LaunchedEffect(settingsState) {
                selectedMapType = settingsState.mapType
                selectedClockColor = settingsState.clockColor
                healthData = settingsState.healthData
            }
            
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
                        composable("settings") { 
                            SettingsScreen(
                                navController = navController, 
                                currentMapType = selectedMapType, 
                                currentClockColor = selectedClockColor
                            ) 
                        }
                        
                        composable("map_type_selection") { 
                            MapTypeSelectionScreen(selectedMapType) { 
                                selectedMapType = it
                                scope.launch { settingsManager.saveMapType(it) }
                            } 
                        }
                        
                        composable("clock_color_selection") {
                            ClockColorSelectionScreen(selectedClockColor) { 
                                selectedClockColor = it
                                scope.launch { settingsManager.saveClockColor(it) }
                            }
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
                                onNavigateToMaxHR = { navController.navigate("health_max_hr") },
                                onNavigateToStepLength = { navController.navigate("health_step_length") }
                            ) 
                        }
                        composable("health_gender") {
                            GenderSelectionScreen(healthData.gender) { 
                                healthData = healthData.copy(gender = it)
                                scope.launch { settingsManager.saveHealthData(healthData) }
                            }
                        }
                        composable("health_age") {
                            NumericInputScreen(
                                value = healthData.age,
                                range = 16..120,
                                onValueChange = { 
                                    healthData = healthData.copy(age = it, maxHR = 220 - it)
                                    scope.launch { settingsManager.saveHealthData(healthData) }
                                },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        composable("health_weight") {
                            NumericInputScreen(
                                value = healthData.weight,
                                range = 30..250,
                                onValueChange = { 
                                    healthData = healthData.copy(weight = it)
                                    scope.launch { settingsManager.saveHealthData(healthData) }
                                },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        composable("health_height") {
                            NumericInputScreen(
                                value = healthData.height,
                                range = 100..230,
                                onValueChange = { 
                                    healthData = healthData.copy(height = it)
                                    scope.launch { settingsManager.saveHealthData(healthData) }
                                },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        composable("health_step_length") {
                            NumericInputScreen(
                                value = healthData.stepLength,
                                range = 30..130,
                                onValueChange = { 
                                    healthData = healthData.copy(stepLength = it)
                                    scope.launch { settingsManager.saveHealthData(healthData) }
                                },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        composable("health_resting_hr") {
                            NumericInputScreen(
                                value = healthData.restingHR,
                                range = 30..200,
                                onValueChange = { 
                                    healthData = healthData.copy(restingHR = it)
                                    scope.launch { settingsManager.saveHealthData(healthData) }
                                },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        composable("health_max_hr") {
                            NumericInputScreen(
                                value = healthData.maxHR,
                                range = 100..240,
                                onValueChange = { 
                                    healthData = healthData.copy(maxHR = it)
                                    scope.launch { settingsManager.saveHealthData(healthData) }
                                },
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
                                healthData = healthData,
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
