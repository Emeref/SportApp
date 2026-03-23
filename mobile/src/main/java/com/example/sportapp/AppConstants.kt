package com.example.sportapp

/**
 * Globalne stałe konfiguracyjne dla aplikacji.
 */
object AppConstants {
    /**
     * Próg wysokości (w metrach) dla obliczeń przewyższeń (totalAscent/totalDescent).
     * Zmiana wysokości mniejsza niż ten próg jest traktowana jako szum i ignorowana.
     */
    const val ELEVATION_THRESHOLD = 0.5
}
