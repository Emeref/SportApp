package com.example.sportapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sportapp.presentation.activities.ActivityListScreen
import com.example.sportapp.presentation.home.HomeScreen
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
                            HomeScreen(
                                onNavigateToStats = { navController.navigate(Screen.OverallStats.route) },
                                onNavigateToActivityList = { navController.navigate(Screen.ActivityList.route) },
                                onSyncClick = { /* TODO: Odpal synchronizację */ },
                                onSettingsClick = { /* TODO: Otwórz opcje */ }
                            )
                        }
                        composable(Screen.OverallStats.route) {
                            OverallStatsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.ActivityList.route) {
                            ActivityListScreen(
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
