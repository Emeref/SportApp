package com.example.sportapp.data

import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.WorkoutLap
import com.example.sportapp.healthconnect.model.ExerciseSessionSyncDto
import com.example.sportapp.healthconnect.model.SessionTimeSeries
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
        activityTypes: List<String>? = null,
        startDate: Date? = null,
        endDate: Date? = null
    ): Flow<Map<String, Any>>

    suspend fun getFilteredStats(
        activityTypes: List<String>? = null,
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

    fun getAllDefinitions(): Flow<List<WorkoutDefinition>>

    suspend fun insertWorkout(workout: WorkoutEntity): Long
    suspend fun insertPoints(points: List<WorkoutPointEntity>)
    suspend fun insertLaps(laps: List<WorkoutLap>)

    suspend fun existsByHCSessionId(hcSessionId: String): Boolean
    suspend fun saveImportedSession(session: ExerciseSessionSyncDto, timeSeries: SessionTimeSeries? = null): Long
    
    suspend fun saveImportedGpx(
        definitionId: Long,
        name: String,
        startTime: Long,
        endTime: Long,
        durationSeconds: Long,
        distanceGps: Double,
        calories: Double,
        avgBpm: Double?,
        maxBpm: Int?,
        totalAscent: Double,
        totalDescent: Double,
        points: List<WorkoutPointEntity>,
        laps: List<WorkoutLap>
    ): Long

    suspend fun updateHCSessionId(activityId: Long, hcSessionId: String)
    suspend fun isExportedToHC(activityId: Long): Boolean
}
