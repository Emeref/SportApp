package com.example.sportapp.data

import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.WorkoutLap
import com.example.sportapp.presentation.activities.ActivityItem
import com.example.sportapp.presentation.settings.ReportingPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.*

class FakeWorkoutRepository : IWorkoutRepository {
    var workouts = MutableStateFlow<List<WorkoutEntity>>(emptyList())
    var points = mutableMapOf<Long, List<WorkoutPointEntity>>()
    var definitions = MutableStateFlow<List<WorkoutDefinition>>(emptyList())
    var laps = mutableMapOf<Long, List<WorkoutLap>>()

    override fun getAllWorkouts(): Flow<List<WorkoutEntity>> = workouts

    override suspend fun getWorkoutById(id: Long): WorkoutEntity? = workouts.value.find { it.id == id }

    override suspend fun getPointsForWorkout(workoutId: Long): List<WorkoutPointEntity> = points[workoutId] ?: emptyList()

    override suspend fun deleteWorkout(workout: WorkoutEntity) {
        workouts.value = workouts.value.filter { it.id != workout.id }
    }

    override suspend fun updateWorkout(workout: WorkoutEntity) {
        workouts.value = workouts.value.map { if (it.id == workout.id) workout else it }
    }

    override suspend fun trimWorkout(workout: WorkoutEntity, startPointId: Long, endPointId: Long) {
        val currentPoints = points[workout.id] ?: return
        points[workout.id] = currentPoints.filter { it.id in startPointId..endPointId }
        updateWorkout(workout)
    }

    override suspend fun getUniqueActivityTypes(): List<String> {
        return workouts.value.map { it.activityName }.distinct().filter { it.isNotEmpty() }.sorted()
    }

    override fun getFilteredStatsFlow(
        activityType: String?,
        startDate: Date?,
        endDate: Date?
    ): Flow<Map<String, Any>> = workouts.map { list ->
        calculateStats(list, activityType, startDate, endDate)
    }

    override suspend fun getFilteredStats(
        activityType: String?,
        startDate: Date?,
        endDate: Date?
    ): Map<String, Any> {
        return calculateStats(workouts.value, activityType, startDate, endDate)
    }

    private fun calculateStats(
        list: List<WorkoutEntity>,
        activityType: String?,
        startDate: Date?,
        endDate: Date?
    ): Map<String, Any> {
        val filtered = list.filter { workout ->
            val typeMatch = activityType == null || workout.activityName == activityType
            val startMatch = startDate == null || workout.startTime >= startDate.time
            val endMatch = endDate == null || workout.startTime <= endDate.time
            typeMatch && startMatch && endMatch
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

    override suspend fun getActivityItems(): List<ActivityItem> {
        return workouts.value.map { workout ->
            ActivityItem(
                id = workout.id.toString(),
                type = workout.activityName,
                date = "2023-10-15 10:30",
                duration = workout.durationFormatted,
                calories = "${workout.totalCalories?.toInt()} kcal",
                distanceGps = "${workout.distanceGps?.toInt()} m",
                distanceSteps = "${workout.distanceSteps?.toInt()} m",
                rawTimestamp = workout.startTime,
                rawDurationSeconds = workout.durationSeconds,
                rawCalories = workout.totalCalories ?: 0.0,
                rawDistanceGps = workout.distanceGps ?: 0.0,
                rawDistanceSteps = workout.distanceSteps ?: 0.0
            )
        }
    }

    override fun getActivityItemsFlow(): Flow<List<ActivityItem>> = flow {
        emit(getActivityItems())
    }

    override fun getAllDefinitions(): Flow<List<WorkoutDefinition>> = definitions

    override suspend fun insertWorkout(workout: WorkoutEntity): Long {
        val id = (workouts.value.maxOfOrNull { it.id } ?: 0L) + 1L
        val newWorkout = workout.copy(id = id)
        workouts.value = workouts.value + newWorkout
        return id
    }

    override suspend fun insertPoints(points: List<WorkoutPointEntity>) {
        if (points.isEmpty()) return
        val workoutId = points.first().workoutId
        this.points[workoutId] = (this.points[workoutId] ?: emptyList()) + points
    }

    override suspend fun insertLaps(laps: List<WorkoutLap>) {
        if (laps.isEmpty()) return
        val workoutId = laps.first().workoutId
        this.laps[workoutId] = (this.laps[workoutId] ?: emptyList()) + laps
    }
}
