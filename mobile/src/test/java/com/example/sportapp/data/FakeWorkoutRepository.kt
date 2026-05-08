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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.util.*

class FakeWorkoutRepository : IWorkoutRepository {
    var workouts = MutableStateFlow<List<WorkoutEntity>>(emptyList())
    var points = mutableMapOf<Long, List<WorkoutPointEntity>>()
    var definitions = MutableStateFlow<List<WorkoutDefinition>>(emptyList())
    var laps = mutableMapOf<Long, List<WorkoutLap>>()
    private val exportedToHC = mutableSetOf<Long>()

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
        activityTypes: List<String>?,
        startDate: Date?,
        endDate: Date?
    ): Flow<Map<String, Any>> = workouts.map { list ->
        calculateStats(list, activityTypes, startDate, endDate)
    }

    override suspend fun getFilteredStats(
        activityTypes: List<String>?,
        startDate: Date?,
        endDate: Date?
    ): Map<String, Any> {
        return calculateStats(workouts.value, activityTypes, startDate, endDate)
    }

    private fun calculateStats(
        list: List<WorkoutEntity>,
        activityTypes: List<String>?,
        startDate: Date?,
        endDate: Date?
    ): Map<String, Any> {
        val filtered = list.filter { workout ->
            val typeMatch = activityTypes == null || activityTypes.contains(workout.activityName)
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
    
    override suspend fun getDefinitionById(id: Long): WorkoutDefinition? = definitions.value.find { it.id == id }

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

    override suspend fun existsByHCSessionId(hcSessionId: String): Boolean = workouts.value.any { it.hcSessionId == hcSessionId }

    override suspend fun saveImportedSession(session: ExerciseSessionSyncDto, timeSeries: SessionTimeSeries?): Long {
        val durationSeconds = Duration.between(session.startTime, session.endTime).seconds
        val id = insertWorkout(WorkoutEntity(
            activityName = session.title,
            startTime = session.startTime.toEpochMilli(),
            durationFormatted = "${durationSeconds / 60}:${durationSeconds % 60}",
            durationSeconds = durationSeconds,
            hcSessionId = session.hcSessionId
        ))
        return id
    }

    override suspend fun saveImportedGpx(
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
    ): Long {
        val id = insertWorkout(WorkoutEntity(
            activityName = name,
            startTime = startTime,
            durationFormatted = "${durationSeconds / 60}:${durationSeconds % 60}",
            durationSeconds = durationSeconds,
            distanceGps = distanceGps,
            totalCalories = calories,
            avgBpm = avgBpm,
            maxBpm = maxBpm,
            totalAscent = totalAscent,
            totalDescent = totalDescent
        ))
        insertPoints(points.map { it.copy(workoutId = id) })
        insertLaps(laps.map { it.copy(workoutId = id) })
        return id
    }

    override suspend fun updateHCSessionId(activityId: Long, hcSessionId: String) {
        workouts.value = workouts.value.map { 
            if (it.id == activityId) it.copy(hcSessionId = hcSessionId) else it 
        }
        exportedToHC.add(activityId)
    }

    override suspend fun isExportedToHC(activityId: Long): Boolean = exportedToHC.contains(activityId)
}
