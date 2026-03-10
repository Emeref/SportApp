package com.example.sportapp.presentation.sensors

import android.location.Location
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DistanceCalculatorTest {

    @Test
    fun `distance between two locations is calculated correctly`() {
        // Roboelectric provides a shadow for Location
        val loc1 = Location("test").apply {
            latitude = 52.2297 // Warsaw
            longitude = 21.0122
        }
        
        val loc2 = Location("test").apply {
            latitude = 50.0647 // Krakow
            longitude = 19.9450
        }

        val distance = loc1.distanceTo(loc2)
        
        // Distance Warsaw-Krakow is approx 250km
        assertEquals(252000.0, distance.toDouble(), 5000.0) 
    }

    @Test
    fun `total distance accumulation works`() {
        var totalDistance = 0f
        
        val locations = listOf(
            Location("test").apply { latitude = 0.0; longitude = 0.0 },
            Location("test").apply { latitude = 0.001; longitude = 0.0 }, // ~111m
            Location("test").apply { latitude = 0.002; longitude = 0.0 }  // ~111m
        )

        var lastLocation: Location? = null
        for (location in locations) {
            lastLocation?.let {
                totalDistance += it.distanceTo(location)
            }
            lastLocation = location
        }

        assertEquals(222.0, totalDistance.toDouble(), 10.0)
    }
}
