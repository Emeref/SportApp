package com.example.sportapp.presentation.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.data.SyncStatusManager
import com.example.sportapp.presentation.settings.MobileSettingsManager
import com.example.sportapp.presentation.settings.MobileSettingsState
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: IWorkoutRepository,
    private val syncStatusManager: SyncStatusManager
) : ViewModel() {
    private val settingsManager = MobileSettingsManager(context)
    private val messageClient by lazy { Wearable.getMessageClient(context) }
    private val nodeClient by lazy { Wearable.getNodeClient(context) }

    private val _settings = settingsManager.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MobileSettingsState()
    )
    val settings = _settings

    // Zmieniamy na Flow, który obserwuje bazę danych
    val stats: StateFlow<Map<String, Any>> = combine(_settings, repository.getAllWorkouts()) { currentSettings, _ ->
        repository.getStatsForPeriod(currentSettings.period, currentSettings.customDays)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    val isSyncing = syncStatusManager.isSyncing

    fun triggerSync() {
        if (isSyncing.value) {
            Log.d("HomeViewModel", "triggerSync: Sync already in progress, ignoring.")
            return
        }
        
        Log.d("HomeViewModel", "triggerSync: Starting sync request...")
        viewModelScope.launch {
            try {
                val nodes = nodeClient.connectedNodes.await()
                if (nodes.isNotEmpty()) {
                    nodes.forEach { node ->
                        messageClient.sendMessage(node.id, "/request_sync", byteArrayOf()).await()
                    }
                } else {
                    Log.w("HomeViewModel", "No connected nodes found for sync")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "triggerSync: Error", e)
            }
        }
    }
}
