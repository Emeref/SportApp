package com.example.sportapp.complication

import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.example.sportapp.core.i18n.AppStrings
import com.example.sportapp.core.i18n.PlStrings
import java.util.Calendar

/**
 * Skeleton for complication data source that returns short text.
 */
class MainComplicationService : SuspendingComplicationDataSourceService() {

    // Using PlStrings as default for Complications as they don't have access to LocalAppStrings easily
    private val strings: AppStrings = PlStrings

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        if (type != ComplicationType.SHORT_TEXT) {
            return null
        }
        return createComplicationData(strings.mon, strings.monday)
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> createComplicationData(strings.sun, strings.sunday)
            Calendar.MONDAY -> createComplicationData(strings.mon, strings.monday)
            Calendar.TUESDAY -> createComplicationData(strings.tue, strings.tuesday)
            Calendar.WEDNESDAY -> createComplicationData(strings.wed, strings.wednesday)
            Calendar.THURSDAY -> createComplicationData(strings.thu, strings.thursday)
            Calendar.FRIDAY -> createComplicationData(strings.fri, strings.friday)
            Calendar.SATURDAY -> createComplicationData(strings.sat, strings.saturday)
            else -> throw IllegalArgumentException("too many days")
        }
    }

    private fun createComplicationData(text: String, contentDescription: String) =
        ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text).build(),
            contentDescription = PlainComplicationText.Builder(contentDescription).build()
        ).build()
}