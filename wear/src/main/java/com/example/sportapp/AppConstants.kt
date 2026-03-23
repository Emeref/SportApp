package com.example.sportapp

/**
 * Globalne stałe konfiguracyjne dla aplikacji Wear OS i Mobile.
 */
object AppConstants {
    /**
     * Mnożnik czułości kółka (Rotary). 
     * Im wyższa wartość, tym szybciej przewija się lista przy jednym obrocie kółka.
     * Domyślnie w systemie jest to około 1.0f.
     */
    const val ROTARY_SENSITIVITY = 7.5f

    /**
     * Próg wysokości (w metrach) dla obliczeń przewyższeń (totalAscent/totalDescent).
     * Zmiana wysokości mniejsza niż ten próg jest traktowana jako szum i ignorowana.
     */
    const val ELEVATION_THRESHOLD = 1.0
}
