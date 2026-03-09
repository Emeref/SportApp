package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class OverallStatsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OverallStatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OverallStatsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
