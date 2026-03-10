package com.example.sportapp.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.sportapp.presentation.settings.ReportingPeriod
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WorkoutRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: WorkoutRepository
    private lateinit var activitiesDir: File

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        repository = WorkoutRepository(context)
        
        // Setup a mock activities directory in the test environment
        activitiesDir = File(context.filesDir, "activities")
        if (!activitiesDir.exists()) activitiesDir.mkdirs()
        
        // Clear existing summary file if any
        val summaryFile = File(activitiesDir, "Podsumowanie_cwiczen.csv")
        if (summaryFile.exists()) summaryFile.delete()
    }

    @Test
    fun `formatDistance returns correct strings for various ranges`() {
        assertEquals("500 m", repository.formatDistance(500.0))
        assertEquals("1.23 km", repository.formatDistance(1234.0))
        assertEquals("12.3 km", repository.formatDistance(12345.0))
        assertEquals("123 km", repository.formatDistance(123456.0))
    }

    @Test
    fun `getFilteredStats returns empty map when no file exists`() {
        val stats = repository.getFilteredStats()
        assertEquals(0, stats["count"])
        assertEquals(0.0, stats["calories"])
    }

    @Test
    fun `getFilteredStats correctly aggregates data from CSV`() {
        val summaryFile = File(activitiesDir, "Podsumowanie_cwiczen.csv")
        summaryFile.writeText(
            "nazwa aktywnosci;data;dlugosc;kalorie;gps_dystans;kroki_dystans;przewyzszenia_gora;przewyzszenia_dol;kroki\n" +
            "Spacer;2024-03-01 10:00:00;00:30:00;200.5;3000.0;2800.0;10.0;5.0;4000\n" +
            "Bieganie;2024-03-02 10:00:00;00:45:00;500.0;8000.0;7500.0;50.0;45.0;10000"
        )

        val stats = repository.getFilteredStats()
        assertEquals(2, stats["count"])
        assertEquals(700.5, stats["calories"] as Double, 0.001)
        assertEquals(11000.0, stats["distanceGpsM"] as Double, 0.001)
        assertEquals(14000L, stats["steps"] as Long)
    }

    @Test
    fun `getFilteredStats filters by activity type`() {
        val summaryFile = File(activitiesDir, "Podsumowanie_cwiczen.csv")
        summaryFile.writeText(
            "nazwa aktywnosci;data;dlugosc;kalorie;gps_dystans;kroki_dystans;przewyzszenia_gora;przewyzszenia_dol;kroki\n" +
            "Spacer;2024-03-01 10:00:00;00:30:00;200.5;3000.0;2800.0;10.0;5.0;4000\n" +
            "Bieganie;2024-03-02 10:00:00;00:45:00;500.0;8000.0;7500.0;50.0;45.0;10000"
        )

        val stats = repository.getFilteredStats(activityType = "Spacer")
        assertEquals(1, stats["count"])
        assertEquals(200.5, stats["calories"] as Double, 0.001)
    }
}
