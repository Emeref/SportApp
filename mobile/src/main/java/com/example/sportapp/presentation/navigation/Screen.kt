package com.example.sportapp.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object OverallStats : Screen("overall_stats")
    object ActivityList : Screen("activity_list")
    object ActivityDetail : Screen("activity_detail/{activityId}") {
        fun createRoute(activityId: String) = "activity_detail/$activityId"
    }
    object ActivityDetailSettingsList : Screen("activity_detail_settings_list")
    object ActivityDetailSettingsEdit : Screen("activity_detail_settings_edit/{typeName}") {
        fun createRoute(typeName: String) = "activity_detail_settings_edit/$typeName"
    }
    object ActivityTrim : Screen("activity_trim/{activityId}") {
        fun createRoute(activityId: String) = "activity_trim/$activityId"
    }
    object ActivityCompare : Screen("activity_compare/{id1}/{id2}") {
        fun createRoute(id1: String, id2: String) = "activity_compare/$id1/$id2"
    }
}
