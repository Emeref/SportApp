package com.example.sportapp.presentation.settings

import com.example.sportapp.TextsWearPL

data class HealthData(
    val gender: Gender = Gender.MALE,
    val age: Int = 40,
    val weight: Int = 87,
    val height: Int = 184,
    val restingHR: Int = 56,
    val maxHR: Int = 220 - 40, // Domyślnie 220 - wiek
    val stepLength: Int = 79
)

enum class Gender {
    MALE, FEMALE;
    
    fun toPolish(): String = when (this) {
        MALE -> TextsWearPL.GENDER_MALE
        FEMALE -> TextsWearPL.GENDER_FEMALE
    }
}
