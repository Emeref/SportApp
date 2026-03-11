package com.example.sportapp.presentation.menu

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.sportapp.presentation.workout.SummaryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _stats = MutableStateFlow(SummaryManager.getWeeklyStats(context))
    val stats = _stats.asStateFlow()

    fun refreshStats() {
        _stats.value = SummaryManager.getWeeklyStats(context)
    }
}
