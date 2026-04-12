package com.example.sportapp.di

import android.content.Context
import com.example.sportapp.data.IUserHealthRepository
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.data.UserHealthRepository
import com.example.sportapp.data.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(
        workoutRepository: WorkoutRepository
    ): IWorkoutRepository

    @Binds
    @Singleton
    abstract fun bindUserHealthRepository(
        userHealthRepository: UserHealthRepository
    ): IUserHealthRepository

    companion object {
        @Provides
        @Singleton
        fun provideContext(@ApplicationContext context: Context): Context = context
    }
}
