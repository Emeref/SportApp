package com.example.sportapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WorkoutEntity::class, WorkoutPointEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
}
