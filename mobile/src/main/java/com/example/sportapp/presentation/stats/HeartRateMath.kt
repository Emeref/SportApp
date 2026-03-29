package com.example.sportapp.presentation.stats

import com.example.sportapp.core.i18n.AppStrings
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.HeartRateZone
import com.example.sportapp.data.model.HeartRateZoneResult
import com.example.sportapp.data.model.ZoneStat

object HeartRateMath {

    fun calculateZones(points: List<WorkoutPointEntity>, maxHr: Int): HeartRateZoneResult {
        if (points.isEmpty() || maxHr <= 0) {
            return HeartRateZoneResult(emptyList(), { strings: AppStrings -> strings.noHrData }, null)
        }

        // 1. Filtracja Moving Average (3 punkty)
        val filteredBpm = points.mapNotNull { it.bpm }.windowed(3, 1) { window ->
            window.average().toInt()
        }

        if (filteredBpm.isEmpty()) {
            return HeartRateZoneResult(emptyList(), { strings: AppStrings -> strings.tooLittleData }, null)
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
            return HeartRateZoneResult(emptyList(), { strings: AppStrings -> strings.hrBelowZones }, null)
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
        val effect: (AppStrings) -> String = when (dominantZone) {
            HeartRateZone.Z0 -> { strings: AppStrings -> strings.effectWarmup }
            HeartRateZone.Z1 -> { strings: AppStrings -> strings.effectRecovery }
            HeartRateZone.Z2 -> { strings: AppStrings -> strings.effectFatBurn }
            HeartRateZone.Z3 -> { strings: AppStrings -> strings.effectAerobic }
            HeartRateZone.Z4 -> { strings: AppStrings -> strings.effectLactate }
            HeartRateZone.Z5 -> { strings: AppStrings -> strings.effectAnaerobic }
            null -> { strings: AppStrings -> strings.effectNone }
        }

        return HeartRateZoneResult(zoneStats, effect, dominantZone)
    }
}
