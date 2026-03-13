package com.example.sportapp.presentation.workout

import android.content.Context
import com.example.sportapp.data.db.WorkoutDao
import java.util.*

object SummaryManager {
    
    data class WeeklyStats(
        val totalSteps: Int,
        val totalDistanceMeters: Int,
        val totalCalories: Int
    )

    suspend fun getWeeklyStats(workoutDao: WorkoutDao): WeeklyStats {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val oneWeekAgo = calendar.timeInMillis

        val workouts = workoutDao.getWorkoutsSince(oneWeekAgo)
        
        var totalSteps = 0
        var totalDistance = 0.0
        var totalCalories = 0.0

        workouts.forEach { workout ->
            totalSteps += workout.steps ?: 0
            val distGps = workout.distanceGps ?: 0.0
            val distSteps = workout.distanceSteps ?: 0.0
            totalDistance += if (distGps > 0) distGps else distSteps
            totalCalories += workout.totalCalories ?: 0.0
        }

        return WeeklyStats(totalSteps, totalDistance.toInt(), totalCalories.toInt())
    }
}
