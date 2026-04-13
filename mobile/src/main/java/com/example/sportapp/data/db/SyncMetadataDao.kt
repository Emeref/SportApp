package com.example.sportapp.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncMetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metadata: SyncMetadataEntity)

    @Query("SELECT * FROM sync_metadata WHERE hcRecordId = :hcId")
    suspend fun getByHCId(hcId: String): SyncMetadataEntity?

    @Query("SELECT * FROM sync_metadata WHERE localRecordId = :localId AND recordType = :type")
    suspend fun getByLocalId(localId: Long, type: String): SyncMetadataEntity?

    @Query("SELECT * FROM sync_metadata")
    fun getAllFlow(): Flow<List<SyncMetadataEntity>>

    @Query("SELECT COUNT(*) FROM workouts WHERE hc_session_id IS NULL")
    fun getUnsyncedWorkoutsCountFlow(): Flow<Int>

    @Delete
    suspend fun delete(metadata: SyncMetadataEntity)
}
