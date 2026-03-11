package com.example.sportapp.presentation.workout

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WearSyncService : WearableListenerService() {

    @Inject
    lateinit var dataLayerManager: DataLayerManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/request_sync") {
            Log.d("WearSyncService", "Sync request received")
            scope.launch {
                dataLayerManager.syncActivities()
            }
        }
    }
}
