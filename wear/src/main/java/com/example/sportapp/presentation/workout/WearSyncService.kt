package com.example.sportapp.presentation.workout

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WearSyncService : WearableListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/request_sync") {
            Log.d("WearSyncService", "Sync request received")
            val dataLayerManager = DataLayerManager(applicationContext)
            scope.launch {
                dataLayerManager.syncActivities()
            }
        }
    }
}
