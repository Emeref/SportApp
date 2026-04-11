package com.example.sportapp.data

import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.WorkoutLap
import com.example.sportapp.healthconnect.model.ExerciseSessionSyncDto
import com.example.sportapp.healthconnect.model.SessionTimeSeries
import com.example.sportapp.presentation.activities.ActivityItem
import com.example.sportapp.presentation.settings.ReportingPeriod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val workoutDefinitionDao: WorkoutDefinitionDao
) : IWorkoutRepository {

    override suspend fun insertWorkout(workout: WorkoutEntity): Long = withContext(Dispatchers.IO) {
        workoutDao.insertWorkout(workout)
    }

    override suspend fun updateWorkout(workout: WorkoutEntity) = withContext(Dispatchers.IO) {
        workoutDao.updateWorkout(workout)
    }

    override fun getAllWorkouts(): Flow<List<WorkoutEntity>> = workoutDao.getAllWorkouts()

    override suspend fun getWorkoutById(id: Long): WorkoutEntity? = withContext(Dispatchers.IO) {
        workoutDao.getWorkoutById(id)
    }

    override suspend fun deleteWorkout(workout: WorkoutEntity) = withContext(Dispatchers.IO) {
        workoutDao.deletePointsForWorkout(workout.id)
        workoutDao.deleteLapsForWorkout(workout.id)
        workoutDao.deleteWorkout(workout)
    }

    override suspend fun insertPoints(points: List<WorkoutPointEntity>) = withContext(Dispatchers.IO) {
        workoutDao.insertPoints(points)
    }

    override suspend fun insertLaps(laps: List<WorkoutLap>) = withContext(Dispatchers.IO) {
        workoutDao.insertLaps(laps)
    }

    override suspend fun getPointsForWorkout(workoutId: Long): List<WorkoutPointEntity> = withContext(Dispatchers.IO) {
        workoutDao.getPointsForWorkout(workoutId)
    }

    suspend fun getWorkoutsSince(since: Long): List<WorkoutEntity> = withContext(Dispatchers.IO) {
        workoutDao.getWorkoutsSince(since)
    }

    override suspend fun getUniqueActivityTypes(): List<String> = withContext(Dispatchers.IO) {
        workoutDao.getWorkoutsSince(0).map { it.activityName }.distinct().sorted()
    }

    override suspend fun trimWorkout(workout: WorkoutEntity, startPointId: Long, endPointId: Long) = withContext(Dispatchers.IO) {
        val allPoints = workoutDao.getPointsForWorkout(workout.id)
        val points = allPoints.filter { it.id in startPointId..endPointId }
        
        if (points.isEmpty()) return@withContext

        val firstPoint = points.first()
        val lastPoint = points.last()

        val totalSteps = (lastPoint.steps ?: 0) - (firstPoint.steps ?: 0)
        val distanceSteps = (lastPoint.distanceSteps ?: 0).toDouble() - (firstPoint.distanceSteps ?: 0).toDouble()
        val distanceGps = (lastPoint.distanceGps ?: 0).toDouble() - (firstPoint.distanceGps ?: 0).toDouble()
        val avgBpm = points.mapNotNull { it.bpm }.average()
        val maxBpm = points.mapNotNull { it.bpm }.maxOrNull()
        val totalCalories = (lastPoint.calorieSum ?: 0.0) - (firstPoint.calorieSum ?: 0.0)
        val maxCalorieMin = points.mapNotNull { it.calorieMin }.maxOrNull()
        val totalAscent = points.mapNotNull { it.totalAscent ?: 0.0 }.let { if (it.isEmpty()) 0.0 else it.last() - it.first() }
        val totalDescent = points.mapNotNull { it.totalDescent ?: 0.0 }.let { if (it.isEmpty()) 0.0 else it.last() - it.first() }
        
        val durationSeconds = points.size.toLong()

        val baseSteps = firstPoint.steps ?: 0
        val baseDistSteps = firstPoint.distanceSteps ?: 0
        val baseDistGps = firstPoint.distanceGps ?: 0
        val baseAscent = firstPoint.totalAscent ?: 0.0
        val baseDescent = firstPoint.totalDescent ?: 0.0
        val baseCalorieSum = firstPoint.calorieSum ?: 0.0

        val updatedPoints = points.mapIndexed { index, point ->
            val seconds = index.toLong()
            val h = seconds / 3600
            val m = (seconds % 3600) / 60
            val s = seconds % 60
            val newTime = String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
            
            point.copy(
                time = newTime,
                steps = (point.steps ?: 0) - baseSteps,
                distanceSteps = (point.distanceSteps ?: 0) - baseDistSteps,
                distanceGps = (point.distanceGps ?: 0) - baseDistGps,
                totalAscent = (point.totalAscent ?: 0.0) - baseAscent,
                totalDescent = (point.totalDescent ?: 0.0) - baseDescent,
                calorieSum = (point.calorieSum ?: 0.0) - baseCalorieSum
            )
        }

        val originalStartIndex = allPoints.indexOfFirst { it.id == startPointId }.coerceAtLeast(0)
        val newStartTime = workout.startTime + (originalStartIndex * 1000L) 
        
        val updatedWorkout = workout.copy(
            startTime = newStartTime,
            steps = totalSteps.coerceAtLeast(0),
            distanceSteps = distanceSteps.coerceAtLeast(0.0),
            distanceGps = distanceGps.coerceAtLeast(0.0),
            avgBpm = if (avgBpm.isNaN()) null else avgBpm,
            maxBpm = maxBpm,
            totalCalories = totalCalories.coerceAtLeast(0.0),
            maxCalorieMin = maxCalorieMin,
            totalAscent = Math.abs(totalAscent),
            totalDescent = Math.abs(totalDescent),
            durationSeconds = durationSeconds,
            durationFormatted = formatDuration(durationSeconds)
        )

        workoutDao.trimWorkout(updatedWorkout, startPointId, endPointId, updatedPoints)
    }

    private fun formatDuration(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
        else String.format(Locale.US, "%02d:%02d", m, s)
    }

    override fun getFilteredStatsFlow(
        activityType: String?,
        startDate: Date?,
        endDate: Date?
    ): Flow<Map<String, Any>> {
        return getAllWorkouts().map { list ->
            calculateStats(list, activityType, startDate, endDate)
        }
    }

    override suspend fun getFilteredStats(
        activityType: String?,
        startDate: Date?,
        endDate: Date?
    ): Map<String, Any> = withContext(Dispatchers.IO) {
        val all = workoutDao.getWorkoutsSince(0)
        calculateStats(all, activityType, startDate, endDate)
    }

    override suspend fun getStatsForPeriod(
        period: ReportingPeriod,
        customDays: Int
    ): Map<String, Any> = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        when (period) {
            ReportingPeriod.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
            }
            ReportingPeriod.WEEK -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            ReportingPeriod.MONTH -> calendar.add(Calendar.MONTH, -1)
            ReportingPeriod.YEAR -> calendar.add(Calendar.YEAR, -1)
            ReportingPeriod.CUSTOM -> calendar.add(Calendar.DAY_OF_YEAR, -customDays)
        }
        val since = calendar.timeInMillis
        val workouts = workoutDao.getWorkoutsSince(since)
        calculateStats(workouts, null, null, null)
    }

    private fun calculateStats(
        list: List<WorkoutEntity>,
        activityType: String?,
        startDate: Date?,
        endDate: Date?
    ): Map<String, Any> {
        val filtered = list.filter { w ->
            val typeMatch = activityType == null || w.activityName == activityType
            val startMatch = startDate == null || w.startTime >= startDate.time
            val endMatch = endDate == null || w.startTime <= endDate.time
            typeMatch && startMatch && endMatch
        }

        val totalWorkouts = filtered.size
        val totalDuration = filtered.sumOf { it.durationSeconds }
        val totalDistanceGps = filtered.sumOf { it.distanceGps ?: 0.0 }
        val totalDistanceSteps = filtered.sumOf { it.distanceSteps ?: 0.0 }
        val totalCalories = filtered.sumOf { it.totalCalories ?: 0.0 }
        val totalSteps = filtered.sumOf { it.steps ?: 0 }
        val totalAscent = filtered.sumOf { it.totalAscent ?: 0.0 }
        val totalDescent = filtered.sumOf { it.totalDescent ?: 0.0 }
        val avgBpm = if (filtered.any { it.avgBpm != null }) filtered.mapNotNull { it.avgBpm }.average() else 0.0
        val avgCadence = if (filtered.any { it.avgCadence != null }) filtered.mapNotNull { it.avgCadence }.average() else 0.0
        
        val maxSpeed = filtered.mapNotNull { it.maxSpeed }.maxOrNull() ?: 0.0
        val maxAltitude = filtered.mapNotNull { it.maxAltitude }.maxOrNull() ?: 0.0
        val maxElevationGain = filtered.mapNotNull { it.totalAscent }.maxOrNull() ?: 0.0
        val maxDistance = filtered.map { (it.distanceGps ?: 0.0) + (it.distanceSteps ?: 0.0) }.maxOrNull() ?: 0.0
        val maxDuration = filtered.map { it.durationSeconds }.maxOrNull() ?: 0L
        val maxCalories = filtered.mapNotNull { it.totalCalories }.maxOrNull() ?: 0.0
        val maxAvgCadence = filtered.mapNotNull { it.avgCadence }.maxOrNull() ?: 0.0
        
        val maxAvgSpeed = filtered
            .filter { it.durationSeconds >= 1800 } 
            .map { 
                val distanceKm = ((it.distanceGps ?: 0.0) + (it.distanceSteps ?: 0.0)) / 1000.0
                val durationHours = it.durationSeconds / 3600.0
                if (durationHours > 0) distanceKm / durationHours else 0.0
            }
            .maxOrNull() ?: 0.0

        return mapOf(
            "count" to totalWorkouts,
            "totalWorkouts" to totalWorkouts,
            "totalDuration" to totalDuration,
            "totalDistance" to (totalDistanceGps + totalDistanceSteps),
            "distanceGpsM" to totalDistanceGps,
            "distanceStepsM" to totalDistanceSteps,
            "calories" to totalCalories,
            "totalCalories" to totalCalories,
            "steps" to totalSteps,
            "ascent" to totalAscent,
            "descent" to totalDescent,
            "avgBpm" to avgBpm,
            "avg_cadence" to avgCadence,
            
            "max_speed" to maxSpeed,
            "max_altitude" to maxAltitude,
            "max_elevation_gain" to maxElevationGain,
            "max_distance" to maxDistance,
            "max_duration" to maxDuration,
            "max_calories" to maxCalories,
            "max_avg_cadence" to maxAvgCadence,
            "max_avg_speed" to maxAvgSpeed,

            "raw_data" to filtered
        )
    }

    override fun formatDistance(meters: Double): String {
        return when {
            meters < 1000 -> "${meters.toInt()} m"
            meters < 10000 -> String.format(Locale.US, "%.2f km", meters / 1000.0)
            meters < 100000 -> String.format(Locale.US, "%.1f km", meters / 1000.0)
            else -> String.format(Locale.US, "%.0f km", meters / 1000.0)
        }
    }

    override suspend fun getActivityItems(): List<ActivityItem> = withContext(Dispatchers.IO) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        workoutDao.getWorkoutsSince(0)
            .map { workout ->
                ActivityItem(
                    id = workout.id.toString(),
                    type = workout.activityName,
                    date = sdf.format(Date(workout.startTime)),
                    duration = workout.durationFormatted,
                    calories = "${workout.totalCalories?.toInt() ?: 0} kcal",
                    distanceGps = formatDistance(workout.distanceGps ?: 0.0),
                    distanceSteps = formatDistance(workout.distanceSteps ?: 0.0),
                    rawTimestamp = workout.startTime,
                    rawDurationSeconds = workout.durationSeconds,
                    rawCalories = workout.totalCalories ?: 0.0,
                    rawDistanceGps = workout.distanceGps ?: 0.0,
                    rawDistanceSteps = workout.distanceSteps ?: 0.0
                )
            }
    }

    override fun getActivityItemsFlow(): Flow<List<ActivityItem>> {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        return workoutDao.getAllWorkouts().map { list ->
            list.map { workout ->
                ActivityItem(
                    id = workout.id.toString(),
                    type = workout.activityName,
                    date = sdf.format(Date(workout.startTime)),
                    duration = workout.durationFormatted,
                    calories = "${workout.totalCalories?.toInt() ?: 0} kcal",
                    distanceGps = formatDistance(workout.distanceGps ?: 0.0),
                    distanceSteps = formatDistance(workout.distanceSteps ?: 0.0),
                    rawTimestamp = workout.startTime,
                    rawDurationSeconds = workout.durationSeconds,
                    rawCalories = workout.totalCalories ?: 0.0,
                    rawDistanceGps = workout.distanceGps ?: 0.0,
                    rawDistanceSteps = workout.distanceSteps ?: 0.0
                )
            }
        }
    }

    override fun getAllDefinitions(): Flow<List<WorkoutDefinition>> = workoutDefinitionDao.getAllDefinitions()

    override suspend fun existsByHCSessionId(hcSessionId: String): Boolean = withContext(Dispatchers.IO) {
        workoutDao.existsByHCSessionId(hcSessionId)
    }

    override suspend fun saveImportedSession(session: ExerciseSessionSyncDto, timeSeries: SessionTimeSeries?): Long = withContext(Dispatchers.IO) {
        val durationSeconds = Duration.between(session.startTime, session.endTime).seconds
        val workout = WorkoutEntity(
            activityName = session.title,
            startTime = session.startTime.toEpochMilli(),
            durationFormatted = formatDuration(durationSeconds),
            durationSeconds = durationSeconds,
            distanceGps = session.distanceMeters,
            totalCalories = session.activeCalories,
            avgBpm = session.avgHeartRate?.toDouble(),
            maxBpm = session.maxHeartRate,
            avgSpeedGps = session.avgSpeedMps,
            maxSpeed = session.maxSpeedMps,
            hcSessionId = session.hcSessionId,
            isSynced = true
        )
        val workoutId = workoutDao.insertWorkout(workout)
        
        if (timeSeries != null) {
            val points = createPointsFromTimeSeries(workoutId, session.startTime, session.endTime, timeSeries)
            if (points.isNotEmpty()) {
                workoutDao.insertPoints(points)
            }
        }
        
        workoutId
    }

    override suspend fun saveImportedGpx(
        definitionId: Long,
        name: String,
        startTime: Long,
        endTime: Long,
        durationSeconds: Long,
        distanceGps: Double,
        calories: Double,
        avgBpm: Double?,
        maxBpm: Int?,
        totalAscent: Double,
        totalDescent: Double,
        points: List<WorkoutPointEntity>,
        laps: List<WorkoutLap>
    ): Long = withContext(Dispatchers.IO) {
        val workout = WorkoutEntity(
            activityName = name,
            startTime = startTime,
            durationFormatted = formatDuration(durationSeconds),
            durationSeconds = durationSeconds,
            distanceGps = distanceGps,
            totalCalories = calories,
            avgBpm = avgBpm,
            maxBpm = maxBpm,
            totalAscent = totalAscent,
            totalDescent = totalDescent,
            isSynced = false
        )
        val workoutId = workoutDao.insertWorkout(workout)
        
        val pointsToSave = points.map { it.copy(workoutId = workoutId) }
        workoutDao.insertPoints(pointsToSave)
        
        val lapsToSave = laps.map { it.copy(workoutId = workoutId) }
        workoutDao.insertLaps(lapsToSave)
        
        workoutId
    }

    private fun createPointsFromTimeSeries(
        workoutId: Long,
        startTime: Instant,
        endTime: Instant,
        timeSeries: SessionTimeSeries
    ): List<WorkoutPointEntity> {
        val durationSeconds = Duration.between(startTime, endTime).seconds
        if (durationSeconds <= 0) return emptyList()

        val points = mutableListOf<WorkoutPointEntity>()
        
        val hrMap = timeSeries.heartRates.associateBy { Duration.between(startTime, it.time).seconds }
        val speedMap = timeSeries.speeds.associateBy { Duration.between(startTime, it.time).seconds }
        val cadenceMap = timeSeries.cadences.associateBy { Duration.between(startTime, it.time).seconds }
        val locationMap = timeSeries.locations.associateBy { Duration.between(startTime, it.time).seconds }
        
        var currentDistance = 0.0
        var currentAscent = 0.0
        var currentDescent = 0.0
        var currentCalories = 0.0
        
        val distanceBySec = timeSeries.distances.associateBy { Duration.between(startTime, it.endTime).seconds }
        val elevationBySec = timeSeries.elevations.associateBy { Duration.between(startTime, it.endTime).seconds }
        val caloriesBySec = timeSeries.calories.associateBy { Duration.between(startTime, it.endTime).seconds }

        for (i in 0..durationSeconds) {
            val hr = hrMap[i]?.bpm
            val speed = speedMap[i]?.speedMps
            val cadence = cadenceMap[i]?.rate
            val loc = locationMap[i]
            
            val distStep = distanceBySec[i]?.distanceMeters ?: 0.0
            currentDistance += distStep
            
            val eleStep = elevationBySec[i]?.elevationMeters ?: 0.0
            if (eleStep > 0) currentAscent += eleStep else currentDescent += Math.abs(eleStep)
            
            val calStep = caloriesBySec[i]?.kilocalories ?: 0.0
            currentCalories += calStep

            val h = i / 3600
            val m = (i % 3600) / 60
            val s = i % 60
            val timeStr = String.format(Locale.US, "%02d:%02d:%02d", h, m, s)

            points.add(WorkoutPointEntity(
                workoutId = workoutId,
                time = timeStr,
                latitude = loc?.latitude,
                longitude = loc?.longitude,
                bpm = hr,
                steps = null, 
                stepsMin = cadence,
                distanceSteps = null,
                distanceGps = currentDistance.toInt(),
                speedGps = speed,
                speedSteps = null,
                altitude = loc?.altitude,
                totalAscent = currentAscent,
                totalDescent = currentDescent,
                calorieMin = calStep,
                calorieSum = currentCalories
            ))
        }

        return points
    }
}
