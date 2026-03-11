package com.example.sportapp.presentation.workout

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataLayerManager @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun syncActivities() {
        Log.d("DataLayerManager", "syncActivities: Starting search for files...")
        val dataClient = Wearable.getDataClient(context)
        
        // Według Device Explorera pliki są bezpośrednio w filesDir
        val searchDirs = listOf(context.filesDir, File(context.filesDir, "activities"))
        
        var filesFound = 0
        searchDirs.forEach { dir ->
            if (!dir.exists()) {
                Log.d("DataLayerManager", "Directory does not exist: ${dir.path}")
                return@forEach
            }
            
            val files = dir.listFiles { file -> 
                file.isFile && (file.name.endsWith(".csv") || file.name == "Podsumowanie_cwiczen.csv")
            } ?: return@forEach

            Log.d("DataLayerManager", "Found ${files.size} files in ${dir.path}")
            filesFound += files.size

            files.forEach { file ->
                try {
                    Log.d("DataLayerManager", "Syncing file: ${file.name} (${file.length()} bytes)")
                    val asset = Asset.createFromBytes(file.readBytes())
                    
                    val request = PutDataMapRequest.create("/activities/${file.name}").apply {
                        dataMap.putAsset("file_asset", asset)
                        dataMap.putLong("timestamp", System.currentTimeMillis())
                    }.asPutDataRequest().setUrgent()

                    dataClient.putDataItem(request).await()
                    Log.d("DataLayerManager", "Successfully pushed to Data Layer: ${file.name}")
                } catch (e: Exception) {
                    Log.e("DataLayerManager", "Error syncing file: ${file.name}", e)
                }
            }
        }
        
        if (filesFound == 0) {
            Log.w("DataLayerManager", "No CSV files found to sync!")
        }
    }
}
