package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.sportapp.data.FakeWorkoutRepository
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.presentation.settings.MobileSettingsManager
import com.example.sportapp.presentation.settings.MobileSettingsState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class OverallStatsViewModelTest {

    private lateinit var viewModel: OverallStatsViewModel
    private lateinit var fakeRepository: FakeWorkoutRepository
    private val mobileSettingsManager = mockk<MobileSettingsManager>()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var context: Context

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeWorkoutRepository()
        context = ApplicationProvider.getApplicationContext()
        
        every { mobileSettingsManager.settingsFlow } returns MutableStateFlow(MobileSettingsState())
        
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
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `overall stats contains raw data for all time`() = runTest {
        viewModel = OverallStatsViewModel(context, fakeRepository, mobileSettingsManager)
        
        viewModel.stats.test {
            // Wait for non-empty stats
            var stats = awaitItem()
            while (stats.isEmpty()) {
                stats = awaitItem()
            }
            
            @Suppress("UNCHECKED_CAST")
            val rawData = stats["raw_data"] as? List<WorkoutEntity>
            assertEquals(2, rawData?.size ?: 0)
        }
    }

    @Test
    fun `filtering by activity name updates stats`() = runTest {
        viewModel = OverallStatsViewModel(context, fakeRepository, mobileSettingsManager)
        
        viewModel.stats.test {
            // Wait for initial data (all types selected by default because _selectedTypes is null)
            var stats = awaitItem()
            while (stats.isEmpty()) {
                stats = awaitItem()
            }

            // toggleAllTypes sets _selectedTypes to emptySet()
            viewModel.toggleAllTypes()
            awaitItem() // Emission with empty stats
            
            // toggleTypeSelection adds "Bieganie" to the empty set
            viewModel.toggleTypeSelection("Bieganie")
            
            // Then we expect a new emission with filtered data
            stats = awaitItem()
            @Suppress("UNCHECKED_CAST")
            val rawData = stats["raw_data"] as? List<WorkoutEntity>
            assertEquals(1, rawData?.size ?: 0)
            assertEquals("Bieganie", rawData?.first()?.activityName)
        }
    }
}
