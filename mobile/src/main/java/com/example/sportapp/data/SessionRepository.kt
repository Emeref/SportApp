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
import java.text.SimpleDateFormat
import java.util.Locale
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

        // Parsowanie nazwy pliku: <Nazwa>_<Data>_<Czas>.csv
        // Przykład: Bieganie_2024-05-20_14-30-05.csv
        val nameParts = fileName.removeSuffix(".csv").split("_")
        val activityName = nameParts.getOrNull(0) ?: "Aktywność"
        
        val activityDate = if (nameParts.size >= 3) {
            val datePart = nameParts[1] // 2024-05-20
            val timePart = nameParts[2].replace("-", ":") // 14-30-05 -> 14:30:05
            // Formatujemy do YYYY-MM-DD hh:mm
            if (timePart.length >= 5) {
                "$datePart ${timePart.substring(0, 5)}"
            } else {
                "$datePart $timePart"
            }
        } else ""

        val times = mutableListOf<String>()
        val route = mutableListOf<LatLng>()
        val chartData = mutableMapOf<String, MutableList<Float?>>()
        
        val columnMapping = mapOf(
            "bpm" to "bpm",
            "srednie_bpm" to "srednie_bpm",
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

        var maxBpm = 0f
        var lastAvgBpm = 0f
        var totalCalories = 0f
        var maxCaloriesMin = 0f

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
                                val rawVal = values.getOrNull(idx)?.replace(",", ".")?.toFloatOrNull()
                                chartData[uiName]?.add(rawVal)
                                
                                rawVal?.let { v ->
                                    when(uiName) {
                                        "bpm" -> if (v > maxBpm) maxBpm = v
                                        "srednie_bpm" -> lastAvgBpm = v
                                        "kalorie_suma" -> totalCalories = v
                                        "kalorie_min" -> if (v > maxCaloriesMin) maxCaloriesMin = v
                                    }
                                }
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

        return@withContext SessionData(
            times = times,
            route = route,
            charts = chartData,
            activityName = activityName,
            activityDate = activityDate,
            duration = times.lastOrNull() ?: "00:00:00",
            maxBpm = maxBpm.toInt(),
            avgBpm = lastAvgBpm.toInt(),
            totalCalories = totalCalories.toInt(),
            maxCaloriesMin = maxCaloriesMin
        )
    }
}

data class SessionData(
    val times: List<String>,
    val route: List<LatLng>,
    val charts: Map<String, List<Float?>>,
    val error: String? = null,
    val activityName: String = "",
    val activityDate: String = "",
    val duration: String = "",
    val maxBpm: Int = 0,
    val avgBpm: Int = 0,
    val totalCalories: Int = 0,
    val maxCaloriesMin: Float = 0f
)
