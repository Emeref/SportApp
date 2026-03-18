package com.example.sportapp.data.db

import androidx.room.*
import com.example.sportapp.data.model.WorkoutDefinition
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDefinitionDao {
    @Query("SELECT * FROM workout_definitions ORDER BY isDefault DESC, name ASC")
    fun getAllDefinitions(): Flow<List<WorkoutDefinition>>

    @Query("SELECT * FROM workout_definitions ORDER BY isDefault DESC, name ASC")
    suspend fun getAllDefinitionsOnce(): List<WorkoutDefinition>

    @Query("SELECT * FROM workout_definitions WHERE id = :id")
    suspend fun getDefinitionById(id: Long): WorkoutDefinition?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefinition(definition: WorkoutDefinition): Long

    @Update
    suspend fun updateDefinition(definition: WorkoutDefinition)

    @Delete
    suspend fun deleteDefinition(definition: WorkoutDefinition)

    @Query("SELECT COUNT(*) FROM workout_definitions WHERE isDefault = 1")
    suspend fun getDefaultCount(): Int
}
