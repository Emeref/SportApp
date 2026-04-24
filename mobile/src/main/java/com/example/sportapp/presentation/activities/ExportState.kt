package com.example.sportapp.presentation.activities

import android.net.Uri

sealed class ExportState {
    object Idle : ExportState()
    data class Exporting(val progress: Float, val message: String) : ExportState()
    data class Success(val uri: Uri, val isZip: Boolean) : ExportState()
    data class Error(val message: String) : ExportState()
}
