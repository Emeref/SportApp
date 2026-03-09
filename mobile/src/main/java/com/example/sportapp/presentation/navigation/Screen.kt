package com.example.sportapp.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object OverallStats : Screen("overall_stats")
    object ActivityList : Screen("activity_list")
    object ActivityDetail : Screen("activity_detail/{activityId}") {
        fun createRoute(activityId: String) = "activity_detail/$activityId"
    }
}
