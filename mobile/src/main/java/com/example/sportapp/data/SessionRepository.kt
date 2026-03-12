package com.example.sportapp.data

import android.content.Context
import android.util.Log
import com.example.sportapp.presentation.settings.MobileSettingsManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(private val context: Context) {
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
        
        // Zaktualizowana lista kolumn zgodna z wymaganiami UI i rzeczywistymi nazwami w CSV
        val columnMapping = mapOf(
            "bpm" to "bpm",
            "kalorie_min" to "kalorie_min",
            "kalorie_suma" to "kalorie_suma",
            "kroki_min" to "kroki_min",
            "odl_kroki" to "kroki_dystans",
            "predkosc_kroki" to "predkosc_kroki",
            "gps_dystans" to "gps_dystans",
            "predkosc" to "predkosc_gps",
            "wysokosc" to "wysokosc",
            "przewyzszenia_gora" to "przewyzszenia_gora",
            "przewyzszenia_dol" to "przewyzszenia_dol"
        )
        
        columnMapping.keys.forEach { chartData[it] = mutableListOf() }

        try {
            BufferedReader(FileReader(file)).use { reader ->
                val headerLine = reader.readLine() ?: return@withContext SessionData(emptyList(), emptyList(), emptyMap(), "Plik jest pusty")
                val header = headerLine.split(";")
                
                val timeIdx = header.indexOf("czas")
                val latIdx = header.indexOf("lat")
                val lonIdx = header.indexOf("lon")
                
                if (timeIdx == -1) {
                    return@withContext SessionData(emptyList(), emptyList(), emptyMap(), "Niepoprawny format: brak kolumny 'czas'")
                }

                val colIndices = columnMapping.mapValues { (_, csvName) -> header.indexOf(csvName) }

                var line = reader.readLine()
                while (line != null) {
                    val values = line.split(";")
                    if (values.isNotEmpty()) {
                        val time = values.getOrNull(timeIdx) ?: ""
                        times.add(time)

                        val lat = values.getOrNull(latIdx)?.toDoubleOrNull()
                        val lon = values.getOrNull(lonIdx)?.toDoubleOrNull()
                        if (lat != null && lon != null) {
                            route.add(LatLng(lat, lon))
                        }

                        colIndices.forEach { (uiName, idx) ->
                            if (idx != -1) {
                                val value = values.getOrNull(idx)?.replace(",", ".")?.toFloatOrNull()
                                chartData[uiName]?.add(value)
                            } else {
                                chartData[uiName]?.add(null)
                            }
                        }
                    }
                    line = reader.readLine()
                }
            }
        } catch (e: Exception) {
            Log.e("SessionRepository", "Error reading session file: $fileName", e)
            return@withContext SessionData(emptyList(), emptyList(), emptyMap(), "Błąd odczytu danych: ${e.message}")
        }

        return@withContext SessionData(times, route, chartData)
    }
}

data class SessionData(
    val times: List<String>,
    val route: List<LatLng>,
    val charts: Map<String, List<Float?>>,
    val error: String? = null
)
