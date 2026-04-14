package com.example.sportapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sportapp.presentation.navigation.Screen
import com.example.sportapp.presentation.definitions.WorkoutDefinitionEditScreen
import com.example.sportapp.presentation.definitions.WorkoutDefinitionListScreen
import com.example.sportapp.presentation.definitions.WorkoutDefinitionViewModel
import com.example.sportapp.presentation.home.HomeScreen
import com.example.sportapp.presentation.home.HomeViewModel
import com.example.sportapp.presentation.settings.*
import com.example.sportapp.presentation.stats.*
import com.example.sportapp.presentation.theme.SportAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.sportapp.presentation.activities.*
import com.example.sportapp.data.strava.StravaStorage
import com.example.sportapp.data.strava.api.StravaAuthApi
import com.example.sportapp.data.db.SyncMetadataDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsManager: MobileSettingsManager

    @Inject
    lateinit var stravaStorage: StravaStorage

    @Inject
    lateinit var stravaAuthApi: StravaAuthApi

    @Inject
    lateinit var syncMetadataDao: SyncMetadataDao

    private val activityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        handleStravaCallback(intent)

        setContent {
            val settingsState by settingsManager.settingsFlow.collectAsStateWithLifecycle(initialValue = MobileSettingsState())
            val texts = settingsState.language.texts

            CompositionLocalProvider(LocalMobileTexts provides texts) {
                SportAppTheme(themeMode = settingsState.themeMode) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        val scope = rememberCoroutineScope()

                        NavHost(navController = navController, startDestination = "home") {
                            composable("home") {
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
                                    onNavigateToHealthData = { navController.navigate("health_data") },
                                    onNavigateToLanguageSelection = { navController.navigate("language_selection") },
                                    onNavigateToSync = { navController.navigate("sync_settings") },
                                    onNavigateToStrava = { navController.navigate("strava_settings") }
                                )
                            }
                            composable("strava_settings") {
                                StravaSettingsScreen(
                                    stravaStorage = stravaStorage,
                                    settingsManager = settingsManager,
                                    syncMetadataDao = syncMetadataDao,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable("sync_settings") {
                                HealthConnectSettingsScreen(
                                    initialState = settingsState,
                                    onSave = { updatedSettings ->
                                        scope.launch {
                                            settingsManager.saveSettings(updatedSettings)
                                            navController.popBackStack()
                                        }
                                    },
                                    onCancel = { navController.popBackStack() },
                                    onNavigateToSyncStatus = { navController.navigate("sync_status") },
                                    onNavigateToExerciseImport = { navController.navigate("exercise_import") },
                                    settingsManager = settingsManager
                                )
                            }
                            composable("sync_status") {
                                SyncStatusScreen(
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable("sync_history") {
                                SyncHistoryScreen(
                                    syncMetadataDao = syncMetadataDao,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable("exercise_import") {
                                ExerciseImportScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable("definitions") {
                                val viewModel: WorkoutDefinitionViewModel = hiltViewModel()
                                WorkoutDefinitionListScreen(
                                    viewModel = viewModel,
                                    onNavigateToEdit = { id -> 
                                        navController.navigate("definition_edit/$id")
                                    },
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable("definition_edit/{id}") { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: -1L
                                val viewModel: WorkoutDefinitionViewModel = hiltViewModel()
                                WorkoutDefinitionEditScreen(
                                    definitionId = id,
                                    onBack = { navController.popBackStack() },
                                    viewModel = viewModel
                                )
                            }
                            composable("health_data") {
                                HealthDataScreen(
                                    initialData = settingsState.healthData,
                                    onSave = { updatedData ->
                                        scope.launch {
                                            settingsManager.saveSettings(settingsState.copy(healthData = updatedData))
                                            navController.popBackStack()
                                        }
                                    },
                                    onCancel = { navController.popBackStack() }
                                )
                            }
                            composable("language_selection") {
                                LanguageSelectionScreen(
                                    currentLanguage = settingsState.language,
                                    onLanguageSelected = { newLanguage ->
                                        scope.launch {
                                            settingsManager.saveSettings(settingsState.copy(language = newLanguage))
                                            navController.popBackStack()
                                        }
                                    },
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(Screen.OverallStats.route) {
                                val viewModel: OverallStatsViewModel = hiltViewModel()
                                OverallStatsScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToOptions = { navController.navigate("stats_settings") }
                                )
                            }
                            composable("stats_settings") {
                                val viewModel: OverallStatsSettingsViewModel = hiltViewModel()
                                OverallStatsSettingsScreen(
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable("widget_selection") {
                                WidgetSelectionScreen(
                                    widgets = settingsState.widgets,
                                    title = texts.SETTINGS_WIDGETS_HOME_TITLE,
                                    initialDays = settingsState.customDays,
                                    daysLabel = texts.SETTINGS_CUSTOM_DAYS_LABEL,
                                    onSave = { updatedWidgets, days ->
                                        scope.launch {
                                            settingsManager.saveSettings(settingsState.copy(
                                                widgets = updatedWidgets,
                                                customDays = days ?: settingsState.customDays
                                            ))
                                            navController.popBackStack()
                                        }
                                    },
                                    onCancel = { navController.popBackStack() }
                                )
                            }
                            composable("watch_widget_selection") {
                                WidgetSelectionScreen(
                                    widgets = settingsState.watchStatsWidgets,
                                    title = texts.SETTINGS_WIDGETS_WATCH_TITLE,
                                    initialDays = settingsState.watchStatsCustomDays,
                                    daysLabel = texts.SETTINGS_WATCH_STATS_DAYS_LABEL,
                                    onSave = { updatedWidgets, days ->
                                        scope.launch {
                                            settingsManager.saveSettings(settingsState.copy(
                                                watchStatsWidgets = updatedWidgets,
                                                watchStatsCustomDays = days ?: settingsState.watchStatsCustomDays
                                            ))
                                            navController.popBackStack()
                                        }
                                    },
                                    onCancel = { navController.popBackStack() }
                                )
                            }
                            composable(Screen.ActivityList.route) {
                                val viewModel: ActivityListViewModel = hiltViewModel()
                                ActivityListScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToDetail = { id -> 
                                        navController.navigate("activity_detail/$id")
                                    },
                                    onNavigateToTrim = { id ->
                                        navController.navigate("activity_trim/$id")
                                    },
                                    onNavigateToCompare = { id1, id2 ->
                                        navController.navigate("compare/$id1,$id2")
                                    },
                                    onNavigateToSettings = { navController.navigate("activity_detail_settings_list") }
                                )
                            }
                            composable("activity_detail_settings_list") {
                                val viewModel: WorkoutDefinitionViewModel = hiltViewModel()
                                ActivityDetailSettingsListScreen(
                                    viewModel = viewModel,
                                    onNavigateToEdit = { typeName ->
                                        navController.navigate("activity_detail_settings_edit/$typeName")
                                    },
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable("activity_detail_settings_edit/{typeName}") { backStackEntry ->
                                val typeName = backStackEntry.arguments?.getString("typeName") ?: ""
                                val viewModel: ActivityDetailSettingsViewModel = hiltViewModel()
                                val settings by viewModel.settings.collectAsStateWithLifecycle()
                                
                                ActivityDetailSettingsEditScreen(
                                    viewModel = viewModel,
                                    initialWidgets = settings.visibleWidgets,
                                    initialCharts = settings.visibleCharts,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable("activity_detail/{activityId}") { backStackEntry ->
                                val viewModel: ActivityDetailViewModel = hiltViewModel()
                                ActivityDetailScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable("activity_trim/{activityId}") { backStackEntry ->
                                val viewModel: ActivityTrimViewModel = hiltViewModel()
                                ActivityTrimScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable("compare/{id1},{id2}") { backStackEntry ->
                                val viewModel: ActivityCompareViewModel = hiltViewModel()
                                ActivityCompareScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleStravaCallback(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null && data.scheme == "sportapp" && data.host == "strava") {
            val code = data.getQueryParameter("code")
            if (code != null) {
                activityScope.launch {
                    try {
                        val response = stravaAuthApi.exchangeToken(
                            clientId = BuildConfig.STRAVA_CLIENT_ID,
                            clientSecret = BuildConfig.STRAVA_CLIENT_SECRET,
                            code = code,
                            grantType = "authorization_code"
                        )
                        if (response.isSuccessful && response.body() != null) {
                            val tokenData = response.body()!!
                            stravaStorage.saveTokens(
                                accessToken = tokenData.accessToken,
                                refreshToken = tokenData.refreshToken,
                                expiresAt = tokenData.expiresAt,
                                athleteName = if (tokenData.athlete != null) "${tokenData.athlete.firstname} ${tokenData.athlete.lastname}" else null
                            )
                            Log.d("StravaAuth", "Success")
                        }
                    } catch (e: Exception) {
                        Log.e("StravaAuth", "Error", e)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleStravaCallback(intent)
    }
}
