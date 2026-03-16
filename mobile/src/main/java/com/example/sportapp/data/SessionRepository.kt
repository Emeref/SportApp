package com.example.sportapp.data

import android.util.Log
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val workoutDao: WorkoutDao
) {

    suspend fun getSessionData(workoutIdString: String): SessionData = withContext(Dispatchers.IO) {
        val workoutId = workoutIdString.toLongOrNull() 
            ?: return@withContext SessionData(emptyList(), emptyList(), emptyMap(), "Niepoprawne ID treningu")

        val workout = workoutDao.getWorkoutById(workoutId)
            ?: return@withContext SessionData(emptyList(), emptyList(), emptyMap(), "Trening nie istnieje w bazie")

        val points = workoutDao.getPointsForWorkout(workoutId)

        val times = mutableListOf<String>()
        val route = mutableListOf<LatLng>()
        val chartData = mutableMapOf<String, MutableList<Float?>>()
        
        val uiColumns = listOf(
            "bpm", "srednie_bpm", "kalorie_min", "kalorie_suma", 
            "kroki_min", "kroki_dystans", "predkosc_kroki", 
            "gps_dystans", "predkosc_gps", "wysokosc", 
            "przewyzszenia_gora", "przewyzszenia_dol"
        )
        
        uiColumns.forEach { chartData[it] = mutableListOf() }

        // Statystyki liczone z punktów (fallback dla starszych treningów)
        var maxBpm = 0
        var lastAvgBpm = 0.0
        var totalCalories = 0.0
        var maxCaloriesMin = 0.0f
        var maxSpeed = 0.0
        var totalDistanceGps = 0.0
        var totalDistanceSteps = 0.0
        var maxSteps = 0
        var maxAlt = -10000.0
        var totalAscent = 0.0
        var totalDescent = 0.0
        var lastAltRef: Double? = null

        points.forEach { point ->
            times.add(point.time)
            
            if (point.latitude != null && point.longitude != null) {
                route.add(LatLng(point.latitude, point.longitude))
            }

            // Mapowanie pól encji na wykresy
            chartData["bpm"]?.add(point.bpm?.toFloat())
            chartData["srednie_bpm"]?.add(point.avgBpm?.toFloat())
            chartData["kalorie_min"]?.add(point.calorieMin?.toFloat())
            chartData["kalorie_suma"]?.add(point.calorieSum?.toFloat())
            chartData["kroki_min"]?.add(point.stepsMin?.toFloat())
            chartData["kroki_dystans"]?.add(point.distanceSteps?.toFloat())
            chartData["predkosc_kroki"]?.add(point.speedSteps?.toFloat())
            chartData["gps_dystans"]?.add(point.distanceGps?.toFloat())
            chartData["predkosc_gps"]?.add(point.speedGps?.toFloat())
            chartData["wysokosc"]?.add(point.altitude?.toFloat())
            chartData["przewyzszenia_gora"]?.add(point.totalAscent?.toFloat())
            chartData["przewyzszenia_dol"]?.add(point.totalDescent?.toFloat())

            point.bpm?.let { if (it > maxBpm) maxBpm = it }
            point.avgBpm?.let { lastAvgBpm = it }
            point.calorieSum?.let { if (it > totalCalories) totalCalories = it }
            point.calorieMin?.let { if (it.toFloat() > maxCaloriesMin) maxCaloriesMin = it.toFloat() }
            
            val sGps = point.speedGps ?: 0.0
            val sSteps = point.speedSteps ?: 0.0
            val currentMax = if (sGps > sSteps) sGps else sSteps
            if (currentMax > maxSpeed) maxSpeed = currentMax

            point.distanceGps?.let { if (it.toDouble() > totalDistanceGps) totalDistanceGps = it.toDouble() }
            point.distanceSteps?.let { if (it.toDouble() > totalDistanceSteps) totalDistanceSteps = it.toDouble() }
            point.steps?.let { if (it > maxSteps) maxSteps = it }

            point.altitude?.let { alt ->
                if (alt > maxAlt) maxAlt = alt
                if (lastAltRef == null) {
                    lastAltRef = alt
                } else {
                    val diff = alt - lastAltRef!!
                    if (diff >= 2.0) { totalAscent += diff; lastAltRef = alt }
                    else if (diff <= -2.0) { totalDescent += Math.abs(diff); lastAltRef = alt }
                }
            }
        }

        val finalTotalDistanceGps = if (totalDistanceGps > 0) totalDistanceGps else (workout.distanceGps ?: 0.0)
        val finalTotalDistanceSteps = if (totalDistanceSteps > 0) totalDistanceSteps else (workout.distanceSteps ?: 0.0)
        val finalTotalSteps = if (maxSteps > 0) maxSteps else (workout.steps ?: 0)
        
        val officialDistanceMeters = if (finalTotalDistanceGps > 0) finalTotalDistanceGps else finalTotalDistanceSteps
        val calculatedPace = if (officialDistanceMeters > 0) (workout.durationSeconds / 60.0) / (officialDistanceMeters / 1000.0) else 0.0

        return@withContext SessionData(
            times = times,
            route = route,
            charts = chartData,
            activityName = workout.activityName,
            activityDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date(workout.startTime)),
            duration = workout.durationFormatted,
            maxBpm = workout.maxBpm ?: maxBpm,
            avgBpm = workout.avgBpm?.toInt() ?: lastAvgBpm.toInt(),
            totalCalories = workout.totalCalories?.toInt() ?: totalCalories.toInt(),
            maxCaloriesMin = workout.maxCalorieMin?.toFloat() ?: maxCaloriesMin,
            maxSpeed = workout.maxSpeed ?: maxSpeed,
            totalDistanceGps = finalTotalDistanceGps,
            totalDistanceSteps = finalTotalDistanceSteps,
            totalSteps = finalTotalSteps,
            avgPace = workout.avgPace ?: calculatedPace,
            totalAscent = workout.totalAscent ?: totalAscent,
            totalDescent = workout.totalDescent ?: totalDescent,
            maxAltitude = workout.maxAltitude ?: (if (maxAlt == -10000.0) 0.0 else maxAlt),
            avgStepLength = workout.avgStepLength ?: (if (finalTotalSteps > 0) officialDistanceMeters / finalTotalSteps else 0.0),
            avgCadence = workout.avgCadence ?: 0.0,
            maxCadence = workout.maxCadence ?: 0.0
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
    val maxCaloriesMin: Float = 0f,
    val maxSpeed: Double = 0.0,
    val totalDistanceGps: Double = 0.0,
    val totalDistanceSteps: Double = 0.0,
    val totalSteps: Int = 0,
    val avgPace: Double = 0.0,
    val totalAscent: Double = 0.0,
    val totalDescent: Double = 0.0,
    val maxAltitude: Double = 0.0,
    val avgStepLength: Double = 0.0,
    val avgCadence: Double = 0.0,
    val maxCadence: Double = 0.0
)
