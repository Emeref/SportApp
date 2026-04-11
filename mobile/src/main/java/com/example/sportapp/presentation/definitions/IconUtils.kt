package com.example.sportapp.presentation.definitions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

fun getIconForName(name: String): ImageVector {
    return when (name) {
        "DirectionsRun" -> Icons.AutoMirrored.Filled.DirectionsRun
        "DirectionsWalk" -> Icons.AutoMirrored.Filled.DirectionsWalk
        "DirectionsBike" -> Icons.AutoMirrored.Filled.DirectionsBike
        "Pool" -> Icons.Default.Pool
        "Mountain" -> Icons.Default.Terrain
        "Hiking" -> Icons.Default.Hiking
        "Fitness" -> Icons.Default.FitnessCenter
        "SelfImprovement" -> Icons.Default.SelfImprovement
        "SportsTennis" -> Icons.Default.SportsTennis
        "Kayaking" -> Icons.Default.Kayaking
        "Snowboarding" -> Icons.Default.Snowboarding
        "DownhillSkiing" -> Icons.Default.DownhillSkiing
        "Surfing" -> Icons.Default.Surfing
        "IceSkating" -> Icons.Default.IceSkating
        "Golf" -> Icons.Default.GolfCourse
        "SportsSoccer" -> Icons.Default.SportsSoccer
        "SportsBasketball" -> Icons.Default.SportsBasketball
        "SportsVolleyball" -> Icons.Default.SportsVolleyball
        "SportsBaseball" -> Icons.Default.SportsBaseball
        "Sailing" -> Icons.Default.Sailing
        "Skateboarding" -> Icons.Default.Skateboarding
        "Rowing" -> Icons.Default.Rowing
        "Stairs" -> Icons.Default.Stairs
        "MusicNote" -> Icons.Default.MusicNote
        "SportsMartialArts" -> Icons.Default.SportsMartialArts
        "Sports" -> Icons.Default.EmojiEvents
        "Timer" -> Icons.Default.Timer
        else -> Icons.AutoMirrored.Filled.DirectionsRun
    }
}
