package com.example.sportapp.data.db

import androidx.room.*
import com.example.sportapp.data.model.WorkoutLap
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

    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getWorkoutFlowById(id: Long): Flow<WorkoutEntity?>

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: List<WorkoutPointEntity>)

    @Query("SELECT * FROM workout_points WHERE workoutId = :workoutId ORDER BY id ASC")
    suspend fun getPointsForWorkout(workoutId: Long): List<WorkoutPointEntity>

    @Query("SELECT * FROM workout_points WHERE workoutId = :workoutId ORDER BY id ASC")
    fun getPointsFlowForWorkout(workoutId: Long): Flow<List<WorkoutPointEntity>>
    
    @Query("SELECT * FROM workouts WHERE startTime >= :since")
    suspend fun getWorkoutsSince(since: Long): List<WorkoutEntity>

    @Query("DELETE FROM workout_points WHERE workoutId = :workoutId")
    suspend fun deletePointsForWorkout(workoutId: Long)

    @Query("DELETE FROM workout_points WHERE workoutId = :workoutId AND id NOT BETWEEN :startId AND :endId")
    suspend fun deletePointsOutsideRange(workoutId: Long, startId: Long, endId: Long)

    @Transaction
    suspend fun trimWorkout(workout: WorkoutEntity, startId: Long, endId: Long, updatedPoints: List<WorkoutPointEntity>) {
        deletePointsOutsideRange(workout.id, startId, endId)
        insertPoints(updatedPoints)
        updateWorkout(workout)
    }

    @Query("SELECT MAX(speedGps) FROM workout_points WHERE workoutId = :workoutId")
    suspend fun getMaxSpeedGps(workoutId: Long): Double?

    @Query("SELECT MAX(speedSteps) FROM workout_points WHERE workoutId = :workoutId")
    suspend fun getMaxSpeedSteps(workoutId: Long): Double?

    @Query("SELECT MAX(distanceGps) FROM workout_points WHERE workoutId = :workoutId")
    suspend fun getMaxDistanceGps(workoutId: Long): Double?

    @Query("SELECT MAX(distanceSteps) FROM workout_points WHERE workoutId = :workoutId")
    suspend fun getMaxDistanceSteps(workoutId: Long): Double?

    @Query("SELECT MAX(steps) FROM workout_points WHERE workoutId = :workoutId")
    suspend fun getMaxSteps(workoutId: Long): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaps(laps: List<WorkoutLap>)

    @Query("SELECT * FROM workout_laps WHERE workoutId = :workoutId ORDER BY lapNumber ASC")
    suspend fun getLapsForWorkout(workoutId: Long): List<WorkoutLap>

    @Query("SELECT * FROM workout_laps WHERE workoutId = :workoutId ORDER BY lapNumber ASC")
    fun getLapsFlowForWorkout(workoutId: Long): Flow<List<WorkoutLap>>

    @Query("DELETE FROM workout_laps WHERE workoutId = :workoutId")
    suspend fun deleteLapsForWorkout(workoutId: Long)

    @Transaction
    suspend fun updateLapsForWorkout(workoutId: Long, laps: List<WorkoutLap>) {
        deleteLapsForWorkout(workoutId)
        insertLaps(laps)
    }
}
