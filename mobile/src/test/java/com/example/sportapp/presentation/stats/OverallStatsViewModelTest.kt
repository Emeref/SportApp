package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.test.core.app.ApplicationProvider
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
import java.io.File
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OverallStatsViewModelTest {

    private lateinit var context: Context
    private lateinit var viewModel: OverallStatsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()
        
        val activitiesDir = File(context.filesDir, "activities")
        if (!activitiesDir.exists()) activitiesDir.mkdirs()
        val summaryFile = File(activitiesDir, "Podsumowanie_cwiczen.csv")
        summaryFile.writeText(
            "nazwa aktywnosci;data;dlugosc;kalorie;gps_dystans;kroki_dystans;przewyzszenia_gora;przewyzszenia_dol;kroki\n" +
            "Spacer;2024-03-01 10:00:00;00:30:00;200;3000;2800;10;5;4000\n" +
            "Bieganie;2024-03-02 10:00:00;00:45:00;500;8000;7500;50;45;10000"
        )

        viewModel = OverallStatsViewModel(context)
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
