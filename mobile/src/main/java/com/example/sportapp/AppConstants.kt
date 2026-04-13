package com.example.sportapp

/**
 * Globalne stałe konfiguracyjne dla aplikacji.
 */
object AppConstants {
    /**
     * Próg wysokości (w metrach) dla obliczeń przewyższeń (totalAscent/totalDescent).
     * Zmiana wysokości mniejsza niż ten próg jest traktowana jako szum i ignorowana.
     */
    const val ELEVATION_THRESHOLD = 1.0

    /**
     * Promień (w kilometrach) używany do sprawdzania, czy trasy są blisko siebie
     * w celu wyświetlenia ich na jednej mapie w ekranie porównania.
     */
    const val MAP_COMPARISON_RADIUS_KM = 3.0
}
