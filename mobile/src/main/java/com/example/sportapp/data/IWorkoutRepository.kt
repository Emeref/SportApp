package com.example.sportapp.data

import com.example.sportapp.presentation.activities.ActivityItem
import com.example.sportapp.presentation.settings.ReportingPeriod
import java.util.*

interface IWorkoutRepository {
    suspend fun getUniqueActivityTypes(): List<String>
    
    suspend fun getFilteredStats(
        activityType: String? = null,
        startDate: Date? = null,
        endDate: Date? = null
    ): Map<String, Any>

    suspend fun getStatsForPeriod(
        period: ReportingPeriod, 
        customDays: Int = 7
    ): Map<String, Any>

    fun formatDistance(meters: Double): String
    
    suspend fun getAllSummaries(): List<Map<String, String>>
    
    suspend fun getActivityItems(): List<ActivityItem>
}
