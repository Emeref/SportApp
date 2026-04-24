package com.example.sportapp.data

import android.content.Context
import android.net.Uri
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportImportManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: IWorkoutRepository,
    private val workoutDao: WorkoutDao
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    suspend fun exportToSae(workoutId: Long): String = withContext(Dispatchers.IO) {
        val workout = repository.getWorkoutById(workoutId) ?: throw Exception("Workout not found")
        val points = repository.getPointsForWorkout(workoutId)
        val laps = workoutDao.getLapsForWorkout(workoutId)

        val exportModel = ActivityExportModel(
            workout = workout.toDto(),
            points = points.map { it.toDto() },
            laps = laps.map { it.toDto() }
        )

        json.encodeToString(exportModel)
    }

    suspend fun importFromSae(uri: Uri): Long = withContext(Dispatchers.IO) {
        val content = context.contentResolver.openInputStream(uri)?.use { inputStream ->
            InputStreamReader(inputStream).readText()
        } ?: throw Exception("Cannot read file")

        val importModel = json.decodeFromString<ActivityExportModel>(content)
        
        val newWorkout = importModel.workout.toEntity().copy(id = 0, isSynced = false)
        val newWorkoutId = repository.insertWorkout(newWorkout)

        val newPoints = importModel.points.map { it.toEntity(newWorkoutId) }
        repository.insertPoints(newPoints)

        val newLaps = importModel.laps.map { it.toEntity(newWorkoutId) }
        repository.insertLaps(newLaps)

        newWorkoutId
    }

    private fun WorkoutEntity.toDto() = WorkoutExportDto(
        activityName = activityName,
        baseType = baseType,
        startTime = startTime,
        durationFormatted = durationFormatted,
        steps = steps,
        distanceSteps = distanceSteps,
        distanceGps = distanceGps,
        avgSpeedSteps = avgSpeedSteps,
        avgSpeedGps = avgSpeedGps,
        totalAscent = totalAscent,
        totalDescent = totalDescent,
        avgBpm = avgBpm,
        maxBpm = maxBpm,
        totalCalories = totalCalories,
        maxCalorieMin = maxCalorieMin,
        durationSeconds = durationSeconds,
        avgPace = avgPace,
        maxSpeed = maxSpeed,
        maxAltitude = maxAltitude,
        minAltitude = minAltitude,
        avgStepLength = avgStepLength,
        avgCadence = avgCadence,
        maxCadence = maxCadence,
        maxPressure = maxPressure,
        minPressure = minPressure,
        bestPace1km = bestPace1km,
        autoLapDistance = autoLapDistance,
        destinationLatitude = destinationLatitude,
        destinationLongitude = destinationLongitude
    )

    private fun WorkoutExportDto.toEntity() = WorkoutEntity(
        activityName = activityName,
        baseType = baseType,
        startTime = startTime,
        durationFormatted = durationFormatted,
        steps = steps,
        distanceSteps = distanceSteps,
        distanceGps = distanceGps,
        avgSpeedSteps = avgSpeedSteps,
        avgSpeedGps = avgSpeedGps,
        totalAscent = totalAscent,
        totalDescent = totalDescent,
        avgBpm = avgBpm,
        maxBpm = maxBpm,
        totalCalories = totalCalories,
        maxCalorieMin = maxCalorieMin,
        durationSeconds = durationSeconds,
        avgPace = avgPace,
        maxSpeed = maxSpeed,
        maxAltitude = maxAltitude,
        minAltitude = minAltitude,
        avgStepLength = avgStepLength,
        avgCadence = avgCadence,
        maxCadence = maxCadence,
        maxPressure = maxPressure,
        minPressure = minPressure,
        bestPace1km = bestPace1km,
        autoLapDistance = autoLapDistance,
        destinationLatitude = destinationLatitude,
        destinationLongitude = destinationLongitude
    )

    private fun WorkoutPointEntity.toDto() = WorkoutPointExportDto(
        time = time,
        latitude = latitude,
        longitude = longitude,
        bpm = bpm,
        steps = steps,
        stepsMin = stepsMin,
        distanceSteps = distanceSteps,
        distanceGps = distanceGps,
        speedGps = speedGps,
        speedSteps = speedSteps,
        altitude = altitude,
        horizontalAccuracy = horizontalAccuracy,
        totalAscent = totalAscent,
        totalDescent = totalDescent,
        calorieMin = calorieMin,
        calorieSum = calorieSum,
        pressure = pressure
    )

    private fun WorkoutPointExportDto.toEntity(workoutId: Long) = WorkoutPointEntity(
        workoutId = workoutId,
        time = time,
        latitude = latitude,
        longitude = longitude,
        bpm = bpm,
        steps = steps,
        stepsMin = stepsMin,
        distanceSteps = distanceSteps,
        distanceGps = distanceGps,
        speedGps = speedGps,
        speedSteps = speedSteps,
        altitude = altitude,
        horizontalAccuracy = horizontalAccuracy,
        totalAscent = totalAscent,
        totalDescent = totalDescent,
        calorieMin = calorieMin,
        calorieSum = calorieSum,
        pressure = pressure
    )

    private fun WorkoutLap.toDto() = WorkoutLapExportDto(
        lapNumber = lapNumber,
        durationMillis = durationMillis,
        distanceMeters = distanceMeters,
        avgPaceSecondsPerKm = avgPaceSecondsPerKm,
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeed,
        avgHeartRate = avgHeartRate,
        maxHeartRate = maxHeartRate,
        totalAscent = totalAscent,
        totalDescent = totalDescent,
        startLocationIndex = startLocationIndex,
        endLocationIndex = endLocationIndex
    )

    private fun WorkoutLapExportDto.toEntity(workoutId: Long) = WorkoutLap(
        workoutId = workoutId,
        lapNumber = lapNumber,
        durationMillis = durationMillis,
        distanceMeters = distanceMeters,
        avgPaceSecondsPerKm = avgPaceSecondsPerKm,
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeed,
        avgHeartRate = avgHeartRate,
        maxHeartRate = maxHeartRate,
        totalAscent = totalAscent,
        totalDescent = totalDescent,
        startLocationIndex = startLocationIndex,
        endLocationIndex = endLocationIndex
    )
}
