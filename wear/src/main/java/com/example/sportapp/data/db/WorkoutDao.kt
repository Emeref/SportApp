package com.example.sportapp.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Query("SELECT * FROM workouts ORDER BY startTime DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): WorkoutEntity?

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteWorkoutById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoint(point: WorkoutPointEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: List<WorkoutPointEntity>)

    @Query("SELECT * FROM workout_points WHERE workoutId = :workoutId ORDER BY id ASC")
    suspend fun getPointsForWorkout(workoutId: Long): List<WorkoutPointEntity>

    @Query("DELETE FROM workout_points WHERE workoutId = :workoutId")
    suspend fun deletePointsForWorkout(workoutId: Long)
    
    @Query("SELECT * FROM workouts WHERE startTime >= :since")
    suspend fun getWorkoutsSince(since: Long): List<WorkoutEntity>

    @Query("SELECT * FROM workouts WHERE isSynced = 0 AND isFinished = 1")
    suspend fun getUnsyncedWorkouts(): List<WorkoutEntity>

    @Query("UPDATE workouts SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)

    @Query("SELECT MAX(id) FROM workouts")
    suspend fun getMaxId(): Long?

    @Transaction
    suspend fun deleteWorkoutWithPoints(id: Long) {
        deletePointsForWorkout(id)
        deleteWorkoutById(id)
    }

    @Transaction
    suspend fun updateWorkoutId(oldId: Long, newId: Long) {
        val workout = getWorkoutById(oldId) ?: return
        val points = getPointsForWorkout(oldId)
        
        // Delete old
        deletePointsForWorkout(oldId)
        deleteWorkoutById(oldId)
        
        // Insert new with new ID
        val updatedWorkout = workout.copy(id = newId, isSynced = true)
        insertWorkout(updatedWorkout)
        
        val updatedPoints = points.map { it.copy(id = 0, workoutId = newId) }
        insertPoints(updatedPoints)
    }
}
