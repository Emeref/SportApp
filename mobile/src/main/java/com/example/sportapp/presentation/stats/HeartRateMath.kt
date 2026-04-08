package com.example.sportapp.presentation.stats

import com.example.sportapp.MobileTexts
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.HeartRateZone
import com.example.sportapp.data.model.HeartRateZoneResult
import com.example.sportapp.data.model.ZoneStat

object HeartRateMath {

    fun calculateZones(points: List<WorkoutPointEntity>, maxHr: Int, texts: MobileTexts): HeartRateZoneResult {
        if (points.isEmpty() || maxHr <= 0) {
            return HeartRateZoneResult(emptyList(), texts.HR_NO_DATA, null)
        }

        // 1. Filtracja Moving Average (3 punkty)
        val filteredBpm = points.mapNotNull { it.bpm }.windowed(3, 1) { window ->
            window.average().toInt()
        }

        if (filteredBpm.isEmpty()) {
            return HeartRateZoneResult(emptyList(), texts.HR_TOO_LITTLE_DATA, null)
        }

        // 2. Grupowanie w strefy
        val zoneCounts = mutableMapOf<HeartRateZone, Long>()
        HeartRateZone.entries.forEach { zoneCounts[it] = 0L }

        filteredBpm.forEach { bpm ->
            HeartRateZone.fromBpm(bpm, maxHr)?.let { zone ->
                zoneCounts[zone] = zoneCounts.getOrDefault(zone, 0L) + 1
            }
        }

        val totalPoints = zoneCounts.values.sum().toFloat()
        if (totalPoints == 0f) {
            return HeartRateZoneResult(emptyList(), texts.HR_BELOW_ZONES, null)
        }

        // 3. Przygotowanie wyników
        val zoneStats = HeartRateZone.entries.map { zone ->
            val count = zoneCounts[zone] ?: 0L
            ZoneStat(
                zone = zone,
                minBpm = (zone.minPercent * maxHr).toInt(),
                maxBpm = (zone.maxPercent * maxHr).toInt(),
                durationSeconds = count, // Zakładamy 1 punkt = 1 sekunda
                percentage = (count / totalPoints) * 100f
            )
        }

        val dominantZone = zoneStats.maxByOrNull { it.durationSeconds }?.zone
        val effect = when (dominantZone) {
            HeartRateZone.Z0 -> texts.HR_EFFECT_Z0
            HeartRateZone.Z1 -> texts.HR_EFFECT_Z1
            HeartRateZone.Z2 -> texts.HR_EFFECT_Z2
            HeartRateZone.Z3 -> texts.HR_EFFECT_Z3
            HeartRateZone.Z4 -> texts.HR_EFFECT_Z4
            HeartRateZone.Z5 -> texts.HR_EFFECT_Z5
            null -> texts.HR_EFFECT_NONE
        }

        return HeartRateZoneResult(zoneStats, effect, dominantZone)
    }
}
