package com.example.sportapp.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.sportapp.data.db.AppDatabase
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutEntity
import kotlinx.coroutines.flow.first
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
class WorkoutRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var workoutDao: WorkoutDao
    private lateinit var repository: WorkoutRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        workoutDao = db.workoutDao()
        repository = WorkoutRepository(context, workoutDao)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `formatDistance returns correct strings for various ranges`() {
        assertEquals("500 m", repository.formatDistance(500.0))
        assertEquals("1.23 km", repository.formatDistance(1234.0))
        assertEquals("12.3 km", repository.formatDistance(12345.0))
        assertEquals("123 km", repository.formatDistance(123456.0))
    }

    @Test
    fun `getFilteredStats correctly aggregates data from database`() = runBlocking {
        val workout1 = WorkoutEntity(
            activityName = "Spacer",
            startTime = System.currentTimeMillis() - 100000,
            durationFormatted = "00:30:00",
            steps = 4000,
            distanceSteps = 2800.0,
            distanceGps = 3000.0,
            avgSpeedSteps = 5.0,
            avgSpeedGps = 6.0,
            totalAscent = 10.0,
            totalDescent = 5.0,
            avgBpm = 100.0,
            maxBpm = 120,
            totalCalories = 200.5,
            maxCalorieMin = 10.0,
            durationSeconds = 1800
        )
        val workout2 = WorkoutEntity(
            activityName = "Bieganie",
            startTime = System.currentTimeMillis(),
            durationFormatted = "00:45:00",
            steps = 10000,
            distanceSteps = 7500.0,
            distanceGps = 8000.0,
            avgSpeedSteps = 10.0,
            avgSpeedGps = 11.0,
            totalAscent = 50.0,
            totalDescent = 45.0,
            avgBpm = 150.0,
            maxBpm = 180,
            totalCalories = 500.4,
            maxCalorieMin = 20.0,
            durationSeconds = 2700
        )

        workoutDao.insertWorkout(workout1)
        workoutDao.insertWorkout(workout2)

        val stats = repository.getFilteredStats()
        assertEquals(700.9, stats["calories"] as Double, 0.1)
        assertEquals(2, stats["count"] as Int)
    }

    @Test
    fun `getAllWorkouts returns workouts from database`() = runBlocking {
        val workout = WorkoutEntity(
            activityName = "Test",
            startTime = System.currentTimeMillis(),
            durationFormatted = "00:10:00",
            steps = 1000,
            distanceSteps = 700.0,
            distanceGps = 800.0,
            avgSpeedSteps = 4.0,
            avgSpeedGps = 4.5,
            totalAscent = 0.0,
            totalDescent = 0.0,
            avgBpm = 120.0,
            maxBpm = 140,
            totalCalories = 100.0,
            maxCalorieMin = 10.0,
            durationSeconds = 600
        )
        workoutDao.insertWorkout(workout)

        val workouts = repository.getAllWorkouts().first()
        assertEquals(1, workouts.size)
        assertEquals("Test", workouts[0].activityName)
    }
}
