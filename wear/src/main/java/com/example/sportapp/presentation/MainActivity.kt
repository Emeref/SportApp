package com.example.sportapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.sportapp.presentation.menu.MainMenu
import com.example.sportapp.presentation.menu.ChooseSportScreen
import com.example.sportapp.presentation.menu.HistoryScreen
import com.example.sportapp.presentation.settings.*
import com.example.sportapp.presentation.theme.SportAppTheme
import com.example.sportapp.presentation.workout.*
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
            
            val settingsState by settingsManager.settingsFlow.collectAsState(initial = UserSettings(MapType.NORMAL, Color.Red, HealthData(), listOf(SportConfig("default", "Default sport"))))
            
            var selectedMapType by remember { mutableStateOf(MapType.NORMAL) }
            var selectedClockColor by remember { mutableStateOf<Color?>(Color.Red) }
            var healthData by remember { mutableStateOf(HealthData()) }

            LaunchedEffect(settingsState) {
                selectedMapType = settingsState.mapType
                selectedClockColor = settingsState.clockColor
                healthData = settingsState.healthData
            }
            
            var currentSummaryData by remember { mutableStateOf<Pair<String, List<Pair<String, String>>>?>(null) }

            // Obsługa uprawnień przy pierwszym uruchomieniu
            val permissionsToRequest = mutableListOf(
                Manifest.permission.BODY_SENSORS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACTIVITY_RECOGNITION
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.POST_NOTIFICATIONS)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add("android.permission.BODY_SENSORS_BACKGROUND")
                }
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val allGranted = permissions.entries.all { it.value }
                if (!allGranted) {
                    Toast.makeText(this, "Niektóre uprawnienia nie zostały przyznane.", Toast.LENGTH_LONG).show()
                }
            }

            LaunchedEffect(Unit) {
                val notGranted = permissionsToRequest.filter {
                    ContextCompat.checkSelfPermission(this@MainActivity, it) != PackageManager.PERMISSION_GRANTED
                }
                if (notGranted.isNotEmpty()) {
                    permissionLauncher.launch(notGranted.toTypedArray())
                }
            }

            SportAppTheme {
                SwipeDismissableNavHost(
                    navController = navController,
                    startDestination = "main_menu"
                ) {
                    composable("main_menu") {
                        MainMenu(
                            onStartWorkout = { navController.navigate("workout_type_selection") },
                            onSettings = { navController.navigate("settings") },
                            onHistory = { navController.navigate("history") }
                        )
                    }

                    composable("workout_type_selection") {
                        ChooseSportScreen(
                            sportsConfig = settingsState.sportsConfig,
                            onWorkoutSelected = { type ->
                                navController.navigate("workout_control/$type")
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        "workout_control/{workoutType}",
                        arguments = listOf(navArgument("workoutType") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val workoutType = backStackEntry.arguments?.getString("workoutType") ?: "walking"
                        WorkoutControlScreen(
                            workoutType = workoutType,
                            healthData = healthData,
                            sportsConfig = settingsState.sportsConfig,
                            onWorkoutFinished = { workoutName, summaryData ->
                                currentSummaryData = workoutName to summaryData
                                navController.navigate("summary")
                            }
                        )
                    }

                    composable("summary") {
                        currentSummaryData?.let { (name, data) ->
                            WorkoutSummaryScreen(
                                workoutName = name,
                                summaryData = data,
                                onDismiss = { 
                                    currentSummaryData = null
                                    navController.popBackStack("main_menu", inclusive = false) 
                                }
                            )
                        }
                    }

                    composable("settings") {
                        SettingsScreen(
                            currentMapType = selectedMapType,
                            currentClockColor = selectedClockColor,
                            onMapTypeChange = { type ->
                                scope.launch { settingsManager.saveMapType(type) }
                            },
                            onClockColorChange = { color ->
                                scope.launch { settingsManager.saveClockColor(color) }
                            },
                            onHealthData = { navController.navigate("health_data") },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("health_data") {
                        HealthDataScreen(
                            data = healthData,
                            onNavigateToGender = { /* TODO */ },
                            onNavigateToAge = { /* TODO */ },
                            onNavigateToWeight = { /* TODO */ },
                            onNavigateToHeight = { /* TODO */ },
                            onNavigateToRestingHR = { /* TODO */ },
                            onNavigateToMaxHR = { /* TODO */ },
                            onNavigateToStepLength = { /* TODO */ }
                        )
                    }

                    composable("history") {
                        HistoryScreen(
                            onActivitySelected = { activity ->
                                // Można dodać szczegóły aktywności
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
