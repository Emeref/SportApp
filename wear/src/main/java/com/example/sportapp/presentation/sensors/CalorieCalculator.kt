package com.example.sportapp.presentation.sensors

import com.example.sportapp.presentation.settings.Gender
import com.example.sportapp.presentation.settings.HealthData

object CalorieCalculator {

    /**
     * Model Keytel (2005) - oparty na HR, wieku, wadze i płci.
     * Zwraca spalone kalorie (kcal) na minutę.
     * 
     * Wzór oryginalny (kJ/min):
     * Male: EE = -55.0969 + 0.6309 * HR + 0.1988 * weight + 0.2017 * age
     * Female: EE = -20.4022 + 0.4472 * HR - 0.1263 * weight + 0.0740 * age
     */
    fun calculateKeytel(hr: Float, data: HealthData): Double {
        if (hr <= 0) return 0.0
        
        val kjPerMin = if (data.gender == Gender.MALE) {
            (-55.0969 + (0.6309 * hr) + (0.1988 * data.weight) + (0.2017 * data.age))
        } else {
            (-20.4022 + (0.4472 * hr) - (0.1263 * data.weight) + (0.074 * data.age))
        }
        
        // Konwersja kJ na kcal: 1 kJ = 1 / 4.184 kcal
        // Wynik jest już w kcal/min
        return kjPerMin / 4.184
    }

    /**
     * Model MET (Metabolic Equivalent of Task).
     * Zwraca spalone kalorie (kcal) na minutę.
     */
    fun calculateMET(met: Double, weight: Int): Double {
        // Formuła: kcal = MET * weight_kg * time_h
        // Na minutę: (MET * weight_kg) / 60
        return (met * weight) / 60.0
    }

    /**
     * Model HRR (Heart Rate Reserve) - Zaawansowana estymacja.
     * Zwraca spalone kalorie (kcal) na minutę.
     */
    fun calculateHRR(hr: Float, data: HealthData): Double {
        if (hr <= 0 || hr < data.restingHR) return 0.0
        
        val reserve = data.maxHR - data.restingHR
        if (reserve <= 0) return 0.0
        
        val intensity = (hr - data.restingHR) / reserve.toDouble()
        
        // Estymacja VO2 na podstawie intensywności (ml/kg/min)
        val estimatedVO2 = (intensity * 40.0) + 3.5 
        
        // kcal/min = (VO2 * weight / 1000) * 5 kcal
        return (estimatedVO2 * data.weight / 1000.0) * 5.0
    }
}
