package com.example.sportapp.data

import com.example.sportapp.presentation.settings.HealthData
import com.example.sportapp.presentation.settings.MobileSettingsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface IUserHealthRepository {
    fun getHealthData(): Flow<HealthData>
    suspend fun updateHealthData(healthData: HealthData)
}

@Singleton
class UserHealthRepository @Inject constructor(
    private val settingsManager: MobileSettingsManager
) : IUserHealthRepository {
    override fun getHealthData(): Flow<HealthData> {
        return settingsManager.settingsFlow.map { it.healthData }
    }

    override suspend fun updateHealthData(healthData: HealthData) {
        val currentState = settingsManager.settingsFlow.first()
        settingsManager.saveSettings(currentState.copy(healthData = healthData))
    }
}
