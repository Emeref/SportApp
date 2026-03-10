package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ActivityDetailViewModelFactory(
    private val context: Context,
    private val sessionId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityDetailViewModel(context, sessionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
