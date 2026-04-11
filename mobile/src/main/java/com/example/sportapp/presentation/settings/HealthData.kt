package com.example.sportapp.presentation.settings

data class HealthData(
    val gender: Gender = Gender.MALE,
    val age: Int = 40,
    val weight: Double = 87.0,
    val height: Double = 184.0,
    val restingHR: Int = 56,
    val maxHR: Int = 220 - 40, // Domyślnie 220 - wiek
    val stepLength: Int = 79,
    val vo2Max: Double? = null
)

enum class Gender {
    MALE, FEMALE;
    
    fun toPolish(): String = when (this) {
        MALE -> "Mężczyzna"
        FEMALE -> "Kobieta"
    }
}
