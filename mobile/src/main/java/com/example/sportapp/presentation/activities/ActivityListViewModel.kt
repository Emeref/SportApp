package com.example.sportapp.presentation.activities

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActivityListViewModel(context: Context) : ViewModel() {
    private val repository = WorkoutRepository(context)

    private val _activities = MutableStateFlow<List<ActivityItem>>(emptyList())
    val activities = _activities.asStateFlow()

    init {
        refreshActivities()
    }

    fun refreshActivities() {
        viewModelScope.launch {
            _activities.value = repository.getActivityItemsSuspend()
        }
    }
}
