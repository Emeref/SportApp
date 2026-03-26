package com.example.sportapp.presentation

import android.Manifest
import android.content.Intent
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
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
        }
    }

    private val ambientObserver = AmbientLifecycleObserver(this, ambientCallback)
    private val _isAmbient = mutableStateOf(false)
    
    private val navigationIntentId = MutableStateFlow<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        
        lifecycle.addObserver(ambientObserver)
        handleIntent(intent)
        
        setContent {
            val navController = rememberSwipeDismissableNavController()
            val scope = rememberCoroutineScope()
            
            val isAmbient by _isAmbient
            
            val settingsState by settingsManager.settingsFlow.collectAsState(initial = UserSettings(
                clockColor = Color.Red,
                healthData = HealthData(),
                screenBehavior = ScreenBehavior.KEEP_SCREEN_ON,
                watchStatsWidgets = emptyList(),
                watchStatsPeriod = ReportingPeriod.WEEK,
                watchStatsCustomDays = 7
            ))
            
            var selectedClockColor by remember { mutableStateOf<Color?>(Color.Red) }
            var healthData by remember { mutableStateOf(HealthData()) }
            var screenBehavior by remember { mutableStateOf(ScreenBehavior.KEEP_SCREEN_ON) }

            LaunchedEffect(settingsState) {
                selectedClockColor = settingsState.clockColor
                healthData = settingsState.healthData
                screenBehavior = settingsState.screenBehavior
            }

            val targetWorkoutId by navigationIntentId.collectAsState()
            LaunchedEffect(targetWorkoutId) {
                targetWorkoutId?.let { id ->
                    navController.navigate("dynamic_workout/$id") {
                        popUpTo("main_menu") { inclusive = false }
                        launchSingleTop = true
                    }
                    navigationIntentId.value = null
                }
            }
            
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
            ) { }

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
                        
                        composable("clock_color_selection") {
                            ClockColorSelectionScreen(selectedClockColor) { 
                                selectedClockColor = it
                                scope.launch { settingsManager.saveClockColor(it) }
                            }
                        }
                        
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
                                label = "Wiek",
                                value = healthData.age,
                                range = 16..120,
                                unit = "lat",
                                onValueChange = { 
                                    healthData = healthData.copy(age = it, maxHR = 220 - it)
                                    scope.launch { settingsManager.saveHealthData(healthData) }
                                },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        composable("health_weight") {
                            NumericInputScreen(
                                label = "Waga",
                                value = healthData.weight,
                                range = 30..250,
                                unit = "kg",
                                onValueChange = { 
                                    healthData = healthData.copy(weight = it)
                                    scope.launch { settingsManager.saveHealthData(healthData) }
                                },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        composable("health_height") {
                            NumericInputScreen(
                                label = "Wzrost",
                                value = healthData.height,
                                range = 100..230,
                                unit = "cm",
                                onValueChange = { 
                                    healthData = healthData.copy(height = it)
                                    scope.launch { settingsManager.saveHealthData(healthData) }
                                },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        composable("health_step_length") {
                            NumericInputScreen(
                                label = "Długość kroku",
                                value = healthData.stepLength,
                                range = 30..130,
                                unit = "cm",
                                onValueChange = { 
                                    healthData = healthData.copy(stepLength = it)
                                    scope.launch { settingsManager.saveHealthData(healthData) }
                                },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        composable("health_resting_hr") {
                            NumericInputScreen(
                                label = "Tętno spoczynkowe",
                                value = healthData.restingHR,
                                range = 30..200,
                                unit = "BPM",
                                onValueChange = { 
                                    healthData = healthData.copy(restingHR = it)
                                    scope.launch { settingsManager.saveHealthData(healthData) }
                                },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        composable("health_max_hr") {
                            NumericInputScreen(
                                label = "Tętno maksymalne",
                                value = healthData.maxHR,
                                range = 100..240,
                                unit = "BPM",
                                onValueChange = { 
                                    healthData = healthData.copy(maxHR = it)
                                    scope.launch { settingsManager.saveHealthData(healthData) }
                                },
                                onDone = { navController.popBackStack() }
                            )
                        }
                        
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

                        composable(
                            "dynamic_workout/{definitionId}",
                            arguments = listOf(navArgument("definitionId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val definitionId = backStackEntry.arguments?.getLong("definitionId") ?: 0L
                            DynamicWorkoutScreen(
                                definitionId = definitionId,
                                clockColor = selectedClockColor,
                                healthData = healthData,
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val id = intent?.getLongExtra("EXTRA_DEFINITION_ID", -1L) ?: -1L
        if (id != -1L) {
            navigationIntentId.value = id
        }
    }
}
