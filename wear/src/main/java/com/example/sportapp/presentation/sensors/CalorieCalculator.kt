package com.example.sportapp.presentation.sensors

import com.example.sportapp.presentation.settings.Gender
import com.example.sportapp.presentation.settings.HealthData

object CalorieCalculator {

    /**
     * Model Keytel (2005) - oparty na HR, wieku, wadze i płci.
     * Zwraca spalone kalorie (kcal) na minutę.
     */
    fun calculateKeytel(hr: Float, data: HealthData): Double {
        if (hr <= 0) return 0.0
        
        return if (data.gender == Gender.MALE) {
            ((-55.0969 + (0.6309 * hr) + (0.1988 * data.weight) + (0.2017 * data.age)) / 4.184) / 60.0
        } else {
            ((-20.4022 + (0.4472 * hr) - (0.1263 * data.weight) + (0.074 * data.age)) / 4.184) / 60.0
        }
    }

    /**
     * Model MET (Metabolic Equivalent of Task).
     * Zwraca spalone kalorie (kcal) na minutę.
     * @param met stała MET dla danego sportu (np. Spacer = 3.5)
     */
    fun calculateMET(met: Double, weight: Int): Double {
        // Formuła: kcal = MET * weight_kg * time_h
        // Na minutę: (MET * weight_kg) / 60
        return (met * weight) / 60.0
    }

    /**
     * Model HRR (Heart Rate Reserve) - Zaawansowana estymacja.
     * Wykorzystuje % rezerwy tętna do szacowania intensywności.
     * Zwraca spalone kalorie (kcal) na minutę.
     */
    fun calculateHRR(hr: Float, data: HealthData): Double {
        if (hr <= 0 || hr < data.restingHR) return 0.0
        
        // Obliczanie % intensywności rezerwy tętna
        val reserve = data.maxHR - data.restingHR
        if (reserve <= 0) return 0.0
        
        val intensity = (hr - data.restingHR) / reserve.toDouble()
        
        // Uproszczony model VO2 (pułapu tlenowego) na podstawie intensywności HRR
        // Średnie VO2max dla zdrowego dorosłego to ok 35-45 ml/kg/min
        val estimatedVO2 = (intensity * 40.0) + 3.5 // 3.5 to MET spoczynkowy
        
        // 1 litr tlenu to ok 5 kcal
        // (VO2 * weight) / 1000 -> litry na minutę
        return (estimatedVO2 * data.weight / 1000.0) * 5.0
    }
}
