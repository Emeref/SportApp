package com.example.sportapp.data

import android.content.Context
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.presentation.activities.ActivityItem
import com.example.sportapp.presentation.settings.ReportingPeriod
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workoutDao: WorkoutDao
) : IWorkoutRepository {

    override fun getAllWorkouts(): Flow<List<WorkoutEntity>> {
        return workoutDao.getAllWorkouts()
    }

    override suspend fun getWorkoutById(id: Long): WorkoutEntity? {
        return workoutDao.getWorkoutById(id)
    }

    override suspend fun getPointsForWorkout(workoutId: Long): List<WorkoutPointEntity> {
        return workoutDao.getPointsForWorkout(workoutId)
    }

    override suspend fun deleteWorkout(workout: WorkoutEntity) {
        workoutDao.deletePointsForWorkout(workout.id)
        workoutDao.deleteWorkout(workout)
    }

    override suspend fun getUniqueActivityTypes(): List<String> = withContext(Dispatchers.IO) {
        workoutDao.getWorkoutsSince(0).map { it.activityName }.distinct().filter { it.isNotEmpty() }
    }

    override fun getFilteredStatsFlow(
        activityType: String?,
        startDate: Date?,
        endDate: Date?
    ): Flow<Map<String, Any>> {
        return workoutDao.getAllWorkouts().map { allWorkouts ->
            val filtered = allWorkouts.filter { workout ->
                val typeMatch = activityType == null || workout.activityName == activityType
                val workoutDate = workout.startTime
                val startMatch = startDate == null || workoutDate >= startDate.time
                val endMatch = endDate == null || workoutDate <= endDate.time
                typeMatch && startMatch && endMatch
            }

            mapOf(
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
    }

    override suspend fun getFilteredStats(
        activityType: String?,
        startDate: Date?,
        endDate: Date?
    ): Map<String, Any> = withContext(Dispatchers.IO) {
        val workouts = workoutDao.getWorkoutsSince(0)

        val filtered = workouts.filter { workout ->
            val typeMatch = activityType == null || workout.activityName == activityType
            val workoutDate = workout.startTime
            val startMatch = startDate == null || workoutDate >= startDate.time
            val endMatch = endDate == null || workoutDate <= endDate.time
            typeMatch && startMatch && endMatch
        }

        mapOf(
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
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        
        when (period) {
            ReportingPeriod.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            ReportingPeriod.WEEK -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            ReportingPeriod.MONTH -> calendar.add(Calendar.MONTH, -1)
            ReportingPeriod.YEAR -> calendar.add(Calendar.YEAR, -1)
            ReportingPeriod.CUSTOM -> calendar.add(Calendar.DAY_OF_YEAR, -customDays)
        }
        
        val startDate = calendar.time
        return getFilteredStats(null, startDate, endDate)
    }

    override fun formatDistance(meters: Double): String {
        return when {
            meters < 1000 -> "${meters.toInt()} m"
            meters < 10000 -> String.format(Locale.US, "%.2f km", meters / 1000.0)
            meters < 100000 -> String.format(Locale.US, "%.1f km", meters / 1000.0)
            else -> String.format(Locale.US, "%.0f km", meters / 1000.0)
        }
    }

    override suspend fun getAllSummaries(): List<Map<String, String>> = withContext(Dispatchers.IO) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        workoutDao.getWorkoutsSince(0).map { workout ->
            mapOf(
                "nazwa aktywnosci" to workout.activityName,
                "data" to sdf.format(Date(workout.startTime)),
                "dlugosc" to workout.durationFormatted,
                "kalorie" to (workout.totalCalories?.toString() ?: "0"),
                "gps_dystans" to (workout.distanceGps?.toString() ?: "0"),
                "kroki_dystans" to (workout.distanceSteps?.toString() ?: "0"),
                "kroki" to (workout.steps?.toString() ?: "0"),
                "przewyzszenia_gora" to (workout.totalAscent?.toString() ?: "0"),
                "przewyzszenia_dol" to (workout.totalDescent?.toString() ?: "0")
            )
        }
    }

    override suspend fun getActivityItems(): List<ActivityItem> = withContext(Dispatchers.IO) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        workoutDao.getWorkoutsSince(0)
            .sortedByDescending { it.startTime }
            .map { workout ->
                ActivityItem(
                    id = workout.id.toString(),
                    type = workout.activityName,
                    date = sdf.format(Date(workout.startTime)),
                    duration = workout.durationFormatted,
                    calories = "${workout.totalCalories?.toInt() ?: 0} kcal",
                    distanceGps = formatDistance(workout.distanceGps ?: 0.0),
                    distanceSteps = formatDistance(workout.distanceSteps ?: 0.0)
                )
            }
    }
}
