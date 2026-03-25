package com.example.sportapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sportapp.presentation.activities.ActivityCompareScreen
import com.example.sportapp.presentation.activities.ActivityCompareViewModel
import com.example.sportapp.presentation.activities.ActivityListScreen
import com.example.sportapp.presentation.activities.ActivityListViewModel
import com.example.sportapp.presentation.activities.ActivityTrimScreen
import com.example.sportapp.presentation.activities.ActivityTrimViewModel
import com.example.sportapp.presentation.definitions.WorkoutDefinitionEditScreen
import com.example.sportapp.presentation.definitions.WorkoutDefinitionListScreen
import com.example.sportapp.presentation.definitions.WorkoutDefinitionViewModel
import com.example.sportapp.presentation.home.HomeScreen
import com.example.sportapp.presentation.home.HomeViewModel
import com.example.sportapp.presentation.navigation.Screen
import com.example.sportapp.presentation.settings.*
import com.example.sportapp.presentation.stats.*
import com.example.sportapp.presentation.theme.SportAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsManager: MobileSettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsState by settingsManager.settingsFlow.collectAsStateWithLifecycle(initialValue = MobileSettingsState())
            
            SportAppTheme(themeMode = settingsState.themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope()
                    val statsViewModel: OverallStatsViewModel = hiltViewModel()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) {
                            val homeViewModel: HomeViewModel = hiltViewModel()
                            HomeScreen(
                                viewModel = homeViewModel,
                                onNavigateToStats = { navController.navigate(Screen.OverallStats.route) },
                                onNavigateToActivityList = { navController.navigate(Screen.ActivityList.route) },
                                onSettingsClick = { navController.navigate("settings") }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                initialState = settingsState,
                                onSave = { updatedSettings ->
                                    scope.launch {
                                        settingsManager.saveSettings(updatedSettings)
                                        navController.popBackStack()
                                    }
                                },
                                onCancel = { navController.popBackStack() },
                                onNavigateToWidgetSelection = { navController.navigate("widget_selection") },
                                onNavigateToWatchWidgetSelection = { navController.navigate("watch_widget_selection") },
                                onNavigateToDefinitions = { navController.navigate("definitions") },
                                onNavigateToHealthData = { navController.navigate("health_data") }
                            )
                        }
                        composable("health_data") {
                            HealthDataScreen(
                                initialData = settingsState.healthData,
                                onSave = { updatedHealthData ->
                                    scope.launch {
                                        settingsManager.saveSettings(settingsState.copy(healthData = updatedHealthData))
                                        navController.popBackStack()
                                    }
                                },
                                onCancel = { navController.popBackStack() }
                            )
                        }
                        composable("definitions") {
                            val viewModel: WorkoutDefinitionViewModel = hiltViewModel()
                            WorkoutDefinitionListScreen(
                                viewModel = viewModel,
                                onNavigateToEdit = { id -> navController.navigate("definition_edit/$id") },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("definition_edit/{definitionId}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("definitionId")?.toLongOrNull() ?: 0L
                            val viewModel: WorkoutDefinitionViewModel = hiltViewModel()
                            WorkoutDefinitionEditScreen(
                                viewModel = viewModel,
                                definitionId = id,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("widget_selection") {
                            WidgetSelectionScreen(
                                widgets = settingsState.widgets,
                                title = "Widgety na stronie głównej",
                                onSave = { updatedWidgets ->
                                    scope.launch {
                                        settingsManager.saveSettings(settingsState.copy(widgets = updatedWidgets))
                                        navController.popBackStack()
                                    }
                                },
                                onCancel = { navController.popBackStack() }
                            )
                        }
                        composable("watch_widget_selection") {
                            WidgetSelectionScreen(
                                widgets = settingsState.watchStatsWidgets,
                                title = "Statystyki na zegarku",
                                onSave = { updatedWidgets ->
                                    scope.launch {
                                        settingsManager.saveSettings(settingsState.copy(watchStatsWidgets = updatedWidgets))
                                        navController.popBackStack()
                                    }
                                },
                                onCancel = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.OverallStats.route) {
                            OverallStatsScreen(
                                viewModel = statsViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToOptions = { navController.navigate("stats_widget_selection") }
                            )
                        }
                        composable("stats_widget_selection") {
                            val statsWidgets by statsViewModel.widgets.collectAsStateWithLifecycle()
                            OverallStatsWidgetScreen(
                                widgets = statsWidgets,
                                onSave = { updatedWidgets ->
                                    statsViewModel.saveWidgets(updatedWidgets)
                                    navController.popBackStack()
                                },
                                onCancel = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.ActivityList.route) {
                            val activityListViewModel: ActivityListViewModel = hiltViewModel()
                            ActivityListScreen(
                                viewModel = activityListViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToDetail = { id -> 
                                    navController.navigate(Screen.ActivityDetail.createRoute(id))
                                },
                                onNavigateToTrim = { id ->
                                    navController.navigate(Screen.ActivityTrim.createRoute(id))
                                },
                                onNavigateToCompare = { id1, id2 ->
                                    navController.navigate(Screen.ActivityCompare.createRoute(id1, id2))
                                },
                                onNavigateToSettings = { navController.navigate(Screen.ActivityDetailSettingsList.route) }
                            )
                        }
                        composable(Screen.ActivityDetailSettingsList.route) {
                            val viewModel: WorkoutDefinitionViewModel = hiltViewModel()
                            ActivityDetailSettingsListScreen(
                                viewModel = viewModel,
                                onNavigateToEdit = { typeName ->
                                    navController.navigate(Screen.ActivityDetailSettingsEdit.createRoute(typeName))
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.ActivityDetailSettingsEdit.route) {
                            val detailSettingsViewModel: ActivityDetailSettingsViewModel = hiltViewModel()
                            ActivityDetailSettingsEditScreen(
                                viewModel = detailSettingsViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.ActivityDetail.route) {
                            val detailViewModel: ActivityDetailViewModel = hiltViewModel()
                            ActivityDetailScreen(
                                viewModel = detailViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.ActivityTrim.route) {
                            val trimViewModel: ActivityTrimViewModel = hiltViewModel()
                            ActivityTrimScreen(
                                viewModel = trimViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.ActivityCompare.route) {
                            val compareViewModel: ActivityCompareViewModel = hiltViewModel()
                            ActivityCompareScreen(
                                viewModel = compareViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
