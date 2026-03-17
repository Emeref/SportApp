package com.example.sportapp.data.db

import androidx.room.*
import com.example.sportapp.data.model.WorkoutDefinition
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDefinitionDao {
    @Query("SELECT * FROM workout_definitions ORDER BY isDefault DESC, name ASC")
    fun getAllDefinitions(): Flow<List<WorkoutDefinition>>

    @Query("SELECT * FROM workout_definitions WHERE id = :id")
    suspend fun getDefinitionById(id: Long): WorkoutDefinition?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefinitions(definitions: List<WorkoutDefinition>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefinition(definition: WorkoutDefinition): Long

    @Query("SELECT COUNT(*) FROM workout_definitions WHERE isDefault = 1")
    suspend fun getDefaultCount(): Int

    @Query("DELETE FROM workout_definitions")
    suspend fun deleteAllDefinitions()

    @Transaction
    suspend fun syncDefinitions(definitions: List<WorkoutDefinition>) {
        deleteAllDefinitions()
        insertDefinitions(definitions)
    }
}
