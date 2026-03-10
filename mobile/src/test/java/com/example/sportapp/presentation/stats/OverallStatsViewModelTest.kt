package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.sportapp.data.FakeWorkoutRepository
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
            summaries = mutableListOf(
                mapOf(
                    "nazwa aktywnosci" to "Spacer",
                    "data" to "2024-03-01 10:00:00",
                    "kalorie" to "200",
                    "gps_dystans" to "3000",
                    "kroki_dystans" to "2800",
                    "kroki" to "4000"
                ),
                mapOf(
                    "nazwa aktywnosci" to "Bieganie",
                    "data" to "2024-03-02 10:00:00",
                    "kalorie" to "500",
                    "gps_dystans" to "8000",
                    "kroki_dystans" to "7500",
                    "kroki" to "10000"
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
