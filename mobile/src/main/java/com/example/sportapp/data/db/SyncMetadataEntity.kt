package com.example.sportapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_metadata")
data class SyncMetadataEntity(
    @PrimaryKey val hcRecordId: String,
    val localRecordId: Long,
    val recordType: String, // "EXERCISE", "HEALTH_PROFILE", or specific activity name
    val lastSyncTime: Long,
    val syncDirection: String, // "FROM_HC", "TO_HC", "BIDIRECTIONAL"
    val localModifiedTime: Long,
    val hcModifiedTime: Long,
    val activityName: String? = null, // Nowe pole dla szczegółów
    val startTime: Long? = null,      // Nowe pole dla czasu rozpoczęcia
    val stravaUploadId: Long? = null,
    val stravaSyncStatus: String = "PENDING" // PENDING, SUCCESS, FAILED
)
