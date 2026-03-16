package com.example.sportapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sportapp.data.model.WorkoutDefinition

@Database(
    entities = [
        WorkoutEntity::class, 
        WorkoutPointEntity::class, 
        WorkoutDefinition::class
    ], 
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutDefinitionDao(): WorkoutDefinitionDao
}
