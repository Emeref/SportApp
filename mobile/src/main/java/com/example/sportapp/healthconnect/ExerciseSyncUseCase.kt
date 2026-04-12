package com.example.sportapp.healthconnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.healthconnect.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class ExerciseSyncUseCase @Inject constructor(
    private val healthConnectClient: HealthConnectClient,
    private val activityRepository: IWorkoutRepository
) {
    suspend fun readExerciseSessions(daysBack: Int = 30): List<ExerciseSessionSyncDto> {
        return withContext(Dispatchers.IO) {
            val endTime = Instant.now()
            val startTime = endTime.minus(daysBack.toLong(), ChronoUnit.DAYS)

            val sessions = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    ExerciseSessionRecord::class,
                    TimeRangeFilter.between(startTime, endTime)
                )
            ).records

            sessions.mapNotNull { session ->
                try {
                    val aggregates = readSessionAggregates(session)
                    val alreadyImported = activityRepository.existsByHCSessionId(session.metadata.id)
                    ExerciseSessionSyncDto(
                        hcSessionId = session.metadata.id,
                        title = session.title ?: "Workout",
                        exerciseType = session.exerciseType,
                        startTime = session.startTime,
                        endTime = session.endTime,
                        distanceMeters = aggregates.distance,
                        activeCalories = aggregates.calories,
                        avgHeartRate = aggregates.avgHR,
                        maxHeartRate = aggregates.maxHR,
                        avgSpeedMps = aggregates.avgSpeed,
                        maxSpeedMps = aggregates.maxSpeed,
                        alreadyImported = alreadyImported
                    )
                } catch (e: Exception) {
                    null // pomiń sesje z błędami
                }
            }
        }
    }

    private suspend fun readSessionAggregates(session: ExerciseSessionRecord): SessionAggregates {
        val timeFilter = TimeRangeFilter.between(session.startTime, session.endTime)
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(
                    DistanceRecord.DISTANCE_TOTAL,
                    ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL,
                    HeartRateRecord.BPM_AVG,
                    HeartRateRecord.BPM_MAX,
                    SpeedRecord.SPEED_AVG,
                    SpeedRecord.SPEED_MAX
                ),
                timeRangeFilter = timeFilter
            )
        )
        return SessionAggregates(
            distance = response[DistanceRecord.DISTANCE_TOTAL]?.inMeters,
            calories = response[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories,
            avgHR = response[HeartRateRecord.BPM_AVG]?.toInt(),
            maxHR = response[HeartRateRecord.BPM_MAX]?.toInt(),
            avgSpeed = response[SpeedRecord.SPEED_AVG]?.inMetersPerSecond,
            maxSpeed = response[SpeedRecord.SPEED_MAX]?.inMetersPerSecond
        )
    }

    suspend fun readSessionTimeSeries(hcSessionId: String): SessionTimeSeries {
        return withContext(Dispatchers.IO) {
            val sessionRecord = healthConnectClient.readRecord(ExerciseSessionRecord::class, hcSessionId).record
            val timeFilter = TimeRangeFilter.between(sessionRecord.startTime, sessionRecord.endTime)

            val heartRates = try {
                healthConnectClient.readRecords(
                    ReadRecordsRequest(HeartRateRecord::class, timeFilter)
                ).records.flatMap { record ->
                    record.samples.map { sample ->
                        HeartRateSample(sample.time, sample.beatsPerMinute.toInt())
                    }
                }
            } catch (e: Exception) { emptyList() }

            val speeds = try {
                healthConnectClient.readRecords(
                    ReadRecordsRequest(SpeedRecord::class, timeFilter)
                ).records.flatMap { record ->
                    record.samples.map { sample ->
                        SpeedSample(sample.time, sample.speed.inMetersPerSecond)
                    }
                }
            } catch (e: Exception) { emptyList() }

            val cadences = try {
                healthConnectClient.readRecords(
                    ReadRecordsRequest(StepsCadenceRecord::class, timeFilter)
                ).records.flatMap { record ->
                    record.samples.map { sample ->
                        CadenceSample(sample.time, sample.rate)
                    }
                }
            } catch (e: Exception) { emptyList() }

            val distances = try {
                healthConnectClient.readRecords(
                    ReadRecordsRequest(DistanceRecord::class, timeFilter)
                ).records.map { record -> 
                    DistanceSample(record.startTime, record.endTime, record.distance.inMeters) 
                }
            } catch (e: Exception) { emptyList() }

            val elevations = try {
                healthConnectClient.readRecords(
                    ReadRecordsRequest(ElevationGainedRecord::class, timeFilter)
                ).records.map { record -> 
                    ElevationSample(record.startTime, record.endTime, record.elevation.inMeters) 
                }
            } catch (e: Exception) { emptyList() }

            val calories = try {
                healthConnectClient.readRecords(
                    ReadRecordsRequest(ActiveCaloriesBurnedRecord::class, timeFilter)
                ).records.map { record -> 
                    CaloriesSample(record.startTime, record.endTime, record.energy.inKilocalories) 
                }
            } catch (e: Exception) { emptyList() }

            // Trasa GPS z ExerciseRoute
            val route = try {
                // Using reflection to safely access exerciseRoute which might be unresolved in some IDE states
                val routeLocations = mutableListOf<LocationSample>()
                try {
                    val getExerciseRoute = sessionRecord.javaClass.getMethod("getExerciseRoute")
                    val exerciseRoute = getExerciseRoute.invoke(sessionRecord)
                    if (exerciseRoute != null) {
                        val getLocations = exerciseRoute.javaClass.getMethod("getLocations")
                        val locations = getLocations.invoke(exerciseRoute) as List<*>
                        for (loc in locations) {
                            if (loc != null) {
                                val time = loc.javaClass.getMethod("getTime").invoke(loc) as Instant
                                val lat = loc.javaClass.getMethod("getLatitude").invoke(loc) as Double
                                val lon = loc.javaClass.getMethod("getLongitude").invoke(loc) as Double
                                val alt = try { loc.javaClass.getMethod("getAltitude").invoke(loc) } catch (e: Exception) { null }
                                val altMeters = alt?.let { 
                                    it.javaClass.getMethod("getInMeters").invoke(it) as Double
                                }
                                routeLocations.add(LocationSample(time, lat, lon, altMeters))
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Fallback or ignore if not available
                }
                routeLocations
            } catch (e: SecurityException) {
                emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            SessionTimeSeries(
                heartRates = heartRates,
                speeds = speeds,
                cadences = cadences,
                distances = distances,
                elevations = elevations,
                locations = route,
                calories = calories
            )
        }
    }

    private data class SessionAggregates(
        val distance: Double?,
        val calories: Double?,
        val avgHR: Int?,
        val maxHR: Int?,
        val avgSpeed: Double?,
        val maxSpeed: Double?
    )
}
