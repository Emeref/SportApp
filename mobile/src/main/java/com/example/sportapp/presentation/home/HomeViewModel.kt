package com.example.sportapp.presentation.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.WorkoutRepository
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel(context: Context) : ViewModel() {
    private val repository = WorkoutRepository(context)
    private val messageClient = Wearable.getMessageClient(context)
    private val nodeClient = Wearable.getNodeClient(context)

    private val _stats = MutableStateFlow(repository.getSummaryStatsLast7Days())
    val stats = _stats.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    fun refreshStats() {
        _stats.value = repository.getSummaryStatsLast7Days()
    }

    fun triggerSync() {
        Log.d("HomeViewModel", "triggerSync: Starting sync request...")
        _isSyncing.value = true
        viewModelScope.launch {
            try {
                val nodes = nodeClient.connectedNodes.await()
                Log.d("HomeViewModel", "triggerSync: Found ${nodes.size} connected nodes")
                
                if (nodes.isEmpty()) {
                    Log.w("HomeViewModel", "triggerSync: No connected Wear OS devices found!")
                    _isSyncing.value = false
                    return@launch
                }

                nodes.forEach { node ->
                    Log.d("HomeViewModel", "triggerSync: Sending /request_sync to node: ${node.displayName}")
                    messageClient.sendMessage(node.id, "/request_sync", byteArrayOf()).await()
                }
                
                // Czekamy chwilę na zakończenie transferu plików (Data Layer jest asynchroniczny)
                delay(3000)
                refreshStats()
                Log.d("HomeViewModel", "triggerSync: Sync finished and stats refreshed")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "triggerSync: Error sending sync request", e)
            } finally {
                _isSyncing.value = false
            }
        }
    }
}
