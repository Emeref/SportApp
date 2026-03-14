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

        // Statystyki liczone z punktów (tak jak "Maks. Spalanie" i wykresy)
        var maxBpm = 0
        var lastAvgBpm = 0.0
        var totalCalories = 0.0
        var maxCaloriesMin = 0.0f
        var maxSpeedGps = 0.0
        var maxSpeedSteps = 0.0
        var totalDistanceGps = 0.0
        var totalDistanceSteps = 0.0
        var maxSteps = 0

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

            // Statystyki liczone w pętli (identycznie jak Maks. Spalanie)
            point.bpm?.let { if (it > maxBpm) maxBpm = it }
            point.avgBpm?.let { lastAvgBpm = it }
            point.calorieSum?.let { if (it > totalCalories) totalCalories = it }
            point.calorieMin?.let { if (it.toFloat() > maxCaloriesMin) maxCaloriesMin = it.toFloat() }
            
            // Nowe 5 widgetów liczone w pętli
            point.speedGps?.let { if (it > maxSpeedGps) maxSpeedGps = it }
            point.speedSteps?.let { if (it > maxSpeedSteps) maxSpeedSteps = it }
            point.distanceGps?.let { if (it.toDouble() > totalDistanceGps) totalDistanceGps = it.toDouble() }
            point.distanceSteps?.let { if (it.toDouble() > totalDistanceSteps) totalDistanceSteps = it.toDouble() }
            point.steps?.let { if (it > maxSteps) maxSteps = it }
        }

        // Fallback do nagłówka treningu jeśli punkty są puste lub nie mają danych
        val finalMaxSpeedGps = if (maxSpeedGps > 0) maxSpeedGps else (workout.avgSpeedGps ?: 0.0)
        val finalMaxSpeedSteps = if (maxSpeedSteps > 0) maxSpeedSteps else (workout.avgSpeedSteps ?: 0.0)
        val finalTotalDistanceGps = if (totalDistanceGps > 0) totalDistanceGps else (workout.distanceGps ?: 0.0)
        val finalTotalDistanceSteps = if (totalDistanceSteps > 0) totalDistanceSteps else (workout.distanceSteps ?: 0.0)
        val finalTotalSteps = if (maxSteps > 0) maxSteps else (workout.steps ?: 0)
        
        val finalMaxBpm = if (maxBpm > 0) maxBpm else (workout.maxBpm ?: 0)
        val finalAvgBpm = if (lastAvgBpm > 0) lastAvgBpm.toInt() else (workout.avgBpm?.toInt() ?: 0)
        val finalTotalCalories = if (totalCalories > 0) totalCalories.toInt() else (workout.totalCalories?.toInt() ?: 0)
        val finalMaxCaloriesMin = if (maxCaloriesMin > 0f) maxCaloriesMin else (workout.maxCalorieMin?.toFloat() ?: 0f)

        return@withContext SessionData(
            times = times,
            route = route,
            charts = chartData,
            activityName = workout.activityName,
            activityDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date(workout.startTime)),
            duration = workout.durationFormatted,
            maxBpm = finalMaxBpm,
            avgBpm = finalAvgBpm,
            totalCalories = finalTotalCalories,
            maxCaloriesMin = finalMaxCaloriesMin,
            maxSpeedGps = finalMaxSpeedGps,
            maxSpeedSteps = finalMaxSpeedSteps,
            totalDistanceGps = finalTotalDistanceGps,
            totalDistanceSteps = finalTotalDistanceSteps,
            totalSteps = finalTotalSteps
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
    val maxSpeedGps: Double = 0.0,
    val maxSpeedSteps: Double = 0.0,
    val totalDistanceGps: Double = 0.0,
    val totalDistanceSteps: Double = 0.0,
    val totalSteps: Int = 0
)
