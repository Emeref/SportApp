package com.example.sportapp.presentation.activities

import androidx.lifecycle.SavedStateHandle
import com.example.sportapp.data.FakeWorkoutRepository
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityTrimViewModelTest {

    private lateinit var viewModel: ActivityTrimViewModel
    private lateinit var fakeRepository: FakeWorkoutRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeWorkoutRepository()
        
        // Przygotowanie danych testowych
        val workoutId = 1L
        val workout = WorkoutEntity(
            id = workoutId,
            activityName = "Bieganie",
            startTime = 1000L,
            durationFormatted = "00:10",
            steps = 100,
            distanceSteps = 100.0,
            distanceGps = 100.0,
            durationSeconds = 10,
            totalCalories = 10.0,
            avgSpeedSteps = 0.0,
            avgSpeedGps = 0.0,
            totalAscent = 0.0,
            totalDescent = 0.0,
            avgBpm = 0.0,
            maxBpm = 0,
            maxCalorieMin = 0.0
        )
        
        val points = listOf(
            WorkoutPointEntity(id = 1, workoutId = workoutId, time = "10:00:01", bpm = 120, steps = 10, distanceGps = 10, distanceSteps = 10, calorieSum = 1.0, latitude = 0.0, longitude = 0.0, stepsMin = 0.0, speedGps = 0.0, speedSteps = 0.0, altitude = 0.0, totalAscent = 0.0, totalDescent = 0.0, calorieMin = 0.0),
            WorkoutPointEntity(id = 2, workoutId = workoutId, time = "10:00:02", bpm = 125, steps = 20, distanceGps = 20, distanceSteps = 20, calorieSum = 2.0, latitude = 0.0, longitude = 0.0, stepsMin = 0.0, speedGps = 0.0, speedSteps = 0.0, altitude = 0.0, totalAscent = 0.0, totalDescent = 0.0, calorieMin = 0.0),
            WorkoutPointEntity(id = 3, workoutId = workoutId, time = "10:00:03", bpm = 130, steps = 30, distanceGps = 30, distanceSteps = 30, calorieSum = 3.0, latitude = 0.0, longitude = 0.0, stepsMin = 0.0, speedGps = 0.0, speedSteps = 0.0, altitude = 0.0, totalAscent = 0.0, totalDescent = 0.0, calorieMin = 0.0),
            WorkoutPointEntity(id = 4, workoutId = workoutId, time = "10:00:04", bpm = 135, steps = 40, distanceGps = 40, distanceSteps = 40, calorieSum = 4.0, latitude = 0.0, longitude = 0.0, stepsMin = 0.0, speedGps = 0.0, speedSteps = 0.0, altitude = 0.0, totalAscent = 0.0, totalDescent = 0.0, calorieMin = 0.0),
            WorkoutPointEntity(id = 5, workoutId = workoutId, time = "10:00:05", bpm = 140, steps = 50, distanceGps = 50, distanceSteps = 50, calorieSum = 5.0, latitude = 0.0, longitude = 0.0, stepsMin = 0.0, speedGps = 0.0, speedSteps = 0.0, altitude = 0.0, totalAscent = 0.0, totalDescent = 0.0, calorieMin = 0.0)
        )
        
        fakeRepository.workouts.value = listOf(workout)
        fakeRepository.points[workoutId] = points

        val savedStateHandle = SavedStateHandle(mapOf("activityId" to workoutId.toString()))
        viewModel = ActivityTrimViewModel(fakeRepository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial data is loaded correctly`() = runTest {
        advanceUntilIdle()
        
        assertNotNull(viewModel.workout.value)
        assertEquals("Bieganie", viewModel.workout.value?.activityName)
        assertEquals(5, viewModel.points.value.size)
        assertEquals(0f..4f, viewModel.trimRange.value)
    }

    @Test
    fun `preview stats are calculated correctly for full range`() = runTest {
        advanceUntilIdle()
        
        val stats = viewModel.previewStats.value
        assertNotNull(stats)
        assertEquals("00:05", stats?.duration)
        assertEquals("40 m", stats?.distanceGps) // 50 - 10
        assertEquals("40 m", stats?.distanceSteps) // 50 - 10
        assertEquals("4 kcal", stats?.calories) // 5.0 - 1.0
        assertEquals("130 bpm", stats?.avgBpm) // (120+125+130+135+140)/5 = 130
    }

    @Test
    fun `trim range change updates preview stats`() = runTest {
        advanceUntilIdle()
        
        // Zmiana zakresu na punkty o indeksach 1-3 (id 2, 3, 4)
        viewModel.onTrimRangeChanged(1f..3f)
        
        val stats = viewModel.previewStats.value
        assertNotNull(stats)
        assertEquals("00:03", stats?.duration)
        assertEquals("20 m", stats?.distanceGps) // 40 (p4) - 20 (p2)
        assertEquals("130 bpm", stats?.avgBpm) // (125+130+135)/3 = 130
    }

    @Test
    fun `saveTrim calls repository trim and notifies completion`() = runTest {
        advanceUntilIdle()
        var completed = false
        
        viewModel.onTrimRangeChanged(1f..3f)
        viewModel.saveTrim { completed = true }
        
        advanceUntilIdle()
        
        assert(completed)
        // Sprawdzenie czy punkty w repozytorium zostały ograniczone
        val remainingPoints = fakeRepository.getPointsForWorkout(1L)
        assertEquals(3, remainingPoints.size)
        assertEquals(2L, remainingPoints.first().id)
        assertEquals(4L, remainingPoints.last().id)
    }
}
