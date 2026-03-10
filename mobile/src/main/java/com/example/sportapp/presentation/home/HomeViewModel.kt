package com.example.sportapp.presentation.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.WorkoutRepository
import com.example.sportapp.presentation.settings.MobileSettingsManager
import com.example.sportapp.presentation.settings.MobileSettingsState
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel(context: Context) : ViewModel() {
    private val repository = WorkoutRepository(context)
    private val settingsManager = MobileSettingsManager(context)
    private val messageClient = Wearable.getMessageClient(context)
    private val nodeClient = Wearable.getNodeClient(context)

    private val _settings = settingsManager.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MobileSettingsState()
    )
    val settings = _settings

    private val _stats = MutableStateFlow<Map<String, Any>>(emptyMap())
    val stats = _stats.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    init {
        viewModelScope.launch {
            _settings.collect {
                refreshStats()
            }
        }
    }

    fun refreshStats() {
        viewModelScope.launch {
            val currentSettings = _settings.value
            _stats.value = repository.getStatsForPeriodSuspend(currentSettings.period, currentSettings.customDays)
        }
    }

    fun triggerSync() {
        Log.d("HomeViewModel", "triggerSync: Starting sync request...")
        _isSyncing.value = true
        viewModelScope.launch {
            try {
                val nodes = nodeClient.connectedNodes.await()
                if (nodes.isNotEmpty()) {
                    nodes.forEach { node ->
                        messageClient.sendMessage(node.id, "/request_sync", byteArrayOf()).await()
                    }
                    delay(3000)
                    refreshStats()
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "triggerSync: Error", e)
            } finally {
                _isSyncing.value = false
            }
        }
    }
}
