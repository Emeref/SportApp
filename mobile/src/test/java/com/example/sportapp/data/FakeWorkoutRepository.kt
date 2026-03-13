package com.example.sportapp.data

import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.presentation.activities.ActivityItem
import com.example.sportapp.presentation.settings.ReportingPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.*

class FakeWorkoutRepository : IWorkoutRepository {
    var summaries = mutableListOf<Map<String, String>>()
    var workouts = mutableListOf<WorkoutEntity>()

    override fun getAllWorkouts(): Flow<List<WorkoutEntity>> = flowOf(workouts)

    override suspend fun getWorkoutById(id: Long): WorkoutEntity? = workouts.find { it.id == id }

    override suspend fun getPointsForWorkout(workoutId: Long): List<WorkoutPointEntity> = emptyList()

    override suspend fun deleteWorkout(workout: WorkoutEntity) {
        workouts.remove(workout)
    }

    override suspend fun getUniqueActivityTypes(): List<String> {
        return workouts.map { it.activityName }.distinct().filter { it.isNotEmpty() }
    }

    override suspend fun getFilteredStats(
        activityType: String?,
        startDate: Date?,
        endDate: Date?
    ): Map<String, Any> {
        val filtered = workouts.filter { workout ->
            val typeMatch = activityType == null || workout.activityName == activityType
            val dateMatch = (startDate == null || workout.startTime >= startDate.time) &&
                            (endDate == null || workout.startTime <= endDate.time)
            typeMatch && dateMatch
        }

        return mapOf(
            "count" to filtered.size,
            "calories" to filtered.sumOf { it.totalCalories ?: 0.0 },
            "distanceGpsM" to filtered.sumOf { it.distanceGps ?: 0.0 },
            "distanceStepsM" to filtered.sumOf { it.distanceSteps ?: 0.0 },
            "ascent" to filtered.sumOf { it.totalAscent ?: 0.0 },
            "descent" to filtered.sumOf { it.totalDescent ?: 0.0 },
            "steps" to filtered.sumOf { it.steps?.toLong() ?: 0L },
            "raw_data" to filtered
        )
    }

    override suspend fun getStatsForPeriod(period: ReportingPeriod, customDays: Int): Map<String, Any> {
        return getFilteredStats()
    }

    override fun formatDistance(meters: Double): String = "${meters.toInt()} m"

    override suspend fun getAllSummaries(): List<Map<String, String>> = summaries

    override suspend fun getActivityItems(): List<ActivityItem> = emptyList()
}
