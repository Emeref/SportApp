package com.example.sportapp.presentation.sensors

import com.example.sportapp.AppConstants

/**
 * Filtr Kalmana dla współrzędnych GPS (Lat, Lng).
 * Implementacja 2D bazująca na uproszczonym modelu stałej pozycji.
 */
class KalmanLocationFilter(
    private val q: Double = AppConstants.KALMAN_GPS_Q,
    private val rMin: Double = AppConstants.KALMAN_GPS_R_MIN
) {
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var variance: Double = -1.0 // -1 oznacza brak inicjalizacji

    /**
     * Przetwarza nowy pomiar GPS.
     * @param newLat Szerokość geograficzna
     * @param newLng Długość geograficzna
     * @param accuracy Dokładność z sensora (wariancja pomiaru)
     * @return Odfiltrowane współrzędne [Lat, Lng]
     */
    fun process(newLat: Double, newLng: Double, accuracy: Float): Pair<Double, Double> {
        var r = accuracy.toDouble()
        if (r < rMin) r = rMin

        if (variance < 0) {
            // Inicjalizacja pierwszą wartością
            lat = newLat
            lng = newLng
            variance = r
        } else {
            // Predykcja (uproszczona: x_k = x_{k-1})
            variance += q

            // Korekta (Kalman Gain)
            val k = variance / (variance + r)
            lat += k * (newLat - lat)
            lng += k * (newLng - lng)
            variance = (1 - k) * variance
        }

        return Pair(lat, lng)
    }

    fun reset() {
        variance = -1.0
    }
}
