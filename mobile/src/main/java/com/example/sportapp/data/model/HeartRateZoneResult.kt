package com.example.sportapp.data.model

import androidx.compose.ui.graphics.Color
import com.example.sportapp.core.i18n.AppStrings

enum class HeartRateZone(
    val minPercent: Float,
    val maxPercent: Float,
    val color: Color,
    val getName: (AppStrings) -> String,
    val getDescription: (AppStrings) -> String
) {
    Z0(0.00f, 0.50f, Color(0xFF9E9E9E), { it.hrZone0Name }, { it.hrZone0Desc }),
    Z1(0.50f, 0.60f, Color(0xFF4CAF50), { it.hrZone1Name }, { it.hrZone1Desc }),
    Z2(0.60f, 0.70f, Color(0xFF8BC34A), { it.hrZone2Name }, { it.hrZone2Desc }),
    Z3(0.70f, 0.80f, Color(0xFFFFEB3B), { it.hrZone3Name }, { it.hrZone3Desc }),
    Z4(0.80f, 0.90f, Color(0xFFFF9800), { it.hrZone4Name }, { it.hrZone4Desc }),
    Z5(0.90f, 1.00f, Color(0xFFF44336), { it.hrZone5Name }, { it.hrZone5Desc });

    companion object {
        fun fromBpm(bpm: Int, maxHr: Int): HeartRateZone? {
            val percent = bpm.toFloat() / maxHr
            return entries.find { percent >= it.minPercent && percent < it.maxPercent }
                ?: if (percent >= 1.0f) Z5 else if (percent < 0.0f) null else Z0
        }
    }
}

data class ZoneStat(
    val zone: HeartRateZone,
    val minBpm: Int,
    val maxBpm: Int,
    val durationSeconds: Long,
    val percentage: Float
)

data class HeartRateZoneResult(
    val zones: List<ZoneStat>,
    val trainingEffect: (AppStrings) -> String,
    val dominantZone: HeartRateZone?
)
