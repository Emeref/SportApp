package com.example.sportapp.data

import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.presentation.activities.ActivityItem
import com.example.sportapp.presentation.settings.ReportingPeriod
import kotlinx.coroutines.flow.Flow
import java.util.*

interface IWorkoutRepository {
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>
    suspend fun getWorkoutById(id: Long): WorkoutEntity?
    suspend fun getPointsForWorkout(workoutId: Long): List<WorkoutPointEntity>
    suspend fun deleteWorkout(workout: WorkoutEntity)
    suspend fun updateWorkout(workout: WorkoutEntity)
    suspend fun trimWorkout(workout: WorkoutEntity, startPointId: Long, endPointId: Long)

    suspend fun getUniqueActivityTypes(): List<String>
    
    fun getFilteredStatsFlow(
        activityType: String? = null,
        startDate: Date? = null,
        endDate: Date? = null
    ): Flow<Map<String, Any>>

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
    
    suspend fun getActivityItems(): List<ActivityItem>
    fun getActivityItemsFlow(): Flow<List<ActivityItem>>
}
