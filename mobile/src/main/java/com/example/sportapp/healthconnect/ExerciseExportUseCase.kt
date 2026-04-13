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
                
                // Precyzyjne wyliczanie czasu trwania na podstawie punktów
                val lastPointOffset = points.lastOrNull()?.let { parseTimeToSeconds(it.time) } ?: activity.durationSeconds
                val effectiveDuration = maxOf(activity.durationSeconds, lastPointOffset)
                
                // POPRAWKA: Rozszerzamy okno sesji o 1s w obie strony.
                // Google Fit i inne aplikacje bywają restrykcyjne i odrzucają trasę,
                // jeśli punkty GPS znajdują się dokładnie na granicy startTime/endTime sesji.
                val sessionStartTime = startTime.minusSeconds(1)
                val sessionEndTime = startTime.plusSeconds(effectiveDuration).plusSeconds(1)

                // Przygotuj trasę GPS (ExerciseRoute) - to jest kluczowe dla widoczności mapy
                val locationPoints = points.filter { it.latitude != null && it.longitude != null }
                val exerciseRoute = if (locationPoints.isNotEmpty()) {
                    buildExerciseRoute(startTime, locationPoints, sessionEndTime)
                } else null

                // Przygotuj główny obiekt sesji ćwiczeń
                val sessionRecord = ExerciseSessionRecord(
                    startTime = sessionStartTime,
                    startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionStartTime),
                    endTime = sessionEndTime,
                    endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionEndTime),
                    exerciseType = healthConnectManager.mapBaseTypeToHealthConnect(activity.activityName),
                    title = activity.activityName,
                    exerciseRoute = exerciseRoute,
                    metadata = Metadata.activelyRecorded(exportDevice)
                )

                // Kolekcjonujemy wszystkie rekordy do jednego zapisu dla lepszej atomowości
                val allRecords = mutableListOf<Record>()
                allRecords.add(sessionRecord)
                
                // Dodaj serie próbek (Tętno, Prędkość, Kadencja) - muszą być idealnie wewnątrz okna czasowego
                allRecords.addAll(buildHeartRateRecords(startTime, points, sessionEndTime))
                allRecords.addAll(buildSpeedRecords(startTime, points, sessionEndTime))
                allRecords.addAll(buildCadenceRecords(startTime, points, sessionEndTime))
                
                // Dystans całkowity
                activity.distanceGps?.let { distance ->
                    if (distance > 0) {
                        allRecords.add(DistanceRecord(
                            startTime = sessionStartTime,
                            startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionStartTime),
                            endTime = sessionEndTime,
                            endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionEndTime),
                            distance = Length.meters(distance),
                            metadata = Metadata.activelyRecorded(exportDevice)
                        ))
                    }
                }

                // Kalorie - Używamy ActiveCaloriesBurnedRecord (preferowane przez Google Fit dla sesji ćwiczeń)
                activity.totalCalories?.let { calories ->
                    if (calories > 0) {
                        allRecords.add(ActiveCaloriesBurnedRecord(
                            startTime = sessionStartTime,
                            startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionStartTime),
                            endTime = sessionEndTime,
                            endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionEndTime),
                            energy = Energy.kilocalories(calories),
                            metadata = Metadata.activelyRecorded(exportDevice)
                        ))
                    }
                }

                // Kroki (ważne dla Google Fit, nawet jeśli aktywność to rower - wysyłamy dla spójności)
                val stepsCount = activity.steps ?: 0
                allRecords.add(StepsRecord(
                    startTime = sessionStartTime,
                    startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionStartTime),
                    endTime = sessionEndTime,
                    endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(sessionEndTime),
                    count = stepsCount.toLong(),
                    metadata = Metadata.activelyRecorded(exportDevice)
                ))

                // Zapisujemy wszystko naraz
                val response = healthConnectClient.insertRecords(allRecords)
                val hcSessionId = response.recordIdsList.first()

                // Zapisz HC session ID w lokalnej bazie
                workoutRepository.updateHCSessionId(activityId, hcSessionId)

                // Zapisz metadane synchronizacji
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
            
            // Punkt musi zawierać się w zakresie sesji. 
            // Dzięki rozszerzeniu okna sesji, punkty na samym początku i końcu są bezpieczne.
            if (!pointTime.isBefore(activityStartTime.minusSeconds(1)) && !pointTime.isAfter(sessionEndTime)) {
                ExerciseRoute.Location(
                    time = pointTime,
                    latitude = loc.latitude!!,
                    longitude = loc.longitude!!,
                    horizontalAccuracy = loc.horizontalAccuracy?.let { Length.meters(it) },
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

    private fun buildHeartRateRecords(startTime: Instant, points: List<WorkoutPointEntity>, sessionEndTime: Instant): List<HeartRateRecord> {
        val samples = points.mapNotNull { point ->
            val pointTime = startTime.plusSeconds(parseTimeToSeconds(point.time))
            if (point.bpm != null && !pointTime.isAfter(sessionEndTime.minusMillis(100))) {
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

    private fun buildSpeedRecords(startTime: Instant, points: List<WorkoutPointEntity>, sessionEndTime: Instant): List<SpeedRecord> {
        val samples = points.mapNotNull { point ->
            val pointTime = startTime.plusSeconds(parseTimeToSeconds(point.time))
            if (point.speedGps != null && !pointTime.isAfter(sessionEndTime.minusMillis(100))) {
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

    private fun buildCadenceRecords(startTime: Instant, points: List<WorkoutPointEntity>, sessionEndTime: Instant): List<StepsCadenceRecord> {
        val samples = points.mapNotNull { point ->
            val pointTime = startTime.plusSeconds(parseTimeToSeconds(point.time))
            if (point.stepsMin != null && !pointTime.isAfter(sessionEndTime.minusMillis(100))) {
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
