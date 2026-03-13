package com.example.sportapp.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Query("SELECT * FROM workouts ORDER BY startTime DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): WorkoutEntity?

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    @Insert
    suspend fun insertPoint(point: WorkoutPointEntity)

    @Query("SELECT * FROM workout_points WHERE workoutId = :workoutId ORDER BY id ASC")
    suspend fun getPointsForWorkout(workoutId: Long): List<WorkoutPointEntity>
    
    @Query("SELECT * FROM workouts WHERE startTime >= :since")
    suspend fun getWorkoutsSince(since: Long): List<WorkoutEntity>
}
