package com.example.sportapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.SyncStatusManager
import com.example.sportapp.data.db.SyncMetadataDao
import com.example.sportapp.data.db.SyncMetadataEntity
import com.example.sportapp.healthconnect.ExerciseExportUseCase
import com.example.sportapp.healthconnect.ExerciseSyncUseCase
import com.example.sportapp.healthconnect.HealthDataSyncUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncStatusViewModel @Inject constructor(
    private val syncStatusManager: SyncStatusManager,
    private val syncMetadataDao: SyncMetadataDao,
    private val healthDataSyncUseCase: HealthDataSyncUseCase,
    private val exerciseSyncUseCase: ExerciseSyncUseCase,
    private val exerciseExportUseCase: ExerciseExportUseCase
) : ViewModel() {

    val isSyncing: StateFlow<Boolean> = syncStatusManager.isSyncing
    val lastHealthSync: StateFlow<Long> = syncStatusManager.lastHealthSync
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)
    val lastWorkoutSync: StateFlow<Long> = syncStatusManager.lastWorkoutSync
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)
    val unsyncedCount: StateFlow<Int> = syncStatusManager.unsyncedWorkoutsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val syncHistory: StateFlow<List<SyncMetadataEntity>> = syncMetadataDao.getAllFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun syncNow() {
        viewModelScope.launch {
            syncStatusManager.setSyncing(true)
            try {
                // 1. Sync health data (Stage 2)
                healthDataSyncUseCase.sync()

                // 2. Import HC activities (Stage 3 & 4)
                exerciseSyncUseCase.sync()

                // 3. Export local activities (Stage 5)
                exerciseExportUseCase.exportAllUnsynced()
                
            } catch (e: Exception) {
                // handle error
            } finally {
                syncStatusManager.setSyncing(false)
            }
        }
    }
}
