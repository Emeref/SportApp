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
        SyncMetadataEntity::class
    ], 
    version = 22
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutDefinitionDao(): WorkoutDefinitionDao
    abstract fun syncMetadataDao(): SyncMetadataDao

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
    }
}
