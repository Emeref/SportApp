package com.example.sportapp.presentation.sensors

import com.example.sportapp.AppConstants

/**
 * Fasada łącząca filtry sygnałów (Kalman) oraz dodatkową logikę biznesową (histereza).
 */
class SignalProcessor {
    private val locationFilter = KalmanLocationFilter()
    private val altitudeFilter = KalmanAltitudeFilter()

    private var lastFilteredAltitude: Double? = null
    var totalAscent: Double = 0.0
        private set
    var totalDescent: Double = 0.0
        private set

    /**
     * Przetwarza lokalizację GPS przez filtr Kalmana.
     */
    fun processLocation(lat: Double, lng: Double, accuracy: Float): Pair<Double, Double> {
        return locationFilter.process(lat, lng, accuracy)
    }

    /**
     * Przetwarza wysokość przez filtr Kalmana i oblicza przewyższenia z uwzględnieniem histerezy.
     */
    fun processAltitude(rawAltitude: Double): Double {
        val filteredAltitude = altitudeFilter.process(rawAltitude)

        lastFilteredAltitude?.let { last ->
            val diff = filteredAltitude - last
            
            // Histereza: dodajemy tylko jeśli zmiana jest większa niż próg (np. 0.5m)
            if (Math.abs(diff) >= AppConstants.ALTITUDE_HYSTERESIS_THRESHOLD) {
                if (diff > 0) {
                    totalAscent += diff
                } else {
                    totalDescent += Math.abs(diff)
                }
                lastFilteredAltitude = filteredAltitude
            }
        } ?: run {
            lastFilteredAltitude = filteredAltitude
        }

        return filteredAltitude
    }

    fun reset() {
        locationFilter.reset()
        altitudeFilter.reset()
        lastFilteredAltitude = null
        totalAscent = 0.0
        totalDescent = 0.0
    }
}
