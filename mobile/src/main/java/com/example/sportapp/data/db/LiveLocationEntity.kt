package com.example.sportapp.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "live_location_points")
data class LiveLocationPoint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val bearing: Float? = null,
    val altitude: Double? = null,
    val accuracy: Float? = null
)

@Dao
interface LiveLocationDao {
    @Insert
    suspend fun insert(point: LiveLocationPoint)

    @Query("SELECT * FROM live_location_points ORDER BY timestamp ASC")
    fun getAllPoints(): Flow<List<LiveLocationPoint>>

    @Query("DELETE FROM live_location_points")
    suspend fun clear()
}
