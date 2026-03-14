package com.example.sportapp.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.sportapp.data.db.AppDatabase
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SessionRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var workoutDao: WorkoutDao
    private lateinit var repository: SessionRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        workoutDao = db.workoutDao()
        repository = SessionRepository(workoutDao)
    }

    @After
    fun teardown() {
        db.close()
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
        workoutId = workoutId,
        time = time,
        latitude = null,
        longitude = null,
        bpm = null,
        avgBpm = null,
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
    fun `getSessionData returns correct max values from points`() = runBlocking {
        // Given: A workout with several points
        val workout = WorkoutEntity(
            activityName = "Test Workout",
            startTime = 123456789L,
            durationFormatted = "10:00",
            steps = 0,
            distanceSteps = 0.0,
            distanceGps = 0.0,
            avgSpeedSteps = 0.0,
            avgSpeedGps = 0.0,
            totalAscent = 0.0,
            totalDescent = 0.0,
            avgBpm = 0.0,
            maxBpm = 0,
            totalCalories = 0.0,
            maxCalorieMin = 0.0,
            durationSeconds = 600
        )
        val workoutId = workoutDao.insertWorkout(workout)

        val points = listOf(
            createPoint(workoutId, "10:00:01", steps = 10, distanceSteps = 8, distanceGps = 10, speedGps = 5.0, speedSteps = 4.0),
            createPoint(workoutId, "10:00:02", steps = 25, distanceSteps = 20, distanceGps = 25, speedGps = 7.0, speedSteps = 6.0),
            createPoint(workoutId, "10:00:03", steps = 50, distanceSteps = 46, distanceGps = 55, speedGps = 6.5, speedSteps = 5.5)
        )
        workoutDao.insertPoints(points)

        // When: Getting session data
        val result = repository.getSessionData(workoutId.toString())

        // Then: Max values should match the highest values in points
        assertEquals("Liczba kroków powinna być 50", 50, result.totalSteps)
        assertEquals("Dystans kroki powinien być 46", 46.0, result.totalDistanceSteps, 0.1)
        assertEquals("Dystans GPS powinien być 55", 55.0, result.totalDistanceGps, 0.1)
        assertEquals("Maks prędkość GPS powinna być 7.0", 7.0, result.maxSpeedGps, 0.1)
        assertEquals("Maks prędkość kroki powinna być 6.0", 6.0, result.maxSpeedSteps, 0.1)
    }

    @Test
    fun `getSessionData falls back to workout header when points are empty`() = runBlocking {
        // Given: A workout with header data but no points
        val workout = WorkoutEntity(
            activityName = "Header Only",
            startTime = 123456789L,
            durationFormatted = "05:00",
            steps = 100,
            distanceSteps = 80.0,
            distanceGps = 90.0,
            avgSpeedSteps = 4.5,
            avgSpeedGps = 5.5,
            totalAscent = 0.0,
            totalDescent = 0.0,
            avgBpm = 120.0,
            maxBpm = 140,
            totalCalories = 50.0,
            maxCalorieMin = 5.0,
            durationSeconds = 300
        )
        val workoutId = workoutDao.insertWorkout(workout)

        // When: Getting session data
        val result = repository.getSessionData(workoutId.toString())

        // Then: Values should be taken from the workout header
        assertEquals(100, result.totalSteps)
        assertEquals(80.0, result.totalDistanceSteps, 0.1)
        assertEquals(90.0, result.totalDistanceGps, 0.1)
        assertEquals(140, result.maxBpm)
    }
}
