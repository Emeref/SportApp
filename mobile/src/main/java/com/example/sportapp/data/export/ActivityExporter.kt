package com.example.sportapp.data.export

import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.WorkoutLap

interface ActivityExporter {
    fun getExtension(): String
    fun getMimeType(): String
    fun generateExport(
        workout: WorkoutEntity,
        points: List<WorkoutPointEntity>,
        laps: List<WorkoutLap> = emptyList()
    ): ByteArray
}
