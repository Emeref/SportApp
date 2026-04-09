package com.example.sportapp.healthconnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.healthconnect.model.ExerciseSessionSyncDto
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

    private data class SessionAggregates(
        val distance: Double?,
        val calories: Double?,
        val avgHR: Int?,
        val maxHR: Int?,
        val avgSpeed: Double?,
        val maxSpeed: Double?
    )
}
