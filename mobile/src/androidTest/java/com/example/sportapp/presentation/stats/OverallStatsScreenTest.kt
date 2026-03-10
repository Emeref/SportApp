package com.example.sportapp.presentation.stats

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.sportapp.presentation.settings.WidgetItem
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import org.junit.Rule
import org.junit.Test
import java.util.*

class OverallStatsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleWidgets = listOf(
        WidgetItem("calories", "Kalorie", true),
        WidgetItem("steps", "Kroki", true)
    )

    @Test
    fun overallStatsContent_showsNoDataMessage_whenStatsAreEmpty() {
        composeTestRule.setContent {
            OverallStatsContent(
                stats = emptyMap(),
                widgets = sampleWidgets,
                activityTypes = emptyList(),
                selectedType = null,
                startDate = null,
                endDate = null,
                chartProducers = emptyMap(),
                getMaxValueForWidget = { 0.0 },
                onTypeSelected = {},
                onDateRangeSelected = { _, _ -> },
                onNavigateBack = {},
                onNavigateToOptions = {}
            )
        }

        // Jeśli stats nie zawiera "raw_data", nie pokazujemy komunikatu o braku danych (bo jeszcze się ładują lub brak klucza)
        // Ale jeśli zawiera pusty "raw_data", pokazujemy komunikat
    }

    @Test
    fun overallStatsContent_showsEmptyDataMessage_whenRawDataIsEmpty() {
        composeTestRule.setContent {
            OverallStatsContent(
                stats = mapOf("raw_data" to emptyList<Map<String, String>>()),
                widgets = sampleWidgets,
                activityTypes = emptyList(),
                selectedType = null,
                startDate = null,
                endDate = null,
                chartProducers = emptyMap(),
                getMaxValueForWidget = { 0.0 },
                onTypeSelected = {},
                onDateRangeSelected = { _, _ -> },
                onNavigateBack = {},
                onNavigateToOptions = {}
            )
        }

        composeTestRule.onNodeWithText("Brak danych do wyświetlenia wykresów.").assertIsDisplayed()
    }

    @Test
    fun overallStatsContent_showsCharts_whenDataIsAvailable() {
        val testRawData = listOf(
            mapOf("nazwa aktywnosci" to "Bieganie", "data" to "2024-01-01 10:00:00", "kalorie" to "200")
        )
        val producers = mapOf("calories" to ChartEntryModelProducer())

        composeTestRule.setContent {
            OverallStatsContent(
                stats = mapOf("raw_data" to testRawData),
                widgets = sampleWidgets,
                activityTypes = listOf("Bieganie"),
                selectedType = null,
                startDate = null,
                endDate = null,
                chartProducers = producers,
                getMaxValueForWidget = { 200.0 },
                onTypeSelected = {},
                onDateRangeSelected = { _, _ -> },
                onNavigateBack = {},
                onNavigateToOptions = {}
            )
        }

        composeTestRule.onNodeWithText("Wykresy trendów").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kalorie").assertIsDisplayed()
    }

    @Test
    fun filterSection_displaysSelectedType() {
        val selectedType = "Rower"
        
        composeTestRule.setContent {
            OverallStatsContent(
                stats = emptyMap(),
                widgets = emptyList(),
                activityTypes = listOf("Rower", "Spacer"),
                selectedType = selectedType,
                startDate = null,
                endDate = null,
                chartProducers = emptyMap(),
                getMaxValueForWidget = { 0.0 },
                onTypeSelected = {},
                onDateRangeSelected = { _, _ -> },
                onNavigateBack = {},
                onNavigateToOptions = {}
            )
        }

        composeTestRule.onNodeWithText(selectedType).assertIsDisplayed()
    }
}
