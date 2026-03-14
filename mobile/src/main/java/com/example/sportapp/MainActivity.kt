package com.example.sportapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sportapp.data.TestDataGenerator
import com.example.sportapp.presentation.activities.ActivityListScreen
import com.example.sportapp.presentation.activities.ActivityListViewModel
import com.example.sportapp.presentation.definitions.WorkoutDefinitionEditScreen
import com.example.sportapp.presentation.definitions.WorkoutDefinitionListScreen
import com.example.sportapp.presentation.definitions.WorkoutDefinitionViewModel
import com.example.sportapp.presentation.home.HomeScreen
import com.example.sportapp.presentation.home.HomeViewModel
import com.example.sportapp.presentation.navigation.Screen
import com.example.sportapp.presentation.settings.MobileSettingsManager
import com.example.sportapp.presentation.settings.SettingsScreen
import com.example.sportapp.presentation.settings.WidgetSelectionScreen
import com.example.sportapp.presentation.stats.ActivityDetailScreen
import com.example.sportapp.presentation.stats.ActivityDetailSettingsViewModel
import com.example.sportapp.presentation.stats.ActivityDetailViewModel
import com.example.sportapp.presentation.stats.OverallStatsScreen
import com.example.sportapp.presentation.stats.OverallStatsViewModel
import com.example.sportapp.presentation.stats.OverallStatsWidgetScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsManager: MobileSettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Generuj dane testowe jeśli jesteśmy w trybie DEBUG
        if (BuildConfig.DEBUG) {
            lifecycleScope.launch(Dispatchers.IO) {
                TestDataGenerator.generateTestData(applicationContext)
            }
        }

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val homeViewModel: HomeViewModel = hiltViewModel()
                    val settingsState by homeViewModel.settings.collectAsState()
                    val scope = rememberCoroutineScope()
                    
                    // Współdzielony ViewModel dla statystyk
                    val statsViewModel: OverallStatsViewModel = hiltViewModel()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) {
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
                                onSave = { updated ->
                                    scope.launch { 
                                        settingsManager.saveSettings(updated)
                                        navController.popBackStack()
                                    }
                                },
                                onCancel = { navController.popBackStack() },
                                onNavigateToWidgetSelection = { navController.navigate("widget_selection") },
                                onNavigateToDefinitions = { navController.navigate("definitions_list") }
                            )
                        }
                        composable("definitions_list") {
                            val definitionViewModel: WorkoutDefinitionViewModel = hiltViewModel()
                            WorkoutDefinitionListScreen(
                                viewModel = definitionViewModel,
                                onNavigateToEdit = { id -> navController.navigate("definition_edit/$id") },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            "definition_edit/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong("id") ?: 0L
                            val definitionViewModel: WorkoutDefinitionViewModel = hiltViewModel()
                            WorkoutDefinitionEditScreen(
                                viewModel = definitionViewModel,
                                definitionId = id,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("widget_selection") {
                            WidgetSelectionScreen(
                                widgets = settingsState.widgets,
                                onSave = { updatedWidgets ->
                                    scope.launch {
                                        settingsManager.saveSettings(settingsState.copy(widgets = updatedWidgets))
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
                            val statsWidgets by statsViewModel.widgets.collectAsState()
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
                                onNavigateToSettings = { navController.navigate("activity_detail_settings") }
                            )
                        }
                        composable("activity_detail_settings") {
                            val detailSettingsViewModel: ActivityDetailSettingsViewModel = hiltViewModel()
                            val detailSettings by detailSettingsViewModel.settings.collectAsState()
                            
                            WidgetSelectionScreen(
                                widgets = detailSettings.visibleElements,
                                title = "Wykresy w szczegółach",
                                onSave = { updatedWidgets ->
                                    detailSettingsViewModel.saveVisibleElements(updatedWidgets)
                                    navController.popBackStack()
                                },
                                onCancel = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.ActivityDetail.route) {
                            val detailViewModel: ActivityDetailViewModel = hiltViewModel()
                            ActivityDetailScreen(
                                viewModel = detailViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
