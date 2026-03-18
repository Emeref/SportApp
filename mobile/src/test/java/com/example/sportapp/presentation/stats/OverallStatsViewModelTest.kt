package com.example.sportapp.presentation.stats

import android.content.Context
import com.example.sportapp.data.FakeWorkoutRepository
import com.example.sportapp.data.db.WorkoutEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class OverallStatsViewModelTest {

    private lateinit var viewModel: OverallStatsViewModel
    private lateinit var fakeRepository: FakeWorkoutRepository
    private val testDispatcher = StandardTestDispatcher()
    private val mockContext = mock(Context::class.java)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeWorkoutRepository()
        
        // Dane testowe: 2 treningi
        val now = System.currentTimeMillis()
        
        val workout1 = WorkoutEntity(
            id = 1, activityName = "Bieganie", startTime = now,
            durationFormatted = "00:30", steps = 3000, distanceSteps = 2500.0,
            distanceGps = 2400.0, durationSeconds = 1800, totalCalories = 300.0,
            avgSpeedSteps = 0.0, avgSpeedGps = 0.0, totalAscent = 0.0, totalDescent = 0.0,
            avgBpm = 140.0, maxBpm = 160, maxCalorieMin = 0.0
        )
        
        val workout2 = WorkoutEntity(
            id = 2, activityName = "Spacer", startTime = now - 1000,
            durationFormatted = "01:00", steps = 5000, distanceSteps = 4000.0,
            distanceGps = 3900.0, durationSeconds = 3600, totalCalories = 200.0,
            avgSpeedSteps = 0.0, avgSpeedGps = 0.0, totalAscent = 0.0, totalDescent = 0.0,
            avgBpm = 100.0, maxBpm = 120, maxCalorieMin = 0.0
        )
        
        fakeRepository.workouts.value = listOf(workout1, workout2)
        viewModel = OverallStatsViewModel(mockContext, fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `overall stats contains raw data for all time`() = runTest {
        advanceUntilIdle()
        
        val stats = viewModel.stats.value
        @Suppress("UNCHECKED_CAST")
        val rawData = stats["raw_data"] as List<WorkoutEntity>
        assertEquals(2, rawData.size)
    }

    @Test
    fun `filtering by activity name updates stats`() = runTest {
        advanceUntilIdle()
        
        viewModel.onTypeSelected("Bieganie")
        advanceUntilIdle()
        
        val stats = viewModel.stats.value
        @Suppress("UNCHECKED_CAST")
        val rawData = stats["raw_data"] as List<WorkoutEntity>
        assertEquals(1, rawData.size)
        assertEquals("Bieganie", rawData.first().activityName)
    }
}
