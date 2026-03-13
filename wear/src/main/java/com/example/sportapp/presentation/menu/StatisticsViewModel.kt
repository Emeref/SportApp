package com.example.sportapp.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.presentation.workout.SummaryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val workoutDao: WorkoutDao
) : ViewModel() {

    private val _stats = MutableStateFlow(SummaryManager.WeeklyStats(0, 0, 0))
    val stats = _stats.asStateFlow()

    init {
        refreshStats()
    }

    fun refreshStats() {
        viewModelScope.launch {
            _stats.value = SummaryManager.getWeeklyStats(workoutDao)
        }
    }
}
