package com.example.sportapp.data

import com.example.sportapp.data.db.WorkoutDao
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

    suspend fun getSessionData(workoutId: Long): SessionData = withContext(Dispatchers.IO) {
        val workout = workoutDao.getWorkoutById(workoutId) ?: return@withContext SessionData(error = "Nie znaleziono treningu")
        val points = workoutDao.getPointsForWorkout(workoutId)

        val times = mutableListOf<String>()
        val route = mutableListOf<LatLng>()
        val heartRates = mutableListOf<Int>()
        val calories = mutableListOf<Float>()
        val speeds = mutableListOf<Float>()
        val altitudes = mutableListOf<Float>()
        val cadence = mutableListOf<Float>()

        var totalCalories = 0.0
        var maxCaloriesMin = 0f
        var maxSpeed = 0.0
        var totalDistanceGps = 0.0
        var totalDistanceSteps = 0.0
        var maxSteps = 0
        var totalAscent = 0.0
        var totalDescent = 0.0
        var maxAlt = -10000.0
        var lastAltRef: Double? = null

        points.forEach { point ->
            times.add(point.time)
            
            point.latitude?.let { lat ->
                point.longitude?.let { lon ->
                    route.add(LatLng(lat, lon))
                }
            }
            
            point.bpm?.let { heartRates.add(it) }
            point.calorieMin?.let { 
                calories.add(it.toFloat())
                if (it.toFloat() > maxCaloriesMin) maxCaloriesMin = it.toFloat()
            }
            point.calorieSum?.let { totalCalories = it }
            
            val sGps = point.speedGps ?: 0.0
            val sSteps = point.speedSteps ?: 0.0
            val currentMax = if (sGps > sSteps) sGps else sSteps
            if (currentMax > maxSpeed) maxSpeed = currentMax
            speeds.add(sGps.toFloat())

            point.stepsMin?.let { cadence.add(it.toFloat()) }

            point.distanceGps?.let { if (it.toDouble() > totalDistanceGps) totalDistanceGps = it.toDouble() }
            point.distanceSteps?.let { if (it.toDouble() > totalDistanceSteps) totalDistanceSteps = it.toDouble() }
            point.steps?.let { if (it > maxSteps) maxSteps = it }

            point.altitude?.let { alt ->
                altitudes.add(alt.toFloat())
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

        val chartData = mapOf(
            "bpm" to heartRates.map { it.toFloat() },
            "kalorie_min" to calories,
            "predkosc" to speeds,
            "wysokosc" to altitudes,
            "kroki_min" to cadence
        )

        val finalTotalDistanceGps = if (totalDistanceGps > 0) totalDistanceGps else (workout.distanceGps ?: 0.0)
        val finalTotalDistanceSteps = if (totalDistanceSteps > 0) totalDistanceSteps else (workout.distanceSteps ?: 0.0)
        val finalTotalSteps = if (maxSteps > 0) maxSteps else (workout.steps ?: 0)
        
        val officialDistanceMeters = if (finalTotalDistanceGps > 0) finalTotalDistanceGps else finalTotalDistanceSteps
        
        val calculatedPace = if (finalTotalDistanceGps >= 500.0) {
            (workout.durationSeconds / 60.0) / (finalTotalDistanceGps / 1000.0)
        } else 0.0

        val calculatedAvgBpm = if (heartRates.isNotEmpty()) heartRates.average().toInt() else 0

        return@withContext SessionData(
            times = times,
            route = route,
            charts = chartData,
            activityName = workout.activityName,
            activityDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date(workout.startTime)),
            duration = workout.durationFormatted,
            maxBpm = workout.maxBpm ?: (if (heartRates.isNotEmpty()) heartRates.maxOrNull() ?: 0 else 0),
            avgBpm = workout.avgBpm?.toInt() ?: calculatedAvgBpm,
            totalCalories = workout.totalCalories?.toInt() ?: totalCalories.toInt(),
            maxCaloriesMin = workout.maxCalorieMin?.toFloat() ?: maxCaloriesMin,
            maxSpeed = workout.maxSpeed ?: maxSpeed,
            totalDistanceGps = finalTotalDistanceGps,
            totalDistanceSteps = finalTotalDistanceSteps,
            totalSteps = finalTotalSteps,
            avgPace = if (finalTotalDistanceGps >= 500.0) (workout.avgPace ?: calculatedPace) else 0.0,
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
    val times: List<String> = emptyList(),
    val route: List<LatLng> = emptyList(),
    val charts: Map<String, List<Float?>> = emptyMap(),
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
