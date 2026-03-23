package com.example.sportapp.presentation.workout

import android.hardware.SensorManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AltitudeManager @Inject constructor() {
    private var initialOffset: Double? = null
    private var lastBarometricAltitude: Double? = null

    /**
     * Kalibruje wysokość barometru na podstawie odczytu GPS.
     * Wywoływane, gdy otrzymamy pierwszy stabilny punkt GPS z wysokością.
     */
    fun setGpsAltitude(gpsAltitude: Double) {
        val currentBaro = lastBarometricAltitude ?: return
        if (initialOffset == null) {
            initialOffset = gpsAltitude - currentBaro
        }
    }

    /**
     * Przetwarza surowy odczyt ciśnienia z sensora.
     * @return Skorygowana wysokość jeśli offset jest znany (GPS kalibracja nastąpiła), 
     *         w przeciwnym razie null.
     */
    fun processPressure(pressure: Float): Double? {
        val rawAltitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure).toDouble()
        lastBarometricAltitude = rawAltitude
        
        // Nie zwracaj wysokości dopóki nie mamy offsetu z GPS
        val offset = initialOffset ?: return null
        return offset + rawAltitude
    }

    /**
     * Resetuje manager dla nowej sesji.
     */
    fun reset() {
        initialOffset = null
        lastBarometricAltitude = null
    }

    fun isCalibrated(): Boolean = initialOffset != null

    fun getOffset(): Double = initialOffset ?: 0.0
}
