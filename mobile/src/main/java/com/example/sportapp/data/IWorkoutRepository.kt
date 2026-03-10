package com.example.sportapp.data

import com.example.sportapp.presentation.activities.ActivityItem
import java.util.*

interface IWorkoutRepository {
    fun getUniqueActivityTypes(): List<String>
    fun getFilteredStats(
        activityType: String? = null,
        startDate: Date? = null,
        endDate: Date? = null
    ): Map<String, Any>
    fun formatDistance(meters: Double): String
    fun getAllSummaries(): List<Map<String, String>>
    fun getActivityItems(): List<ActivityItem>
}
