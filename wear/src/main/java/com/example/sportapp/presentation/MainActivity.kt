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
import androidx.wear.ambient.AmbientLifecycleObserver
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.sportapp.presentation.menu.ChooseSportScreen
import com.example.sportapp.presentation.menu.MainMenuScreen
import com.example.sportapp.presentation.menu.StatisticsScreen
import com.example.sportapp.presentation.menu.WorkoutReadyScreen
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

    private val ambientCallback = object : AmbientLifecycleObserver.AmbientLifecycleCallback {
        override fun onEnterAmbient(ambientDetails: AmbientLifecycleObserver.AmbientDetails) {
            _isAmbient.value = true
        }

        override fun onExitAmbient() {
            _isAmbient.value = false
        }

        override fun onUpdateAmbient() {
            // Update UI if needed
        }
    }

    private val ambientObserver = AmbientLifecycleObserver(this, ambientCallback)
    private val _isAmbient = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycle.addObserver(ambientObserver)
        
        setContent {
            val navController = rememberSwipeDismissableNavController()
            val scope = rememberCoroutineScope()
            
            val isAmbient by _isAmbient
            
            val settingsState by settingsManager.settingsFlow.collectAsState(initial = UserSettings(MapType.NORMAL, Color.Red, HealthData(), 5, true, SettingsManager.Orange, ScreenBehavior.KEEP_SCREEN_ON))
            
            var selectedMapType by remember { mutableStateOf(MapType.NORMAL) }
            var selectedClockColor by remember { mutableStateOf<Color?>(Color.Red) }
            var healthData by remember { mutableStateOf(HealthData()) }
            var autoCenterDelay by remember { mutableIntStateOf(5) }
            var showRoute by remember { mutableStateOf(true) }
            var routeColor by remember { mutableStateOf(SettingsManager.Orange) }
            var screenBehavior by remember { mutableStateOf(ScreenBehavior.KEEP_SCREEN_ON) }

            LaunchedEffect(settingsState) {
                selectedMapType = settingsState.mapType
                selectedClockColor = settingsState.clockColor
                healthData = settingsState.healthData
                autoCenterDelay = settingsState.autoCenterDelay
                showRoute = settingsState.showRoute
                routeColor = settingsState.routeColor
                screenBehavior = settingsState.screenBehavior
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
                        if (selectedClockColor != null && !isAmbient) {
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
                        composable(
                            "workout_ready/{definitionId}",
                            arguments = listOf(navArgument("definitionId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val definitionId = backStackEntry.arguments?.getLong("definitionId") ?: 0L
                            WorkoutReadyScreen(navController, definitionId)
                        }
                        composable("statistics") { StatisticsScreen() }
                        composable("settings") { 
                            SettingsScreen(
                                navController = navController, 
                                currentMapType = selectedMapType, 
                                currentClockColor = selectedClockColor,
                                currentScreenBehavior = screenBehavior
                            ) 
                        }

                        composable("screen_behavior_selection") {
                            ScreenBehaviorSelectionScreen(screenBehavior) {
                                screenBehavior = it
                                scope.launch { settingsManager.saveScreenBehavior(it) }
                            }
                        }

                        composable("map_settings") {
                            MapSettingsScreen(
                                navController = navController,
                                currentMapType = selectedMapType,
                                currentAutoCenterDelay = autoCenterDelay,
                                showRoute = showRoute,
                                onShowRouteToggle = {
                                    showRoute = it
                                    scope.launch { settingsManager.saveShowRoute(it) }
                                },
                                routeColor = routeColor
                            )
                        }
                        
                        composable("map_type_selection") { 
                            MapTypeSelectionScreen(selectedMapType) { 
                                selectedMapType = it
                                scope.launch { settingsManager.saveMapType(it) }
                            } 
                        }

                        composable("auto_center_delay_selection") {
                            AutoCenterDelaySelectionScreen(autoCenterDelay) {
                                autoCenterDelay = it
                                scope.launch { settingsManager.saveAutoCenterDelay(it) }
                            }
                        }

                        composable("route_color_selection") {
                            RouteColorSelectionScreen(routeColor) {
                                routeColor = it
                                scope.launch { settingsManager.saveRouteColor(it) }
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
                                autoCenterDelay = autoCenterDelay,
                                showRoute = showRoute,
                                routeColor = routeColor,
                                screenBehavior = screenBehavior,
                                isAmbient = isAmbient,
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
