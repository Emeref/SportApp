package com.example.sportapp.data

import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SessionRepositoryTest {

    @Mock
    private lateinit var workoutDao: WorkoutDao

    private lateinit var repository: SessionRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = SessionRepository(workoutDao)
    }

    private fun createPoint(
        workoutId: Long,
        time: String,
        steps: Int = 0,
        distanceSteps: Int = 0,
        distanceGps: Int = 0,
        speedGps: Double = 0.0,
        speedSteps: Double = 0.0
    ) = WorkoutPointEntity(
        id = 0,
        workoutId = workoutId,
        time = time,
        latitude = 52.0,
        longitude = 21.0,
        bpm = null,
        steps = steps,
        stepsMin = null,
        distanceSteps = distanceSteps,
        distanceGps = distanceGps,
        speedGps = speedGps,
        speedSteps = speedSteps,
        altitude = null,
        totalAscent = null,
        totalDescent = null,
        calorieMin = null,
        calorieSum = null
    )

    @Test
    fun `getSessionData returns mapped data from dao`() = runTest {
        val workoutId = 1L
        val workout = WorkoutEntity(
            id = workoutId,
            activityName = "Walking",
            startTime = 1000L,
            durationFormatted = "00:01:00",
            steps = 100,
            distanceSteps = 70.0,
            distanceGps = 80.0,
            avgSpeedSteps = 1.1,
            avgSpeedGps = 1.2,
            totalAscent = 5.0,
            totalDescent = 2.0,
            avgBpm = 120.0,
            maxBpm = 140,
            totalCalories = 50.0,
            maxCalorieMin = 5.0,
            durationSeconds = 60L
        )
        val points = listOf(
            createPoint(workoutId, "10:00"),
            createPoint(workoutId, "10:01")
        )

        `when`(workoutDao.getWorkoutById(workoutId)).thenReturn(workout)
        `when`(workoutDao.getPointsForWorkout(workoutId)).thenReturn(points)

        val result = repository.getSessionData(workoutId)

        assertEquals("Walking", result.activityName)
        assertEquals(100, result.totalSteps)
        assertEquals(80.0, result.totalDistanceGps, 0.1)
    }
}
