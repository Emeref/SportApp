package com.example.sportapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sportapp.data.model.WorkoutDefinition

@Database(
    entities = [
        WorkoutEntity::class, 
        WorkoutPointEntity::class, 
        WorkoutDefinition::class
    ], 
    version = 24
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutDefinitionDao(): WorkoutDefinitionDao

    companion object {
        val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE workouts ADD COLUMN autoLapDistance REAL")
            }
        }
        val MIGRATION_15_16 = object : Migration(15, 16) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE workouts ADD COLUMN isFinished INTEGER NOT NULL DEFAULT 1")
            }
        }
        val MIGRATION_16_17 = object : Migration(16, 17) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE workout_points ADD COLUMN horizontalAccuracy REAL")
            }
        }
        val MIGRATION_17_18 = object : Migration(17, 18) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE workouts ADD COLUMN baseType TEXT NOT NULL DEFAULT 'Other'")
            }
        }
        
        val MIGRATION_18_24 = object : Migration(18, 24) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Helper to safely add columns if they don't exist
                fun addColumnIfNotExists(table: String, column: String, definition: String) {
                    val cursor = db.query("PRAGMA table_info($table)")
                    var exists = false
                    try {
                        val nameIndex = cursor.getColumnIndex("name")
                        if (nameIndex != -1) {
                            while (cursor.moveToNext()) {
                                if (cursor.getString(nameIndex) == column) {
                                    exists = true
                                    break
                                }
                            }
                        }
                    } finally {
                        cursor.close()
                    }
                    if (!exists) {
                        db.execSQL("ALTER TABLE $table ADD COLUMN $column $definition")
                    }
                }

                // Add missing columns to workouts table
                addColumnIfNotExists("workouts", "isSynced", "INTEGER NOT NULL DEFAULT 0")
                addColumnIfNotExists("workouts", "hc_session_id", "TEXT")
                addColumnIfNotExists("workouts", "avgPace", "REAL")
                addColumnIfNotExists("workouts", "maxSpeed", "REAL")
                addColumnIfNotExists("workouts", "maxAltitude", "REAL")
                addColumnIfNotExists("workouts", "minAltitude", "REAL")
                addColumnIfNotExists("workouts", "avgStepLength", "REAL")
                addColumnIfNotExists("workouts", "avgCadence", "REAL")
                addColumnIfNotExists("workouts", "maxCadence", "REAL")
                addColumnIfNotExists("workouts", "maxPressure", "REAL")
                addColumnIfNotExists("workouts", "minPressure", "REAL")
                addColumnIfNotExists("workouts", "bestPace1km", "REAL")
                addColumnIfNotExists("workouts", "destinationLatitude", "REAL")
                addColumnIfNotExists("workouts", "destinationLongitude", "REAL")
                
                // Add missing columns to workout_points table
                addColumnIfNotExists("workout_points", "pressure", "REAL")

                // Add missing columns to workout_definitions table
                addColumnIfNotExists("workout_definitions", "autoLapDistance", "REAL")
                addColumnIfNotExists("workout_definitions", "sortOrder", "INTEGER NOT NULL DEFAULT 0")
                addColumnIfNotExists("workout_definitions", "displayOrder", "INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
