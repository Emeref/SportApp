package com.example.sportapp.presentation.sensors

import com.example.sportapp.AppConstants

/**
 * Filtr Kalmana dla wysokości (1D).
 * Optymalizowany pod kątem eliminacji szumu sensora ciśnienia.
 */
class KalmanAltitudeFilter(
    private val q: Double = AppConstants.KALMAN_ALTITUDE_Q,
    private val r: Double = AppConstants.KALMAN_ALTITUDE_R
) {
    private var altitude: Double = 0.0
    private var variance: Double = -1.0

    /**
     * Przetwarza nowy pomiar wysokości.
     * @param newAltitude Surowa wysokość w metrach
     * @return Odfiltrowana wysokość
     */
    fun process(newAltitude: Double): Double {
        if (variance < 0) {
            altitude = newAltitude
            variance = r
        } else {
            variance += q
            val k = variance / (variance + r)
            altitude += k * (newAltitude - altitude)
            variance = (1 - k) * variance
        }
        return altitude
    }

    fun reset() {
        variance = -1.0
    }
}
