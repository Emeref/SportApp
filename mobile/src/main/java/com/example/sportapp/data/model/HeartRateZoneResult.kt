package com.example.sportapp.data.model

import androidx.compose.ui.graphics.Color

enum class HeartRateZone(
    val displayName: String,
    val minPercent: Float,
    val maxPercent: Float,
    val color: Color,
    val description: String
) {
    Z1("Bardzo lekki", 0.50f, 0.60f, Color(0xFF4CAF50), "Baza tlenowa / Regeneracja"),
    Z2("Lekki", 0.60f, 0.70f, Color(0xFF8BC34A), "Spalanie tłuszczu"),
    Z3("Umiarkowany", 0.70f, 0.80f, Color(0xFFFFEB3B), "Poprawa wydolności tlenowej"),
    Z4("Ciężki", 0.80f, 0.90f, Color(0xFFFF9800), "Wytrzymałość siłowa / Próg mleczanowy"),
    Z5("Maksymalny", 0.90f, 1.00f, Color(0xFFF44336), "Beztlenowy - VO2 Max");

    companion object {
        fun fromBpm(bpm: Int, maxHr: Int): HeartRateZone? {
            val percent = bpm.toFloat() / maxHr
            return values().find { percent >= it.minPercent && percent < it.maxPercent }
                ?: if (percent >= 1.0f) Z5 else if (percent < 0.5f && percent > 0.3f) Z1 else null
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
    val trainingEffect: String,
    val dominantZone: HeartRateZone?
)
