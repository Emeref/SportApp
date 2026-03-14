package com.example.sportapp.presentation

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.sportapp.presentation.menu.ChooseSportScreen
import com.example.sportapp.presentation.menu.MainMenuScreen
import com.example.sportapp.presentation.menu.StatisticsScreen
import com.example.sportapp.presentation.settings.*
import com.example.sportapp.presentation.theme.SportAppTheme
import com.example.sportapp.presentation.workout.DynamicWorkoutScreen
import com.example.sportapp.presentation.workout.WorkoutSummaryScreen
import com.google.maps.android.compose.MapType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberSwipeDismissableNavController()
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
            
            // Permissions
            val permissions = mutableListOf(
                Manifest.permission.BODY_SENSORS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }

            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { /* Handle results if needed */ }

            LaunchedEffect(Unit) {
                launcher.launch(permissions.toTypedArray())
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
                        composable("statistics") { StatisticsScreen() }
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

                        // Dynamiczny Trening
                        composable(
                            "dynamic_workout/{definitionId}",
                            arguments = listOf(navArgument("definitionId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val definitionId = backStackEntry.arguments?.getLong("definitionId") ?: 0L
                            DynamicWorkoutScreen(
                                definitionId = definitionId,
                                mapType = selectedMapType,
                                clockColor = selectedClockColor,
                                healthData = healthData,
                                onEndWorkout = { name, summary ->
                                    currentSummaryData = name to summary
                                    navController.navigate("workout_summary") {
                                        popUpTo("main_menu") { inclusive = false }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
