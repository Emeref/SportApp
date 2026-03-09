package com.example.sportapp.data

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream

class SyncService : WearableListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("SyncService", "onDataChanged: Received ${dataEvents.count} events")
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path ?: return@forEach
                Log.d("SyncService", "Data changed on path: $path")
                
                if (path.startsWith("/activities/")) {
                    val fileName = path.substringAfterLast("/")
                    val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                    val asset = dataMapItem.dataMap.getAsset("file_asset") ?: return@forEach

                    scope.launch {
                        saveAssetToFile(asset, fileName)
                    }
                }
            }
        }
    }

    private suspend fun saveAssetToFile(asset: com.google.android.gms.wearable.Asset, fileName: String) {
        try {
            Log.d("SyncService", "Downloading asset: $fileName")
            val dataClient = Wearable.getDataClient(this)
            val fd = dataClient.getFdForAsset(asset).await()
            
            fd.inputStream.use { inputStream ->
                val activitiesDir = File(filesDir, "activities")
                if (!activitiesDir.exists()) {
                    activitiesDir.mkdirs()
                }

                val file = File(activitiesDir, fileName)
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                Log.d("SyncService", "File saved successfully to: ${file.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e("SyncService", "Error saving asset: $fileName", e)
        }
    }
}
