package com.example.sportapp.presentation.sensors

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SignalProcessorTest {

    private lateinit var signalProcessor: SignalProcessor

    @Before
    fun setUp() {
        signalProcessor = SignalProcessor()
    }

    @Test
    fun `test GPS kalman filter stability`() {
        val lat = 52.2297
        val lng = 21.0122
        val accuracy = 10f // Słaba dokładność
        
        // Pierwszy punkt inicjalizuje filtr
        val (lat1, lng1) = signalProcessor.processLocation(lat, lng, accuracy)
        assertEquals(lat, lat1, 0.0001)
        assertEquals(lng, lng1, 0.0001)

        // Symulacja "pływania" pozycji o +/- 0.0001 (ok. 10m)
        val lat2 = lat + 0.0001
        val lng2 = lng - 0.0001
        val (fLat, fLng) = signalProcessor.processLocation(lat2, lng2, accuracy)

        // Odfiltrowana wartość powinna być bliżej oryginału niż surowy skok
        assertTrue("Lat should be smoothed", Math.abs(fLat - lat) < 0.0001)
        assertTrue("Lng should be smoothed", Math.abs(fLng - lng) < 0.0001)
    }

    @Test
    fun `test altitude hysteresis and filtering`() {
        val initialAlt = 100.0
        signalProcessor.processAltitude(initialAlt)
        
        // Mały szum (0.2m) - nie powinien zwiększać totalAscent (próg 0.5m)
        signalProcessor.processAltitude(100.2)
        assertEquals(0.0, signalProcessor.totalAscent, 0.01)

        // Skok powyżej progu (0.7m)
        signalProcessor.processAltitude(100.7)
        assertTrue("Total ascent should be recorded", signalProcessor.totalAscent > 0.0)
        
        val firstAscent = signalProcessor.totalAscent

        // Kolejny mały szum (0.1m)
        signalProcessor.processAltitude(100.8)
        assertEquals(firstAscent, signalProcessor.totalAscent, 0.01)
    }

    @Test
    fun `test total descent recording`() {
        signalProcessor.processAltitude(200.0)
        
        // Spadek o 1m
        signalProcessor.processAltitude(199.0)
        assertTrue("Total descent should be recorded", signalProcessor.totalDescent >= 0.5)
    }
}
