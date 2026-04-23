package com.example.sportapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.WorkoutLap

@Database(
    entities = [
        WorkoutEntity::class, 
        WorkoutPointEntity::class, 
        WorkoutDefinition::class,
        WorkoutLap::class,
        SyncMetadataEntity::class,
        LiveLocationPoint::class
    ], 
    version = 24
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutDefinitionDao(): WorkoutDefinitionDao
    abstract fun syncMetadataDao(): SyncMetadataDao
    abstract fun liveLocationDao(): LiveLocationDao

    companion object {
        val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE workouts ADD COLUMN autoLapDistance REAL")
            }
        }
        val MIGRATION_15_16 = object : Migration(15, 16) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE workouts ADD COLUMN hc_session_id TEXT")
            }
        }
        val MIGRATION_16_17 = object : Migration(16, 17) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS sync_metadata (
                        hcRecordId TEXT NOT NULL, 
                        localRecordId INTEGER NOT NULL, 
                        recordType TEXT NOT NULL, 
                        lastSyncTime INTEGER NOT NULL, 
                        syncDirection TEXT NOT NULL, 
                        localModifiedTime INTEGER NOT NULL, 
                        hcModifiedTime INTEGER NOT NULL, 
                        PRIMARY KEY(hcRecordId)
                    )
                """.trimIndent())
            }
        }
        val MIGRATION_17_18 = object : Migration(17, 18) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE workouts ADD COLUMN isFinished INTEGER NOT NULL DEFAULT 1")
            }
        }
        val MIGRATION_18_19 = object : Migration(18, 19) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sync_metadata ADD COLUMN activityName TEXT")
                db.execSQL("ALTER TABLE sync_metadata ADD COLUMN startTime INTEGER")
            }
        }
        val MIGRATION_19_20 = object : Migration(19, 20) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE workout_points ADD COLUMN horizontalAccuracy REAL")
            }
        }
        val MIGRATION_20_21 = object : Migration(20, 21) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE workouts ADD COLUMN baseType TEXT NOT NULL DEFAULT 'Other'")
            }
        }
        val MIGRATION_21_22 = object : Migration(21, 22) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sync_metadata ADD COLUMN stravaUploadId INTEGER")
                db.execSQL("ALTER TABLE sync_metadata ADD COLUMN stravaSyncStatus TEXT NOT NULL DEFAULT 'PENDING'")
            }
        }
        val MIGRATION_22_23 = object : Migration(22, 23) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS live_location_points (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        latitude REAL NOT NULL, 
                        longitude REAL NOT NULL, 
                        timestamp INTEGER NOT NULL, 
                        bearing REAL, 
                        altitude REAL, 
                        accuracy REAL
                    )
                """.trimIndent())
            }
        }
        val MIGRATION_23_24 = object : Migration(23, 24) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Table workout_laps
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `workout_laps` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `workoutId` INTEGER NOT NULL, 
                        `lapNumber` INTEGER NOT NULL, 
                        `durationMillis` INTEGER NOT NULL, 
                        `distanceMeters` REAL NOT NULL, 
                        `avgPaceSecondsPerKm` INTEGER NOT NULL, 
                        `avgSpeed` REAL NOT NULL, 
                        `maxSpeed` REAL NOT NULL, 
                        `avgHeartRate` INTEGER NOT NULL, 
                        `maxHeartRate` INTEGER NOT NULL, 
                        `totalAscent` REAL NOT NULL, 
                        `totalDescent` REAL NOT NULL, 
                        `startLocationIndex` INTEGER NOT NULL, 
                        `endLocationIndex` INTEGER NOT NULL, 
                        FOREIGN KEY(`workoutId`) REFERENCES `workouts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE 
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_laps_workoutId` ON `workout_laps` (`workoutId`)")
                
                // Table live_location_points fix (ensure it exists and has columns)
                val liveLocCursor = db.query("PRAGMA table_info(live_location_points)")
                val hasColumns = liveLocCursor.count > 0
                liveLocCursor.close()
                if (!hasColumns) {
                    db.execSQL("DROP TABLE IF EXISTS live_location_points")
                    db.execSQL("""
                        CREATE TABLE live_location_points (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                            latitude REAL NOT NULL, 
                            longitude REAL NOT NULL, 
                            timestamp INTEGER NOT NULL, 
                            bearing REAL, 
                            altitude REAL, 
                            accuracy REAL
                        )
                    """.trimIndent())
                }

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
