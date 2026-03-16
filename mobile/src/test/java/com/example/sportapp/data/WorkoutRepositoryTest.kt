package com.example.sportapp.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.sportapp.data.db.AppDatabase
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
        repository = WorkoutRepository(workoutDao)
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
    fun `insert and get workout works correctly`() = runTest {
        val workout = WorkoutEntity(
            activityName = "Running",
            startTime = 1000L,
            durationFormatted = "00:10:00",
            steps = 1000,
            distanceSteps = 800.0,
            distanceGps = 1000.0,
            avgSpeedSteps = 1.3,
            avgSpeedGps = 1.6,
            totalAscent = 10.0,
            totalDescent = 5.0,
            avgBpm = 140.0,
            maxBpm = 160,
            totalCalories = 100.0,
            maxCalorieMin = 10.0,
            durationSeconds = 600L
        )

        repository.insertWorkout(workout)
        val all = repository.getAllWorkouts().first()
        
        assertEquals(1, all.size)
        assertEquals("Running", all[0].activityName)
    }
}
