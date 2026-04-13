package com.example.sportapp.healthconnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.*
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.units.*
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.data.db.SyncMetadataDao
import com.example.sportapp.data.db.SyncMetadataEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject

class ExerciseExportUseCase @Inject constructor(
    private val healthConnectClient: HealthConnectClient,
    private val workoutRepository: IWorkoutRepository,
    private val healthConnectManager: HealthConnectManager,
    private val syncMetadataDao: SyncMetadataDao
) {
    private val exportDevice = Device(type = Device.TYPE_PHONE)

    suspend fun exportAllUnsynced() {
        withContext(Dispatchers.IO) {
            val allWorkouts = workoutRepository.getActivityItems()
            allWorkouts.forEach { item ->
                if (!workoutRepository.isExportedToHC(item.id.toLong())) {
                    exportActivityToHC(item.id.toLong())
                }
            }
        }
    }

    suspend fun exportActivityToHC(activityId: Long): ExportResult {
        return withContext(Dispatchers.IO) {
            try {
                val activity = workoutRepository.getWorkoutById(activityId)
                    ?: return@withContext ExportResult.Error("Aktywność nie znaleziona")

                if (!activity.isFinished) {
                    return@withContext ExportResult.Error("Aktywność jeszcze trwa")
                }

                val points = workoutRepository.getPointsForWorkout(activityId)
                val startTime = Instant.ofEpochMilli(activity.startTime)
                
                val lastPointOffset = points.lastOrNull()?.let { parseTimeToSeconds(it.time) } ?: activity.durationSeconds
                val effectiveDuration = maxOf(activity.durationSeconds, lastPointOffset)
                
                val sessionStartTime = startTime.minusSeconds(2)
                val sessionEndTime = startTime.plusSeconds(effectiveDuration).plusSeconds(2)

                // 1. Walidacja punktów GPS (minimum 2 punkty, brak 0,0)
                val locationPoints = points.filter { 
                    it.latitude != null && it.longitude != null && 
                    it.latitude != 0.0 && it.longitude != 0.0 &&
                    it.latitude in -90.0..90.0 && it.longitude in -180.0..180.0
                }
                val exerciseRoute = if (locationPoints.size >= 2) {
                    buildExerciseRoute(startTime, locationPoints, sessionEndTime)
                } else null

                // 2. Główny rekord sesji
                val sessionRecord = ExerciseSessionRecord(
                    startTime = sessionStartTime,
                    startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionStartTime),
                    endTime = sessionEndTime,
                    endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionEndTime),
                    exerciseType = healthConnectManager.mapBaseTypeToHealthConnect(activity.baseType),
                    title = activity.activityName,
                    exerciseRoute = exerciseRoute,
                    metadata = Metadata.activelyRecorded(exportDevice)
                )

                val allRecords = mutableListOf<Record>()
                allRecords.add(sessionRecord)
                
                // 3. Serie danych z fail-checkami
                allRecords.addAll(buildHeartRateRecords(startTime, points, sessionEndTime))
                allRecords.addAll(buildSpeedRecords(startTime, points, sessionEndTime))
                allRecords.addAll(buildCadenceRecords(startTime, points, sessionEndTime))
                
                // 4. Dystans jako interwały (lepiej przyswajane przez Google Fit i Stravę przez bridge)
                allRecords.addAll(buildDistanceRecords(startTime, points, sessionEndTime))

                // 5. Kalorie (> 0)
                activity.totalCalories?.let { calories ->
                    if (calories > 0.1) {
                        val cTime = sessionStartTime.plusMillis(500)
                        allRecords.add(ActiveCaloriesBurnedRecord(
                            startTime = cTime,
                            endTime = sessionEndTime.minusMillis(500),
                            startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionStartTime),
                            endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionEndTime),
                            energy = Energy.kilocalories(calories),
                            metadata = Metadata.activelyRecorded(exportDevice)
                        ))
                    }
                }

                // 6. Przewyższenia
                activity.totalAscent?.let { ascent ->
                    if (ascent > 0.1) {
                        allRecords.add(ElevationGainedRecord(
                            startTime = sessionStartTime.plusMillis(500),
                            endTime = sessionEndTime.minusMillis(500),
                            startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionStartTime),
                            endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionEndTime),
                            elevation = Length.meters(ascent),
                            metadata = Metadata.activelyRecorded(exportDevice)
                        ))
                    }
                }

                // 7. Kroki (> 0)
                val stepsCount = activity.steps ?: 0
                if (stepsCount > 0) {
                    allRecords.add(StepsRecord(
                        startTime = sessionStartTime.plusMillis(500),
                        endTime = sessionEndTime.minusMillis(500),
                        startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionStartTime),
                        endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionEndTime),
                        count = stepsCount.toLong(),
                        metadata = Metadata.activelyRecorded(exportDevice)
                    ))
                }

                // Zapisz wszystko naraz
                val response = healthConnectClient.insertRecords(allRecords)
                val hcSessionId = response.recordIdsList.first()

                workoutRepository.updateHCSessionId(activityId, hcSessionId)

                syncMetadataDao.insert(
                    SyncMetadataEntity(
                        hcRecordId = hcSessionId,
                        localRecordId = activityId,
                        recordType = "EXERCISE",
                        lastSyncTime = System.currentTimeMillis(),
                        syncDirection = "TO_HC",
                        localModifiedTime = activity.startTime,
                        hcModifiedTime = System.currentTimeMillis(),
                        activityName = activity.activityName,
                        startTime = activity.startTime
                    )
                )

                ExportResult.Success(hcSessionId)

            } catch (e: SecurityException) {
                ExportResult.PermissionDenied
            } catch (e: Exception) {
                ExportResult.Error(e.message ?: "Błąd eksportu")
            }
        }
    }

    private fun buildExerciseRoute(
        activityStartTime: Instant,
        locations: List<WorkoutPointEntity>,
        sessionEndTime: Instant
    ): ExerciseRoute {
        val routeLocations = locations.mapNotNull { loc ->
            val pointTime = activityStartTime.plusSeconds(parseTimeToSeconds(loc.time))
            if (!pointTime.isBefore(activityStartTime) && !pointTime.isAfter(sessionEndTime.minusSeconds(1))) {
                ExerciseRoute.Location(
                    time = pointTime,
                    latitude = loc.latitude!!,
                    longitude = loc.longitude!!,
                    horizontalAccuracy = Length.meters(loc.horizontalAccuracy?.coerceAtLeast(0.1) ?: 10.0),
                    altitude = loc.altitude?.let { Length.meters(it) }
                )
            } else null
        }
        return ExerciseRoute(routeLocations)
    }

    private fun buildDistanceRecords(startTime: Instant, points: List<WorkoutPointEntity>, sessionEndTime: Instant): List<DistanceRecord> {
        val records = mutableListOf<DistanceRecord>()
        var lastDist = 0.0
        var lastTime = startTime.plusMillis(100)

        // Grupowanie dystansu w 10-sekundowe interwały dla lepszej czytelności w Google Fit
        points.chunked(10).forEach { chunk ->
            val point = chunk.last()
            val pointTime = startTime.plusSeconds(parseTimeToSeconds(point.time))
            val currentDist = point.distanceGps?.toDouble() ?: lastDist
            
            if (currentDist > lastDist && pointTime.isAfter(lastTime) && !pointTime.isAfter(sessionEndTime.minusMillis(100))) {
                records.add(DistanceRecord(
                    startTime = lastTime,
                    endTime = pointTime,
                    startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(lastTime),
                    endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(pointTime),
                    distance = Length.meters(currentDist - lastDist),
                    metadata = Metadata.activelyRecorded(exportDevice)
                ))
                lastDist = currentDist
                lastTime = pointTime
            }
        }
        return records
    }

    private fun parseTimeToSeconds(timeStr: String): Long {
        val parts = timeStr.split(":")
        return try {
            when (parts.size) {
                3 -> parts[0].toLong() * 3600 + parts[1].toLong() * 60 + parts[2].toLong()
                2 -> parts[0].toLong() * 60 + parts[1].toLong()
                else -> 0L
            }
        } catch (e: Exception) {
            0L
        }
    }

    private fun buildHeartRateRecords(startTime: Instant, points: List<WorkoutPointEntity>, sessionEndTime: Instant): List<HeartRateRecord> {
        val samples = points.mapNotNull { point ->
            val pointTime = startTime.plusSeconds(parseTimeToSeconds(point.time))
            if (point.bpm != null && point.bpm >= 1 && !pointTime.isAfter(sessionEndTime.minusSeconds(1))) {
                HeartRateRecord.Sample(time = pointTime, beatsPerMinute = point.bpm.toLong())
            } else null
        }
        if (samples.isEmpty()) return emptyList()
        return listOf(HeartRateRecord(
            startTime = samples.first().time,
            endTime = samples.last().time,
            startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(samples.first().time),
            endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(samples.last().time),
            samples = samples,
            metadata = Metadata.activelyRecorded(exportDevice)
        ))
    }

    private fun buildSpeedRecords(startTime: Instant, points: List<WorkoutPointEntity>, sessionEndTime: Instant): List<SpeedRecord> {
        val samples = points.mapNotNull { point ->
            val pointTime = startTime.plusSeconds(parseTimeToSeconds(point.time))
            if (point.speedGps != null && point.speedGps >= 0 && !pointTime.isAfter(sessionEndTime.minusSeconds(1))) {
                SpeedRecord.Sample(time = pointTime, speed = Velocity.metersPerSecond(point.speedGps / 3.6))
            } else null
        }
        if (samples.isEmpty()) return emptyList()
        return listOf(SpeedRecord(
            startTime = samples.first().time,
            endTime = samples.last().time,
            startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(samples.first().time),
            endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(samples.last().time),
            samples = samples,
            metadata = Metadata.activelyRecorded(exportDevice)
        ))
    }

    private fun buildCadenceRecords(startTime: Instant, points: List<WorkoutPointEntity>, sessionEndTime: Instant): List<StepsCadenceRecord> {
        val samples = points.mapNotNull { point ->
            val pointTime = startTime.plusSeconds(parseTimeToSeconds(point.time))
            if (point.stepsMin != null && point.stepsMin >= 0 && !pointTime.isAfter(sessionEndTime.minusSeconds(1))) {
                StepsCadenceRecord.Sample(time = pointTime, rate = point.stepsMin)
            } else null
        }
        if (samples.isEmpty()) return emptyList()
        return listOf(StepsCadenceRecord(
            startTime = samples.first().time,
            endTime = samples.last().time,
            startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(samples.first().time),
            endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(samples.last().time),
            samples = samples,
            metadata = Metadata.activelyRecorded(exportDevice)
        ))
    }
}

sealed class ExportResult {
    data class Success(val hcSessionId: String) : ExportResult()
    object PermissionDenied : ExportResult()
    data class Error(val message: String) : ExportResult()
}
