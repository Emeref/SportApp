package com.example.sportapp.presentation.stats

import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.HeartRateZone
import com.example.sportapp.data.model.HeartRateZoneResult
import com.example.sportapp.data.model.ZoneStat

object HeartRateMath {

    fun calculateZones(points: List<WorkoutPointEntity>, maxHr: Int): HeartRateZoneResult {
        if (points.isEmpty() || maxHr <= 0) {
            return HeartRateZoneResult(emptyList(), "Brak danych tętna", null)
        }

        // 1. Filtracja Moving Average (3 punkty)
        val filteredBpm = points.mapNotNull { it.bpm }.windowed(3, 1) { window ->
            window.average().toInt()
        }

        if (filteredBpm.isEmpty()) {
            return HeartRateZoneResult(emptyList(), "Zbyt mało danych", null)
        }

        // 2. Grupowanie w strefy
        val zoneCounts = mutableMapOf<HeartRateZone, Long>()
        HeartRateZone.values().forEach { zoneCounts[it] = 0L }

        filteredBpm.forEach { bpm ->
            HeartRateZone.fromBpm(bpm, maxHr)?.let { zone ->
                zoneCounts[zone] = zoneCounts.getOrDefault(zone, 0L) + 1
            }
        }

        val totalPoints = zoneCounts.values.sum().toFloat()
        if (totalPoints == 0f) {
            return HeartRateZoneResult(emptyList(), "Tętno poniżej stref", null)
        }

        // 3. Przygotowanie wyników
        val zoneStats = HeartRateZone.values().map { zone ->
            val count = zoneCounts[zone] ?: 0L
            ZoneStat(
                zone = zone,
                minBpm = (zone.minPercent * maxHr).toInt(),
                maxBpm = (zone.maxPercent * maxHr).toInt(),
                durationSeconds = count, // Zakładamy 1 punkt = 1 sekunda (zgodnie z TODO.md)
                percentage = (count / totalPoints) * 100f
            )
        }

        val dominantZone = zoneStats.maxByOrNull { it.durationSeconds }?.zone
        val effect = when (dominantZone) {
            HeartRateZone.Z1 -> "Baza tlenowa i regeneracja"
            HeartRateZone.Z2 -> "Efektywne spalanie tłuszczu"
            HeartRateZone.Z3 -> "Poprawa wydolności tlenowej"
            HeartRateZone.Z4 -> "Zwiększenie progu mleczanowego"
            HeartRateZone.Z5 -> "Trening beztlenowy i VO2 Max"
            null -> "Brak dominującej strefy"
        }

        return HeartRateZoneResult(zoneStats, effect, dominantZone)
    }
}
