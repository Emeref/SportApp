package com.example.sportapp.di

import android.content.Context
import androidx.room.Room
import com.example.sportapp.data.db.AppDatabase
import com.example.sportapp.data.db.SyncMetadataDao
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutDefinitionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sportapp_db"
        )
        .addMigrations(
            AppDatabase.MIGRATION_14_15, 
            AppDatabase.MIGRATION_15_16,
            AppDatabase.MIGRATION_16_17
        )
        .build()
    }

    @Provides
    fun provideWorkoutDao(database: AppDatabase): WorkoutDao {
        return database.workoutDao()
    }

    @Provides
    fun provideWorkoutDefinitionDao(database: AppDatabase): WorkoutDefinitionDao {
        return database.workoutDefinitionDao()
    }

    @Provides
    fun provideSyncMetadataDao(database: AppDatabase): SyncMetadataDao {
        return database.syncMetadataDao()
    }
}
