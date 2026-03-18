package com.example.sportapp.presentation.workout

import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.presentation.settings.ReportingPeriod
import java.util.*

object SummaryManager {
    
    data class WatchStats(
        val count: Int = 0,
        val calories: Int = 0,
        val distanceGpsM: Int = 0,
        val distanceStepsM: Int = 0,
        val ascent: Int = 0,
        val descent: Int = 0,
        val steps: Int = 0
    )

    suspend fun getStatsForPeriod(
        workoutDao: WorkoutDao,
        period: ReportingPeriod,
        customDays: Int
    ): WatchStats {
        val calendar = Calendar.getInstance()
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
        val since = calendar.timeInMillis

        val workouts = workoutDao.getWorkoutsSince(since)
        
        var count = workouts.size
        var calories = 0.0
        var distanceGps = 0.0
        var distanceSteps = 0.0
        var ascent = 0.0
        var descent = 0.0
        var steps = 0

        workouts.forEach { workout ->
            calories += workout.totalCalories ?: 0.0
            distanceGps += workout.distanceGps ?: 0.0
            distanceSteps += workout.distanceSteps ?: 0.0
            ascent += workout.totalAscent ?: 0.0
            descent += workout.totalDescent ?: 0.0
            steps += workout.steps ?: 0
        }

        return WatchStats(
            count = count,
            calories = calories.toInt(),
            distanceGpsM = distanceGps.toInt(),
            distanceStepsM = distanceSteps.toInt(),
            ascent = ascent.toInt(),
            descent = descent.toInt(),
            steps = steps
        )
    }
}
