package com.example.sportapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.sportapp.data.db.SyncMetadataDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.syncDataStore by preferencesDataStore(name = "sync_prefs")

@Singleton
class SyncStatusManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val syncMetadataDao: SyncMetadataDao
) {
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    companion object {
        private val LAST_HEALTH_SYNC = longPreferencesKey("last_health_sync")
        private val LAST_WORKOUT_SYNC = longPreferencesKey("last_workout_sync")
    }

    val lastHealthSync: Flow<Long> = context.syncDataStore.data.map { it[LAST_HEALTH_SYNC] ?: 0L }
    val lastWorkoutSync: Flow<Long> = context.syncDataStore.data.map { it[LAST_WORKOUT_SYNC] ?: 0L }

    val unsyncedWorkoutsCount: Flow<Int> = syncMetadataDao.getUnsyncedWorkoutsCountFlow()

    fun setSyncing(syncing: Boolean) {
        _isSyncing.value = syncing
    }

    suspend fun updateLastHealthSync(timestamp: Long) {
        context.syncDataStore.edit { it[LAST_HEALTH_SYNC] = timestamp }
    }

    suspend fun updateLastWorkoutSync(timestamp: Long) {
        context.syncDataStore.edit { it[LAST_WORKOUT_SYNC] = timestamp }
    }
}
