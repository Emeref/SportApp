package com.example.sportapp.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.presentation.settings.SettingsManager
import com.example.sportapp.presentation.workout.SummaryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _stats = MutableStateFlow(SummaryManager.WatchStats())
    val stats: StateFlow<SummaryManager.WatchStats> = _stats.asStateFlow()
    
    val settings = settingsManager.settingsFlow

    init {
        observeSettingsAndRefresh()
    }

    private fun observeSettingsAndRefresh() {
        viewModelScope.launch {
            settings.collectLatest { userSettings ->
                _stats.value = SummaryManager.getStatsForPeriod(
                    workoutDao,
                    userSettings.watchStatsPeriod,
                    userSettings.watchStatsCustomDays
                )
            }
        }
    }

    fun refreshStats() {
        viewModelScope.launch {
            val userSettings = settings.first()
            _stats.value = SummaryManager.getStatsForPeriod(
                workoutDao,
                userSettings.watchStatsPeriod,
                userSettings.watchStatsCustomDays
            )
        }
    }
}
