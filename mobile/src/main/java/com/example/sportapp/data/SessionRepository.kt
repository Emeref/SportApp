package com.example.sportapp.data

import android.content.Context
import android.util.Log
import com.example.sportapp.presentation.settings.MobileSettingsManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

data class SessionData(
    val times: List<String>,
    val route: List<LatLng>,
    val charts: Map<String, List<Float?>>,
    val error: String? = null
)

class SessionRepository(private val context: Context) {
    private val settingsManager = MobileSettingsManager(context)

    private suspend fun getActivitiesDir(): File = withContext(Dispatchers.IO) {
        val useTestData = settingsManager.settingsFlow.first().useTestData
        val dirName = if (useTestData) "test_activities" else "activities"
        val dir = File(context.filesDir, dirName)
        if (!dir.exists()) dir.mkdirs()
        dir
    }

    suspend fun getSessionData(fileName: String): SessionData = withContext(Dispatchers.IO) {
        val file = File(getActivitiesDir(), fileName)
        if (!file.exists()) return@withContext SessionData(emptyList(), emptyList(), emptyMap(), "Plik nie istnieje")

        val times = mutableListOf<String>()
        val route = mutableListOf<LatLng>()
        val chartData = mutableMapOf<String, MutableList<Float?>>()
        
        val columns = listOf(
            "bpm", "srednie_bpm", "kroki", "kroki_min", "kroki_dystans", 
            "gps_dystans", "predkosc_gps", "wysokosc", "przewyzszenia_gora", "przewyzszenia_dol"
        )
        columns.forEach { chartData[it] = mutableListOf() }

        try {
            val lines = file.readLines()
            if (lines.size < 2) return@withContext SessionData(emptyList(), emptyList(), emptyMap(), "Plik jest pusty lub uszkodzony")

            val header = lines[0].split(";")
            val timeIdx = header.indexOf("czas")
            val latIdx = header.indexOf("lat")
            val lonIdx = header.indexOf("lon")
            
            // Check if critical column exists
            if (timeIdx == -1) {
                return@withContext SessionData(emptyList(), emptyList(), emptyMap(), "Niepoprawny format: brak kolumny 'czas'")
            }

            val colIndices = columns.associateWith { header.indexOf(it) }

            for (i in 1 until lines.size) {
                val values = lines[i].split(";")
                if (values.size < 1) continue // Skip empty lines

                val time = values.getOrNull(timeIdx) ?: ""
                times.add(time)

                val lat = values.getOrNull(latIdx)?.toDoubleOrNull()
                val lon = values.getOrNull(lonIdx)?.toDoubleOrNull()
                if (lat != null && lon != null) {
                    route.add(LatLng(lat, lon))
                }

                colIndices.forEach { (name, idx) ->
                    if (idx != -1) {
                        val value = values.getOrNull(idx)?.replace(",", ".")?.toFloatOrNull()
                        chartData[name]?.add(value)
                    } else {
                        // Kolumna nie istnieje w pliku - wypełniamy zerem (null zostanie zamieniony na 0f w ViewModelu)
                        chartData[name]?.add(null)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SessionRepository", "Error reading session file: $fileName", e)
            return@withContext SessionData(emptyList(), emptyList(), emptyMap(), "Błąd odczytu danych: ${e.message}")
        }

        return@withContext SessionData(times, route, chartData)
    }
}
