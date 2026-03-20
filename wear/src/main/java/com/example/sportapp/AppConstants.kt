package com.example.sportapp

/**
 * Globalne stałe konfiguracyjne dla aplikacji Wear OS.
 */
object AppConstants {
    /**
     * Mnożnik czułości kółka (Rotary). 
     * Im wyższa wartość, tym szybciej przewija się lista przy jednym obrocie kółka.
     * Domyślnie w systemie jest to około 1.0f.
     */
    const val ROTARY_SENSITIVITY = 7.5f

    /**
     * Parametry Filtra Kalmana dla GPS.
     * Q - wariancja procesu (jak bardzo ufamy modelowi ruchu).
     * R_MIN - minimalna wariancja pomiaru (jak bardzo ufamy sensorowi).
     */
    const val KALMAN_GPS_Q = 0.0001
    const val KALMAN_GPS_R_MIN = 0.1

    /**
     * Parametry Filtra Kalmana dla wysokości.
     * Q - wariancja procesu (im mniejsza, tym płynniejszy wykres).
     * R - wariancja pomiaru (szum sensora).
     */
    const val KALMAN_ALTITUDE_Q = 0.005
    const val KALMAN_ALTITUDE_R = 0.8

    /**
     * Histereza dla przewyższeń (metry).
     * Ignoruj zmiany wysokości mniejsze niż ten próg, aby uniknąć szumu.
     */
    const val ALTITUDE_HYSTERESIS_THRESHOLD = 0.5
}
