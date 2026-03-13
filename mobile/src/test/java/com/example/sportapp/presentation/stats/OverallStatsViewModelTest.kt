package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.sportapp.data.FakeWorkoutRepository
import com.example.sportapp.data.db.WorkoutEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OverallStatsViewModelTest {

    private lateinit var context: Context
    private lateinit var viewModel: OverallStatsViewModel
    private lateinit var fakeRepository: FakeWorkoutRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()
        
        fakeRepository = FakeWorkoutRepository().apply {
            workouts = mutableListOf(
                WorkoutEntity(
                    id = 1,
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
                    totalCalories = 200.0,
                    maxCalorieMin = 10.0,
                    durationSeconds = 1800
                ),
                WorkoutEntity(
                    id = 2,
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
                    totalCalories = 500.0,
                    maxCalorieMin = 20.0,
                    durationSeconds = 2700
                )
            )
        }

        viewModel = OverallStatsViewModel(context, fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial activity types are loaded from repository`() = runTest {
        advanceUntilIdle()
        val types = viewModel.activityTypes.value
        assertEquals(2, types.size)
        assert(types.contains("Spacer"))
        assert(types.contains("Bieganie"))
    }

    @Test
    fun `onTypeSelected updates selectedType state`() = runTest {
        viewModel.onTypeSelected("Spacer")
        assertEquals("Spacer", viewModel.selectedType.value)
        
        viewModel.onTypeSelected("Wszystkie")
        assertNull(viewModel.selectedType.value)
    }

    @Test
    fun `onDateRangeSelected updates date states`() = runTest {
        val start = Date()
        val end = Date()
        viewModel.onDateRangeSelected(start, end)
        assertEquals(start, viewModel.startDate.value)
        assertEquals(end, viewModel.endDate.value)
    }

    @Test
    fun `getMaxValueForWidget returns correct maximum value for distanceGps`() = runTest {
        advanceUntilIdle()
        val maxValue = viewModel.getMaxValueForWidget("distanceGps")
        assertEquals(8000.0, maxValue, 0.001)
    }

    @Test
    fun `chartProducers are updated when data changes`() = runTest {
        advanceUntilIdle()
        val stepsProducer = viewModel.chartProducers["steps"]
        assert(stepsProducer != null)
    }
}
