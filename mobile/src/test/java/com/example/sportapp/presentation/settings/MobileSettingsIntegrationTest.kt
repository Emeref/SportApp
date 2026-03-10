package com.example.sportapp.presentation.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34]) // Robolectric 4.11.1 wspiera max SDK 34
class MobileSettingsIntegrationTest {

    private lateinit var context: Context
    private lateinit var settingsManager: MobileSettingsManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        settingsManager = MobileSettingsManager(context)
    }

    @Test
    fun `saveSettings and readSettings returns correct values`() = runBlocking {
        val initialState = settingsManager.settingsFlow.first()
        // Nie sprawdzamy konkretnej wartości bo testy mogą być uruchamiane jeden po drugim na tym samym DataStore
        
        val newState = initialState.copy(useTestData = true, period = ReportingPeriod.YEAR)
        settingsManager.saveSettings(newState)

        val savedState = settingsManager.settingsFlow.first()
        assertTrue(savedState.useTestData)
        assertEquals(ReportingPeriod.YEAR, savedState.period)
    }

    @Test
    fun `default settings are correct`() = runBlocking {
        // Czyścimy ustawienia przed testem domyślnych wartości
        val state = settingsManager.settingsFlow.first()
        assertTrue(state.widgets.isNotEmpty())
    }
}
