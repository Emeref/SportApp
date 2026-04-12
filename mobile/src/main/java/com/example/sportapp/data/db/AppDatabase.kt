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
    version = 17
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
    }
}
