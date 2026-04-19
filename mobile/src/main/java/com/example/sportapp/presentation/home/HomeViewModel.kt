package com.example.sportapp.presentation.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.data.SyncStatusManager
import com.example.sportapp.data.db.LiveLocationDao
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.WorkoutSensor
import com.example.sportapp.presentation.settings.MobileSettingsManager
import com.example.sportapp.presentation.settings.MobileSettingsState
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: IWorkoutRepository,
    private val syncStatusManager: SyncStatusManager,
    private val liveLocationDao: LiveLocationDao
) : ViewModel(), MessageClient.OnMessageReceivedListener {
    private val settingsManager = MobileSettingsManager(context)
    private val messageClient by lazy { Wearable.getMessageClient(context) }
    private val nodeClient by lazy { Wearable.getNodeClient(context) }

    private val _settings = settingsManager.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MobileSettingsState()
    )
    val settings = _settings

    val stats: StateFlow<Map<String, Any>> = combine(_settings, repository.getAllWorkouts()) { currentSettings, _ ->
        repository.getStatsForPeriod(currentSettings.period, currentSettings.customDays)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    val isSyncing = syncStatusManager.isSyncing

    val locationDefinitions: StateFlow<List<WorkoutDefinition>> = repository.getAllDefinitions()
        .map { definitions ->
            definitions.filter { def ->
                def.sensors.any { it.isRecording && (it.sensorId == WorkoutSensor.SPEED_GPS.id || it.sensorId == WorkoutSensor.DISTANCE_GPS.id || it.sensorId == WorkoutSensor.MAP.id) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    init {
        try {
            messageClient.addListener(this)
        } catch (e: Exception) {
            Log.e("HomeViewModel", "init: Error adding listener", e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            messageClient.removeListener(this)
        } catch (e: Exception) {
            Log.e("HomeViewModel", "onCleared: Error removing listener", e)
        }
    }

    override fun onMessageReceived(event: MessageEvent) {
        if (event.path == "/activity_started") {
            viewModelScope.launch {
                liveLocationDao.clear() // Clear old data before starting new session
                _navigationEvent.emit("live_tracking")
            }
        }
    }

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
            } catch (e: ApiException) {
                Log.e("HomeViewModel", "triggerSync: Wearable API error", e)
                if (e.statusCode == 17) {
                    _errorEvent.emit("ERROR_WEARABLE_NOT_AVAILABLE")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "triggerSync: Error", e)
            }
        }
    }

    fun startActivityOnWatch(definition: WorkoutDefinition) {
        viewModelScope.launch {
            try {
                val nodes = nodeClient.connectedNodes.await()
                if (nodes.isNotEmpty()) {
                    nodes.forEach { node ->
                        messageClient.sendMessage(node.id, "/start_activity", definition.id.toString().toByteArray()).await()
                    }
                } else {
                    _errorEvent.emit("ERROR_NO_WATCH_CONNECTED")
                }
            } catch (e: ApiException) {
                Log.e("HomeViewModel", "startActivityOnWatch: Wearable API error", e)
                if (e.statusCode == 17) {
                    _errorEvent.emit("ERROR_WEARABLE_NOT_AVAILABLE")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "startActivityOnWatch: Error", e)
            }
        }
    }
}
