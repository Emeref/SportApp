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
                    // Tutaj moglibyśmy dodać check czy jest finished, 
                    // ale getActivityItems może nie zwracać isFinished.
                    // exportActivityToHC sam sprawdzi status encji.
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

                // BLOKADA: Nie eksportuj jeśli nie jest zakończona
                if (!activity.isFinished) {
                    return@withContext ExportResult.Error("Aktywność jeszcze trwa")
                }

                val points = workoutRepository.getPointsForWorkout(activityId)
                val startTime = Instant.ofEpochMilli(activity.startTime)
                
                // NAPRAWA BŁĘDU CZASU: Upewnij się, że endTime obejmuje ostatni punkt danych
                val lastPointTimeOffset = points.lastOrNull()?.let { parseTimeToSeconds(it.time) } ?: activity.durationSeconds
                val effectiveDuration = maxOf(activity.durationSeconds, lastPointTimeOffset)
                val endTime = startTime.plusSeconds(effectiveDuration)

                // Przygotuj trasę jeśli są punkty GPS
                val locationPoints = points.filter { it.latitude != null && it.longitude != null }
                val exerciseRoute = if (locationPoints.isNotEmpty()) {
                    buildExerciseRoute(startTime, locationPoints, endTime)
                } else null

                // Zapisz sesję główną z opcjonalną trasą
                val sessionRecord = ExerciseSessionRecord(
                    startTime = startTime,
                    startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(startTime),
                    endTime = endTime,
                    endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(endTime),
                    exerciseType = healthConnectManager.mapBaseTypeToHealthConnect(activity.activityName),
                    title = activity.activityName,
                    exerciseRoute = exerciseRoute,
                    metadata = Metadata.activelyRecorded(exportDevice)
                )

                val insertedRecords = healthConnectClient.insertRecords(listOf(sessionRecord))
                val hcSessionId = insertedRecords.recordIdsList.first()

                // Zapisz powiązane rekordy zbiorowo
                val records = mutableListOf<Record>()
                
                records.addAll(buildHeartRateRecords(startTime, points, endTime))
                records.addAll(buildSpeedRecords(startTime, points, endTime))
                records.addAll(buildCadenceRecords(startTime, points, endTime))
                
                // Dystans całkowity
                activity.distanceGps?.let { distance ->
                    if (distance > 0) {
                        records.add(DistanceRecord(
                            startTime = startTime,
                            startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(startTime),
                            endTime = endTime,
                            endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(endTime),
                            distance = Length.meters(distance),
                            metadata = Metadata.activelyRecorded(exportDevice)
                        ))
                    }
                }

                // Kalorie
                activity.totalCalories?.let { calories ->
                    if (calories > 0) {
                        records.add(TotalCaloriesBurnedRecord(
                            startTime = startTime,
                            startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(startTime),
                            endTime = endTime,
                            endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(endTime),
                            energy = Energy.kilocalories(calories),
                            metadata = Metadata.activelyRecorded(exportDevice)
                        ))
                    }
                }

                if (records.isNotEmpty()) {
                    healthConnectClient.insertRecords(records)
                }

                // Zapisz HC session ID w lokalnej bazie
                workoutRepository.updateHCSessionId(activityId, hcSessionId)

                // Zapisz metadane synchronizacji ze szczegółami aktywności
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
        sessionStartTime: Instant,
        locations: List<WorkoutPointEntity>,
        sessionEndTime: Instant
    ): ExerciseRoute {
        val routeLocations = locations.mapNotNull { loc ->
            val pointTime = sessionStartTime.plusSeconds(parseTimeToSeconds(loc.time))
            // Punkt musi być w zakresie (HC walidacja)
            if (!pointTime.isAfter(sessionEndTime)) {
                ExerciseRoute.Location(
                    time = pointTime,
                    latitude = loc.latitude!!,
                    longitude = loc.longitude!!,
                    altitude = loc.altitude?.let { Length.meters(it) }
                )
            } else null
        }
        return ExerciseRoute(routeLocations)
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

    private fun buildHeartRateRecords(startTime: Instant, points: List<WorkoutPointEntity>, endTime: Instant): List<HeartRateRecord> {
        val samples = points.mapNotNull { point ->
            val pointTime = startTime.plusSeconds(parseTimeToSeconds(point.time))
            if (point.bpm != null && !pointTime.isAfter(endTime)) {
                HeartRateRecord.Sample(
                    time = pointTime,
                    beatsPerMinute = point.bpm.toLong()
                )
            } else null
        }
        if (samples.isEmpty()) return emptyList()
        return listOf(HeartRateRecord(
            startTime = samples.first().time,
            startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(samples.first().time),
            endTime = samples.last().time,
            endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(samples.last().time),
            samples = samples,
            metadata = Metadata.activelyRecorded(exportDevice)
        ))
    }

    private fun buildSpeedRecords(startTime: Instant, points: List<WorkoutPointEntity>, endTime: Instant): List<SpeedRecord> {
        val samples = points.mapNotNull { point ->
            val pointTime = startTime.plusSeconds(parseTimeToSeconds(point.time))
            if (point.speedGps != null && !pointTime.isAfter(endTime)) {
                SpeedRecord.Sample(
                    time = pointTime,
                    speed = Velocity.metersPerSecond(point.speedGps)
                )
            } else null
        }
        if (samples.isEmpty()) return emptyList()
        return listOf(SpeedRecord(
            startTime = samples.first().time,
            startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(samples.first().time),
            endTime = samples.last().time,
            endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(samples.last().time),
            samples = samples,
            metadata = Metadata.activelyRecorded(exportDevice)
        ))
    }

    private fun buildCadenceRecords(startTime: Instant, points: List<WorkoutPointEntity>, endTime: Instant): List<StepsCadenceRecord> {
        val samples = points.mapNotNull { point ->
            val pointTime = startTime.plusSeconds(parseTimeToSeconds(point.time))
            if (point.stepsMin != null && !pointTime.isAfter(endTime)) {
                StepsCadenceRecord.Sample(
                    time = pointTime,
                    rate = point.stepsMin
                )
            } else null
        }
        if (samples.isEmpty()) return emptyList()
        return listOf(StepsCadenceRecord(
            startTime = samples.first().time,
            startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(samples.first().time),
            endTime = samples.last().time,
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
