package com.example.sportapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sportapp.presentation.activities.ActivityListScreen
import com.example.sportapp.presentation.activities.ActivityListViewModel
import com.example.sportapp.presentation.activities.ActivityListViewModelFactory
import com.example.sportapp.presentation.home.HomeScreen
import com.example.sportapp.presentation.home.HomeViewModel
import com.example.sportapp.presentation.home.HomeViewModelFactory
import com.example.sportapp.presentation.navigation.Screen
import com.example.sportapp.presentation.stats.OverallStatsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) {
                            val homeViewModel: HomeViewModel = viewModel(
                                factory = HomeViewModelFactory(applicationContext)
                            )
                            HomeScreen(
                                viewModel = homeViewModel,
                                onNavigateToStats = { navController.navigate(Screen.OverallStats.route) },
                                onNavigateToActivityList = { navController.navigate(Screen.ActivityList.route) },
                                onSettingsClick = { /* TODO: Otwórz opcje */ }
                            )
                        }
                        composable(Screen.OverallStats.route) {
                            OverallStatsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.ActivityList.route) {
                            val activityListViewModel: ActivityListViewModel = viewModel(
                                factory = ActivityListViewModelFactory(applicationContext)
                            )
                            ActivityListScreen(
                                viewModel = activityListViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToDetail = { id -> 
                                    navController.navigate(Screen.ActivityDetail.createRoute(id))
                                }
                            )
                        }
                        composable(Screen.ActivityDetail.route) {
                            // Placeholder dla szczegółów
                            Surface { }
                        }
                    }
                }
            }
        }
    }
}
