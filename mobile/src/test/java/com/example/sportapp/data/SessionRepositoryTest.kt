package com.example.sportapp.data

import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
        speedSteps: Double = 0.0,
        bpm: Int? = null,
        calorieMin: Double? = null,
        calorieSum: Double? = null,
        altitude: Double? = null,
        totalAscent: Double? = null,
        totalDescent: Double? = null,
        stepsMin: Double? = null
    ) = WorkoutPointEntity(
        id = 0,
        workoutId = workoutId,
        time = time,
        latitude = 52.0,
        longitude = 21.0,
        bpm = bpm,
        steps = steps,
        stepsMin = stepsMin,
        distanceSteps = distanceSteps,
        distanceGps = distanceGps,
        speedGps = speedGps,
        speedSteps = speedSteps,
        altitude = altitude,
        totalAscent = totalAscent,
        totalDescent = totalDescent,
        calorieMin = calorieMin,
        calorieSum = calorieSum
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

    @Test
    fun `getSessionData maps all charts correctly`() = runTest {
        val workoutId = 1L
        val workout = WorkoutEntity(
            id = workoutId,
            activityName = "Bieganie",
            startTime = 1000L,
            durationFormatted = "00:10:00",
            steps = 1000,
            distanceSteps = 1000.0,
            distanceGps = 1100.0,
            avgSpeedSteps = 10.0,
            avgSpeedGps = 11.0,
            totalAscent = 20.0,
            totalDescent = 10.0,
            avgBpm = 150.0,
            maxBpm = 180,
            totalCalories = 200.0,
            maxCalorieMin = 25.0,
            durationSeconds = 600L
        )
        
        val points = listOf(
            createPoint(workoutId, "10:00", bpm = 140, calorieMin = 10.0, speedGps = 5.0, altitude = 100.0, stepsMin = 160.0),
            createPoint(workoutId, "10:01", bpm = 160, calorieMin = 15.0, speedGps = 6.0, altitude = 105.0, stepsMin = 170.0)
        )

        `when`(workoutDao.getWorkoutById(workoutId)).thenReturn(workout)
        `when`(workoutDao.getPointsForWorkout(workoutId)).thenReturn(points)

        val result = repository.getSessionData(workoutId)

        assertNotNull(result.charts["bpm"])
        assertEquals(2, result.charts["bpm"]?.size)
        assertEquals(140f, result.charts["bpm"]?.get(0))
        assertEquals(160f, result.charts["bpm"]?.get(1))

        assertNotNull(result.charts["kalorie_min"])
        assertEquals(10f, result.charts["kalorie_min"]?.get(0))

        assertNotNull(result.charts["predkosc"])
        assertEquals(5f, result.charts["predkosc"]?.get(0))

        assertNotNull(result.charts["wysokosc"])
        assertEquals(100f, result.charts["wysokosc"]?.get(0))

        assertNotNull(result.charts["kroki_min"])
        assertEquals(160f, result.charts["kroki_min"]?.get(0))
    }
}
