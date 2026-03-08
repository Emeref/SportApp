package com.example.sportapp.presentation.activities

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.sportapp.data.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActivityListViewModel(context: Context) : ViewModel() {
    private val repository = WorkoutRepository(context)

    private val _activities = MutableStateFlow(repository.getActivityItems())
    val activities = _activities.asStateFlow()

    fun refreshActivities() {
        _activities.value = repository.getActivityItems()
    }
}
