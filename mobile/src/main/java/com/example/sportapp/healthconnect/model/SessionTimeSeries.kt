package com.example.sportapp.healthconnect.model

import java.time.Instant

data class SessionTimeSeries(
    val heartRates: List<HeartRateSample> = emptyList(),
    val speeds: List<SpeedSample> = emptyList(),
    val cadences: List<CadenceSample> = emptyList(),
    val distances: List<DistanceSample> = emptyList(),
    val elevations: List<ElevationSample> = emptyList(),
    val locations: List<LocationSample> = emptyList(),
    val calories: List<CaloriesSample> = emptyList()
)

data class HeartRateSample(val time: Instant, val bpm: Int)
data class SpeedSample(val time: Instant, val speedMps: Double)
data class CadenceSample(val time: Instant, val rate: Double)
data class DistanceSample(val startTime: Instant, val endTime: Instant, val distanceMeters: Double)
data class ElevationSample(val startTime: Instant, val endTime: Instant, val elevationMeters: Double)
data class LocationSample(val time: Instant, val latitude: Double, val longitude: Double, val altitude: Double?)
data class CaloriesSample(val startTime: Instant, val endTime: Instant, val kilocalories: Double)
